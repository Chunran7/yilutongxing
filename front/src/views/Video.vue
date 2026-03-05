<!-- src/views/Video.vue -->
<template>
  <el-container class="video-container">
    <el-main class="main">
      <div class="content">
        <h1 class="section-title">视频列表</h1>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 视频列表 -->
        <el-row :gutter="24" v-else>
          <el-col :span="8" v-for="video in pagedVideos" :key="video.id">
            <div class="video-card" @click="goToVideo(video.id)">
              <div class="video-thumbnail-container">
                <img
                  :src="normalizeMedia(video.cover) || 'https://placehold.co/400x300/2ecc71/ffffff?text=Video+' + video.id"
                  alt="视频封面" class="video-thumbnail" />
                <div class="video-overlay">
                  <div class="video-play-button">
                    <span class="el-icon-video-play"></span>
                  </div>
                </div>

              </div>
              <div class="video-info">
                <h3 class="video-title">{{ video.title }}</h3>
                <p class="video-description">{{ truncate(video.description, 60) }}</p>
                <div class="video-meta">
                  <span class="video-views">{{ video.views || 0 }} 次观看</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination background layout="prev, pager, next" :total="videos.length" :page-size="pageSize"
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
import Footer from '@/components/Footer.vue'
import { getVideoListService } from '@/api/video.js'
import { normalizeMedia } from '@/utils/media.js'

const router = useRouter()

// 视频数据
const videos = ref([])
// 加载状态
const loading = ref(true)

const pageSize = 9
const currentPage = ref(1)

// 模拟获取视频列表
const getVideoList = async () => {
  try {
    loading.value = true
    const list = await getVideoListService()
    videos.value = list || []
  } catch (error) {
    console.error('获取视频列表失败:', error)
    // 模拟数据用于演示
    videos.value = Array.from({ length: 30 }, (_, i) => ({
      id: i + 1,
      title: `教学视频 ${i + 1}`,
      summary: `这是第 ${i + 1} 个视频的简介，包含主要学习内容。`,
      cover: 'https://placehold.co/300x150'
    }))
  } finally {
    loading.value = false
  }
}

// 当前页的视频
const pagedVideos = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return videos.value.slice(start, start + pageSize)
})

// 跳转视频详情
const goToVideo = (id) => {
  router.push(`/video/${id}`)
}

// 摘要截断
const truncate = (text, maxLen) => {
  return text?.length <= maxLen ? text : text?.slice(0, maxLen) + '...'
}

onMounted(() => {
  getVideoList()
})
</script>

<style scoped>
.video-container {
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

/* 视频卡片样式 */
.video-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  margin-bottom: 30px;
  border-radius: 16px;
  overflow: hidden;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.video-card:hover {
  box-shadow: 0 16px 40px rgba(46, 204, 113, 0.25);
  transform: translateY(-10px) scale(1.03);
}

.video-thumbnail-container {
  position: relative;
  width: 100%;
  padding-top: 75%;
  /* 4:3 宽高比 */
  overflow: hidden;
  background: #f0f0f0;
}

.video-thumbnail {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.video-card:hover .video-thumbnail {
  transform: scale(1.1);
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.3) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.video-play-button {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  transform: scale(0.8);
  transition: all 0.3s ease;
  color: #2ecc71;
}

.video-card:hover .video-play-button {
  transform: scale(1);
  background: rgba(255, 255, 255, 1);
}

.video-play-button .el-icon-video-play {
  font-size: 30px;
  margin-left: 5px;
}

.video-duration {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.video-info {
  padding: 16px;
}

.video-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
  transition: color 0.3s ease;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 42px;
}

.video-card:hover .video-title {
  color: #2ecc71;
}

.video-description {
  font-size: 14px;
  color: #718096;
  margin-bottom: 16px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  height: 72px;
}

.video-meta {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.video-views {
  font-size: 13px;
  color: #a0aec0;
  display: flex;
  align-items: center;
  gap: 6px;
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