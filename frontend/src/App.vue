<template>
  <div class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">отечественный мессенджер</p>
        <h1>СУПРЕМУМ</h1>
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