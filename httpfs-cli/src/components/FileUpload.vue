<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { uploadFiles } from "../services/FilesService";

export default defineComponent({
  props: {
    path: {
      type: Array as PropType<Array<string>>,
    },
  },
  setup(props, context) {
    const file = ref<HTMLInputElement>();
    const hasFiles = ref(false);

    return {
      file,
      hasFiles,
      changeFiles() {
        console.log(file.value?.files?.length);
        hasFiles.value = file.value?.files?.length > 0;
      },
      async fileUpload() {
        const files = file.value.files;
        const uploads = await uploadFiles(props.path, files).finally(() => {
          file.value.value = null;
        });
        context.emit("done", uploads);
      },
    };
  },
});
</script>

<template>
  <div>
    <input ref="file" multiple="true" type="file" @change="changeFiles" />
    <button @click="fileUpload" :disabled="!hasFiles">アップロード</button>
  </div>
</template>
