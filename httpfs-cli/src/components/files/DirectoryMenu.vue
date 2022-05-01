<script setup lang="ts">
import { ref, computed } from "vue";

import FileUpload from "./actions/FileUpload.vue";
import CreateDirectory from "./actions/CreateDirectory.vue";

const props = defineProps<{
  path: string[];
  name: string;
}>();

const emit = defineEmits<{
  (e: "done"): void;
}>();

const showCreateDirectory = ref(false);
const showFileUpload = ref(false);
const menuDetails = ref<HTMLDetailsElement>();

const closeMenuAction = () => {
  showCreateDirectory.value = false;
  showFileUpload.value = false;
};

const doneMenuAction = () => {
  closeMenuAction();
  emit("done");
};

const openMenuAction = (action: () => void) => {
  menuDetails.value.open = false;
  action();
};
</script>

<template>
  <details ref="menuDetails" role="list">
    <summary aria-haspopup="listbox" role="link">
      {{ props.name }}
    </summary>
    <ul role="listbox">
      <li>
        <a
          @click.prevent="
            openMenuAction(() => {
              showCreateDirectory = true;
            })
          "
          >フォルダ作成</a
        >
        <a
          @click.prevent="
            openMenuAction(() => {
              showFileUpload = true;
            })
          "
          >ファイル追加</a
        >
      </li>
    </ul>
  </details>

  <Teleport to="body">
    <FileUpload
      :path="props.path"
      :show="showFileUpload"
      @close="closeMenuAction"
      @done="doneMenuAction"
    />
    <CreateDirectory
      :path="props.path"
      :show="showCreateDirectory"
      @close="closeMenuAction"
      @done="doneMenuAction"
    />
  </Teleport>
</template>
