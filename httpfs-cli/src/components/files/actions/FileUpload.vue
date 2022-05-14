<script setup lang="ts">
import { ref } from 'vue';
import { FsItem, uploadFiles } from '../../../services/files';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (
    e: 'done',
    uploads: FsItem[],
    failures: { err: Error; items: FsItem[] }[]
  ): void;
  (e: 'upload', uploads: FsItem[], failures: FsItem[], err?: Error): void;
}>();

const file = ref<HTMLInputElement>();

const fileUpload = async () => {
  const { files } = file.value;
  const failures = [] as { err: Error; items: FsItem[] }[];
  if (files.length > 0) {
    const items = await uploadFiles(
      props.path,
      files,
      (items: FsItem[], err) => {
        const ngs = items.filter(item => !item.creationTime);
        const oks = items.filter(item => item.creationTime);
        emit('upload', oks, ngs, err);
      }
    ).finally(() => {
      file.value.value = null;
    });
    emit('done', items, failures);
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

<template>
  <input
    style="display: none"
    ref="file"
    type="file"
    multiple="true"
    @change="fileUpload"
  />
</template>
