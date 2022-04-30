<script setup lang="ts">
import { ref } from "vue";
import { createDirectory, FsItem } from "../../../services/FilesService";
import Modal from "../../util/Modal.vue";

const props = defineProps<{
  path: string[];
  show: Boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "done", item: FsItem): void;
}>();

const dirname = ref("");

const mkdir = async () => {
  if (dirname.value) {
    const item = await createDirectory(props.path, dirname.value).finally(
      () => {
        dirname.value = "";
      }
    );
    emit("done", item);
  }
};
</script>

<template>
  <Modal :show="show" @close="emit('close')">
    <h3>新しいフォルダ</h3>
    <p>
      <input type="text" v-model="dirname" />
    </p>
    <footer>
      <a href="#" @click.prevent="emit('close')">キャンセル</a>
      <a href="#" @click.prevent="mkdir">作成</a>
    </footer>
  </Modal>
</template>

<style scoped>
h3 {
  margin-bottom: 1rem;
}
footer {
  margin-top: 1rem;
  display: flex;
  column-gap: 1rem;
  justify-content: flex-end;
}
</style>
