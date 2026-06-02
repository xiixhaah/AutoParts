<template>
  <div class="manage-container">
    <el-card class="box-card">
      <div class="header-action">
        <span class="title">配件基础信息管理</span>
        <el-button type="success" :icon="Plus" @click="openAddDialog">新增配件</el-button>
      </div>

      <el-table :data="paginatedData" v-loading="loading" border stripe style="width: 100%; margin-top: 15px;">
        <el-table-column type="index" label="序号" width="60" align="center" :index="indexMethod"/>
        <el-table-column label="首图展示" width="100" align="center">
          <template #default="scope">
            <img v-if="scope.row.imageSrc" :src="`/auto_parts_images/${scope.row.imageSrc}`" class="table-img" @error="handleImgError"/>
            <span v-else class="no-img">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="partsName" label="配件名称" min-width="150" />
        <el-table-column prop="partsCode" label="配件编码" min-width="150" />
        <el-table-column prop="partsType" label="配件类别" width="120" align="center" />
        <el-table-column prop="retailPr" label="单价(元)" width="100" align="center" />
        <el-table-column prop="adaptedVehicle" label="适用车型" min-width="150" />
        <el-table-column prop="enterpriseName" label="供应商" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" :icon="Edit" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="sourceData.length"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '配件信息编辑' : '新增配件'" width="650px" top="5vh" @closed="resetForm">
      <el-form ref="formRef" :model="editForm" :rules="formRules" label-width="100px">
        <el-form-item label="配件名称" prop="partsName">
          <el-input v-model="editForm.partsName" placeholder="请输入名称"></el-input>
        </el-form-item>
        
        <el-form-item label="配件编码" prop="partsCode">
          <el-input v-model="editForm.partsCode" placeholder="如: 3104015-TB04X01"></el-input>
        </el-form-item>

        <el-form-item label="配件类别" prop="partsType">
          <el-select v-model="editForm.partsType" placeholder="请选择配件类别" style="width: 100%;">
            <el-option label="发动机系统" value="发动机系统" />
            <el-option label="传动系统" value="传动系统" />
            <el-option label="制动系统" value="制动系统" />
            <el-option label="底盘及转向" value="底盘及转向" />
            <el-option label="电气及照明" value="电气及照明" />
            <el-option label="车身附件" value="车身附件" />
          </el-select>
        </el-form-item>

        <el-form-item label="配件单价" prop="retailPr">
          <el-input-number v-model="editForm.retailPr" :precision="2" :step="10" style="width: 150px;" />
        </el-form-item>
        
        <el-form-item label="适用车型" prop="adaptedVehicle">
          <el-input v-model="editForm.adaptedVehicle" placeholder="如: DF45 DF44"></el-input>
        </el-form-item>

        <el-form-item label="供应商" prop="enterpriseID">
          <el-select v-model="editForm.enterpriseID" placeholder="请选择区域供应商" style="width: 100%;">
            <el-option label="四川成都 (飞恒)" value="E-01" />
            <el-option label="湖北十堰 (襄建/汉乘)" value="E-02" />
            <el-option label="山东济南 (华麦/泰丰)" value="E-03" />
            <el-option label="福建莆田 (中路通)" value="E-04" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="配件图片" prop="imageList" class="is-required">
          <div class="image-wall">
            <div class="image-item" v-for="(img, index) in editForm.imageList" :key="index">
              <img :src="`/auto_parts_images/${img}`" />
              <div class="img-mask" @click="removeImage(index)">
                <el-icon><Delete /></el-icon>
              </div>
            </div>
            <div class="upload-trigger" @click="triggerFileSelect">
              <el-icon><Plus /></el-icon>
            </div>
            <input type="file" ref="fileInputRef" accept="image/*" style="display: none" @change="onFileSelected" />
          </div>
          <div v-if="imgErrorMsg" style="color: #F56C6C; font-size: 12px; margin-top: 4px; line-height: 1;">{{ imgErrorMsg }}</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveData" :loading="submitLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="cropVisible" title="滑动裁剪图片" width="700px" append-to-body destroy-on-close @opened="initCropper" @closed="destroyCropper">
      <div class="cropper-wrapper">
        <img id="cropImageManage" :src="rawImageUrl" style="display: block; max-width: 100%;" />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cropVisible = false">放弃裁剪</el-button>
          <el-button type="primary" @click="confirmCrop" :loading="cropLoading">确认裁剪并上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { usePagination } from '../hooks/usePagination' 

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const imgErrorMsg = ref('')

const { currentPage, pageSize, sourceData, paginatedData, indexMethod, setSourceData } = usePagination(5)

// ✨ 表单数据模型全部对齐后端
const editForm = ref({ 
  partsID: '', 
  partsName: '', 
  partsCode: '', 
  partsType: '', // 改为 partsType
  retailPr: 0, 
  adaptedVehicle: '', 
  enterpriseID: '', // 改为 enterpriseID
  imageList: [] 
})

