<script setup lang="ts">
import { computed } from 'vue';
import { FsItem } from '../../../services/FilesService';

const props = defineProps<{
  item: FsItem;
}>();

const icon = computed(() => {
  const { directory, mimeType } = props.item;
  if (directory) {
    return 'folder';
  }
  if (mimeType?.startsWith('image')) {
    return 'image';
  }
  if (mimeType === 'application/zip') {
    return 'folder_zip';
  }
  if (mimeType === 'application/pdf') {
    return 'picture_as_pdf';
  }
  return 'description';
});
</script>

<template>
  <span class="icon">{{ icon }}</span>
</template>
