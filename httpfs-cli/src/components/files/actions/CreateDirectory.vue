<script setup lang="ts">
import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { HttpException } from '../../../services';
import { createDirectory, FsItem } from '../../../services/files';
import Prompt from '../../ui/Prompt.vue';

const { t } = useI18n();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', item: FsItem): void;
}>();

const shared = injectSharedState();

const state = reactive({
  path: null as string[],
  show: false,
  dirname: '',
  invalid: false,
});

function clear() {
  state.path = null;
  state.show = false;
  state.dirname = '';
  state.invalid = false;
}

function close() {
  clear();
  emit('close');
}

async function action() {
  if (!state.dirname) {
    return;
  }

  try {
    const item = await shared.withLoading(() =>
      createDirectory({ path: state.path, dirname: state.dirname })
    );
    clear();
    emit('done', item);
  } catch (e) {
    if (e instanceof HttpException) {
      state.invalid = true;
    } else {
      throw e;
    }
  }
}

defineExpose({
  open(path: string[]) {
    clear();
    state.path = path;
    state.show = true;
  },
});
</script>

<template>
  <Prompt
    :show="state.show"
    :invalid="state.invalid"
    :title="t('messages.newDirectory')"
    :action="t('actions.create')"
    v-model="state.dirname"
    @cancel="close"
    @ok="action"
    @update:invalid="(value) => (state.invalid = value)"
  />
</template>
