<script setup lang="ts">
import { reactive } from 'vue';
import { createDirectory } from '../../../services/FilesService';
import Modal from '../../util/Modal.vue';
import { vAutoFocus } from '../../../directives/vAutoFocus';
import { useI18n } from 'vue-i18n';
import { FsItem } from '../../../services/FsItem';
import { HttpException } from '../../../services/HttpException';

const props = defineProps<{
  path: string[];
}>();

const { t } = useI18n();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', item: FsItem): void;
}>();

const state = reactive({
  show: false,
  dirname: '',
  invalid: false,
});

function close() {
  state.show = false;
  state.dirname = '';
  state.invalid = false;
  emit('close');
}

async function mkdir() {
  if (state.dirname) {
    try {
      const item = await createDirectory(props.path, state.dirname);
      state.show = false;
      state.dirname = '';
      emit('done', item);
    } catch (e) {
      if (e instanceof HttpException) {
        state.invalid = true;
      } else {
        throw e;
      }
    }
  }
}

defineExpose({
  open() {
    state.show = true;
  },
});
</script>

<template>
  <Modal :show="state.show" transision="slide" @close="close">
    <article class="card">
      <h3>{{ t('messages.newDirectory') }}</h3>
      <p>
        <input
          type="text"
          tabindex="1"
          v-auto-focus="state.show"
          v-model="state.dirname"
          :aria-invalid="state.invalid || null"
          @input="state.invalid = false"
        />
      </p>
      <nav>
        <a href="#" tabindex="1" role="link" @click.prevent="close">
          {{ t('actions.cancel') }}
        </a>
        <span class="devider"></span>
        <a href="#" tabindex="2" role="link" @click.prevent="mkdir">
          {{ t('actions.create') }}
        </a>
      </nav>
    </article>
  </Modal>
</template>

<style scoped lang="scss">
.card {
  width: 600px;
}
</style>
