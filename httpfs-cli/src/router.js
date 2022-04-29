import { createRouter, createWebHistory } from "vue-router";
import FilesList from "./components/FilesList.vue";

export default createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/:path(.*)*",
      component: FilesList,
    },
  ],
});
