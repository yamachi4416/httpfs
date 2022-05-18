<script setup lang="ts">
import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { deleteItems, FsItem } from '../../../services/files';
import Confirm from '../../ui/Confirm.vue';

const { t } = useI18n();

const shared = injectSharedState();

const state = reactive({
  show: false,
  path: null as string[],
  targets: [] as FsItem[],
});

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', mtsts: string[]): void;
}>();

function clear() {
  state.show = false;
  state.path = null;
  state.targets = null;
}

function close() {
  clear();
  emit('close');
}

async function deleteSelectedItems() {
  const mtsts = await shared.withLoading(() =>
    deleteItems(state.path, state.targets)
  );
  // TODO
  clear();
  emit('done', mtsts);
}

defineExpose({
  open(path: string[], targets: FsItem[]) {
    clear();
    state.show = true;
    state.path = [...path];
    state.targets = [...targets];
  },
  close,
});
</script>

<template>
  <Confirm
    :show="state.show"
    :title="t('messages.deleteFileConfirm')"
    @cancel="close"
    @ok="deleteSelectedItems"
  />
</template>
