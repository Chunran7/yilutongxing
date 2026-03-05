<template>
  <div class="comment-node" :style="{ marginLeft: depth * 16 + 'px' }">
    <div class="comment-header">
      <el-avatar :src="node.authorPic || defaultAvatar" :size="28" />
      <div class="meta">
        <div class="line1">
          <span class="name">{{ node.author || '匿名用户' }}</span>
          <span v-if="node.replyUserName" class="reply">回复 {{ node.replyUserName }}</span>
          <span class="time">{{ formatTime(node.createTime) }}</span>
        </div>
        <div class="line2">
          <span class="content">{{ node.content }}</span>
        </div>
      </div>
      <div class="actions">
        <el-button link size="small" @click="$emit('reply', node)">回复</el-button>
        <el-button v-if="canDelete" link size="small" type="danger" @click="$emit('delete', node)">删除</el-button>
      </div>
    </div>

    <!-- 只展示两层：根评论 + 所有回复统一放在第二层 -->
    <div v-if="depth === 0 && node.replies && node.replies.length" class="children">
      <CommentNode
        v-for="child in node.replies"
        :key="child.id"
        :node="child"
        :depth="depth + 1"
        :currentUserId="currentUserId"
        @reply="$emit('reply', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import defaultAvatar from '@/assets/default.png'

defineOptions({ name: 'CommentNode' })

const props = defineProps({
  node: { type: Object, required: true },
  depth: { type: Number, default: 0 },
  currentUserId: { type: [Number, String], default: null }
})

defineEmits(['reply', 'delete'])

const canDelete = computed(() => {
  if (!props.currentUserId) return false
  return String(props.node.userId) === String(props.currentUserId)
})

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return String(t)
  return d.toLocaleString()
}
</script>

<style scoped>
.comment-node {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.meta {
  flex: 1;
}

.line1 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #666;
}

.name {
  font-weight: 600;
  color: #333;
}

.reply {
  color: #409eff;
}

.time {
  margin-left: auto;
  font-size: 12px;
  color: #999;
}

.line2 {
  margin-top: 4px;
  font-size: 14px;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}

.actions {
  display: flex;
  gap: 6px;
}

.children {
  margin-top: 6px;
}
</style>
