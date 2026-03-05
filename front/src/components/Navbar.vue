<!-- src/components/Navbar.vue -->
<template>
  <el-header class="header" style="background-color: #ffffff; height: 60px">
    <div class="navbar-left">
      <img :src="logo" alt="网站Logo" class="navbar-logo" @click="router.push('/home')">
    </div>
    <el-menu mode="horizontal" text-color="#000000" active-text-color="#0040ff" class="top-menu"
      :default-active="activeIndex">
      <el-menu-item index="/home" @click="router.push('/home')">首页</el-menu-item>
      <el-menu-item index="/article" @click="router.push('/article')">文章</el-menu-item>
      <el-menu-item index="/video" @click="router.push('/video')">视频</el-menu-item>
      <el-menu-item index="/forum" @click="router.push('/forum')">讨论</el-menu-item>
    </el-menu>
    <div class="navbar-right">
      <el-button v-if="!isLoggedIn" @click="router.push('/login')" type="primary">登录/注册</el-button>

      <el-dropdown v-else trigger="click" @command="handleCommand">
        <span class="user-entry">
          <el-avatar :size="32" :src="avatarSrc" class="user-avatar">
            <span v-if="!avatarSrc">{{ (displayName || 'U').slice(0, 1).toUpperCase() }}</span>
          </el-avatar>
          <span class="user-name">{{ displayName }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import logo from '@/assets/logo.png'
import { getMeService } from '@/api/user'
import { normalizeMedia } from '@/utils/media.js'

const route = useRoute()
const router = useRouter()
const activeIndex = computed(() => route.path)

// 添加登录状态管理
const isLoggedIn = ref(false)
const me = ref(null)

const displayName = computed(() => {
  const u = me.value
  if (!u) return ''
  return u.nickname || u.username || ''
})

const avatarSrc = computed(() => normalizeMedia(me.value?.userPic || me.value?.user_pic || ''))

const refreshAuth = async () => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  if (!token) {
    me.value = null
    return
  }
  // 优先用缓存，避免每次切页都请求
  const cache = localStorage.getItem('user')
  if (cache) {
    try {
      me.value = JSON.parse(cache)
    } catch {
      // ignore
    }
  }
  try {
    const u = await getMeService()
    me.value = u
    localStorage.setItem('user', JSON.stringify(u))
  } catch {
    // token 可能无效，交给 request.js 的 401 统一处理
  }
}

// 页面加载时检查登录状态
onMounted(() => {
  refreshAuth()
})

watch(
  () => route.fullPath,
  () => refreshAuth()
)

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  me.value = null
  isLoggedIn.value = false
  ElMessage.success('已退出登录')
  router.push('/login')
}

const handleCommand = (cmd) => {
  if (cmd === 'profile') {
    router.push('/profile/edit')
    return
  }
  if (cmd === 'logout') {
    logout()
  }
}
</script>

<style scoped>
.navbar-left {
  float: left;
  height: 60px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.navbar-logo {
  height: 40px;
  width: auto;
}

.navbar-right {
  position: absolute;
  right: 30px;
  top: 0;
  height: 60px;
  display: flex;
  align-items: center;
}

.user-entry {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.user-avatar {
  margin-right: 8px;
}

.user-name {
  color: #222;
  font-size: 14px;
}
</style>