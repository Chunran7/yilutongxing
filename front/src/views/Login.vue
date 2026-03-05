<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">
        {{ mode === 'login' ? '用户登录' : mode === 'register' ? '用户注册' : '忘记密码' }}
      </h2>

      <el-form :model="mode === 'forgot' ? forgotForm : formModel" :rules="mode === 'forgot' ? forgotRules : rules"
        ref="formRef" label-width="80px">
        <!-- 登录 / 注册 -->
        <template v-if="mode !== 'forgot'">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formModel.username" placeholder="请输入用户名" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="formModel.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>

          <el-form-item v-if="mode === 'register'" label="确认密码" prop="repassword">
            <el-input v-model="formModel.repassword" type="password" placeholder="请再次输入密码" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" :disabled="submitting" @click="submitForm">
              {{ mode === 'login' ? '登录' : '注册' }}
            </el-button>
            <el-button type="text" @click="toggleMode" :disabled="submitting">
              {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
            </el-button>
            <el-button v-if="mode === 'login'" type="text" @click="switchToForgot">忘记密码？</el-button>
          </el-form-item>
        </template>

        <!-- 忘记密码 -->
        <template v-else>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="forgotForm.email" placeholder="请输入注册时使用的邮箱" />
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <el-input v-model="forgotForm.code" placeholder="请输入验证码" style="width:60%" />
            <el-button style="margin-left:8px" @click="sendCode" :loading="sendingCode">发送验证码</el-button>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="forgotForm.newPassword" type="password" placeholder="请输入新密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="resetting" @click="submitReset">重置密码</el-button>
            <el-button type="text" @click="switchToLogin">返回登录</el-button>
          </el-form-item>
        </template>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { userLoginService, userRegisterService, sendForgotCodeService, resetPasswordService } from '@/api/user'

const router = useRouter()
const route = useRoute()

// mode: 'login' | 'register' | 'forgot'
const mode = ref('login')
const submitting = ref(false)

const formRef = ref(null)
const formModel = reactive({ username: '', password: '', repassword: '' })

const validateRepassword = (rule, value, callback) => {
  if (mode.value !== 'login') {
    if (!value) return callback(new Error('请再次输入密码'))
    if (value !== formModel.password) return callback(new Error('两次输入的密码不一致'))
  }
  callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32 位', trigger: 'blur' }
  ],
  repassword: [{ validator: validateRepassword, trigger: 'blur' }]
}

const toggleMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  formRef.value?.resetFields()
}

const switchToForgot = () => {
  mode.value = 'forgot'
  formRef.value?.resetFields()
}

const switchToLogin = () => {
  mode.value = 'login'
  formRef.value?.resetFields()
}

// 忘记密码逻辑（集成在此页面）
const forgotFormRef = ref(null)
const forgotForm = reactive({ email: '', code: '', newPassword: '' })
const sendingCode = ref(false)
const resetting = ref(false)

const forgotRules = {
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const sendCode = async () => {
  if (sendingCode.value) return
  try {
    await formRef.value?.validateField('email')
  } catch { return }
  sendingCode.value = true
  try {
    const res = await sendForgotCodeService({ email: forgotForm.email })
    if (res?.devMode && res?.code) {
      ElMessage.info(`开发者模式：验证码 ${res.code}（真实环境会发邮件）`)
    } else {
      ElMessage.success('如果该邮箱已注册，验证码会发送至邮箱')
    }
  } catch (e) {
    ElMessage.error(e?.message || '发送失败')
  } finally {
    sendingCode.value = false
  }
}

const submitReset = async () => {
  if (resetting.value) return
  try {
    await formRef.value?.validate()
  } catch { return }
  resetting.value = true
  try {
    await resetPasswordService({ email: forgotForm.email, code: forgotForm.code, newPassword: forgotForm.newPassword })
    ElMessage.success('密码已重置，请使用新密码登录')
    switchToLogin()
    // clear
    forgotForm.email = ''
    forgotForm.code = ''
    forgotForm.newPassword = ''
  } catch (e) {
    ElMessage.error(e?.message || '重置失败')
  } finally {
    resetting.value = false
  }
}

const submitForm = async () => {
  if (submitting.value) return

  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    if (mode.value === 'login') {
      // 登录：后端返回 token 字符串（request.js 已统一只返回 data）
      const token = await userLoginService({
        username: formModel.username,
        password: formModel.password
      })
      if (!token) throw new Error('登录失败：未获取到 token')

      localStorage.setItem('token', token)
      ElMessage.success('登录成功')

      // 支持登录后回跳
      let redirect = '/home'
      if (route.query.redirect) {
        try {
          redirect = decodeURIComponent(String(route.query.redirect))
        } catch {
          redirect = String(route.query.redirect)
        }
      }
      router.replace(redirect)
    } else {
      await userRegisterService({
        username: formModel.username,
        password: formModel.password
      })
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
      formRef.value?.resetFields()
    }
  } catch (e) {
    ElMessage.error(e?.message || (mode.value === 'login' ? '登录失败' : '注册失败'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}
</style>
