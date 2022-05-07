<script setup lang="ts">
import { onBeforeUnmount, watch } from 'vue';
import { defineModal } from '../../plugins/modals';

const props = defineProps<{
  show: boolean;
}>();

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

onBeforeUnmount(() => modal.remove());

watch(
  () => props.show,
  (value, oldValue) => {
    if (!value === !oldValue) return;
    if (value) {
      modal.add();
    } else {
      modal.remove();
    }
  }
);
</script>

<template>
  <transition name="modal">
    <div v-if="props.show" class="modal" @click.self="modal.close()">
      <slot></slot>
    </div>
  </transition>
</template>
