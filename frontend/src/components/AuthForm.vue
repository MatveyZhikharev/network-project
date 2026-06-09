<template>
  <section class="panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Authentication</p>
        <h2>Вход и регистрация</h2>
      </div>
      <span :class="['status-chip', statusKindClass]">{{ statusText }}</span>
    </div>

    <form class="stack" @submit.prevent>
      <label class="field">
        <span>Username</span>
        <input v-model.trim="form.username" type="text" placeholder="matvey" />
      </label>

      <label class="field">
        <span>Email</span>
        <input v-model.trim="form.email" type="email" placeholder="matvey@example.com" />
      </label>

      <label class="field">
        <span>Password</span>
        <input v-model="form.password" type="password" placeholder="minimum 6 symbols" />
      </label>

      <div class="actions">
        <button class="btn btn-primary" @click="emit('register', { ...form })">Register</button>
        <button class="btn btn-secondary" @click="emit('login', { username: form.username, password: form.password })">Login</button>
        <button class="btn btn-secondary" @click="emit('me')">/me</button>
      </div>
    </form>
  </section>
</template>

<script setup>
import { reactive, computed } from 'vue'

const props = defineProps({
  statusText: { type: String, default: 'Не авторизован' },
  statusKind: { type: String, default: 'idle' }
})

const emit = defineEmits(['register', 'login', 'me'])

const form = reactive({
  username: '',
  email: '',
  password: ''
})

const statusKindClass = computed(() => `status-${props.statusKind}`)
</script>