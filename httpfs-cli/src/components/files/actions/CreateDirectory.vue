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
  show: boolean;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "done", item: FsItem): void;
}>();

const state = reactive({
  dirname: "",
  invalid: false,
});

function close() {
  state.dirname = "";
  emit("close");
}

async function mkdir() {
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
}
</script>

<template>
  <Modal :show="show" @close="close">
    <article>
      <h3>新しいフォルダ</h3>
      <p>
        <input
          type="text"
          tabindex="1"
          v-model="state.dirname"
          :aria-invalid="state.invalid || null"
          @input="state.invalid = false"
        />
      </p>
      <nav>
        <a href="#" tabindex="1" role="link" @click.prevent="close"
          >キャンセル</a
        >
        <span class="devider"></span>
        <a href="#" tabindex="2" role="link" @click.prevent="mkdir">作成</a>
      </nav>
    </article>
  </Modal>
</template>
