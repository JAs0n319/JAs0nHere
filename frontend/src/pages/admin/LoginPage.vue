<template>
  <div class="min-h-screen flex items-center justify-center page-bg">
    <div class="card w-full max-w-md">
      <div class="card-inner">
        <h1 class="hero-title mb-2">管理员登录</h1>

        <!-- 允许浏览器密码管理器工作 -->
        <form @submit.prevent="onSubmit" class="space-y-4" autocomplete="on">
          <label class="block" for="email">
            <span class="text-muted text-sm">邮箱</span>
            <input
                id="email"
                name="username"
                v-model.trim="email"
                type="email"
                inputmode="email"
                autocapitalize="none"
                autocomplete="username"
                required
                class="w-full mt-1 px-3 py-2 rounded-lg border border-default bg-surface text-primary"
            />
          </label>

          <label class="block" for="password">
            <span class="text-muted text-sm">密码</span>
            <input
                id="password"
                name="current-password"
                v-model.trim="password"
                type="password"
                autocomplete="current-password"
                required
                class="w-full mt-1 px-3 py-2 rounded-lg border border-default bg-surface text-primary"
            />
          </label>

          <div class="row justify-between">
            <label class="text-sm text-muted" for="remember">
              <input id="remember" type="checkbox" v-model="remember" class="mr-1 align-middle" />
              记住我
            </label>
          </div>

          <button :disabled="loading" class="btn w-full justify-center">
            <span v-if="!loading">登录</span>
            <span v-else>登录中…</span>
          </button>

          <p v-if="error" class="meta" style="color: var(--c-pill-fg)">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

// 初始化：优先用上次“记住”的邮箱与勾选状态
const email = ref<string>(localStorage.getItem('login_email') ?? '')
const password = ref('')
const remember = ref(localStorage.getItem('remember_me') === '1') // 默认按上次选择
const loading = ref(false)
const error = ref('')

function resolveRedirect(q: unknown, fallback = '/admin'): string {
  if (typeof q === 'string') return q
  if (Array.isArray(q) && typeof q[0] === 'string') return q[0]
  return fallback
}

async function onSubmit() {
  loading.value = true
  error.value = ''
  try {
    await auth.login({ email: email.value, password: password.value })

    // 记住邮箱与勾选状态；密码不落地（交由浏览器密码管理器）
    localStorage.setItem('remember_me', remember.value ? '1' : '0')
    if (remember.value) {
      localStorage.setItem('login_email', email.value)
    } else {
      localStorage.removeItem('login_email')
    }

    const target = resolveRedirect(route.query.redirect, '/admin')
    router.replace(target)
  } catch (e: unknown) {
    const anyErr = e as { response?: { data?: { message?: string } } }
    error.value = anyErr?.response?.data?.message || '登录失败，请检查邮箱或密码。'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
input { transition: border-color .2s ease, box-shadow .2s ease; }
input:focus {
  outline: none;
  border-color: var(--c-accent-strong);
  box-shadow: var(--ring-focus);
}
button[disabled] { opacity: .6; cursor: not-allowed; }
</style>
