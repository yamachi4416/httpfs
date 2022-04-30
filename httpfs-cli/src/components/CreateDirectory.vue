<script lang="ts">
import { ref, PropType, defineComponent } from "vue";
import { createDirectory } from "../services/FilesService";

export default defineComponent({
  props: {
    path: Array as PropType<Array<string>>,
  },
  setup(props, context) {
    const dirname = ref("");

    return {
      dirname,
      async createDirectory() {
        const item = await createDirectory(props.path, dirname.value).finally(
          () => {
            dirname.value = "";
          }
        );
        context.emit("done", item);
      },
    };
  },
});
</script>

<template>
  <div>
    <input type="text" v-model="dirname" />
    <button @click="createDirectory" :disabled="!dirname">フォルダ作成</button>
  </div>
</template>
