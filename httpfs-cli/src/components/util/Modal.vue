<script setup lang="ts">
import { watch, onBeforeUnmount, defineProps, defineEmits } from "vue";
import { onBeforeRouteLeave, onBeforeRouteUpdate } from "vue-router";

const props = defineProps<{
  show: boolean;
  title: string;
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
  document.body.classList.remove("modal-is-open");
  document.body.classList.remove("modal-is-opening");
}

onBeforeUnmount(() => dispose());

onBeforeRouteLeave(() => close());

onBeforeRouteUpdate(() => close());

watch(
  () => props.show,
  (show) => {
    if (show) {
      initialize();
      document.body.classList.add("modal-is-open");
      document.body.classList.add("modal-is-opening");
    } else {
      dispose();
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
