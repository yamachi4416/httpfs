<script setup lang="ts">
import { watch, onBeforeUnmount, defineProps, defineEmits } from "vue";

const props = defineProps<{
  show: Boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
}>();

const escapeEvent = (event: KeyboardEvent) => {
  if (event.key === "Escape") {
    emit("close");
  }
};

const addEscapeEvent = () => {
  document.addEventListener("keydown", escapeEvent);
};

const removeEscapeEvent = () => {
  document.removeEventListener("keydown", escapeEvent);
};

onBeforeUnmount(() => removeEscapeEvent());

watch(
  () => props.show,
  (show) => {
    if (show) {
      addEscapeEvent();
    } else {
      removeEscapeEvent();
    }
  }
);
</script>

<template>
  <dialog :open="props.show ? true : null" @click.self="emit('close')">
    <article>
      <slot></slot>
    </article>
  </dialog>
</template>

<style scoped>
article {
  width: 600px;
  max-width: 100%;
}
</style>
