<script setup lang="ts">
import { reactive, shallowRef } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState } from '../../../compositions';
import { FsItem, MultiStatus, uploadFiles } from '../../../services/files';
import ShowErrors from './ShowErrors.vue';

const { t } = useI18n();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'progress', item: FsItem, err?: Error): void;
  (e: 'done', mtsts: MultiStatus[]): void;
}>();

const state = reactive({
  path: null as string[],
});
const shared = injectSharedState();
const file = shallowRef<HTMLInputElement>();
const showErrors = shallowRef<InstanceType<typeof ShowErrors>>();

function clear() {
  state.path = null;
}

const action = async () => {
  const { files } = file.value;

  if (files.length === 0) {
    emit('close');
    return;
  }

  const mtsts = await shared.withLoading(() =>
    uploadFiles({
      path: state.path,
      files,
      callback: (item, err) => emit('progress', item, err),
    }).finally(() => {
      file.value.value = null;
    })
  );

  await showErrors.value.open(mtsts);
  emit('done', mtsts);
};

defineExpose({
  open(path: string[]) {
    clear();
    state.path = path;
    file.value?.click();
  },
});
</script>

<script lang="ts">
export type OnFileUploadProgress = (item: FsItem, err?: Error) => void;
</script>

<template>
  <teleport to="body">
    <input
      style="display: none"
      ref="file"
      type="file"
      multiple="true"
      @change="action"
    />
    <ShowErrors ref="showErrors" :title="t('messages.hasErrorsUploadFiles')" />
  </teleport>
</template>
