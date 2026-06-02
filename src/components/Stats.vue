<template>
  <div class="stats-container">
    <el-row :gutter="20" class="kpi-row">
      <el-col :span="6">
        <el-card shadow="always" class="kpi-card">
          <div class="kpi-title">系统库内配件总数</div>
          <div class="kpi-value" style="color: #409EFF;">1,245</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="always" class="kpi-card">
          <div class="kpi-title">接入供应商/企业数</div>
          <div class="kpi-value" style="color: #67C23A;">15</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="always" class="kpi-card">
          <div class="kpi-title">VISTA-AFR 平均检索精度</div>
          <div class="kpi-value" style="color: #E6A23C;">84.3%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="always" class="kpi-card">
          <div class="kpi-title">近30天累计检索次数</div>
          <div class="kpi-value" style="color: #F56C6C;">8,502</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="barChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <div ref="trendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div ref="modeChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div ref="rankChartRef" class="chart-box-wide"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const pieChartRef = ref(null)
const barChartRef = ref(null)
const rankChartRef = ref(null)
const trendChartRef = ref(null)
const modeChartRef = ref(null)

let chartInstances = []

const initCharts = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/manage/stats')
    const { enterpriseData, rankingData } = res.data

    // 1. 供应链/厂家占比 (环形图) - 保持真实数据
    const pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      title: { text: '各合作厂家配件数量占比分布', left: 'center', textStyle: { fontSize: 15 } },
      tooltip: { trigger: 'item' },
      legend: { type: 'scroll', orient: 'vertical', left: 10, top: 40 },
      series: [
        {
          type: 'pie', radius: ['45%', '70%'], center: ['60%', '55%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          data: enterpriseData
        }
      ]
    })

    // 2. 配件类别库存 (柱状图) - ✨ 改为完善的模拟数据
    const barChart = echarts.init(barChartRef.value)
    const mockCategoryData = [
      { name: '发动机系统', value: 420 },
      { name: '传动系统', value: 315 },
      { name: '制动系统', value: 280 },
      { name: '底盘及转向', value: 195 },
      { name: '电气及照明', value: 160 },
      { name: '车身附件', value: 110 }
    ]
    barChart.setOption({
      title: { text: '系统各类别配件存量规模', left: 'center', textStyle: { fontSize: 15 } },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: mockCategoryData.map(item => item.name), axisLabel: { interval: 0 } },
      yAxis: { type: 'value' },
      series: [{ 
        data: mockCategoryData.map(item => item.value), 
        type: 'bar', 
        barWidth: '35%', 
        itemStyle: { color: '#5470C6', borderRadius: [4,4,0,0] },
        label: { show: true, position: 'top' }
      }]
    })

    // 3. 热度排行 (截取前5)
    const rankChart = echarts.init(rankChartRef.value)
    const topRanking = rankingData.slice(0, 5) 
    rankChart.setOption({
      title: { text: '系统高频热门检索配件排行 (Top 5)', left: 'center', textStyle: { fontSize: 15 } },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', name: '检索命中次数' },
      yAxis: { type: 'category', data: topRanking.map(item => item.name).reverse() },
      series: [{
          name: '频次', type: 'bar',
          data: topRanking.map(item => item.value).reverse(),
          itemStyle: { color: '#91CC75', borderRadius: [0,4,4,0] },
          label: { show: true, position: 'right' }
      }]
    })

    // 4. 模拟数据：近一个月检索频次趋势
    const trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      title: { text: '近30日系统检索频次趋势', left: 'center', textStyle: { fontSize: 15 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', boundaryGap: false, data: Array.from({length: 30}, (_, i) => `日${i+1}`) },
      yAxis: { type: 'value' },
      series: [{
        name: '检索量', type: 'line', smooth: true,
        areaStyle: { opacity: 0.3, color: '#EE6666' },
        itemStyle: { color: '#EE6666' },
        data: Array.from({length: 30}, () => Math.floor(Math.random() * 300) + 100) 
      }]
    })

    // 5. 模拟数据：检索模式占比
    const modeChart = echarts.init(modeChartRef.value)
    modeChart.setOption({
      title: { text: '多模态检索方式使用占比', left: 'center', textStyle: { fontSize: 15 } },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', 
        radius: '45%', // ✨ 把半径从 60% 缩小到 45%，给文字留出足够的空间
        center: ['50%', '55%'],
        label: {
          show: true,
          formatter: '{b}\n{d}%', // ✨ 换行显示名称和百分比，非常美观
          lineHeight: 18
        },
        data: [
          { value: 45, name: '纯文本检索' },
          { value: 30, name: '图文混合检索' },
          { value: 25, name: '纯图像检索' }
        ],
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
      }]
    })

    chartInstances = [pieChart, barChart, rankChart, trendChart, modeChart]
    window.addEventListener('resize', handleResize)

  } catch (error) {
    ElMessage.error('加载系统统计数据异常，请检查数据接口')
  }
}

const handleResize = () => { chartInstances.forEach(chart => chart.resize()) }

onMounted(() => { initCharts() })
onUnmounted(() => { window.removeEventListener('resize', handleResize) })
</script>

<style scoped>
.stats-container { width: 100%; padding-bottom: 20px;}
.kpi-row { margin-bottom: 20px; }
.kpi-card { text-align: center; border-radius: 8px; }
.kpi-title { font-size: 18px; color: #000; margin-bottom: 10px; font-weight: bold;}
.kpi-value { font-size: 28px; font-weight: 900; }
.chart-row { margin-bottom: 20px; }
.chart-box { width: 100%; height: 350px; }
.chart-box-wide { width: 100%; height: 300px; }
</style>