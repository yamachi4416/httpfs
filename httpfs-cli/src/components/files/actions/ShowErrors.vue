<script setup lang="ts">
import { shallowReactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { MultiStatus } from '../../../services/files';
import Modal from '../../ui/Modal.vue';
import FileIcon from '../fileslist/FileIcon.vue';

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
    <article class="show-errors">
      <h3>{{ props.title }}</h3>
      <div class="show-errors-detail">
        <dl v-for="st in state.errors" :key="st.item.path">
          <dt>
            <FileIcon :item="st.item" />
            <span>{{ st.item.name }}</span>
          </dt>
          <dd>{{ t(`errors.${st.detail}`) }}</dd>
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
.show-errors {
  @include card;

  width: 600px;

  &-detail {
    display: flex;
    display: block;
    flex: 1;
    flex-direction: column;
    column-gap: 0.5em;
    overflow: auto;

    dl {
      width: 100%;
      margin-bottom: 0;

      dt {
        display: flex;
        column-gap: 0.5em;
        align-items: center;
      }
    }
  }
}
</style>
