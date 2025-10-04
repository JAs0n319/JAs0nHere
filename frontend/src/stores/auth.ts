import { defineStore } from 'pinia'
import axios from 'axios'

interface User { id?: number | string; email?: string; role?: string }
interface AuthState {
    accessToken: string | null
    refreshToken: string | null
    user: User | null
    loading: boolean
}

export const useAuthStore = defineStore('auth', {
    state: (): AuthState => ({
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken'),
        user: JSON.parse(localStorage.getItem('user') || 'null'),
        loading: false,
    }),
    getters: {
        isAuthenticated: (s) => !!s.accessToken,
    },
    actions: {
        async login(payload: { email: string; password: string }) {
            this.loading = true
            try {
                // 你后端的登录接口，按需调整
                const { data } = await axios.post('/api/auth/login', payload, { withCredentials: true })
                this.accessToken = data.accessToken
                this.refreshToken = data.refreshToken ?? null
                this.user = data.user ?? null

                localStorage.setItem('accessToken', this.accessToken!)
                if (this.refreshToken) localStorage.setItem('refreshToken', this.refreshToken)
                if (this.user) localStorage.setItem('user', JSON.stringify(this.user))

                // 让后续请求自带 token（也有拦截器兜底）
                axios.defaults.headers.common['Authorization'] = `Bearer ${this.accessToken}`
            } finally {
                this.loading = false
            }
        },
        async logout() {
            this.accessToken = null
            this.refreshToken = null
            this.user = null
            localStorage.removeItem('accessToken')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('user')
            delete axios.defaults.headers.common['Authorization']
            try {
                // 可选：通知后端销毁刷新令牌
                await axios.post('/api/auth/logout', {}, { withCredentials: true })
            } catch { /* 忽略 */ }
        },
        async refresh() {
            if (!this.refreshToken) throw new Error('no refresh token')
            const { data } = await axios.post('/api/auth/refresh', { refreshToken: this.refreshToken }, { withCredentials: true })
            this.accessToken = data.accessToken
            localStorage.setItem('accessToken', this.accessToken!)
            axios.defaults.headers.common['Authorization'] = `Bearer ${this.accessToken}`
            return this.accessToken
        },
    },
})
