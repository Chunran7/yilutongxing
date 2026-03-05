<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="header">
          <div class="title">个人中心</div>
          <el-button size="small" @click="refreshAll" :loading="loadingAll">刷新</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <!-- 1) 资料/头像上传 -->
        <el-tab-pane label="个人资料" name="profile">
          <div class="profile-row">
            <div class="avatar-box">
              <el-avatar :src="normalizePic(form.userPic) || defaultAvatar" :size="88" />
              <div class="avatar-actions">
                <el-upload :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="doAvatarUpload">
                  <el-button type="primary">本地上传头像</el-button>
                </el-upload>
                <div class="hint">支持 png/jpg/jpeg/gif/webp，最大 5MB</div>
              </div>
            </div>

            <div class="stat-box">
              <div class="stat"><span class="num">{{ me.followerCount || 0 }}</span><span class="lbl">粉丝</span></div>
              <div class="stat"><span class="num">{{ me.followingCount || 0 }}</span><span class="lbl">关注</span></div>
            </div>
          </div>

          <el-form :model="form" label-width="90px" style="max-width: 520px">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="设置一个昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="可选" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="savingProfile">保存</el-button>
              <span v-if="lastSavedAt" class="edited">编辑于 {{ lastSavedAt }}</span>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 2) 我发布的帖子 -->
        <el-tab-pane label="我的帖子" name="posts">
          <div class="toolbar">
            <el-button type="primary" @click="router.push('/forum')">去讨论区发布</el-button>
          </div>

          <el-table :data="myPosts" v-loading="loadingMyPosts" style="width: 100%">
            <el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
            <el-table-column label="回复" prop="replyCount" width="80" />
            <el-table-column label="点赞" prop="likeCount" width="80" />
            <el-table-column label="时间" width="220">
              <template #default="{ row }">
                <span v-if="isEditedPost(row)">编辑于 {{ formatTime(row.updateTime) }}</span>
                <span v-else>{{ formatTime(row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="router.push(`/post/${row.id}`)">查看</el-button>
                <el-button size="small" @click="openEditPost(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deletePost(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!loadingMyPosts && myPosts.length === 0" description="你还没有发布任何帖子" />
        </el-tab-pane>

        <!-- 3) 关注列表 -->
        <el-tab-pane label="关注列表" name="following">
          <el-table :data="following" v-loading="loadingFollowing" style="width: 100%">
            <el-table-column label="用户" min-width="240">
              <template #default="{ row }">
                <div class="usercell">
                  <el-avatar :src="normalizePic(row.userPic) || defaultAvatar" :size="32" />
                  <div class="uinfo">
                    <div class="uname">{{ row.nickname || row.username }}</div>
                    <div class="uid">{{ row.username }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="粉丝" width="90" prop="followerCount" />
            <el-table-column label="关注" width="90" prop="followingCount" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="unfollow(row)">取消关注</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loadingFollowing && following.length === 0" description="你还没有关注任何人" />
        </el-tab-pane>

        <!-- 4) 点赞管理 -->
        <el-tab-pane label="点赞" name="likes">
          <el-tabs v-model="likeTab" class="inner-tabs">
            <el-tab-pane label="帖子" name="post">
              <el-table :data="likedPosts" v-loading="loadingLikedPosts" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/post/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelLikePost(row)">取消点赞</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingLikedPosts && likedPosts.length === 0" description="暂无点赞的帖子" />
            </el-tab-pane>

            <el-tab-pane label="文章" name="article">
              <el-table :data="likedArticles" v-loading="loadingLikedArticles" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/article/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelLikeArticle(row)">取消点赞</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingLikedArticles && likedArticles.length === 0" description="暂无点赞的文章" />
            </el-tab-pane>

            <el-tab-pane label="视频" name="video">
              <el-table :data="likedVideos" v-loading="loadingLikedVideos" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/video/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelLikeVideo(row)">取消点赞</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingLikedVideos && likedVideos.length === 0" description="暂无点赞的视频" />
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>

        <!-- 5) 收藏管理 -->
        <el-tab-pane label="收藏" name="favorites">
          <el-tabs v-model="favTab" class="inner-tabs">
            <el-tab-pane label="帖子" name="post">
              <el-table :data="favPosts" v-loading="loadingFavPosts" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/post/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelFavPost(row)">取消收藏</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingFavPosts && favPosts.length === 0" description="暂无收藏的帖子" />
            </el-tab-pane>

            <el-tab-pane label="文章" name="article">
              <el-table :data="favArticles" v-loading="loadingFavArticles" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/article/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelFavArticle(row)">取消收藏</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingFavArticles && favArticles.length === 0" description="暂无收藏的文章" />
            </el-tab-pane>

            <el-tab-pane label="视频" name="video">
              <el-table :data="favVideos" v-loading="loadingFavVideos" style="width: 100%">
                <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip />
                <el-table-column label="作者" prop="author" width="160" show-overflow-tooltip />
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="router.push(`/video/${row.id}`)">查看</el-button>
                    <el-button size="small" type="warning" @click="cancelFavVideo(row)">取消收藏</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loadingFavVideos && favVideos.length === 0" description="暂无收藏的视频" />
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑帖子弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑帖子" width="680px">
      <el-form :model="editForm" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPost" @click="savePostEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

import defaultAvatar from '@/assets/default.png'
import { getMeService, updateProfileService, uploadAvatarService, getFollowingService, toggleFollowService } from '@/api/user.js'
import { normalizeMedia } from '@/utils/media.js'
import { getMyPostsService, updateMyPostService, deleteMyPostService, getMyLikedPostsService, getMyFavoritedPostsService, togglePostLikeService, togglePostFavoriteService } from '@/api/post.js'
import { getMyLikedArticlesService, getMyFavoritedArticlesService, toggleArticleLikeService, toggleArticleFavoriteService } from '@/api/article.js'
import { getMyLikedVideosService, getMyFavoritedVideosService, toggleVideoLikeService, toggleVideoFavoriteService } from '@/api/video.js'

const router = useRouter()

const activeTab = ref('profile')
const likeTab = ref('post')
const favTab = ref('post')

const me = reactive({})
const form = reactive({ username: '', nickname: '', email: '', userPic: '' })

const lastSavedAt = ref('')
const savingProfile = ref(false)
const loadingAll = ref(false)

// 我的帖子
const myPosts = ref([])
const loadingMyPosts = ref(false)

// 关注
const following = ref([])
const loadingFollowing = ref(false)

// 点赞
const likedPosts = ref([])
const likedArticles = ref([])
const likedVideos = ref([])
const loadingLikedPosts = ref(false)
const loadingLikedArticles = ref(false)
const loadingLikedVideos = ref(false)

// 收藏
const favPosts = ref([])
const favArticles = ref([])
const favVideos = ref([])
const loadingFavPosts = ref(false)
const loadingFavArticles = ref(false)
const loadingFavVideos = ref(false)

// 编辑帖子
const editDialogVisible = ref(false)
const editForm = reactive({ id: null, title: '', content: '' })
const savingPost = ref(false)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  return d.toLocaleString()
}

