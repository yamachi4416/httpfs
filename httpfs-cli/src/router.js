import { createRouter, createWebHistory } from 'vue-router';
import Files from './components/Files.vue';

export default createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/:path(.*)*',
      component: Files,
    },
  ],
});
