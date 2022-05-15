<script setup lang="ts">
import { ref } from 'vue';
import { FsItem, uploadFiles, MultiStatus } from '../../../services/files';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', uploads: MultiStatus[]): void;
  (e: 'upload', uploads: FsItem, err?: Error): void;
}>();

const file = ref<HTMLInputElement>();

const fileUpload = async () => {
  const { files } = file.value;
  if (files.length > 0) {
    const items = await uploadFiles({
      path: props.path,
      files,
      callback: (item, err) => {
        emit('upload', item, err);
      },
    }).finally(() => {
      file.value.value = null;
    });
    emit('done', items);
  } else {
    emit('close');
  }
};

defineExpose({
  open() {
    file.value?.click();
  },
});
</script>

<script lang="ts">
export type OnFileUpload = (item: FsItem, err?: Error) => void;
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
