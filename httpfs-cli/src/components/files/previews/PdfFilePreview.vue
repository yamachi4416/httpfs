<script setup lang="ts">
import {
  getDocument,
  GlobalWorkerOptions,
  version as pdfjsVersion,
} from 'pdfjs-dist';
import workerSrc from 'pdfjs-dist/build/pdf.worker?url';
import { nextTick, onBeforeUnmount, ref, shallowReactive, watch } from 'vue';
import { FsItem } from '../../../services/files';
import Waiting from '../../ui/Waiting.vue';

const cdnBaseUrl = `https://cdn.jsdelivr.net/npm/pdfjs-dist@${pdfjsVersion}`;

GlobalWorkerOptions.workerSrc = workerSrc;

const props = defineProps<{
  item: FsItem;
}>();

const state = shallowReactive({
  waiting: true,
  ovserver: null as IntersectionObserver,
});

const container = ref<HTMLElement>();

async function loadPdf(item: FsItem) {
  const pdf = await getDocument({
    url: item.endpoint,
    cMapUrl: `${cdnBaseUrl}/cmaps/`,
    cMapPacked: true,
    standardFontDataUrl: `${cdnBaseUrl}/standard_fonts`,
  }).promise;

  const renderers = [...Array(pdf.numPages)].map((_, i) => {
    const baseWidth = screen.width;
    return {
      async page() {
        const page = await pdf.getPage(i + 1);
        const sizes = page.getViewport({ scale: 1 });
        const scale = (baseWidth / sizes.width) * 2;
        const viewport = page.getViewport({ scale });

        const img = document.createElement('img');
        img.width = viewport.width;
        img.height = viewport.height;
        img.style.height = 'auto';
        img.style.width = '100%';
        img.style.maxWidth = '100%';
        img.style.border = 'none';
        img.style.backgroundColor = '#fff';
        img.dataset.pageNumber = `${i}`;
        container.value.appendChild(img);

        const status = {
          rendered: false,
          requested: false,
        };

        return {
          render(beforeRender?: () => void) {
            if (status.rendered || status.requested) return;
            status.requested = true;
            return new Promise(resolve =>
              requestAnimationFrame(() => {
                if (!status.requested) return;

                status.rendered = true;
                beforeRender?.();

                const canvas = document.createElement('canvas');
                canvas.height = viewport.height;
                canvas.width = viewport.width;

                page
                  .render({
                    canvasContext: canvas.getContext('2d'),
                    viewport,
                  })
                  .promise.then(() => {
                    page.cleanup();
                    canvas.toBlob(blob => {
                      img.addEventListener('load', () => resolve(null), {
                        once: true,
                      });
                      img.src = URL.createObjectURL(blob);
                    }, 'image/png');
                  });
              })
            );
          },
          cancel() {
            if (status.rendered) return;
            status.requested = false;
          },
        };
      },
    };
  });

  const tasks = await Promise.all(renderers.map(r => r.page()));
  const observer = new IntersectionObserver(
    entities =>
      entities.forEach(entity => {
        const target = entity.target as HTMLElement;
        const task = tasks[Number(target.dataset.pageNumber)];
        if (entity.isIntersecting) {
          task?.render(() => observer.unobserve(target));
        } else {
          task?.cancel();
        }
      }),
    { root: container.value }
  );

  if (tasks.length) {
    await tasks[0].render();
    state.waiting = false;
    await nextTick();
    Array.prototype.slice
      .call(container.value.childNodes, 1)
      .forEach((node: HTMLElement) => observer.observe(node));
    state.ovserver = observer;
  }
}

onBeforeUnmount(() => {
  state.ovserver?.disconnect();
  container.value?.childNodes.forEach(node => node.remove());
});

watch(
  () => props.item,
  async item => {
    state.waiting = true;

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
    <div
      ref="container"
      v-show="!state.waiting"
      class="pdf-file-preview-inner"
    ></div>
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
  }
}
</style>