// 只要编辑过，就显示“编辑于 ...”；否则显示发表时间
// 后端：create_time / update_time
const isEditedPost = (row) => {
  if (!row) return false
  const ct = row.createTime
  const ut = row.updateTime
  if (!ct || !ut) return false

  const cd = new Date(ct)
  const ud = new Date(ut)
  const cm = isNaN(cd.getTime()) ? null : cd.getTime()
  const um = isNaN(ud.getTime()) ? null : ud.getTime()

  // 解析成功：用毫秒差判断（避免字符串格式差异）
  if (cm !== null && um !== null) return Math.abs(um - cm) > 1000

  // 解析失败：退化为字符串比较
  return String(ct) !== String(ut)
}

// use shared normalizeMedia
const normalizePic = normalizeMedia

const refreshMe = async () => {
  const data = await getMeService()
  Object.assign(me, data || {})
  form.username = me.username || ''
  form.nickname = me.nickname || ''
  form.email = me.email || ''
  form.userPic = me.userPic || ''
}

const refreshFollowing = async () => {
  loadingFollowing.value = true
  try {
    const data = await getFollowingService()
    following.value = Array.isArray(data) ? data : []
  } finally {
    loadingFollowing.value = false
  }
}

const refreshMyPosts = async () => {
  loadingMyPosts.value = true
  try {
    const data = await getMyPostsService()
    myPosts.value = Array.isArray(data) ? data : []
  } finally {
    loadingMyPosts.value = false
  }
}

