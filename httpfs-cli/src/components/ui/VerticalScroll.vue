<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';

const props = defineProps<{
  index: number;
  items: any[];
}>();

const emit = defineEmits<{
  (e: 'shown', idx: number): void;
}>();

const scrollContainer = ref(null as HTMLElement);
const scrollItems = ref([] as Element[]);

function observe() {
  const observer = new IntersectionObserver(
    entities => {
      entities.forEach(entity => {
        if (entity.isIntersecting) {
          const idx = scrollItems.value.indexOf(entity.target);
          emit('shown', idx);
        }
      });
    },
    {
      root: scrollContainer.value,
      threshold: [0.5],
    }
  );

  onBeforeUnmount(() => {
    observer.disconnect();
  });

  scrollItems.value?.forEach(element => observer.observe(element));
}

onMounted(() => {
  observe();
  scrollItems.value[props.index]?.scrollIntoView();
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

  &-item {
    width: 100%;
    min-width: 100%;
    height: 100%;
    min-height: 100%;
    scroll-snap-align: start;
    scroll-snap-stop: always;
  }
}
</style>
