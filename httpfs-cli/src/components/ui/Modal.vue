<script setup lang="ts">
import { computed, ref } from '@vue/reactivity';
import { onBeforeUnmount, watch } from 'vue';
import { defineModal } from '../../plugins/modals';

const props = defineProps<{
  show: boolean;
  transision?: 'slide' | 'slidedown' | 'fade' | 'scale';
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

<style scoped lang="scss">
.modal {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: var(--vh, 100vh);

  & > :where(*) {
    display: flex;
    flex-direction: column;
    row-gap: var(--spacing);
    width: fit-content;
    max-width: 100%;
    height: fit-content;
    max-height: 100%;

    & > :where(*) {
      display: flex;
      align-items: center;
      margin: 0;
    }

    & > :where(h3) {
      margin: 0;
      font-size: 1.2em;
    }

    & > :where(nav) {
      justify-content: flex-end;

      :where(a) {
        text-decoration: none;
      }

      :where(.devider) {
        width: 1px;
        height: 1.5em;
        margin-right: 1em;
        margin-left: 1em;
        background-color: var(--muted-border-color);
      }
    }
  }
}
</style>

<style lang="scss">
.transision-modal-slide,
.transision-modal-slidedown {
  background-color: rgb(0 0 0 / 30%);

  > * {
    align-self: flex-start;
    transform: translateY(0);
  }

  &-enter-active,
  &-leave-active {
    transition: background-color 0.3s ease;

    > * {
      transition: transform 0.2s ease-in-out;
    }
  }

  &-enter-from,
  &-leave-to {
    background-color: rgb(0 0 0 / 0%);

    > * {
      transform: translateY(-150%);
    }
  }
}

.transision-modal-slide {
  > * {
    @include media-less-sm {
      align-self: flex-end;
    }
  }

  &-enter-from,
  &-leave-to {
    > * {
      @include media-less-sm {
        transform: translateY(150%);
      }
    }
  }
}

.transision-modal-scale {
  opacity: 1;

  > * {
    opacity: 1;
    transform: scale(1);
  }

  &-enter-active,
  &-leave-active {
    transition: opacity 0.3s ease;

    > * {
      transition-timing-function: ease;
      transition-duration: 0.2s;
      transition-property: transform, opacity;
    }
  }

  &-enter-from,
  &-leave-to {
    opacity: 1;

    > * {
      opacity: 0;
      transform: scale(90%);
    }
  }
}
</style>
