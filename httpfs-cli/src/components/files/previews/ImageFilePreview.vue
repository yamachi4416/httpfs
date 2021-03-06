<script setup lang="ts">
import { reactive, watch } from 'vue';
import { FsItem } from '../../../services/files';
import Waiting from '../../ui/Waiting.vue';

const props = defineProps<{
  item: FsItem;
}>();

const state = reactive({
  waiting: true,
  width: null as number,
  height: null as number,
});

function loadImage(item: FsItem) {
  const img = new Image();
  img.onload = () => {
    state.waiting = false;
    state.width = img.width;
    state.height = img.height;
  };

  img.onerror = () => {
    state.waiting = false;
  };

  img.src = props.item.endpoint;
}

watch(
  () => props.item,
  item => {
    state.waiting = true;
    state.width = null;
    state.height = null;
    if (item) {
      loadImage(item);
    }
  },
  {
    immediate: true,
  }
);
</script>

<template>
  <Waiting class="image-file-preview" :waiting="state.waiting">
    <img
      :src="props.item.endpoint"
      :title="props.item.name"
      :width="state.width"
      :height="state.height"
    />
  </Waiting>
</template>

<style scoped lang="scss">
.image-file-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;

  img {
    width: auto;
    max-width: 100%;
    height: auto;
    max-height: 100%;
    object-fit: cover;
    opacity: 0;
    animation: fadein 0.5s forwards;
  }

  @keyframes fadein {
    0% {
      opacity: 0;
    }

    100% {
      opacity: 1;
    }
  }
}
</style>
