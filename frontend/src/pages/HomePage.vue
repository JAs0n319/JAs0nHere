<template>

  <!-- Hero 区块（首屏大标题区域） -->
  <section class="hero">
    <div class="container">
      <div class="row">
        <!-- v-for 遍历 pills 数组，每个元素生成一个 span -->
        <span v-for="(pill, i) in pills" :key="i" class="pill">{{ pill }}</span>
      </div>
      <!-- 主标题 -->
      <h1 class="hero-title">写代码、写博客、也做点有趣的小玩意。</h1>
      <!-- 副标题 -->
      <p class="hero-sub">这里是我的个人主页，核心内容在「博客」。欢迎订阅、交流与指教。</p>
      <!-- 按钮区 -->
      <div class="row" style="margin-top:14px">
        <a class="btn" href="https://blog.jas0nhere.com/" target="_blank" rel="noopener">进入博客</a>
        <RouterLink class="btn btn--ghost" to="/projects">查看项目</RouterLink>
      </div>
    </div>
  </section>

  <!-- 主体两栏布局：左边内容，右边侧边栏 -->
  <main class="container grid main">
    <div class="content">
      <!-- 精选文章（置顶） -->
      <section>
        <div class="section-head">
          <h2 style="margin:0">精选文章</h2>
          <a class="chip" href="https://blog.jas0nhere.com/" target="_blank" rel="noopener">全部 →</a>
        </div>

        <!-- 遍历精选文章列表 -->
        <article v-for="post in featuredPosts" :key="post.slug" class="card">
          <a :href="post.href" target="_blank" rel="noopener">
            <div class="card-inner">
              <h3 style="margin:0 0 6px">{{ post.title }}</h3>
              <div class="row">
                <span class="meta">{{ post.date }} · {{ post.meta }}</span>
                <span class="tag">{{ post.readingTime }}</span>
              </div>
              <p class="post-excerpt">{{ post.excerpt }}</p>
            </div>
          </a>
        </article>
      </section>

      <!-- 最新文章 -->
      <section style="margin-top:24px">
        <div class="section-head">
          <h2 style="margin:0">最新文章</h2>
          <a class="chip" href="https://blog.jas0nhere.com/" target="_blank" rel="noopener">更多 →</a>
        </div>

        <!-- 遍历最新文章列表 -->
        <div class="grid post-grid">
          <article v-for="post in latestPosts" :key="post.slug" class="card">
            <a :href="post.href" target="_blank" rel="noopener">
              <div class="card-inner">
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="meta">{{ post.date }} · {{ post.meta }}</div>
                <p class="post-excerpt">{{ post.excerpt }}</p>
              </div>
            </a>
          </article>
        </div>

        <!-- 分页（前端示意，后续接后端） -->
        <div class="row" style="justify-content:center; margin-top:16px">
          <button class="chip" @click="goPage(pagination.page - 1)" :disabled="pagination.page <= 1">上一页</button>
          <button
              v-for="p in totalPages"
              :key="p"
              class="chip"
              :aria-current="p === pagination.page ? 'page' : false"
              @click="goPage(p)"
          >
            {{ p }}
          </button>
          <button class="chip" @click="goPage(pagination.page + 1)" :disabled="pagination.page >= totalPages">下一页</button>
        </div>
      </section>
    </div>

    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="block">
        <h3 style="margin:0 0 8px">关于我</h3>
        <p style="margin:0">{{ about.text }}</p>
        <div class="row" style="margin-top:8px">
          <a class="chip" :href="about.github" target="_blank" rel="noopener">GitHub</a>
          <a class="chip" :href="about.mailto">Email: JAs0n.Zhou.Jiaxuan@Gmail.com</a>
        </div>
      </div>

      <!-- 分类列表 -->
      <div class="block" style="margin-top:16px">
        <h3 style="margin:0 0 8px">分类</h3>
        <ul class="list">
          <li v-for="c in categories" :key="c.slug">
            <RouterLink :to="`/cat/${c.slug}`">{{ c.name }}<span class="meta">{{ c.count }}</span></RouterLink>
          </li>
        </ul>
      </div>

      <!-- 热门标签 -->
      <div class="block" style="margin-top:16px">
        <h3 style="margin:0 0 8px">热门标签</h3>
        <div class="row">
          <RouterLink class="tag" v-for="t in tags" :key="t.slug" :to="`/tag/${t.slug}`">{{ t.name }}</RouterLink>
        </div>
      </div>

    </aside>
  </main>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { RouterLink } from 'vue-router'

// ===== 页面逻辑部分 =====

// Hero 区的标签 pills
const pills = reactive([
  '👋 你好 · Vue & Java 开发',
  '部署 · Nginx · Cloudflare',
  'DigitalOcean'
])

// 精选文章（示例静态数据）
const featuredPosts = reactive([
  {
    slug: 'hello-vue',
    href: 'https://blog.jas0nhere.com/post/hello-vue',
    cover: 'https://picsum.photos/1200/480?random=1',
    title: '用 Vue + Vite 搭建主页：从 0 到上线',
    date: '2025-08-23',
    meta: 'Vue / Vite',
    readingTime: '阅读 8 分钟',
    excerpt: '目录结构、环境变量、构建与部署的坑点记录。含 Nginx 反向代理与 Cloudflare 基础设置。'
  }
])

// 最新文章（示例静态数据）
const latestPosts = reactive([
  {
    slug: 'nginx-cf',
    href: 'https://blog.jas0nhere.com/post/nginx-cf',
    title: 'Nginx 反向代理与 Cloudflare 一把梭',
    date: '2025-08-22',
    meta: 'Nginx / Cloudflare',
    excerpt: '从 A 记录到反代、证书、常见 403/502 的排查路径。'
  },
  {
    slug: 'do-setup',
    href: 'https://blog.jas0nhere.com/post/do-setup',
    title: 'DigitalOcean 云主机初始化清单',
    date: '2025-08-21',
    meta: 'DevOps',
    excerpt: '用户/SSH、UFW、防火墙、Fail2ban 与日志轮转的最佳实践。'
  },
  {
    slug: 'markdown-flow',
    href: 'https://blog.jas0nhere.com/post/markdown-flow',
    title: 'Markdown 写作流：从编辑到发布',
    date: '2025-08-18',
    meta: 'Markdown / 写作',
    excerpt: '选择编辑器、图片托管、Frontmatter 与自动化发布流水线。'
  }
])

// 关于我（侧边栏）
const about = reactive({
  text: 'Jason · Vue/Java 开发者。记录工程实践与部署经验。',
  github: 'https://github.com/JAs0n319',
  mailto: 'mailto:jas0n.zhou.jiaxuan@gmail.com'
})

// 分类（侧边栏）
const categories = reactive([
  { slug: 'vue', name: 'Vue', count: 12 },
  { slug: 'devops', name: 'DevOps', count: 9 },
  { slug: 'backend', name: '后端', count: 7 },
  { slug: 'notes', name: '随笔', count: 5 }
])

// 标签（侧边栏）
const tags = reactive([
  { slug: 'vue', name: 'Vue' },
  { slug: 'java', name: 'Java' },
  { slug: 'nginx', name: 'Nginx' },
  { slug: 'cf', name: 'Cloudflare' },
  { slug: 'do', name: 'DigitalOcean' }
])

// 分页参数
const pagination = reactive({
  page: 1,
  pageSize: 9,
  total: 27
})

// 计算总页数
const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.pageSize)))

// 切换分页
function goPage(p: number) {
  if (p < 1 || p > totalPages.value) return
  pagination.page = p
  // TODO: 调用接口，根据 page 拉取最新文章列表下
}

</script>