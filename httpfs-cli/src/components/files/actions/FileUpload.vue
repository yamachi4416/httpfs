<script setup lang="ts">
import { ref } from 'vue';
import { uploadFiles } from '../../../services/FilesService';
import { FsItem } from '../../../services/FsItem';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'done', items: FsItem[]): void;
  (e: 'upload', items: FsItem[]): void;
}>();

const file = ref<HTMLInputElement>();

const fileUpload = async () => {
  const { files } = file.value;
  if (files.length > 0) {
    const items = await uploadFiles(props.path, files, items => {
      emit('upload', items);
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

<template>
  <input
    style="display: none"
    ref="file"
    type="file"
    multiple="true"
    @change="fileUpload"
  />
</template>
