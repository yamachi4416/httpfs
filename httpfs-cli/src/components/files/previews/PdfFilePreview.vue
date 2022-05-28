<script setup lang="ts">
import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist';
import workerSrc from 'pdfjs-dist/build/pdf.worker?url';
import { nextTick, reactive, ref, watch } from 'vue';
import { FsItem } from '../../../services/files';
import Waiting from '../../ui/Waiting.vue';

GlobalWorkerOptions.workerSrc = workerSrc;

const props = defineProps<{
  item: FsItem;
}>();

const state = reactive({
  waiting: true,
  totalPages: 0,
});

const canvas = ref<HTMLCanvasElement[]>();

// TODO: スクロールでページを読み込むようにする
async function loadPdf(item: FsItem) {
  const pdf = await getDocument({
    url: item.endpoint,
    cMapUrl: '/node_modules/pdfjs-dist/cmaps/',
    cMapPacked: true,
  }).promise;

  state.totalPages = pdf.numPages;
  await nextTick();

  const tasks = canvas.value.map(async (canvas, i) => {
    const page = await pdf.getPage(i + 1);
    const sizes = page.getViewport({ scale: 1 });
    const scale = (screen.width / sizes.width) * 2;
    const viewport = page.getViewport({ scale });
    const canvasContext = canvas.getContext('2d');

    canvas.height = viewport.height;
    canvas.width = viewport.width;

    await page.render({ canvasContext, viewport }).promise;
    page.cleanup();
  });

  await tasks[0];
  state.waiting = false;

  await Promise.all(tasks);

  pdf.cleanup();
}

watch(
  () => props.item,
  async item => {
    state.waiting = true;
    state.totalPages = 0;

    if (item) {
      await loadPdf(item);
    }
  },
  {
    immediate: true,
  }
);
</script>

<template>
  <Waiting class="pdf-file-preview" :waiting="state.waiting" :noif="true">
    <div v-show="!state.waiting" class="pdf-file-preview-inner">
      <canvas v-for="i in state.totalPages" :key="i" ref="canvas" />
    </div>
  </Waiting>
</template>

<style scoped lang="scss">
.pdf-file-preview {
  display: flex;
  justify-content: center;
  max-width: 100%;
  padding: 0 var(--spacing);
  @include media-less-md {
    padding: 0;
  }

  &,
  &-inner {
    width: 100%;
    height: 100%;
    max-height: 100%;
  }

  &-inner {
    max-width: 80%;
    overflow: auto;
    text-align: center;

    &::-webkit-scrollbar {
      width: 0;
    }

    @include media-less-md {
      max-width: 100%;
    }

    canvas {
      max-width: 100%;
    }
  }
}
</style>
