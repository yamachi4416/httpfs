<script setup lang="ts">
import { onBeforeMount, onErrorCaptured, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import Loading from './components/ui/Loading.vue';
import { injectSharedState } from './compositions';
import { HttpException } from './services';

const shared = injectSharedState();
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
  <div>
    <router-view />
    <teleport to="body">
      <Loading :show="shared.loading" />
    </teleport>
  </div>
</template>
