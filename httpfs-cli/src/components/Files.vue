<script setup lang="ts">
import { onBeforeMount, reactive, computed, watch } from "vue";
import { useRoute } from "vue-router";
import { fetchDirectoryItems, FsItem } from "../services/FilesService";

import Breadcrumb from "./files/Breadcrumb.vue";
import FilesList from "./files/FilesList.vue";
import DirectoryMenu from "./files/DirectoryMenu.vue";
import SelectAll from "./util/SelectAll.vue";

const route = useRoute();

const state = reactive({
  path: Array.from(route.params.path),
  items: [] as FsItem[],
  ready: false,
});

const parentPath = computed(() => {
  const path = state.path;
  if (path.length > 0) {
    return `/${path.slice(0, path.length - 1).join("/")}`;
  }
  return "";
});

watch(
  () => route.params.path,
  async (newPath) => {
    state.ready = false;
    state.path = Array.from(newPath);
    await fetchItems();
    state.ready = true;
  }
);

const fetchItems = async () => {
  state.items = await fetchDirectoryItems(state.path);
};

onBeforeMount(async () => {
  await fetchItems();
  state.ready = true;
});
</script>

<template>
  <nav>
    <ul>
      <li>
        <router-link v-if="parentPath" :to="parentPath" class="secondary"
          >←</router-link
        >
      </li>
      <li>
        <SelectAll v-if="state.ready" :items="state.items" />
      </li>
    </ul>
  </nav>
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
