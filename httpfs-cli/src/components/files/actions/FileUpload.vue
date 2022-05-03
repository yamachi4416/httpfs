<script setup lang="ts">
import { ref, watch } from "vue";
import { uploadFiles, FsItem } from "../../../services/FilesService";

const props = defineProps<{
  path: string[];
  show: Boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "done", items: FsItem[]): void;
  (e: "upload", items: FsItem[]): void;
}>();

const file = ref<HTMLInputElement>();

watch(
  () => props.show,
  (show) => {
    if (show && file?.value) {
      file.value.click();
      if (props.show) {
        emit("close");
      }
    }
  }
);

const fileUpload = async () => {
  const files = file.value.files;
  if (files.length > 0) {
    const items = await uploadFiles(props.path, files, (items) => {
      emit("upload", items);
    }).finally(() => {
      file.value.value = null;
    });
    emit("done", items);
  } else {
    emit("close");
  }
};
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
