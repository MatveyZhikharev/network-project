<template>
  <section class="grid-single">
    <AuthForm
        :status-text="status.text"
        :status-kind="status.kind"
        @register="handleRegister"
        @login="handleLogin"
        @me="handleMe"
    />

    <section class="panel">
      <div class="panel-header">
        <div>
          <p class="eyebrow">About</p>
          <h2>Что нужно для запуска</h2>
        </div>
      </div>
      <ul class="info-list">
        <li>Backend должен быть поднят на <code>http://localhost:8080</code>.</li>
        <li>Frontend шлёт запросы с <code>credentials: include</code>, потому что backend использует сессию.</li>
        <li>Если <code>/api/auth/me</code> падает после логина, значит backend ещё не сохраняет SecurityContext между запросами.</li>
      </ul>
    </section>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import AuthForm from '../components/AuthForm.vue'
import { authApi } from '../services/api.js'

const status = reactive({
  text: 'Не авторизован',
  kind: 'idle'
})

async function handleRegister(payload) {
  try {
    const user = await authApi.register(payload)
    status.text = `Регистрация успешна: ${user.username}`
    status.kind = 'success'
  } catch (error) {
    status.text = error.message
    status.kind = 'error'
  }
}

async function handleLogin(payload) {
  try {
    const user = await authApi.login(payload)
    status.text = `Логин успешен: ${user.username}`
    status.kind = 'success'
  } catch (error) {
    status.text = error.message
    status.kind = 'error'
  }
}

async function handleMe() {
  try {
    const user = await authApi.me()
    status.text = `Текущий пользователь: ${user.username}`
    status.kind = 'success'
  } catch (error) {
    status.text = error.message
    status.kind = 'error'
  }
}
</script>