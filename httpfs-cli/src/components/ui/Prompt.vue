<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import Modal from './Modal.vue';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    show: boolean;
    title?: string;
    action?: string;
    invalid?: boolean;
    modelValue: string;
  }>(),
  {
    show: false,
    invalid: false,
  }
);

const emit = defineEmits<{
  (e: 'ok');
  (e: 'cancel');
  (e: 'update:modelValue', value: string);
  (e: 'update:invalid', value: boolean);
}>();

const value = computed({
  get: () => props.modelValue,
  set: value => {
    emit('update:modelValue', value);
    emit('update:invalid', false);
  },
});
</script>

<template>
  <Modal :show="show" transision="slide" @close="emit('cancel')">
    <article class="card prompt">
      <h3>{{ title }}</h3>
      <input
        type="text"
        tabindex="1"
        v-auto-focus="show"
        v-model="value"
        :aria-invalid="invalid || null"
      />
      <nav>
        <a href="#" tabindex="1" role="link" @click.prevent="emit('cancel')">
          {{ t('actions.cancel') }}
        </a>
        <span class="devider"></span>
        <a href="#" tabindex="2" role="link" @click.prevent="emit('ok')">
          {{ action || t('actions.ok') }}
        </a>
      </nav>
    </article>
  </Modal>
</template>

<style scoped lang="scss">
.prompt {
  width: 600px;

  input[type='text'] {
    margin: 0;
  }
}
</style>
