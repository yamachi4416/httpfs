<script setup lang="ts">
import { watch, onBeforeUnmount, defineProps, defineEmits } from "vue";
import { onBeforeRouteLeave, onBeforeRouteUpdate } from "vue-router";

const props = defineProps<{
  show: boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
}>();

function close() {
  if (props.show) {
    emit("close");
    return false;
  }
  return true;
}

function escapeEvent(event: KeyboardEvent) {
  if (event.key === "Escape") {
    close();
  }
}

function initialize() {
  document.addEventListener("keydown", escapeEvent);
}

function dispose() {
  document.removeEventListener("keydown", escapeEvent);
}

onBeforeUnmount(() => dispose());

onBeforeRouteLeave(() => close());

onBeforeRouteUpdate(() => close());

watch(
  () => props.show,
  (show) => {
    if (show) {
      initialize();
    } else {
      dispose();
    }
  }
);
</script>

<template>
  <transition name="modal">
    <div v-if="props.show" class="modal" @click.self="emit('close')">
      <slot></slot>
    </div>
  </transition>
</template>
