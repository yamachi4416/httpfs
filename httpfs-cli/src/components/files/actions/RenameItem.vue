<script setup lang="ts">
import { reactive, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { renameItem, FsItem } from '../../../services/files';
import { HttpException } from '../../../services';
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
  item: null as FsItem,
  newName: '',
  invalid: false,
});

const title = computed(() => {
  if (state.item) {
    return t(`messages.rename${state.item.directory ? 'Directory' : 'File'}`);
  } else {
    return '';
  }
});

function clear() {
  state.path = null;
  state.show = false;
  state.item = null;
  state.newName = '';
  state.invalid = false;
}

function close() {
  clear();
  emit('close');
}

async function action() {
  if (!state.newName || state.item.name === state.newName) {
    return;
  }

  try {
    const item = await shared.withLoading(() =>
      renameItem({
        path: state.path,
        name: state.item.name,
        newName: state.newName,
      })
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
  open(path: string[], item: FsItem) {
    clear();
    state.path = path;
    state.item = item;
    state.newName = item.name;
    state.show = true;
  },
});
</script>

<template>
  <Prompt
    :show="state.show"
    :invalid="state.invalid"
    :title="title"
    :action="t('actions.renameItem')"
    v-model="state.newName"
    @cancel="close"
    @ok="action"
    @update:invalid="value => (state.invalid = value)"
  />
</template>
