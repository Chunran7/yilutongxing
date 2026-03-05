<template>
  <div class="container">
    <el-card class="card" v-if="post">
      <template #header>
        <div class="header">
          <div class="left">
            <div class="title">{{ post.title }}</div>
            <div class="meta">
              <el-avatar :src="post.authorPic || defaultAvatar" :size="30" />
              <span class="author">{{ post.author || '匿名用户' }}</span>
              <span class="time">{{ formatPostTime(post) }}</span>
              <span class="stat">浏览 {{ post.views || 0 }}</span>
              <span class="stat">回复 {{ post.replyCount || 0 }}</span>
              <span class="stat">赞 {{ post.likeCount || 0 }}</span>
            </div>
          </div>

          <div class="right">
            <el-button
              v-if="token && authorProfile && String(authorProfile.id) !== String(me?.id)"
              :type="authorProfile.followed ? 'primary' : 'default'"
              @click="toggleFollowAuthor"
            >
              {{ authorProfile.followed ? '已关注' : '关注' }}
            </el-button>
          </div>
        </div>
      </template>

      <div class="content">{{ post.content }}</div>

      <div class="actions">
        <el-button :type="post.liked ? 'primary' : 'default'" :disabled="!token" @click="toggleLike">
          {{ post.liked ? '已赞' : '点赞' }} ({{ post.likeCount || 0 }})
        </el-button>
        <el-button :type="post.favorited ? 'warning' : 'default'" :disabled="!token" @click="toggleFavorite">
          {{ post.favorited ? '已藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>

    <el-card class="card" v-else>
      <el-skeleton :rows="6" animated />
    </el-card>

    <el-card class="card">
      <template #header>
        <div class="header2">
          <span>评论（树状）</span>
          <el-button link @click="loadComments">刷新</el-button>
        </div>
      </template>

      <div v-if="!token" class="tip">
        需要登录后才能发表评论/回复/点赞/收藏。<el-button link type="primary" @click="goLogin">去登录</el-button>
      </div>

      <div class="reply-box" v-if="token">
        <div class="replying" v-if="replyTarget">
          正在回复：<b>{{ replyTarget.author }}</b>
          <el-button link @click="cancelReply">取消</el-button>
        </div>
        <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="写下你的评论..." />
        <div class="reply-actions">
          <el-button type="primary" :loading="commentSubmitting" @click="submitComment">发布</el-button>
        </div>
      </div>

      <div v-if="commentLoading" style="padding: 10px 0">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else>
        <div v-if="comments.length === 0" class="empty">暂无评论</div>
        <CommentNode
          v-for="c in comments"
          :key="c.id"
          :node="c"
          :depth="0"
          :currentUserId="me?.id"
          @reply="onReply"
          @delete="onDelete"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import defaultAvatar from '@/assets/default.png'

import CommentNode from '@/components/CommentNode.vue'
import {
  getPostByIdService,
  getPostCommentsService,
  createCommentService,
  deleteCommentService,
  togglePostLikeService,
  togglePostFavoriteService
} from '@/api/post'
import { getMeService, getUserProfileService, toggleFollowService } from '@/api/user'

const route = useRoute()
const router = useRouter()
const token = ref(localStorage.getItem('token'))
const refreshToken = () => { token.value = localStorage.getItem('token') }
const hasToken = () => { refreshToken(); return !!token.value }

const id = ref(route.params.id)
const post = ref(null)
const me = ref(null)
const authorProfile = ref(null)

const comments = ref([])
const commentLoading = ref(false)
const commentContent = ref('')
const commentSubmitting = ref(false)
const replyTarget = ref(null)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  return d.toLocaleString()
}

// 只展示两种：未编辑=发表时间；已编辑=编辑于 updateTime
const isEditedPost = (p) => {
  if (!p) return false
  const ct = p.createTime
  const ut = p.updateTime
  if (!ct || !ut) return false
  const cd = new Date(ct)
  const ud = new Date(ut)
  const cm = isNaN(cd.getTime()) ? null : cd.getTime()
  const um = isNaN(ud.getTime()) ? null : ud.getTime()
  if (cm !== null && um !== null) return Math.abs(um - cm) > 1000
  return String(ct) !== String(ut)
}

const formatPostTime = (p) => {
  if (!p) return ''
  if (isEditedPost(p)) return `编辑于 ${formatTime(p.updateTime)}`
  return formatTime(p.createTime)
}

const goLogin = () => router.push('/login')

const loadMe = async () => {
  if (!hasToken()) return
  try {
    me.value = await getMeService()
  } catch (e) {}
}

const loadPost = async () => {
  try {
    post.value = await getPostByIdService(id.value)
    // 拉作者 profile，显示关注按钮
    try {
      authorProfile.value = await getUserProfileService(post.value.userId)
    } catch (_) {}
  } catch (e) {
    ElMessage.error(e?.message || '加载帖子失败')
  }
}

const loadComments = async () => {
  commentLoading.value = true
  try {
    comments.value = await getPostCommentsService(id.value)
  } catch (e) {
    ElMessage.error(e?.message || '加载评论失败')
  } finally {
    commentLoading.value = false
  }
}

const onReply = (node) => {
  if (!hasToken()) return goLogin()
  replyTarget.value = node
}

const cancelReply = () => {
  replyTarget.value = null
}

const submitComment = async () => {
  if (!hasToken()) return goLogin()
  const content = commentContent.value.trim()
  if (!content) return ElMessage.warning('评论不能为空')

  commentSubmitting.value = true
  try {
    await createCommentService({
      postId: Number(id.value),
      content,
      parentId: replyTarget.value ? replyTarget.value.id : 0,
      replyUserId: replyTarget.value ? replyTarget.value.userId : null
    })
    ElMessage.success('发布成功')
    commentContent.value = ''
    replyTarget.value = null
    await loadComments()
    await loadPost() // 更新 replyCount
  } catch (e) {
    ElMessage.error(e?.message || '发布失败')
  } finally {
    commentSubmitting.value = false
  }
}

const onDelete = async (node) => {
  if (!hasToken()) return goLogin()
  try {
    await ElMessageBox.confirm('确认删除这条评论吗？删除后将直接隐藏（若删除的是一级评论，其下所有回复也会隐藏）。', '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }

  try {
    await deleteCommentService(node.id)
    ElMessage.success('已删除')
    await loadComments()
    await loadPost()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

const toggleLike = async () => {
  if (!hasToken()) return goLogin()
  try {
    const res = await togglePostLikeService(id.value)
    post.value.liked = !!res.liked
    post.value.likeCount = res.likeCount
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const toggleFavorite = async () => {
  if (!hasToken()) return goLogin()
  try {
    const res = await togglePostFavoriteService(id.value)
    post.value.favorited = !!res.favorited
    ElMessage.success(post.value.favorited ? '已收藏' : '已取消收藏')
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const toggleFollowAuthor = async () => {
  if (!hasToken()) return goLogin()
  try {
    authorProfile.value = await toggleFollowService(post.value.userId)
    ElMessage.success(authorProfile.value.followed ? '已关注' : '已取消关注')
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

onMounted(async () => {
  await loadMe()
  await loadPost()
  await loadComments()
})
</script>

<style scoped>
.container {
  max-width: 980px;
  margin: 0 auto;
  padding: 18px 12px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card {
  border-radius: 12px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.title {
  font-size: 18px;
  font-weight: 800;
  line-height: 1.2;
}

.meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #666;
}

.content {
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 15px;
  line-height: 1.7;
  color: #333;
}

.actions {
  margin-top: 14px;
  display: flex;
  gap: 10px;
}

.header2 {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tip {
  padding: 10px;
  background: #f7f9ff;
  border: 1px solid #e6efff;
  border-radius: 10px;
  margin-bottom: 10px;
}

.reply-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 10px;
}

.replying {
  font-size: 13px;
  color: #555;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
}

.empty {
  color: #888;
  padding: 10px 0;
}
</style>
