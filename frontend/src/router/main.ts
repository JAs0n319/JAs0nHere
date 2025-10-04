import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import { useAuthStore } from '../stores/auth'

// 可选：给 meta 声明类型
declare module 'vue-router' {
    interface RouteMeta { requiresAuth?: boolean }
}

const routes: RouteRecordRaw[] = [
    // Home 不需要鉴权
    { path: '/', name: 'Home', component: HomePage },

    // 登录页（仅供访问 /admin* 未登录时跳转）
    { path: '/admin/login', name: 'AdminLogin', component: () => import('@/pages/admin/LoginPage.vue') },

    // Admin 命名空间（需要鉴权）
    {
        path: '/admin',
        component: () => import('@/layouts/AdminLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'AdminDashboard',
                component: () => import('@/pages/admin/DashboardPage.vue'),
                meta: { requiresAuth: true },
            },
            // ... 其它后台子页面（同样 meta.requiresAuth = true）
        ],
    },

    // 兜底
    { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
    scrollBehavior: () => ({ top: 0 }),
})

// —— 守卫逻辑 ——
// 1) 仅当目标在 /admin* 命名空间且未登录时，跳转到 /admin/login
// 2) 已登录访问 /admin/login 时，跳回 HomePage
router.beforeEach((to) => {
    const auth = useAuthStore()
    const isAuthed = !!auth.accessToken
    const isAdminLogin = to.name === 'AdminLogin'
    const inAdminNs = to.path.startsWith('/admin')

    if (!isAuthed && inAdminNs && !isAdminLogin) {
        return { name: 'AdminLogin', query: { redirect: to.fullPath } }
    }

    if (isAuthed && isAdminLogin) {
        return { name: 'Home' } // 已登录不再进登录页，回到首页
    }

    // 其它情况放行（包括未登录访问 Home 等公开页面）
})

export default router
