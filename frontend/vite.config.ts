import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import path from 'node:path'
import {fileURLToPath} from "node:url";

// 用回调拿到 mode，便于只在开发启用 devtools
export default defineConfig(({ mode }) => ({
    base: '/', // 保持根路径部署
    plugins: [
        vue(),
        tailwindcss(),
        // 只在开发环境启用 DevTools，避免生产包引入调试代码
        ...(mode === 'development' ? [vueDevTools()] : [])
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        }
    },
    // 显式声明构建产物结构，和 Nginx 配置一一对应
    build: {
        outDir: 'dist',      // 最终上传这个目录
        assetsDir: 'assets', // 入口HTML旁边的 /assets/*，避免被误重写
        target: 'esnext',
        sourcemap: false
    }
}))