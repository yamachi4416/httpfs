<script setup lang="ts">
import { useAttrs } from 'vue';

const props = withDefaults(
  defineProps<{
    waiting?: boolean;
  }>(),
  {
    waiting: true,
  }
);

const attrs = useAttrs();
</script>

<script lang="ts">
export default {
  inheritAttrs: false
}
</script>

<template>
  <div v-if="props.waiting" class="waiting">
    <div class="loader"></div>
  </div>
  <div v-else v-bind="attrs">
    <slot />
  </div>
</template>

<style scoped lang="scss">
.waiting {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;

  .loader {
    &,
    &::after {
      width: 3em;
      height: 3em;
      border-radius: 50%;
    }

    position: relative;
    border: 0.2em solid var(--progress-color);
    border-left-color: transparent;
    transform: translateZ(0);
    animation: loader 1.1s infinite linear;

    @keyframes loader {
      0% {
        transform: rotate(0deg);
      }

      100% {
        transform: rotate(360deg);
      }
    }
  }
}
</style>
