import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '../views/AuthView.vue'
import ChatView from '../views/ChatView.vue'

const routes = [
  { path: '/', redirect: '/auth' },
  { path: '/auth', name: 'auth', component: AuthView },
  { path: '/chat', name: 'chat', component: ChatView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router