<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { shallowReactive } from 'vue';
import { FsItem } from '../../services/files';
import Modal from '../ui/Modal.vue';
import VerticalScroll from '../ui/VerticalScroll.vue';
import ImageFilePreviewVue from './previews/ImageFilePreview.vue';
import MiscFilePreviewVue from './previews/MiscFilePreview.vue';
import PdfFilePreviewVue from './previews/PdfFilePreview.vue';
import VideoFilePreviewVue from './previews/VideoFilePreview.vue';

const state = shallowReactive({
  show: false,
  index: -1,
  items: [] as {
    item: FsItem;
    shown: boolean;
    preview: ReturnType<typeof previewComponent>;
  }[],
});

const video = document.createElement('video');
const currentItem = computed(() => state.items[state.index]?.item);

function previewComponent(item: FsItem) {
  if (item?.mimeType?.startsWith('image/')) {
    return ImageFilePreviewVue;
  }
  if (item?.mimeType === 'application/pdf') {
    return PdfFilePreviewVue;
  }
  if (video.canPlayType(item?.mimeType)) {
    return VideoFilePreviewVue;
  }
  return MiscFilePreviewVue;
}

function close() {
  state.index = -1;
  state.show = false;
  state.items = [];
}

function updateIndex(idx: number) {
  const item = state.items[idx];
  if (item) {
    item.shown = true;
  }
  state.index = idx;
}

defineExpose({
  async open(items: FsItem[], index: number) {
    state.index = index;
    state.items = items.map((item, idx) => ({
      get item() {
        return this.shown ? item : null;
      },
      shown: idx === index,
      preview: previewComponent(item),
    }));
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
              <span>{{ currentItem?.name }}</span>
            </span>
            <span class="file-preview-header-menu">
              <a
                v-show="!currentItem?.directory"
                class="secondary icon"
                @click.prevent="currentItem?.download()"
              >
                download
              </a>
            </span>
          </div>
          <div class="file-preview-body">
            <VerticalScroll
              :items="state.items"
              :index="state.index"
              v-slot="{ item, idx }"
              @update:index="updateIndex"
            >
              <Component
                :is="item.preview"
                :item="item.item"
                :current="state.index === idx"
              />
            </VerticalScroll>
          </div>
        </article>
      </div>
    </Modal>
  </teleport>
</template>

<style scoped lang="scss">
.file-preview {
  @include fill-height;

  &-wrapper {
    width: 100%;
    height: 100%;
  }

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
      padding-right: 0;
      padding-left: 0;
      overflow: hidden;

      > * {
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  &-body {
    flex: 1;
    width: 100%;
    max-width: 100%;
    height: 100%;
    max-height: 100%;
    overflow: hidden;
  }
}
</style>
