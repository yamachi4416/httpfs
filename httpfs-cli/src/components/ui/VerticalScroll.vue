<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { onBeforeUnmount, onMounted, ref } from 'vue';

const props = defineProps<{
  index: number;
  items: any[];
}>();

const emit = defineEmits<{
  (e: 'update:index', idx: number): void;
}>();

const scrollContainer = ref(null as HTMLElement);
const scrollItems = ref([] as Element[]);

function observe() {
  const observer = new IntersectionObserver(
    entities => {
      entities.forEach(entity => {
        if (entity.isIntersecting) {
          const idx = scrollItems.value.indexOf(entity.target);
          emit('update:index', idx);
        }
      });
    },
    {
      root: scrollContainer.value,
      threshold: [0.5],
    }
  );

  scrollItems.value?.forEach(element => observer.observe(element));

  return observer;
}

function addKeyboardHandler() {
  const handlers = new Map([
    ['ArrowLeft', prev],
    ['ArrowRight', next],
  ]);

  const handler = (evt: KeyboardEvent) => {
    if (handlers.has(evt.key)) {
      evt.preventDefault();
      handlers.get(evt.key)();
    }
  };

  window.addEventListener('keydown', handler);

  return {
    disconnect() {
      window.removeEventListener('keydown', handler);
    },
  };
}

const hasPrev = computed(() => props.index > 0);
const hasNext = computed(() => props.index + 1 < props.items.length);

function prev() {
  if (hasPrev.value) {
    scrollItems.value[props.index - 1].scrollIntoView({ behavior: 'smooth' });
  }
}

function next() {
  if (hasNext.value) {
    scrollItems.value[props.index + 1].scrollIntoView({ behavior: 'smooth' });
  }
}

onMounted(() => {
  scrollItems.value[props.index]?.scrollIntoView();

  const disposables = [observe(), addKeyboardHandler()];

  onBeforeUnmount(() => {
    disposables.forEach(dispose => dispose.disconnect());
  });
});
</script>

<template>
  <div ref="scrollContainer" class="vertical-scroll">
    <div
      ref="scrollItems"
      class="vertical-scroll-item"
      v-for="(item, idx) of props.items"
      :key="idx"
    >
      <slot :item="item" :idx="idx"></slot>
    </div>
    <nav class="vertical-scroll-navigation">
      <span>
        <a
          v-show="hasPrev"
          class="icon secondary"
          href="#"
          @click.prevent="prev()"
        >
          arrow_back_ios
        </a>
      </span>
      <span>
        <a
          v-show="hasNext"
          class="icon secondary"
          href="#"
          @click.prevent="next()"
        >
          arrow_forward_ios
        </a>
      </span>
    </nav>
  </div>
</template>

<style scoped lang="scss">
.vertical-scroll {
  display: flex;
  flex: 1;
  width: 100%;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-snap-type: x mandatory;

  &::-webkit-scrollbar {
    height: 0;
  }

  &-item {
    width: 100%;
    min-width: 100%;
    height: 100%;
    min-height: 100%;
    scroll-snap-align: start;
    scroll-snap-stop: always;
  }

  &-navigation {
    position: absolute;
    right: 0;
    left: 0;
    align-items: center;
    height: 100%;
    padding: var(--spacing);
    pointer-events: none;

    & > * {
      a {
        padding: var(--spacing);
        font-size: 2em;
        pointer-events: fill;
      }
    }

    @include media-less-sm {
      display: none;
    }
  }
}
</style>
