<script lang="ts">
import { ref, watch, defineComponent } from "vue";
import { useRouter, useRoute } from "vue-router";
import { downloadFile } from "../services/FilesService";
import Breadcrumb from "./files/Breadcrumb.vue";
import FilesList from "./files/FilesList.vue";
import DirectoryMenu from "./files/DirectoryMenu.vue";

export default defineComponent({
  components: {
    Breadcrumb,
    FilesList,
    DirectoryMenu,
  },

  setup() {
    const route = useRoute();
    const router = useRouter();
    const path = ref(Array.from(route.params.path));

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
      reloadFilesList,
      clickItem,
    };
  },
});
</script>

<template>
  <div>
    <Breadcrumb :path="path">
      <template v-slot:default="s">
        <DirectoryMenu :path="path" :name="s.item?.name" />
      </template>
    </Breadcrumb>
    <FilesList
      :path="path"
      :reload="reloadFilesList"
      @select="clickItem"
      @done="reloadFilesList = false"
    />
  </div>
</template>