const formRules = {
  partsName: [{ required: true, message: '配件名称不能为空', trigger: 'blur' }],
  partsCode: [{ required: true, message: '配件编码不能为空', trigger: 'blur' }],
  partsType: [{ required: true, message: '请选择配件类别', trigger: 'change' }],
  retailPr: [{ required: true, message: '配件单价不能为空', trigger: 'blur' }],
  adaptedVehicle: [{ required: true, message: '适用车型不能为空', trigger: 'blur' }],
  enterpriseID: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

const fileInputRef = ref(null)
const cropVisible = ref(false)
const rawImageUrl = ref('')
const cropLoading = ref(false)

let rawCropper = null

const triggerFileSelect = () => { fileInputRef.value.click() }

const onFileSelected = (e) => {
  const file = e.target.files[0]
  if (!file) return
  destroyCropper()
  rawImageUrl.value = URL.createObjectURL(file)
  cropVisible.value = true
  e.target.value = '' 
}

const initCropper = () => {
  if (!window.Cropper) {
    ElMessage.warning('裁剪工具加载中，请稍等...')
    setTimeout(initCropper, 500)
    return
  }
  const imgElement = document.getElementById('cropImageManage')
  if (!imgElement) return

  const startCrop = () => {
    if (rawCropper) {
      rawCropper.destroy()
      rawCropper = null
    }
    rawCropper = new window.Cropper(imgElement, {
      aspectRatio: 1,         
      viewMode: 1,            
      dragMode: 'move',       
      autoCropArea: 0.8,      
      background: true,       
      movable: true,
      zoomable: true,
      rotatable: false,
      scalable: true
    })
  }

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
    ElMessage.warning('裁剪器还未准备好，请稍后再试')
    return
  }
  try {
    const canvas = rawCropper.getCroppedCanvas({
      width: 500, 
      height: 500,
      fillColor: '#fff',
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    })

    if (!canvas) throw new Error("无法获取 Canvas，请检查图片")

    cropLoading.value = true
    canvas.toBlob(async (blob) => {
      try {
        const formData = new FormData()
        const fileName = `crop_${new Date().getTime()}.jpg`
        formData.append('file', blob, fileName)
        const res = await axios.post('http://localhost:8080/api/manage/upload', formData)
        if (res.data) {
          editForm.value.imageList.push(res.data)
          imgErrorMsg.value = '' 
          cropVisible.value = false
          ElMessage.success('图片裁剪并上传成功！')
        } else {
          ElMessage.error('后端返回路径为空，请检查后端')
        }
      } catch (error) {
        ElMessage.error('图片上传失败，请检查网络或接口')
        console.error(error)
      } finally {
        cropLoading.value = false
        destroyCropper()
      }
    }, 'image/jpeg', 0.9) 
  } catch (err) {
    ElMessage.error(err.message)
    console.error(err)
  }
}

const removeImage = (index) => {
  editForm.value.imageList.splice(index, 1)
}

const fetchList = async (isReset = true) => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/manage/list')
    setSourceData(res.data, isReset)
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  if(formRef.value) formRef.value.resetFields()
  editForm.value = { partsID: '', partsName: '', partsCode: '', partsType: '', retailPr: 0, adaptedVehicle: '', enterpriseID: '', imageList: [] }
  imgErrorMsg.value = ''
}

const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = async (row) => {
  isEdit.value = true
  // ✨ 打开编辑时，注入对应的 partsType 和 enterpriseID
  editForm.value = { 
    ...JSON.parse(JSON.stringify(row)),
    partsType: row.partsType || '', 
    enterpriseID: row.enterpriseID || '' 
  }
  editForm.value.imageList = [] 
  dialogVisible.value = true
  
  try {
    const res = await axios.get(`http://localhost:8080/api/manage/images/${row.partsID}`)
    editForm.value.imageList = res.data || []
  } catch (e) {
    console.error("获取图片列表失败", e)
  }
}

const saveData = () => {
  if (editForm.value.imageList.length === 0) {
    imgErrorMsg.value = '请至少上传一张配件图片'
    return
  } else {
    imgErrorMsg.value = ''
  }

  formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查并完善表单必填项')
      return
    }
    submitLoading.value = true
    try {
      const url = isEdit.value ? 'http://localhost:8080/api/manage/update' : 'http://localhost:8080/api/manage/add'
      await axios.post(url, editForm.value)
      
      ElMessage.success({ message: isEdit.value ? '配件信息修改成功！' : '新配件添加成功！', duration: 3000 })
      dialogVisible.value = false
      fetchList(isEdit.value ? false : true) 
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要永久删除配件 "${row.partsName}" 吗？`, '危险操作', {
    confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'error',
  }).then(async () => {
    await axios.delete(`http://localhost:8080/api/manage/delete/${row.partsID}`)
    ElMessage.success('配件删除成功')
    fetchList(false) 
  }).catch(() => {})
}

const handleImgError = (e) => { e.target.src = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png' }

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

  fetchList() 
})
</script>

<style scoped>
.manage-container { width: 100%; }
.header-action { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 16px; font-weight: bold; color: #409EFF; }
.table-img { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #ebeef5; }
.no-img { font-size: 12px; color: #999; }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }

.image-wall { display: flex; flex-wrap: wrap; gap: 10px; }
.image-item { width: 100px; height: 100px; border-radius: 6px; overflow: hidden; position: relative; border: 1px solid #dcdfe6; }
.image-item img { width: 100%; height: 100%; object-fit: cover; display: block; }
.img-mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); color: white; display: flex; justify-content: center; align-items: center; font-size: 20px; opacity: 0; cursor: pointer; transition: opacity 0.3s; }
.image-item:hover .img-mask { opacity: 1; }

.upload-trigger { width: 100px; height: 100px; border: 1px dashed #dcdfe6; border-radius: 6px; display: flex; justify-content: center; align-items: center; font-size: 28px; color: #8c939d; cursor: pointer; transition: border-color 0.3s; box-sizing: border-box; }
.upload-trigger:hover { border-color: #409eff; }

.cropper-wrapper { 
  width: 100%; 
  height: 400px; 
  background: #f8f8f8; 
  overflow: hidden;
}
</style>