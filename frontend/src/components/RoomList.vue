<template>
  <section class="panel rooms-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Rooms</p>
        <h2>Комнаты</h2>
      </div>
      <button class="btn btn-secondary" @click="$emit('reload')">Reload</button>
    </div>

    <div v-if="rooms.length" class="room-list">
      <div
          v-for="room in rooms"
          :key="room.id"
          class="room-item"
          :class="{ active: selectedRoomId === room.id }"
      >
        <!-- Основная строка комнаты -->
        <button class="room-item__main" @click="$emit('select', room)">
          <div class="room-top">
            <strong>{{ room.name }}</strong>
            <span class="badge" :class="room.accessType === 'PROTECTED' ? 'badge-protected' : ''">
              {{ room.accessType }}
            </span>
          </div>
          <p class="room-description">{{ room.description || 'Без описания' }}</p>
        </button>

        <!-- Панель вступления — появляется при выборе комнаты -->
        <div v-if="selectedRoomId === room.id" class="room-join">
          <input
              v-if="room.accessType === 'PROTECTED'"
              :value="joinPassword"
              @input="$emit('update:join-password', $event.target.value)"
              type="password"
              class="join-input"
              placeholder="Пароль для входа"
              @keyup.enter="$emit('join')"
          />
          <button class="btn btn-primary btn-join" @click="$emit('join')">
            Войти в комнату
          </button>
          <span v-if="roomStatus.kind !== 'idle'" :class="['status-chip', 'status-' + roomStatus.kind]">
            {{ roomStatus.text }}
          </span>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>Пока нет комнат.</p>
      <p class="muted">Наведи на панель слева, чтобы создать.</p>
    </div>
  </section>
</template>

<script setup>
defineProps({
  rooms: { type: Array, default: () => [] },
  selectedRoomId: { type: String, default: null },
  joinPassword: { type: String, default: '' },
  roomStatus: { type: Object, default: () => ({ text: '', kind: 'idle' }) }
})

defineEmits(['reload', 'select', 'join', 'update:join-password'])
</script>