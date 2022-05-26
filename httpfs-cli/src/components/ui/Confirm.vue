<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import Modal from './Modal.vue';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    show: boolean;
    title?: string;
    message?: string;
  }>(),
  {
    show: false,
    invalid: false,
  }
);

const emit = defineEmits<{
  (e: 'ok');
  (e: 'cancel');
}>();

</script>

<template>
  <Modal :show="props.show" transision="slidedown" @close="emit('cancel')">
    <article class="card confirm">
      <h3>{{ props.title }}</h3>
      <p>{{ props.message }}</p>
      <nav>
        <a href="#" tabindex="1" role="link" @click.prevent="emit('cancel')">
          {{ t('actions.cancel') }}
        </a>
        <span class="devider"></span>
        <a href="#" tabindex="2" role="link" @click.prevent="emit('ok')">
          {{ t('actions.ok') }}
        </a>
      </nav>
    </article>
  </Modal>
</template>

<style scoped lang="scss">
.confirm {
  width: 400px;

  p {
    margin: 0;
  }
}
</style>
