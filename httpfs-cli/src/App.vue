<script setup lang="ts">
import { onBeforeMount, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import Loading from './components/ui/Loading.vue';
import { injectSharedState } from './compositions';

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

router.afterEach((to, from, failure) => {
  if (!failure) {
    const state = history.state;
    if (state?.back == null) {
      router.push({
        ...router.currentRoute.value,
        hash: '#',
      });
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
