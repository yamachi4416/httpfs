<script setup lang="ts">
import { onBeforeMount, reactive, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  downloadFile,
  fetchDirectoryItems,
  FsItem,
} from "../services/FilesService";

import Breadcrumb from "./files/Breadcrumb.vue";
import FilesList from "./files/FilesList.vue";
import DirectoryMenu from "./files/DirectoryMenu.vue";

const route = useRoute();
const router = useRouter();

const state = reactive({
  path: Array.from(route.params.path),
  items: [] as FsItem[],
});

watch(
  () => route.params.path,
  async (newPath) => {
    state.path = Array.from(newPath);
    await fetchItems();
  }
);

const fetchItems = async () => {
  state.items = await fetchDirectoryItems(state.path);
};

const clickItem = (item: FsItem) => {
  if (item.directory) {
    router.push({
      params: {
        path: [...state.path, item.name],
      },
    });
  } else {
    downloadFile(state.path, item.name);
  }
};

onBeforeMount(async () => await fetchItems());
</script>

<template>
  <div>
    <Breadcrumb :path="state.path">
      <template v-slot:default="slot">
        <DirectoryMenu
          :path="state.path"
          :name="slot.item?.name"
          @done="fetchItems()"
        />
      </template>
    </Breadcrumb>
    <FilesList :path="state.path" :items="state.items" @select="clickItem" />
  </div>
</template>
