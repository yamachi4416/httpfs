<script setup lang="ts">
import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { HttpException } from '../../../services';
import { createDirectory, FsItem } from '../../../services/files';
import Prompt from '../../ui/Prompt.vue';

const { t } = useI18n();

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', item: FsItem): void;
}>();

const shared = injectSharedState();

const state = reactive({
  show: false,
  dirname: '',
  invalid: false,
});

function clear() {
  state.show = false;
  state.dirname = '';
  state.invalid = false;
}

function close() {
  clear();
  emit('close');
}

async function mkdir() {
  if (!state.dirname) {
    return;
  }

  try {
    const item = await shared.withLoading(() =>
      createDirectory({ path: props.path, dirname: state.dirname })
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
  open() {
    clear();
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
    @ok="mkdir"
    @update:invalid="(value) => (state.invalid = value)"
  />
</template>
