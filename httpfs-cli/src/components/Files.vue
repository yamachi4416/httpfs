<script lang="ts">
import { ref, watch, defineComponent } from "vue";
import { useRouter, useRoute } from "vue-router";
import { downloadFile } from "../services/FilesService";
import Breadcrumb from "./files/Breadcrumb.vue";
import FilesList from "./files/FilesList.vue";
import FileUpload from "./files/actions/FileUpload.vue";
import CreateDirectory from "./files/actions/CreateDirectory.vue";

export default defineComponent({
  components: {
    Breadcrumb,
    FilesList,
    FileUpload,
    CreateDirectory,
  },

  setup() {
    const route = useRoute();
    const router = useRouter();
    const path = ref(Array.from(route.params.path));

    const showCreateDirectory = ref(false);
    const showFileUpload = ref(false);
    const reloadFilesList = ref(false);

    watch(
      () => route.params.path,
      async (newPath) => {
        path.value = Array.from(newPath);
      }
    );

    const clickItem = (item: any) => {
      if (item.directory) {
        router.push({
          params: {
            path: [...path.value, item.name],
          },
        });
      } else {
        downloadFile(path.value, item.name);
      }
    };

    return {
      path,
      showCreateDirectory,
      showFileUpload,
      reloadFilesList,
      clickItem,
    };
  },
});
</script>

<template>
  <div>
    <Breadcrumb
      :path="path"
      :menues="[
        {
          name: 'フォルダ追加',
          click: () => {
            showCreateDirectory = true;
          },
        },
        {
          name: 'ファイル追加',
          click: () => {
            showFileUpload = true;
          },
        },
      ]"
    />
    <FilesList
      :path="path"
      :reload="reloadFilesList"
      @select="clickItem"
      @done="reloadFilesList = false"
    />
    <FileUpload
      :path="path"
      :show="showFileUpload"
      @close="showFileUpload = false"
      @done="
        () => {
          showFileUpload = false;
          reloadFilesList = true;
        }
      "
    />
    <CreateDirectory
      :path="path"
      :show="showCreateDirectory"
      @close="showCreateDirectory = false"
      @done="
        () => {
          showCreateDirectory = false;
          reloadFilesList = true;
        }
      "
    />
  </div>
</template>
