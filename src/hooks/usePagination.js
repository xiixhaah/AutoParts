import { ref, computed } from 'vue'

export function usePagination(initialPageSize = 5) {
  const currentPage = ref(1)
  const pageSize = ref(initialPageSize)
  const sourceData = ref([]) 

  const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return sourceData.value.slice(start, end)
  })

  const indexMethod = (index) => {
    return (currentPage.value - 1) * pageSize.value + index + 1
  }

  // 增加 isReset 参数：如果传 false，就不重置页码
  const setSourceData = (data, isReset = true) => {
    sourceData.value = data
    if (isReset) {
      currentPage.value = 1 
    }
  }

  return { currentPage, pageSize, sourceData, paginatedData, indexMethod, setSourceData }
}