const refreshLikes = async () => {
  loadingLikedPosts.value = true
  loadingLikedArticles.value = true
  loadingLikedVideos.value = true
  try {
    const [p, a, v] = await Promise.all([getMyLikedPostsService(), getMyLikedArticlesService(), getMyLikedVideosService()])
    likedPosts.value = Array.isArray(p) ? p : []
    likedArticles.value = Array.isArray(a) ? a : []
    likedVideos.value = Array.isArray(v) ? v : []
  } finally {
    loadingLikedPosts.value = false
    loadingLikedArticles.value = false
    loadingLikedVideos.value = false
  }
}

const refreshFavorites = async () => {
  loadingFavPosts.value = true
  loadingFavArticles.value = true
  loadingFavVideos.value = true
  try {
    const [p, a, v] = await Promise.all([getMyFavoritedPostsService(), getMyFavoritedArticlesService(), getMyFavoritedVideosService()])
    favPosts.value = Array.isArray(p) ? p : []
    favArticles.value = Array.isArray(a) ? a : []
    favVideos.value = Array.isArray(v) ? v : []
  } finally {
    loadingFavPosts.value = false
    loadingFavArticles.value = false
    loadingFavVideos.value = false
  }
}

const refreshAll = async () => {
  loadingAll.value = true
  try {
    await refreshMe()
    await Promise.all([refreshFollowing(), refreshMyPosts(), refreshLikes(), refreshFavorites()])
  } catch (e) {
    ElMessage.error(e?.message || '刷新失败')
  } finally {
    loadingAll.value = false
  }
}

onMounted(() => {
  refreshAll()
})

const onTabChange = async (name) => {
  // 按需刷新（避免信息过期）
  if (name === 'posts') return refreshMyPosts()
  if (name === 'following') return refreshFollowing()
  if (name === 'likes') return refreshLikes()
  if (name === 'favorites') return refreshFavorites()
  if (name === 'profile') return refreshMe()
}

const saveProfile = async () => {
  savingProfile.value = true
  try {
    // 重要：空字符串不要写入数据库（否则 author 的 COALESCE 会被空昵称截断；邮箱也会触发唯一索引冲突）
    const nickname = (form.nickname || '').trim()
    const email = (form.email || '').trim()
    const userPic = (form.userPic || '').trim()
    await updateProfileService({

      nickname: nickname ? nickname : null,
      email: email ? email : null,
      userPic: userPic ? userPic : null
    })
    lastSavedAt.value = new Date().toLocaleString()
    ElMessage.success('保存成功')
    await refreshMe()
  } catch (e) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    savingProfile.value = false
  }
}

const beforeAvatarUpload = (file) => {
  const okType = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif', 'image/webp'].includes(file.type)
  if (!okType) {
    ElMessage.warning('仅支持 png/jpg/jpeg/gif/webp')
    return false
  }
  const okSize = file.size <= 5 * 1024 * 1024
  if (!okSize) {
    ElMessage.warning('文件过大（最大 5MB）')
    return false
  }
  return true
}

