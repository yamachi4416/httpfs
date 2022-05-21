import { createRouter, createWebHistory } from 'vue-router';
import Files from '../components/Files.vue';

export default createRouter({
  history: createWebHistory(import.meta.env.VITE_ROUTER_BASE),
  routes: [
    {
      path: '/:path(.*)*',
      component: Files,
    },
  ],
});
