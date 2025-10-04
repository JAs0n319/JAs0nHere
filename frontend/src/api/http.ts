import axios from 'axios'
import type { InternalAxiosRequestConfig, AxiosError } from 'axios'
import { useAuthStore } from '@/stores/auth'

export const http = axios.create({
    baseURL: import.meta.env.VITE_API_BASE || '/api',
    withCredentials: true,
})

// 让 config 支持 _retry 标记，避免 401 死循环
declare module 'axios' {
    export interface InternalAxiosRequestConfig {
        _retry?: boolean
    }
}

// 请求拦截：自动挂 token
http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const auth = useAuthStore()
    const t = auth.accessToken
    if (t) {
        config.headers = config.headers || {}
        ;(config.headers as any).Authorization = `Bearer ${t}`
    }
    return config
})

// 响应拦截：401 时刷新 + 重放
let refreshing = false
let subscribers: Array<(token: string) => void> = []

const addSubscriber = (cb: (token: string) => void) => subscribers.push(cb)
const notifySubscribers = (token: string) => {
    subscribers.forEach((cb) => cb(token))
    subscribers = []
}
const clearSubscribers = () => { subscribers = [] }

http.interceptors.response.use(
    (r) => r,
    async (err: AxiosError) => {
        const resp = err.response
        const original = err.config as InternalAxiosRequestConfig | undefined
        const auth = useAuthStore()

        // 仅处理 401 且具备 refreshToken，且原请求存在，且未重试过
        if (resp?.status === 401 && auth.refreshToken && original && !original._retry) {
            original._retry = true

            if (!refreshing) {
                refreshing = true
                try {
                    // 要求 auth.refresh() 返回 string；若不是，将抛错
                    const tokenMaybe = (await auth.refresh()) as unknown
                    const newToken =
                        typeof tokenMaybe === 'string' && tokenMaybe.length > 0
                            ? tokenMaybe
                            : (() => { throw new Error('refresh returned empty token') })()

                    refreshing = false
                    notifySubscribers(newToken)

                    original.headers = original.headers || {}
                    ;(original.headers as any).Authorization = `Bearer ${newToken}`
                    return http.request(original)
                } catch (e) {
                    refreshing = false
                    clearSubscribers()
                    await auth.logout()
                    // 让路由守卫去处理跳转
                    return Promise.reject(e)
                }
            }

            // 正在刷新：把当前请求挂起，等待刷新完成后重放
            return new Promise((resolve, reject) => {
                addSubscriber((token: string) => {
                    try {
                        if (!original) throw new Error('original request missing')
                        original.headers = original.headers || {}
                        ;(original.headers as any).Authorization = `Bearer ${token}`
                        resolve(http.request(original))
                    } catch (e) {
                        reject(e)
                    }
                })
            })
        }

        // 其它情况，按原样抛出
        return Promise.reject(err)
    }
)
