<script setup lang="ts">
import { ref } from 'vue';
import { injectSharedState } from '../../../compositions';
import { FsItem, MultiStatus, uploadFiles } from '../../../services/files';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'progress', item: FsItem, err?: Error): void;
  (e: 'done', mtsts: MultiStatus[]): void;
}>();

const shared = injectSharedState();

const file = ref<HTMLInputElement>();

const fileUpload = async () => {
  const { files } = file.value;

  if (files.length === 0) {
    emit('close');
    return;
  }

  const mtsts = await shared.withLoading(() =>
    uploadFiles({
      path: props.path,
      files,
      callback: (item, err) => emit('progress', item, err),
    }).finally(() => {
      file.value.value = null;
    })
  );
  emit('done', mtsts);
};

defineExpose({
  open() {
    file.value?.click();
  },
});
</script>

<script lang="ts">
export type OnFileUploadProgress = (item: FsItem, err?: Error) => void;
</script>

<template>
  <input
    style="display: none"
    ref="file"
    type="file"
    multiple="true"
    @change="fileUpload"
  />
</template>
