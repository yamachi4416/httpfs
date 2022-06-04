import { createRouter, createWebHistory } from 'vue-router';
import Files from '../components/Files.vue';

function routerBase() {
  const baseUrl = document
    .getElementsByTagName('base')[0]
    .getAttribute('href');
  const routerBase = document
    .querySelector('link[rel="start"]')
    .getAttribute('href');
  return `${baseUrl.replace(/\/$/, '')}${routerBase}`;
}

export default createRouter({
  history: createWebHistory(routerBase()),
  routes: [
    {
      path: '/:path(.*)*',
      component: Files,
    },
  ],
});
