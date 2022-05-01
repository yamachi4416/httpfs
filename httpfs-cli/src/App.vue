<script setup lang="ts">
import { onErrorCaptured } from "vue";
import { useRouter } from "vue-router";
import { HttpException } from "./services/FilesService"

const router = useRouter();

onErrorCaptured((err, instalce, info) => {
  console.log(err, instalce, info);
  if (err instanceof HttpException) {
    if (err.status === 404) {
      router.back();
    }
  }
});
</script>

<template>
  <main class="container-fluid">
    <router-view></router-view>
  </main>
</template>
