<script setup lang="ts">
import { onBeforeMount, onErrorCaptured, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { HttpException } from './services/FilesService';

const router = useRouter();

onBeforeMount(() => {
  const setVH = () => {
    document.documentElement.style.setProperty(
      '--vh',
      `${window.innerHeight}px`
    );
  };
  setVH();

  window.addEventListener('resize', setVH);
  onUnmounted(() => {
    window.removeEventListener('resize', setVH);
  });
});

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
  <router-view />
</template>
