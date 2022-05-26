<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    show: boolean;
  }>(),
  {
    show: false,
  }
);
</script>

<template>
  <div v-if="props.show" class="loading">
    <div class="spinner">
      <div v-for="i in 3" :key="i" class="bounce"></div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.loading {
  @include fill-content;

  position: fixed;
  z-index: 99;
  display: flex;
  align-items: center;
  justify-content: center;

  .spinner {
    display: flex;
    column-gap: 1em;

    .bounce {
      display: inline-block;
      width: 1em;
      height: 1em;
      background-color: var(--progress-color);
      border-radius: 100%;
      opacity: 0.2;
      animation: bouncedelay 1.4s infinite ease-in-out both;

      @keyframes bouncedelay {
        0%,
        80%,
        100% {
          transform: scale(0);
        }

        40% {
          transform: scale(1);
        }
      }

      &:nth-last-of-type(2) {
        animation-delay: -0.16s;
      }

      &:nth-last-of-type(3) {
        animation-delay: -0.32s;
      }
    }
  }
}
</style>