const doAvatarUpload = async ({ file }) => {
  try {
    const res = await uploadAvatarService(file)
    form.userPic = res?.url || ''
    ElMessage.success('头像上传成功')
    await refreshMe()
  } catch (e) {
    ElMessage.error(e?.message || '上传失败')
  }
}

const openEditPost = (row) => {
  editForm.id = row.id
  editForm.title = row.title
  editForm.content = row.content
  editDialogVisible.value = true
}

const savePostEdit = async () => {
  if (!editForm.id) return
  if (!editForm.title || !editForm.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  savingPost.value = true
  try {
    await updateMyPostService(editForm.id, { title: editForm.title, content: editForm.content })
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    await refreshMyPosts()
  } catch (e) {
    ElMessage.error(e?.message || '更新失败')
  } finally {
    savingPost.value = false
  }
}

const deletePost = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该帖子吗？删除后将无法在列表中看到。', '提示', { type: 'warning' })
    await deleteMyPostService(row.id)
    ElMessage.success('删除成功')
    await refreshMyPosts()
  } catch (e) {
    // cancel ignore
  }
}

const unfollow = async (row) => {
  try {
    await ElMessageBox.confirm(`确认取消关注「${row.nickname || row.username}」吗？`, '提示', { type: 'warning' })
    await toggleFollowService(row.id)
    ElMessage.success('已取消关注')
    await Promise.all([refreshFollowing(), refreshMe()])
  } catch (e) {
    // cancel ignore
  }
}

// 点赞/收藏管理：本质是 toggle 后刷新列表
const cancelLikePost = async (row) => {
  await togglePostLikeService(row.id)
  ElMessage.success('已取消点赞')
  await refreshLikes()
}
const cancelLikeArticle = async (row) => {
  await toggleArticleLikeService(row.id)
  ElMessage.success('已取消点赞')
  await refreshLikes()
}
const cancelLikeVideo = async (row) => {
  await toggleVideoLikeService(row.id)
  ElMessage.success('已取消点赞')
  await refreshLikes()
}

const cancelFavPost = async (row) => {
  await togglePostFavoriteService(row.id)
  ElMessage.success('已取消收藏')
  await refreshFavorites()
}
const cancelFavArticle = async (row) => {
  await toggleArticleFavoriteService(row.id)
  ElMessage.success('已取消收藏')
  await refreshFavorites()
}
const cancelFavVideo = async (row) => {
  await toggleVideoFavoriteService(row.id)
  ElMessage.success('已取消收藏')
  await refreshFavorites()
}
</script>

<style scoped>
.profile-page {
  padding: 18px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: 700;
  position: relative;
}

.title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.profile-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.avatar-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hint {
  font-size: 12px;
  color: #999;
}

.stat-box {
  display: flex;
  gap: 22px;
}

.stat {
  text-align: center;
}

.num {
  font-size: 20px;
  font-weight: 700;
  display: block;
  color: #667eea;
}

.lbl {
  font-size: 12px;
  color: #666;
}

.edited {
  margin-left: 12px;
  font-size: 12px;
  color: #999;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  justify-content: flex-end;
}

.usercell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.uinfo {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.uname {
  font-weight: 600;
}

.uid {
  font-size: 12px;
  color: #999;
}

.inner-tabs {
  margin-top: 6px;
}

/* 卡片样式优化 */
:deep(.el-card) {
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 按钮样式优化 */
:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  border-radius: 12px 12px 0 0;
}

:deep(.el-table__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

:deep(.el-table__header th) {
  color: white;
  font-weight: 600;
}

:deep(.el-table__body-wrapper) {
  border-radius: 0 0 12px 12px;
}

:deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

:deep(.el-table__body tr:hover) {
  background-color: #f0f4ff;
}

/* 头像样式优化 */
:deep(.el-avatar) {
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 标签页样式优化 */
:deep(.el-tabs__item) {
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-tabs__item:hover) {
  color: #667eea;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
