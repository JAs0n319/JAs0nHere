<template>
  <div class="min-h-screen flex flex-col">
    <!-- 顶部导航 -->
    <NavBar />

    <!-- 路由视图（带淡入过渡） -->
    <main class="flex-1">
      <RouterView v-slot="{ Component }">
        <transition name="view-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </RouterView>
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'

/**
 * 预先应用暗黑模式，减少切换闪烁
 * - 与 NavBar 内的切换逻辑兼容（同一 KEY）
 * - 若本地无偏好，则按系统偏好
 */
const THEME_KEY = 'theme-preference' // 'dark' | 'light' | null
try {
  const saved = localStorage.getItem(THEME_KEY)
  const sysDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches
  const needDark = saved ? saved === 'dark' : sysDark
  if (needDark) document.documentElement.classList.add('dark')
} catch {
  // localStorage 在隐私模式下可能异常，静默降级
}
</script>

<style>
/* 全局微调（不使用 scoped，作用于全站） */
html, body, #app {
  height: 100%;
}

/* 字体与滚动体验 */
html { -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }
:root { color-scheme: light dark; }
body { margin: 0; }

/* 路由淡入过渡 */
.view-fade-enter-active, .view-fade-leave-active { transition: opacity .18s ease; }
.view-fade-enter-from, .view-fade-leave-to { opacity: 0; }
</style>

// TODO: 修改图标