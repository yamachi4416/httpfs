<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { shallowReactive } from 'vue';
import { FsItem } from '../../services/files';
import Modal from '../ui/Modal.vue';
import ImageFilePreview from './previews/ImageFilePreview.vue';
import MiscFilePreview from './previews/MiscFilePreview.vue';

const state = shallowReactive({
  show: false,
  item: null as FsItem,
});

const previewComponent = computed(() => {
  if (state.show) {
    if (state.item?.mimeType?.startsWith('image/')) {
      return ImageFilePreview;
    }
    return MiscFilePreview;
  }
  return null;
});

function close() {
  state.show = false;
  state.item = null;
}

defineExpose({
  async open(item: FsItem) {
    state.item = item;
    state.show = true;
  },
});
</script>

<template>
  <teleport to="body">
    <Modal
      :show="state.show"
      transision="scale"
      @close="close"
      class="file-preview-modal"
    >
      <div class="file-preview-wrapper">
        <article class="file-preview">
          <div class="file-preview-header">
            <span class="file-preview-header-close">
              <a class="secondary icon" @click="close">close</a>
            </span>
            <span class="file-preview-header-title">
              <span>{{ state.item.name }}</span>
            </span>
            <span class="file-preview-header-menu">
              <a class="secondary icon" @click.prevent="state.item.download()">
                download
              </a>
            </span>
          </div>
          <p class="file-preview-body">
            <Component :is="previewComponent" :item="state.item" />
          </p>
        </article>
      </div>
    </Modal>
  </teleport>
</template>

<style scoped lang="scss">
.file-preview {
  &-modal {
    max-height: var(--vh);
  }

  &-wrapper {
    width: 100%;
    height: 100%;
  }

  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  min-width: 100%;
  max-width: 100%;
  height: 100%;
  padding: calc(var(--spacing) / 2);
  padding-top: 0;
  margin: 0;
  background-color: #000;

  &-header {
    display: flex;
    width: 100%;

    > * {
      display: flex;
      align-items: center;
      padding: var(--spacing);
      margin: 0;
    }

    &-close,
    &-menu {
      a {
        text-decoration: none;
        cursor: pointer;
      }
    }

    &-title {
      flex: 1;
    }
  }

  &-body {
    flex: 1;
    width: 100%;
    overflow: hidden;
  }
}
</style>
