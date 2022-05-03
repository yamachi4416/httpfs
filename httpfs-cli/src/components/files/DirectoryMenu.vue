<script setup lang="ts">
import { ref } from "vue";

import FileUpload from "./actions/FileUpload.vue";
import CreateDirectory from "./actions/CreateDirectory.vue";
import { FsItem } from "../../services/FilesService";

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: "done"): void;
  (e: "upload", items: FsItem[]): void;
}>();

const showCreateDirectory = ref(false);
const showFileUpload = ref(false);

const closeMenuAction = () => {
  showCreateDirectory.value = false;
  showFileUpload.value = false;
};

const doneMenuAction = () => {
  closeMenuAction();
  emit("done");
};

const uploadProgress = (items: FsItem[]) => {
  emit("upload", items);
};

defineExpose({
  openCreateDirectory() {
    showCreateDirectory.value = true;
  },
  openFileUpload() {
    showFileUpload.value = true;
  },
});
</script>

<template>
  <Teleport to="body">
    <FileUpload
      :path="props.path"
      :show="showFileUpload"
      @close="closeMenuAction"
      @done="doneMenuAction"
      @upload="uploadProgress"
    />
    <CreateDirectory
      :path="props.path"
      :show="showCreateDirectory"
      @close="closeMenuAction"
      @done="doneMenuAction"
    />
  </Teleport>
</template>
