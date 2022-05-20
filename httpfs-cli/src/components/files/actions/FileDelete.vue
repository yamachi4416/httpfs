<script setup lang="ts">
import { reactive, shallowRef } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { deleteItems, FsItem, MultiStatus } from '../../../services/files';
import Confirm from '../../ui/Confirm.vue';
import ShowErrors from './ShowErrors.vue';

const { t } = useI18n();

const shared = injectSharedState();

const state = reactive({
  show: false,
  path: null as string[],
  targets: [] as FsItem[],
});

const showErrors = shallowRef<InstanceType<typeof ShowErrors>>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', mtsts: MultiStatus[]): void;
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

  clear();
  await showErrors.value.open(mtsts.filter(st => st.statusCode !== 404));
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
  <teleport to="body">
    <Confirm
      :show="state.show"
      :title="t('messages.deleteFileConfirm')"
      @cancel="close"
      @ok="deleteSelectedItems"
    />
    <ShowErrors ref="showErrors" :title="t('messages.hasErrorsFileDelete')" />
  </teleport>
</template>
