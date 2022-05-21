import { createRouter, createWebHistory } from 'vue-router';
import Files from '../components/Files.vue';

const baseUrl = document.getElementsByTagName('base')[0]?.getAttribute('href');
const routerBase = import.meta.env.VITE_ROUTER_BASE;

export default createRouter({
  history: createWebHistory(`${baseUrl?.replace(/\/$/, '')}${routerBase}/`),
  routes: [
    {
      path: '/:path(.*)*',
      component: Files,
    },
  ],
});
