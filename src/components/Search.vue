<template>
  <div class="app-container">
    <el-card class="search-box">
      <div class="input-area">
        <el-input 
          v-model="searchText" 
          placeholder="请输入配件描述 (如: 配件编码、适用车型等)" 
          class="search-input"
          clearable>
        </el-input>
        
        <div class="k-input">
          <span class="k-label">返回数量:</span>
          <el-input-number v-model="kValue" :min="1" :max="50" controls-position="right" style="width: 100px;" @change="applySecondaryFilters" />
        </div>
        
        <input type="file" ref="fileInputRef" accept="image/*" style="display: none" @change="onFileSelected" />
        <el-button type="info" plain :icon="Picture" @click="triggerFileSelect">上传查询图片</el-button>

        <el-button type="primary" :icon="Search" @click="doSearch" :loading="loading">
          智能检索
        </el-button>
      </div>

      <div v-if="previewUrl" class="image-preview">
        <span>当前查询图像：</span>
        <img :src="previewUrl" alt="query-img" />
        <el-button type="danger" link @click="clearImage">移除图像</el-button>
      </div>
    </el-card>

    <el-card class="filter-area" v-if="hasSearched">
      <div class="result-header">
        <div class="header-left">
          <span class="title">检索结果展示</span>
        </div>
        <div class="header-filters">
          <span class="filter-label">二次筛选:</span>
          <el-select v-model="filterArea" placeholder="配件来源" size="small" style="width: 150px; margin-right: 10px;" @change="applySecondaryFilters" clearable>
            <el-option label="全部地区" value="" />
            <el-option label="四川成都" value="飞恒" />
            <el-option label="湖北十堰" value="襄建" />
            <el-option label="山东济南 " value="山东" />
            <el-option label="福建莆田" value="中路通" />
          </el-select>
          <el-select v-model="sortType" placeholder="默认排序" size="small" style="width: 140px;" @change="applySecondaryFilters">
            <el-option label="综合相似度优先" value="sim" />
            <el-option label="按价格升序" value="priceAsc" />
            <el-option label="按价格降序" value="priceDesc" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card class="result-area" v-if="hasSearched">
      <div v-if="sourceData.length > 0">
        <el-table :data="paginatedData" style="width: 100%" stripe border>
          <el-table-column type="index" label="序号" width="80" align="center" :index="indexMethod"></el-table-column>
          <el-table-column label="配件图像" width="120" align="center">
            <template #default="scope">
              <img :src="`/auto_parts_images/${scope.row.imageSrc}`" class="table-image" alt="配件图片" @error="handleImageError" />
            </template>
          </el-table-column>
          <el-table-column prop="partsName" label="配件名称" min-width="150" align="center" />
          <el-table-column prop="partsCode" label="配件编码" min-width="150" align="center" />
          <el-table-column prop="retailPr" label="配件价格(¥)" min-width="120" align="center">
            <template #default="scope">
              <span style="color: #f56c6c; font-weight: bold;">{{ scope.row.retailPr }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="adaptedVehicle" label="适用车型" min-width="150" align="center" />
          <el-table-column prop="enterpriseName" label="供应商" min-width="200" align="center" show-overflow-tooltip />
          <el-table-column label="操作" width="120" align="center">
            <template #default>
               <el-button size="small" type="primary" plain>查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="sourceData.length" />
        </div>
      </div>
      <el-empty v-else description="未找到匹配的配件" />
    </el-card>

    <el-dialog 
      v-model="cropVisible" 
      title="框选需要检索的配件区域" 
      width="700px" 
      top="5vh" 
      append-to-body 
      destroy-on-close 
      @opened="initCropper" 
      @closed="destroyCropper"
    >
      <div style="width: 100%; height: 400px; background-color: #f8f8f8; overflow: hidden;">
        <img id="cropImage" :src="rawImageUrl" alt="原图" style="display: block; max-width: 100%;" />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cropVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCrop" :loading="cropLoading">确认区域</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ✨ 绝对不 import Cropper，防止 Vite 报错
import { ref, onMounted } from 'vue' 
import { Search, Picture } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

import { usePagination } from '../hooks/usePagination'

// ✨ 核武器：静默注入原厂 JS 和 CSS，完全绕开本地依赖和 Vite 解析
onMounted(() => {
  if (!document.getElementById('cropper-style')) {
    const link = document.createElement('link')
    link.id = 'cropper-style'
    link.rel = 'stylesheet'
    link.href = 'https://unpkg.com/cropperjs@1.6.1/dist/cropper.min.css'
    document.head.appendChild(link)
  }
  if (!document.getElementById('cropper-script')) {
    const script = document.createElement('script')
    script.id = 'cropper-script'
    script.src = 'https://unpkg.com/cropperjs@1.6.1/dist/cropper.min.js'
    document.head.appendChild(script)
  }
})

const searchText = ref('')
const searchFile = ref(null) 
const previewUrl = ref('')
const loading = ref(false)
const hasSearched = ref(false)
const kValue = ref(5) 
const filterArea = ref('')
const sortType = ref('sim')
const originalResults = ref([]) 

const { currentPage, pageSize, sourceData, paginatedData, indexMethod, setSourceData } = usePagination(5)

const applySecondaryFilters = () => {
  if (originalResults.value.length === 0) return
  let finalData = [...originalResults.value]
  if (filterArea.value) {
    finalData = finalData.filter(item => item.enterpriseName && item.enterpriseName.includes(filterArea.value))
  }
  if (sortType.value === 'priceAsc') finalData.sort((a, b) => a.retailPr - b.retailPr)
  else if (sortType.value === 'priceDesc') finalData.sort((a, b) => b.retailPr - a.retailPr)
  setSourceData(finalData.slice(0, kValue.value))
}

const fileInputRef = ref(null)
const cropVisible = ref(false)
const rawImageUrl = ref('')
const cropLoading = ref(false)

// ✨ 最纯净的原生变量，脱离所有 Vue 响应式作用域！
let rawCropper = null

const triggerFileSelect = () => { fileInputRef.value.click() }

const onFileSelected = (e) => {
  const file = e.target.files[0]
  if (!file) return
  rawImageUrl.value = URL.createObjectURL(file)
  cropVisible.value = true 
  e.target.value = '' 
}

const initCropper = () => {
  if (!window.Cropper) {
    ElMessage.warning('裁剪工具初始化中，请稍等...')
    setTimeout(initCropper, 500)
    return
  }

  const imgElement = document.getElementById('cropImage')
  if (!imgElement) return

  const startCrop = () => {
    if (rawCropper) {
      rawCropper.destroy()
      rawCropper = null
    }
    
    // 调用 window 上的原厂类，绝对不可能没有方法！
    rawCropper = new window.Cropper(imgElement, {
      aspectRatio: 1,         // 强制正方形选区
      viewMode: 1,            // 限制框不出界
      dragMode: 'crop',       // 允许自由画框
      autoCropArea: 0.8,
      background: true
    })
  }

  // ✨ 强制延迟 300ms，等弹窗动画彻底播完，高度彻底稳定！不再被压扁！
  setTimeout(() => {
    if (imgElement.complete && imgElement.naturalWidth !== 0) {
      startCrop()
    } else {
      imgElement.onload = startCrop
    }
  }, 300)
}

const destroyCropper = () => {
  if (rawCropper) { 
    rawCropper.destroy()
    rawCropper = null 
  }
  if (rawImageUrl.value) {
    URL.revokeObjectURL(rawImageUrl.value)
    rawImageUrl.value = ''
  }
}

const confirmCrop = () => {
  if (!rawCropper) {
    ElMessage.error('裁剪组件未就绪，请稍后再试')
    return
  }
  
  try {
    // ✨ 这个方法 1000% 存在，因为它是原生对象！
    const canvas = rawCropper.getCroppedCanvas({ 
      width: 224, 
      height: 224, 
      fillColor: '#fff',
      imageSmoothingEnabled: true, 
      imageSmoothingQuality: 'high' 
    }) 
    
    if (!canvas) throw new Error("提取失败，请检查原图是否损坏")

    cropLoading.value = true
    canvas.toBlob((blob) => {
      if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
      previewUrl.value = URL.createObjectURL(blob)
      searchFile.value = blob
      cropVisible.value = false
    }, 'image/jpeg', 0.9)
  } catch (e) {
    ElMessage.error(e.message || '裁剪出错，请重试')
  } finally {
    cropLoading.value = false
  }
}

const clearImage = () => {
  searchFile.value = null
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = ''
}

const handleImageError = (e) => {
  e.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'
}

const doSearch = async () => {
  if (!searchText.value && !searchFile.value) {
    ElMessage.warning('请输入文本或上传查询图片')
    return
  }
  loading.value = true
  hasSearched.value = true
  
  const formData = new FormData()
  if (searchText.value) formData.append('text', searchText.value)
  if (searchFile.value) formData.append('file', searchFile.value, 'query.jpg')

  try {
    const res = await axios.post('http://localhost:8080/api/search', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    originalResults.value = res.data
    applySecondaryFilters()
    ElMessage.success('检索完成')
  } catch (error) {
    ElMessage.error('检索服务通信异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.app-container { max-width: 1400px; margin: 0 auto; padding: 20px; font-family: sans-serif; }
.search-box { text-align: center; margin-bottom: 10px; padding: 20px 0;}
.filter-area { margin-bottom: 10px; }
.input-area { display: flex; justify-content: center; align-items: center; gap: 15px; margin-top: 10px; }
.search-input { width: 400px; }
.k-input { display: flex; align-items: center; font-weight: bold; color: #606266; }
.k-label { margin-right: 8px; font-size: 14px; }
.image-preview { margin-top: 15px; display: flex; align-items: center; justify-content: center; gap: 10px; }
.image-preview img { height: 60px; width: 60px; object-fit: cover; border-radius: 4px; border: 2px solid #409EFF; }
.result-header { display: flex; justify-content: space-between; align-items: center; padding: 5px 0;}
.header-left .title { font-weight: bold; color: #409EFF; font-size: 16px; }
.header-filters { display: flex; align-items: center; }
.filter-label { font-size: 14px; color: #606266; margin-right: 10px; }
.table-image { width: 60px; height: 60px; object-fit: cover; border: 1px solid #eee; border-radius: 4px;}
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>