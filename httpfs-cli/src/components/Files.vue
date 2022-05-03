<script setup lang="ts">
import { onBeforeMount, reactive, computed, watch } from "vue";
import { useRoute } from "vue-router";
import {
  fetchDirectoryItems,
  deleteItems,
  FsItem,
} from "../services/FilesService";

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

const onUpload = (items: FsItem[]) => {
  const map = new Map<string, FsItem>();
  state.items.forEach((item) => map.set(item.path, item));
  items.forEach((item) =>
    map.has(item.path)
      ? Object.assign(map.get(item.path), item)
      : state.items.push(item)
  );
};

const deleteSelectedItems = async (items: FsItem[]) => {
  await deleteItems(state.path, items).finally(async () => await fetchItems());
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
        <router-link v-if="parentPath" :to="parentPath">←</router-link>
      </li>
      <li>
        <SelectAll v-if="state.ready" :items="state.items">
          <template v-slot="{ items, count }">
            <button v-if="count > 0" @click="deleteSelectedItems(items)">
              削除
            </button>
          </template>
        </SelectAll>
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
          @upload="onUpload"
        />
      </template>
    </Breadcrumb>
    <FilesList :path="state.path" :items="state.items" />
  </div>
</template>
