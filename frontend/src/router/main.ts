import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'

const routes: RouteRecordRaw[] = [
    { path: '/', name: 'Home', component: HomePage },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL), // 或 createWebHistory()
    routes,
    scrollBehavior() { return { top: 0 } },
})

export default router