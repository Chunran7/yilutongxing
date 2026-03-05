<template>
  <div class="container">
    <el-card class="card" v-if="video">
      <template #header>
        <div class="header">
          <div class="left">
            <div class="title">{{ video.title }}</div>
            <div class="meta">
              <el-avatar :src="normalizeMedia(video.authorPic) || defaultAvatar" :size="28" />
              <span class="author">{{ video.author || '匿名用户' }}</span>
              <span class="time">{{ formatTime(video.createTime) }}</span>
              <span class="stat">浏览 {{ video.views || 0 }}</span>
              <span class="stat">赞 {{ video.likeCount || 0 }}</span>
            </div>
          </div>

          <!-- 移除关注按钮 -->
        </div>
      </template>

      <video class="player" controls :poster="normalizeMedia(video.cover)">
        <source :src="normalizeMedia(video.url)" type="video/mp4" />
        你的浏览器不支持 video 标签。
      </video>

      <div v-if="video.description" class="desc">{{ video.description }}</div>

      <div class="actions">
        <el-button :type="video.liked ? 'primary' : 'default'" :disabled="!token" @click="toggleLike">
          {{ video.liked ? '已赞' : '点赞' }} ({{ video.likeCount || 0 }})
        </el-button>
        <el-button :type="video.favorited ? 'warning' : 'default'" :disabled="!token" @click="toggleFavorite">
          {{ video.favorited ? '已藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>

    <el-card class="card" v-else>
      <el-skeleton :rows="8" animated />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/default.png'
import { normalizeMedia } from '@/utils/media.js'

import { getVideoByIdService, toggleVideoLikeService, toggleVideoFavoriteService } from '@/api/video'
import { getMeService } from '@/api/user'

const route = useRoute()
const router = useRouter()
const token = ref(localStorage.getItem('token'))
const refreshToken = () => { token.value = localStorage.getItem('token') }
const hasToken = () => { refreshToken(); return !!token.value }
const id = ref(route.params.id)

const video = ref(null)
const me = ref(null)
// const authorProfile = ref(null)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  return d.toLocaleString()
}

const goLogin = () => router.push('/login')

const loadMe = async () => {
  if (!hasToken()) return
  try {
    me.value = await getMeService()
  } catch { }
}

const loadVideo = async () => {
  try {
    video.value = await getVideoByIdService(id.value)
    // 移除关注相关功能
  } catch (e) {
    ElMessage.error(e?.message || '加载视频失败')
  }
}

const toggleLike = async () => {
  if (!hasToken()) return goLogin()
  try {
    const res = await toggleVideoLikeService(id.value)
    video.value.liked = !!res.liked
    video.value.likeCount = res.likeCount
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const toggleFavorite = async () => {
  if (!hasToken()) return goLogin()
  try {
    const res = await toggleVideoFavoriteService(id.value)
    video.value.favorited = !!res.favorited
    ElMessage.success(video.value.favorited ? '已收藏' : '已取消收藏')
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

// 移除关注作者功能

onMounted(async () => {
  await loadMe()
  await loadVideo()
})
</script>

<style scoped>
.container {
  max-width: 980px;
  margin: 0 auto;
  padding: 18px 12px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.card {
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
}

.card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
}

.title {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.3;
  position: relative;
  margin-bottom: 12px;
}

.title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.meta {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.player {
  width: 100%;
  border-radius: 12px;
  background: #000;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 18px;
  transition: all 0.3s ease;
}

.player:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.desc {
  margin-top: 0;
  padding: 15px;
  border-radius: 12px;
  background: #f7f7f7;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  border-left: 4px solid #667eea;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 16px;
  padding: 15px 0;
  border-top: 1px solid #eee;
}

/* 按钮样式优化 */
:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
  padding: 8px 20px;
  font-weight: 500;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 头像样式优化 */
:deep(.el-avatar) {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
