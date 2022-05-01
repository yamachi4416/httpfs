<script setup lang="ts">
import { reactive, ref } from "vue";
import {
  createDirectory,
  FsItem,
  HttpException,
} from "../../../services/FilesService";
import Modal from "../../util/Modal.vue";

const props = defineProps<{
  path: string[];
  show: Boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "done", item: FsItem): void;
}>();

const state = reactive({
  dirname: "",
  invalid: false,
});

const mkdir = async () => {
  if (state.dirname) {
    try {
      const item = await createDirectory(props.path, state.dirname);
      state.dirname = "";
      emit("done", item);
    } catch (e) {
      if (e instanceof HttpException) {
        state.invalid = true;
      } else {
        throw e;
      }
    }
  }
};
</script>

<template>
  <Modal :show="show" @close="emit('close')">
    <h3>新しいフォルダ</h3>
    <p>
      <input
        type="text"
        v-model="state.dirname"
        :aria-invalid="state.invalid || null"
        @input="state.invalid = false"
      />
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
