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
    </div>

    <ChatPanel
        :room="selectedRoom"
        :messages="messages"
        :current-username="currentUsername"
        :ws-status="wsStatus"
        @send-message="handleSendMessage"
    />
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, reactive, ref } from 'vue'
import ChatPanel from '../components/ChatPanel.vue'
import RoomList from '../components/RoomList.vue'
import { authApi, roomsApi } from '../services/api'
import { connectToRoom, disconnectWs, sendRoomMessage } from '../services/ws'

const rooms = ref([])
const selectedRoom = ref(null)
const messages = ref([])
const joinPassword = ref('')
const wsStatus = ref('disconnected')
const currentUsername = ref('')

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
    messages.value.push({
      senderUsername: currentUsername.value || 'you',
      content,
      createdAt: new Date().toISOString()
    })
  } catch (error) {
    roomStatus.text = error.message
    roomStatus.kind = 'error'
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
