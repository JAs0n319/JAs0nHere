<template>
  <header class="nav sticky top-0 z-40 bg-surface border-b border-default">
    <div class="container h-14 flex items-center justify-between">
      <!-- 左：Logo -->
      <RouterLink to="/" class="logo no-active">
        <span class="logo-mark brand-badge">J</span>
        <span class="logo-text text-primary transition-colors">JAs0nHere</span>
      </RouterLink>

      <!-- 中：桌面导航（用 RouterLink 的激活类接管高亮） -->
      <nav class="hidden md:flex items-center gap-2">
        <RouterLink
            v-for="item in navs"
            :key="item.to"
            :to="item.to"
            class="nav-link chip"
            active-class="chip-active"
            exact-active-class="chip-active"
        >
          {{ item.label }}
        </RouterLink>
        <a
            href="https://blog.jas0nhere.com/"
            target="_blank"
            rel="noopener"
            class="nav-link chip"
        >
          博客 ↗
        </a>
      </nav>

      <!-- 右：操作 -->
      <div class="flex items-center gap-2">
        <!-- 主题切换（仅图标） -->
        <button
            class="h-9 w-9 inline-flex items-center justify-center rounded-xl border border-[var(--c-border)] dark:border-[var(--c-border)]"
            @click="toggleTheme"
            :aria-label="isDark ? '切换到浅色' : '切换到深色'"
        >
          <svg v-if="!isDark" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 fill-current" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 18a6 6 0 1 1 0-12 6 6 0 0 1 0 12m0 4a1 1 0 0 1-1-1v-1.1a1 1 0 1 1 2 0V21a1 1 0 0 1-1 1M4.2 19.8a1 1 0 0 1 0-1.4l.78-.78a1 1 0 0 1 1.42 1.42l-.78.78a1 1 0 0 1-1.42 0M1 13a1 1 0 1 1 0-2h1.1a1 1 0 0 1 0 2H1Zm3.2-8.8a1 1 0 0 1 1.4 0l.78.78A1 1 0 0 1 4.96 6l-.78-.78a1 1 0 0 1 0-1.42M12 3a1 1 0 0 1 1 1v1.1a1 1 0 0 1-2 0V4a1 1 0 0 1 1-1m8.8 2.2a1 1 0 0 1 0 1.4L20 7.38A1 1 0 0 1 18.58 6l.78-.78a1 1 0 0 1 1.42 0M23 11a1 1 0 1 1 0 2h-1.1a1 1 0 1 1 0-2H23m-3.2 8.8a1 1 0 0 1-1.4 0l-.78-.78A1 1 0 1 1 19.04 17l.78.78a1 1 0 0 1 0 1.42"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 fill-current" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M21 12.79A9 9 0 1 1 11.21 3a7 7 0 1 0 9.79 9.79Z"/>
          </svg>
        </button>

        <!-- 移动端菜单按钮 -->
        <button
            class="h-9 w-9 inline-flex items-center justify-center rounded-xl border border-[var(--c-border)] dark:border-[var(--c-border)] md:hidden"
            @click="open = true"
            aria-label="打开菜单"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 fill-current" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M3 6h18v2H3zM3 11h18v2H3zM3 16h18v2H3z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 抽屉 -->
    <Transition
        enter-active-class="transition-opacity duration-200"
        enter-from-class="opacity-0"
        leave-active-class="transition-opacity duration-200"
        leave-to-class="opacity-0"
    >
      <div v-if="open" class="fixed inset-0 z-50">
        <div class="absolute inset-0 backdrop" @click="open = false"></div>
        <aside class="ml-auto h-full w-[78%] max-w-xs drawer-surface border-l border-default p-4 flex flex-col gap-2">
          <div class="flex items-center justify-between mb-2">
            <span class="font-bold text-primary">菜单</span>
            <button
                class="h-9 w-9 inline-flex items-center justify-center rounded-xl border border-[var(--c-border)] dark:border-[var(--c-border)]"
                @click="open = false"
                aria-label="关闭菜单"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 fill-current" viewBox="0 0 24 24" aria-hidden="true">
                <path d="M18.3 5.7 12 12l6.3 6.3-1.4 1.4L10.6 13.4 4.3 19.7 2.9 18.3 9.2 12 2.9 5.7 4.3 4.3l6.3 6.3 6.3-6.3z"/>
              </svg>
            </button>
          </div>
          <RouterLink
              v-for="item in navs"
              :key="item.to"
              :to="item.to"
              class="chip"
              active-class="chip-active"
              exact-active-class="chip-active"
              @click="open = false"
          >
            {{ item.label }}
          </RouterLink>
          <a href="https://blog.jas0nhere.com/" target="_blank" rel="noopener" class="chip">博客 ↗</a>
        </aside>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'

const navs = [
  { label: '首页', to: '/' },
  { label: '项目', to: '/projects' },
  { label: '关于', to: '/about' },
  { label: '联系', to: '/contact' }
] as const

const open = ref(false)

/** 主题切换（localStorage 记忆） */
const isDark = ref(false)
const THEME_KEY = 'theme-preference' // 'dark' | 'light'

function applyTheme(val: 'dark' | 'light') {
  document.documentElement.classList.toggle('dark', val === 'dark')
  isDark.value = val === 'dark'
}

function initTheme() {
  const saved = (localStorage.getItem(THEME_KEY) as 'dark' | 'light' | null)
  const prefersDark = window.matchMedia?.('(prefers-color-scheme: dark)')?.matches ?? false
  applyTheme(saved ?? (prefersDark ? 'dark' : 'light'))
}

function toggleTheme() {
  const next = isDark.value ? 'light' : 'dark'
  localStorage.setItem(THEME_KEY, next)
  applyTheme(next)
}

onMounted(initTheme)
</script>

<style scoped>
@reference "tailwindcss";

/* 关闭 RouterLink 默认激活/悬浮背景 */
.no-active,
.no-active:hover,
.no-active:focus {
  background: transparent !important;
  border: 0 transparent !important;
  box-shadow: none !important;
}

/* 链接基础 */
.nav-link { text-decoration: none; }

/* Logo 排版（颜色仍交给 main.css） */
.logo { display: flex; align-items: center; gap: .5rem; font-weight: 800; font-size: 1.125rem; line-height: 1.2; }
.logo-mark { height: 2rem; width: 2rem; flex-shrink: 0; border-radius: 9999px; display: inline-flex; align-items: center; justify-content: center; line-height: 1; }
.logo-text { letter-spacing: 0.01em; line-height: 1.2; }

/* 徽记使用主品牌色（具体颜色值在 main.css 里） */
.brand-badge { background: var(--c-logo-mark-bg); color: var(--c-logo-mark-fg); }

</style>