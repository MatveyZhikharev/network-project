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
      <button
          v-for="room in rooms"
          :key="room.id"
          class="room-item"
          :class="{ active: selectedRoomId === room.id }"
          @click="$emit('select', room)"
      >
        <div class="room-top">
          <strong>{{ room.name }}</strong>
          <span class="badge">{{ room.accessType }}</span>
        </div>
        <p class="room-description">{{ room.description || 'Без описания' }}</p>
        <p class="room-meta">ownerId: {{ room.createdByUserId }}</p>
      </button>
    </div>

    <div v-else class="empty-state">
      <p>Пока нет комнат.</p>
      <p class="muted">Создай первую или нажми Reload.</p>
    </div>
  </section>
</template>

<script setup>
defineProps({
  rooms: { type: Array, default: () => [] },
  selectedRoomId: { type: String, default: null }
})

defineEmits(['reload', 'select'])
</script>