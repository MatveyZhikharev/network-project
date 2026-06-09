<template>
  <section class="chat-panel">
    <header class="chat-panel__header">
      <div>
        <p class="eyebrow">Live chat</p>
        <h2>{{ room ? room.name : 'Комната не выбрана' }}</h2>
        <p class="muted">{{ room ? room.description || 'Без описания' : 'Сначала выбери комнату и войди в неё.' }}</p>
      </div>
      <span :class="['status-chip', wsStatusClass]">{{ wsStatus }}</span>
    </header>

    <div class="messages">
      <article
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="{ self: isSelf(message) }"
      >
        <div class="message-meta">
          <strong>{{ message.senderUsername || 'system' }}</strong>
          <span>{{ message.createdAt }}</span>
        </div>
        <p>{{ message.content }}</p>
      </article>

      <div v-if="!messages.length" class="empty-state">
        <p>Сообщений пока нет.</p>
        <p class="muted">Подключи WebSocket и отправь первое сообщение.</p>
      </div>
    </div>

    <form class="composer" @submit.prevent="submitMessage">
      <textarea v-model="draft" placeholder="Напиши сообщение" />
      <button class="btn btn-primary" type="submit">Send</button>
    </form>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  room: { type: Object, default: null },
  messages: { type: Array, default: () => [] },
  currentUsername: { type: String, default: '' },
  wsStatus: { type: String, default: 'disconnected' }
})

const emit = defineEmits(['send-message'])
const draft = ref('')

const wsStatusClass = computed(() => {
  if (props.wsStatus === 'connected') return 'status-success'
  if (props.wsStatus === 'connecting') return 'status-warning'
  if (props.wsStatus.includes('error')) return 'status-error'
  return 'status-idle'
})

function submitMessage() {
  if (!draft.value.trim()) return
  emit('send-message', draft.value.trim())
  draft.value = ''
}

function isSelf(message) {
  return message.senderUsername === props.currentUsername
}
</script>