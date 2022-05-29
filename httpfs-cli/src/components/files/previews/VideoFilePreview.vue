<script setup lang="ts">
import { ref, shallowReactive, watch } from 'vue';
import { FsItem } from '../../../services/files';
import Waiting from '../../ui/Waiting.vue';

const props = withDefaults(
  defineProps<{
    item: FsItem;
    current: boolean;
  }>(),
  {
    item: null,
    current: false,
  }
);

const state = shallowReactive({
  waiting: true,
  loading: true,
  autopause: false,
});

const video = ref<HTMLVideoElement>();

watch(
  () => props.current,
  (value, oldValue) => {
    if (value) {
      if (state.autopause) {
        video.value?.play();
      }
    } else if (video.value) {
      state.autopause = !video?.value.paused;
      video.value?.pause();
    }
  }
);

watch(
  () => props.item,
  async item => {
    state.waiting = true;
    state.loading = true;
    state.autopause = false;

    if (item) {
      state.loading = false;
    }
  },
  {
    immediate: true,
  }
);
</script>

<template>
  <Waiting class="video-file-preview" :waiting="state.waiting" :noif="true">
    <video
      v-if="!state.loading"
      v-show="!state.waiting"
      ref="video"
      controls="true"
      @canplay="state.waiting = false"
    >
      <source :src="item?.endpoint" :type="item?.mimeType" />
    </video>
  </Waiting>
</template>

<style scoped lang="scss">
.video-file-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;

  video {
    width: auto;
    width: 100%;
    max-width: 80%;
    height: auto;
    max-height: 100%;

    @include media-less-md {
      max-width: 100%;
    }
  }
}
</style>
