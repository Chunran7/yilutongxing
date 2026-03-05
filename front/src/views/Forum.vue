<template>
  <div class="forum-page container">
    <aside class="forum-aside">
      <div class="user-card" @click="goToProfile">
        <div class="user-avatar">
          <img :src="avatarSrc || defaultAvatar" alt="avatar" />
        </div>
        <div class="user-info">
          <div class="user-nickname">{{ me?.nickname || me?.username || '未登录' }}</div>
          <div class="user-action">{{ token ? '点击编辑个人信息' : '点击去登录' }}</div>
        </div>
      </div>

      <el-button class="btn-post" type="primary" @click="openPostDialog">发布帖子</el-button>

      <div class="filters">
        <el-input v-model="keyword" placeholder="搜索标题/内容" clearable @keyup.enter="reload" />
        <div class="row">
          <el-select v-model="sortBy" placeholder="排序字段" style="width: 120px">
            <el-option label="最新" value="create_time" />
            <el-option label="最热(回复)" value="reply_count" />
            <el-option label="最多赞" value="like_count" />
            <el-option label="最多浏览" value="views" />
          </el-select>
          <el-select v-model="order" placeholder="顺序" style="width: 110px">
            <el-option label="降序" value="DESC" />
            <el-option label="升序" value="ASC" />
          </el-select>
          <el-button @click="reload">查询</el-button>
        </div>
      </div>
    </aside>

    <main class="forum-main">
      <el-card class="post-list-card">
        <template #header>
          <div class="header">
            <span>帖子列表</span>
            <el-button link @click="reload">刷新</el-button>
          </div>
        </template>

        <div v-if="loading" style="padding: 12px 0">
          <el-skeleton :rows="6" animated />
        </div>

        <div v-else>
          <div v-if="posts.length === 0" class="empty">暂无帖子</div>

          <div v-for="p in posts" :key="p.id" class="post-item">
            <div class="left" @click="goDetail(p.id)">
              <div class="title">{{ p.title }}</div>
              <div class="meta">
                <span class="author">作者：{{ p.author || '匿名用户' }}</span>
                <span class="time">{{ formatTime(p.createTime) }}</span>
                <span class="stat">浏览 {{ p.views || 0 }}</span>
                <span class="stat">回复 {{ p.replyCount || 0 }}</span>
                <span class="stat">赞 {{ p.likeCount || 0 }}</span>
              </div>
              <div class="preview">{{ p.content }}</div>
            </div>

            <div class="right">
              <el-button :type="p.liked ? 'primary' : 'default'" size="small" @click="toggleLike(p)" :disabled="!token">
                {{ p.liked ? '已赞' : '点赞' }}
              </el-button>
              <el-button :type="p.favorited ? 'warning' : 'default'" size="small" @click="toggleFavorite(p)"
                :disabled="!token">
                {{ p.favorited ? '已藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="pager">
          <el-pagination background layout="prev, pager, next" :page-size="pageSize" :current-page="page"
            @current-change="onPageChange" />
        </div>
      </el-card>
    </main>

    <el-dialog v-model="postDialogVisible" title="发布帖子" width="520px">
      <el-form :model="postForm" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postForm.content" type="textarea" :rows="7" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="postSubmitting" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/default.png'
import { normalizeMedia } from '@/utils/media.js'
import { useRouter } from 'vue-router'
import { getMeService } from '@/api/user'
import {
  getAllpostsService,
  insertPostService,
  togglePostLikeService,
  togglePostFavoriteService
} from '@/api/post'

const router = useRouter()

const token = ref(localStorage.getItem('token'))
const refreshToken = () => { token.value = localStorage.getItem('token') }
const me = ref(null)

const avatarSrc = computed(() => normalizeMedia(me.value?.userPic || me.value?.user_pic))

const loading = ref(false)
const posts = ref([])

const page = ref(1)
const pageSize = ref(10)

const keyword = ref('')
const sortBy = ref('create_time')
const order = ref('DESC')

const postDialogVisible = ref(false)
const postSubmitting = ref(false)
const postForm = ref({ title: '', content: '' })

const goToProfile = () => {
  refreshToken()
  if (!token.value) return router.push('/login')
  router.push('/profile/edit')
}

const goDetail = (id) => router.push(`/post/${id}`)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  return d.toLocaleString()
}

const loadMe = async () => {
  refreshToken()
  if (!token.value) return
  try {
    me.value = await getMeService()
    localStorage.setItem('user', JSON.stringify(me.value))
  } catch (e) {
    // 交给 request.js 的 401 统一处理，这里不手动清 token
  }
}

const loadPosts = async () => {
  loading.value = true
  try {
    posts.value = await getAllpostsService({
      page: page.value,
      pageSize: pageSize.value,
      sortBy: sortBy.value,
      order: order.value,
      keyword: keyword.value
    })
  } catch (e) {
    ElMessage.error(e?.message || '加载帖子失败')
  } finally {
    loading.value = false
  }
}

const reload = () => {
  page.value = 1
  loadPosts()
}

const onPageChange = (p) => {
  page.value = p
  loadPosts()
}

const openPostDialog = () => {
  refreshToken()
  if (!token.value) return router.push('/login')
  postForm.value = { title: '', content: '' }
  postDialogVisible.value = true
}

const submitPost = async () => {
  if (!postForm.value.title?.trim() || !postForm.value.content?.trim()) {
    return ElMessage.warning('标题和内容不能为空')
  }
  postSubmitting.value = true
  try {
    await insertPostService({
      title: postForm.value.title.trim(),
      content: postForm.value.content.trim()
    })
    ElMessage.success('发布成功')
    postDialogVisible.value = false
    reload()
  } catch (e) {
    ElMessage.error(e?.message || '发布失败')
  } finally {
    postSubmitting.value = false
  }
}

const toggleLike = async (p) => {
  refreshToken()
  if (!token.value) return
  try {
    const res = await togglePostLikeService(p.id)
    p.liked = !!res.liked
    p.likeCount = res.likeCount
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const toggleFavorite = async (p) => {
  refreshToken()
  if (!token.value) return
  try {
    const res = await togglePostFavoriteService(p.id)
    p.favorited = !!res.favorited
    ElMessage.success(p.favorited ? '已收藏' : '已取消收藏')
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

onMounted(async () => {
  refreshToken()
  await loadMe()
  await loadPosts()
})
</script>

<style scoped>
html,
body {
  min-height: 100vh;
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.forum-page {
  display: flex;
  gap: 16px;
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 12px;
  width: 100%;
}

.forum-aside {
  width: 290px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.user-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-radius: 16px;
  background: #fff;
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.user-avatar img {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f4f8;
}

.user-nickname {
  font-weight: 700;
  font-size: 16px;
  color: #2c3e50;
}

.user-action {
  margin-top: 4px;
  color: #888;
  font-size: 12px;
}

.btn-post {
  border-radius: 12px;
  font-weight: 600;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  border: none;
  transition: all 0.3s ease;
}

.btn-post:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
  background: linear-gradient(135deg, #2980b9, #27ae60);
}

.filters {
  padding: 16px;
  background: #fff;
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filters .row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.forum-main {
  flex: 1;
}

.post-list-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
}

.header span {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  position: relative;
  padding-bottom: 8px;
}

.header span::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  border-radius: 2px;
}

.post-item {
  display: flex;
  gap: 16px;
  padding: 18px;
  border-bottom: 1px solid #f0f4f8;
  transition: all 0.3s ease;
  background: #fff;
  margin: 0 8px;
  border-radius: 12px;
  margin-bottom: 8px;
}

.post-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  background: #fafbfc;
}

.left {
  flex: 1;
  cursor: pointer;
}

.title {
  font-weight: 700;
  font-size: 18px;
  color: #2c3e50;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.title:hover {
  color: #3498db;
}

.meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #7f8c8d;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.preview {
  margin-top: 8px;
  color: #555;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: pre-wrap;
}

.right {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
}

.right .el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

.right .el-button--primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.right .el-button--primary:hover {
  background: linear-gradient(135deg, #2980b9, #2471a3);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(52, 152, 219, 0.4);
}

.right .el-button--warning {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.right .el-button--warning:hover {
  background: linear-gradient(135deg, #e67e22, #d35400);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(243, 156, 18, 0.4);
}

.right .el-button--default {
  background: linear-gradient(135deg, #ecf0f1, #bdc3c7);
  color: #2c3e50;
}

.right .el-button--default:hover {
  background: linear-gradient(135deg, #bdc3c7, #95a5a6);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(236, 240, 241, 0.8);
}

.empty {
  padding: 40px 20px;
  color: #888;
  text-align: center;
  font-size: 14px;
}

.pager {
  margin-top: 20px;
  padding: 16px;
  display: flex;
  justify-content: center;
  background: #fff;
  border-radius: 12px;
  margin: 16px 8px 0;
}

.pager .el-pagination__sizes,
.pager .el-pagination__total,
.pager .el-pagination__jump {
  color: #7f8c8d;
}

.pager .el-pagination button {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.pager .el-pagination button:hover {
  background: #f0f4f8;
}

.pager .el-pagination .el-pager li {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.pager .el-pagination .el-pager li:hover {
  background: #f0f4f8;
}

.pager .el-pagination .el-pager li.active {
  background: linear-gradient(135deg, #3498db, #2ecc71);
  border-color: transparent;
}
</style>
