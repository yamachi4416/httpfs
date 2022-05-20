<script setup lang="ts">
import { shallowReactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { MultiStatus } from '../../../services/files';
import Modal from '../../ui/Modal.vue';

const { t } = useI18n();

const props = defineProps<{
  title?: string;
}>();

const state = shallowReactive({
  show: false,
  errors: [] as MultiStatus[],
});

const emit = defineEmits<{
  (e: 'close');
}>();

function clear() {
  state.show = false;
  state.errors = [];
}

function close() {
  clear();
  emit('close');
}

defineExpose({
  async open(mtsts: MultiStatus[]) {
    clear();

    const errors = mtsts.filter(m => m.isError);
    if (errors.length === 0) {
      return Promise.resolve();
    }

    return new Promise<void>(resolve => {
      state.show = true;
      state.errors = errors;
      const dispose = watch(
        () => state.show,
        () => {
          dispose();
          clear();
          resolve();
        }
      );
    });
  },
});
</script>

<template>
  <Modal :show="state.show" transision="scale" @close="close">
    <article class="card">
      <h3>{{ props.title }}</h3>
      <div class="errors">
        <dl v-for="st in state.errors" :key="st.item.path">
          <dt>{{ st.item.name }}</dt>
          <dd>({{ st.statusCode }}) {{ st.status }}</dd>
        </dl>
      </div>
      <nav>
        <a href="#" tabindex="2" role="link" @click.prevent="close">
          {{ t('actions.ok') }}
        </a>
      </nav>
    </article>
  </Modal>
</template>

<style scoped lang="scss">
.card {
  width: 600px;

  .errors {
    display: flex;
    flex-direction: column;
    column-gap: 0.5em;
    flex: 1;
    overflow: auto;
    display: block;
    dl {
      width: 100%;
      margin-bottom: 0;
    }
  }
}
</style>
