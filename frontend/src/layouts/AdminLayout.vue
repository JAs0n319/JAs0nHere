<template>
  <div class="min-h-screen page-bg">
    <header class="nav">
      <div class="container py-3 row">
        <span class="brand-badge pill">Admin</span>

        <nav class="row ml-auto">
          <RouterLink to="/admin" class="nav-link">仪表盘</RouterLink>
          <!-- 根据角色/权限再决定显示哪些入口 -->
          <button class="chip ml-2" @click="onLogout">退出</button>
        </nav>
      </div>
    </header>

    <main class="container py-6 main grid">
      <section class="card">
        <div class="card-inner">
          <RouterView />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
const router = useRouter()
const auth = useAuthStore()
async function onLogout() {
  await auth.logout()
  router.replace({ name: 'AdminLogin' })
}
</script>
