<script setup lang="ts">
import { watch, onBeforeUnmount, defineProps, defineEmits } from "vue";

const props = defineProps<{
  show: boolean;
  title: string;
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
  <dialog open v-if="props.show" @click.self="emit('close')">
    <article>
      <header>
        <a
          href="#"
          aria-label="Close"
          class="close"
          @click.prevent="emit('close')"
        ></a>
        {{ props.title }}
      </header>
      <slot></slot>
    </article>
  </dialog>
</template>
