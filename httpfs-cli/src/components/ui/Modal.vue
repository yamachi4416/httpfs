<script setup lang="ts">
import { computed, ref } from '@vue/reactivity';
import { onBeforeUnmount, watch } from 'vue';
import { defineModal } from '../../plugins/modals';

const props = defineProps<{
  show: boolean;
  transision?: 'slide' | 'fade' | 'scale';
}>();

const transisionName = computed(() => {
  if (props.transision) {
    return `transision-modal-${props.transision}`;
  } else {
    return null;
  }
});

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const modal = defineModal(() => {
  if (props.show) {
    emit('close');
    return true;
  }
  return false;
});

const zIndex = ref(0);

onBeforeUnmount(() => modal.remove());

watch(
  () => props.show,
  (value, oldValue) => {
    if (!value === !oldValue) return;
    if (value) {
      zIndex.value = modal.add();
    } else {
      modal.remove();
    }
  }
);
</script>

<template>
  <transition :name="transisionName">
    <div
      v-if="props.show"
      class="modal"
      :class="transisionName"
      @click.self="modal.close()"
      :style="{ zIndex }"
    >
      <slot></slot>
    </div>
  </transition>
</template>
