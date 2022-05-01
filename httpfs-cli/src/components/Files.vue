<script setup lang="ts">
import { onBeforeMount, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";
import {
  fetchDirectoryItems,
  FsItem,
} from "../services/FilesService";

import Breadcrumb from "./files/Breadcrumb.vue";
import FilesList from "./files/FilesList.vue";
import DirectoryMenu from "./files/DirectoryMenu.vue";

const route = useRoute();

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
    <FilesList :path="state.path" :items="state.items" />
  </div>
</template>
