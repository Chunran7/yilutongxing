<template>
  <div class="forgot-container">
    <div class="forgot-box">
      <h2 class="forgot-title">忘记密码</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入注册时使用的邮箱" />
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码" style="width:60%" />
          <el-button style="margin-left:8px" @click="sendCode" :loading="sending">发送验证码</el-button>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">重置密码</el-button>
          <el-button type="text" @click="goLogin">返回登录</el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { sendForgotCodeService, resetPasswordService } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const form = reactive({ email: '', code: '', newPassword: '' })
const sending = ref(false)
const submitting = ref(false)

const rules = {
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const sendCode = async () => {
  if (sending.value) return
  try {
    await formRef.value?.validateField('email')
  } catch { return }
  sending.value = true
  try {
    const res = await sendForgotCodeService({ email: form.email })
    if (res?.devMode && res?.code) {
      ElMessage.info(`开发者模式：验证码 ${res.code}（真实环境会发邮件）`)
    } else {
      ElMessage.success('如果该邮箱已注册，验证码会发送至邮箱')
    }
  } catch (e) {
    ElMessage.error(e?.message || '发送失败')
  } finally {
    sending.value = false
  }
}

const submit = async () => {
  if (submitting.value) return
  try {
    await formRef.value?.validate()
  } catch { return }
  submitting.value = true
  try {
    await resetPasswordService({ email: form.email, code: form.code, newPassword: form.newPassword })
    ElMessage.success('密码已重置，请使用新密码登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e?.message || '重置失败')
  } finally {
    submitting.value = false
  }
}

const goLogin = () => router.push('/login')
</script>

<style scoped>
.forgot-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.forgot-box {
  width: 480px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.forgot-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
}
</style>
