<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside-menu">
      <div class="logo">汽车配件检索系统</div>
      <el-menu
        active-text-color="#409EFF"
        background-color="#304156"
        text-color="#bfcbd9"
        :default-active="$route.path"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/search">
          <el-icon><Search /></el-icon>
          <span>配件检索</span>
        </el-menu-item>
        <el-menu-item index="/manage">
          <el-icon><Menu /></el-icon>
          <span>配件管理</span>
        </el-menu-item>
        <el-menu-item index="/stats">
          <el-icon><DataLine /></el-icon>
          <span>数据统计与分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <div class="header-right">
          <el-avatar size="small" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          <span class="username">管理员</span>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { Search, Menu, DataLine } from '@element-plus/icons-vue'
</script>

<style scoped>
.layout-container { height: 100vh; display: flex; }
.aside-menu { background-color: #304156; box-shadow: 2px 0 6px rgba(0,21,41,.35); z-index: 10; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; background: #2b3643; }
.el-menu-vertical { border-right: none; }
.header { display: flex; justify-content: space-between; align-items: center; background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.page-title { font-size: 18px; font-weight: 600; color: #333; }
.header-right { display: flex; align-items: center; gap: 10px; font-size: 14px; }
.main-content { background-color: #f0f2f5; padding: 20px; }
</style>
