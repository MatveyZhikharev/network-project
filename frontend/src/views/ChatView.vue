<template>
  <section class="chat-layout">
    <div class="chat-sidebar">

      <section class="panel create-panel">
        <div class="create-panel__inner">
          <div class="panel-header">
            <div>
              <p class="eyebrow">Create room</p>
              <h2>Новая комната</h2>
            </div>
            <span :class="['status-chip', roomStatusClass]">{{ roomStatus.text }}</span>
          </div>
          <form class="stack" @submit.prevent="createRoom">
            <label class="field">
              <span>Name</span>
              <input v-model.trim="createForm.name" type="text" placeholder="functional-analysis" />
            </label>
            <label class="field">
              <span>Description</span>
              <input v-model.trim="createForm.description" type="text" placeholder="Hilbert space memes" />
            </label>
            <label class="field">
              <span>Access type</span>
              <select v-model="createForm.accessType">
                <option value="PUBLIC">PUBLIC</option>
                <option value="PROTECTED">PROTECTED</option>
              </select>
            </label>
            <label class="field" v-if="createForm.accessType === 'PROTECTED'">
              <span>Password</span>
              <input v-model="createForm.password" type="password" placeholder="пароль для входа" />
            </label>
            <button class="btn btn-primary" type="submit">Create room</button>
          </form>
        </div>
      </section>

      <RoomList
          :rooms="rooms"
          :selected-room-id="selectedRoom?.id ?? null"
          :join-password="joinPassword"
          :room-status="roomStatus"
          @reload="loadRooms"
          @select="selectRoom"
          @join="joinRoom"
          @update:join-password="joinPassword = $event"
      />

      <section class="panel user-panel">
        <div class="user-panel__inner">
          <p class="eyebrow">Аккаунт</p>
          <p class="user-panel__name">{{ currentUsername }}</p>
          <button class="btn btn-danger" @click="handleDeleteAccount">Удалить аккаунт</button>
        </div>
      </section>
    </div>

    <ChatPanel
        :room="selectedRoom"
        :messages="messages"
        :current-username="currentUsername"
        :ws-status="wsStatus"
        @send-message="handleSendMessage"
    />

    <!-- Confirmation modal -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="modal">
        <h3>Удалить аккаунт?</h3>
        <p>Это действие необратимо. Все данные будут удалены.</p>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showDeleteConfirm = false">Отмена</button>
          <button class="btn btn-danger" @click="confirmDeleteAccount">Удалить</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import ChatPanel from '../components/ChatPanel.vue'
import RoomList from '../components/RoomList.vue'
import { authApi, roomsApi } from '../services/api'
import { connectToRoom, disconnectWs, sendRoomMessage } from '../services/ws'

const router = useRouter()
const rooms = ref([])
const selectedRoom = ref(null)
const messages = ref([])
const joinPassword = ref('')
const wsStatus = ref('disconnected')
const currentUsername = ref('')
const showDeleteConfirm = ref(false)

const createForm = reactive({
  name: '',
  description: '',
  accessType: 'PUBLIC',
  password: ''
})

const roomStatus = reactive({
  text: 'Готово к работе',
  kind: 'idle'
})

const roomStatusClass = computed(() => `status-${roomStatus.kind}`)

async function loadRooms() {
  try {
    rooms.value = await roomsApi.getAll()
    roomStatus.text = 'Список загружен'
    roomStatus.kind = 'success'
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
  }
}

function selectRoom(room) {
  selectedRoom.value = room
  messages.value = []
  connectWs()
}

async function createRoom() {
  try {
    await roomsApi.create({
      name: createForm.name,
      description: createForm.description,
      accessType: createForm.accessType,
      password: createForm.password || null
    })
    roomStatus.text = 'Комната создана'
    roomStatus.kind = 'success'
    createForm.name = ''
    createForm.description = ''
    createForm.password = ''
    await loadRooms()
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
  }
}

async function joinRoom() {
  if (!selectedRoom.value) {
    roomStatus.text = 'Сначала выбери комнату'
    roomStatus.kind = 'warning'
    return
  }
  try {
    await roomsApi.join(selectedRoom.value.id, { password: joinPassword.value || null })
    roomStatus.text = `Вошёл в ${selectedRoom.value.name}`
    roomStatus.kind = 'success'
    joinPassword.value = ''
    connectWs()
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
  }
}

async function connectWs() {
  if (!selectedRoom.value) return

  try {
    messages.value = await roomsApi.getMessages(selectedRoom.value.id)
  } catch (e) {
    messages.value = []
  }

  connectToRoom(
      selectedRoom.value.id,
      message => {
        messages.value.push(message)
        if (message.senderUsername) {
          currentUsername.value ||= message.senderUsername
        }
      },
      status => { wsStatus.value = status }
  )
}

function handleSendMessage(content) {
  if (!selectedRoom.value) {
    roomStatus.text = 'Комната не выбрана'
    roomStatus.kind = 'warning'
    return
  }

  if (wsStatus.value !== 'connected') {
    roomStatus.text = `WS статус: ${wsStatus.value}`
    roomStatus.kind = 'warning'
    return
  }

  try {
    sendRoomMessage(selectedRoom.value.id, content)
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
  }
}

function handleDeleteAccount() {
  showDeleteConfirm.value = true
}

async function confirmDeleteAccount() {
  try {
    disconnectWs()
    await authApi.deleteAccount()
    router.push('/auth')
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
    showDeleteConfirm.value = false
  }
}

authApi.me().then(user => {
  currentUsername.value = user.username
}).catch(() => {})

loadRooms()

onBeforeUnmount(() => {
  disconnectWs()
})
</script>

<style scoped>
.user-panel {
  margin-top: auto;
}
.user-panel__inner {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.user-panel__name {
  font-weight: 600;
  font-size: 0.95rem;
}
.btn-danger {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}
.btn-danger:hover {
  background: #c53030;
}
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}
.modal {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  max-width: 400px;
  width: 90%;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.modal h3 {
  margin: 0;
  font-size: 1.25rem;
}
.modal p {
  margin: 0;
  color: #666;
}
.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}
.btn-secondary {
  background: #eee;
  color: #333;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
}
.btn-secondary:hover {
  background: #ddd;
}
</style>
