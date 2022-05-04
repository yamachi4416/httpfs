<script setup lang="ts">
import { computed } from "vue";
import { FsItem } from "../../../services/FilesService";

const props = defineProps<{
  item: FsItem;
}>();

const icon = computed(() => {
  const { directory, name, mimeType } = props.item;
  if (directory) {
    return "folder";
  } else if (mimeType?.startsWith("image")) {
    return "image";
  } else if (mimeType === "application/zip") {
    return "folder_zip";
  } else if (mimeType === "application/pdf") {
    return "picture_as_pdf";
  }
  return "description";
});
</script>

<template>
  <span class="icon">{{ icon }}</span>
</template>
