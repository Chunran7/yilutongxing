<template>
  <el-container class="article-container">
    <el-main class="main">
      <div class="content">
        <h1 class="section-title">文章列表</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 文章列表 -->
        <el-row :gutter="24" v-else>
          <el-col :span="8" v-for="article in pagedArticles" :key="article.id">
            <div class="article-card" @click="goToArticle(article.id)">
              <div class="article-img-container">
                <img
                  :src="normalizeMedia(article.firstPicture) || 'https://placehold.co/600x400/3498db/ffffff?text=Travel+' + article.id"
                  alt="封面图" class="article-img" />
                <div class="article-img-overlay"></div>
                <div class="article-category">旅行攻略</div>
                <div class="article-reading-time">5分钟阅读</div>
              </div>
              <div class="article-content">
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-description">{{ truncate(article.description, 40) }}</p>
                <div class="article-meta">
                  <span class="article-date">{{ article.createTime ? new
                    Date(article.createTime).toLocaleDateString() : new
                      Date().toLocaleDateString() }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination background layout="prev, pager, next" :total="articles.length" :page-size="pageSize"
            v-model:current-page="currentPage" />
        </div>
      </div>
    </el-main>

    <Footer />
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleListService } from '@/api/article.js'
import Footer from '@/components/Footer.vue'
import { normalizeMedia } from '@/utils/media.js'

const router = useRouter()

// 文章数据
const articles = ref([])
// 加载状态
const loading = ref(true)

const pageSize = 9
const currentPage = ref(1)

// 获取文章列表
const getArticleList = async () => {
  try {
    loading.value = true
    const list = await getArticleListService()
    articles.value = list || []
  } catch (error) {
    console.error('获取文章列表失败:', error)
    // 模拟数据用于演示
    articles.value = Array.from({ length: 42 }, (_, i) => ({
      id: i + 1,
      title: `文章标题 ${i + 1}`,
      description: `这是第 ${i + 1} 篇文章的摘要内容，简要描述文章主要内容，让用户快速了解文章。`,
      img: 'https://placehold.co/300x150'
    }))
  } finally {
    loading.value = false
  }
}

// 当前页的文章
const pagedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return articles.value.slice(start, start + pageSize)
})

// 跳转文章详情
const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

// 摘要截断
const truncate = (text, maxLen) => {
  return text?.length <= maxLen ? text : text?.slice(0, maxLen) + '...'
}

// 当从缓存中激活组件时调用
import { onActivated } from 'vue'
onActivated(() => {
  // 可以选择在每次返回页面时刷新数据，或者注释掉下面这行以保持数据不变
  // getArticleList()
})

onMounted(() => {
  getArticleList()
})
</script>

<style scoped>
.article-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.main {
  background-color: transparent;
  flex: 1;
  padding: 30px 0;
}

.content {
  width: 80%;
  margin: 0 auto;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
}

.section-title {
  text-align: center;
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin: 60px 0 40px;
  position: relative;
  padding-bottom: 20px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  border-radius: 2px;
}

.article-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  margin: 20px 0;
  border-radius: 16px;
  overflow: hidden;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.article-card:hover {
  box-shadow: 0 16px 40px rgba(52, 152, 219, 0.25);
  transform: translateY(-10px) scale(1.03);
}

.article-img-container {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
}

.article-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .article-img {
  transform: scale(1.1);
}

.article-img-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0) 60%, rgba(0, 0, 0, 0.3) 100%);
}

.article-category {
  position: absolute;
  top: 15px;
  left: 15px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.article-reading-time {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background: rgba(255, 255, 255, 0.9);
  color: #2c3e50;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.article-content {
  padding: 20px;
}

.article-title {
  margin: 0 0 14px;
  font-size: 21px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.4;
  transition: color 0.3s ease;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-card:hover .article-title {
  color: #3498db;
}

.article-description {
  font-size: 15px;
  color: #7f8c8d;
  margin-bottom: 18px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-meta {
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.article-date {
  font-size: 13px;
  color: #95a5a6;
  font-weight: 500;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
}

.loading {
  padding: 30px;
}
</style>