<template>
  <div class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Vue + Spring Boot</p>
        <h1>Network Chat</h1>
      </div>
      <nav class="app-nav">
        <RouterLink to="/auth">Auth</RouterLink>
        <RouterLink to="/chat">Chat</RouterLink>
      </nav>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>