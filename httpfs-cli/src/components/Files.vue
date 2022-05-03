<script setup lang="ts">
import { onBeforeMount, reactive, computed, watch, ref } from "vue";
import { useRoute } from "vue-router";
import {
  fetchDirectoryItems,
  deleteItems,
  FsItem,
} from "../services/FilesService";

import FilesList from "./files/FilesList.vue";
import DirectoryMenu from "./files/DirectoryMenu.vue";
import Breadcrumb from "./util/Breadcrumb.vue";
import SelectAll from "./util/SelectAll.vue";

const route = useRoute();

const state = reactive({
  path: Array.from(route.params.path),
  items: [] as FsItem[],
  ready: false,
});

const selectAll = ref<InstanceType<typeof SelectAll>>();
const menuDetaileElement = ref<HTMLDetailsElement>();
const directoryMenu = ref<InstanceType<typeof DirectoryMenu>>();

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
  if (menuDetaileElement?.value) {
    menuDetaileElement.value.open = false;
  }
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
  <header>
    <nav>
      <ul>
        <li v-if="(selectAll?.count || 0) === 0">
          <router-link v-if="parentPath" :to="parentPath">&#x25C0;</router-link>
        </li>
        <li>
          <SelectAll ref="selectAll" v-if="state.ready" :items="state.items" />
        </li>
      </ul>
      <ul></ul>
      <ul>
        <details ref="menuDetaileElement" role="list" dir="rtl">
          <summary aria-haspopup="listbox" role="link">︙</summary>
          <ul role="listbox">
            <li v-if="selectAll?.count > 0">
              <a @click="deleteSelectedItems(selectAll?.items)">削除</a>
            </li>
            <li>
              <a @click="directoryMenu?.openCreateDirectory">フォルダ作成</a>
            </li>
            <li>
              <a @click="directoryMenu?.openFileUpload">ファイル追加</a>
            </li>
          </ul>
        </details>
      </ul>
    </nav>
    <nav class="breadcrumb">
      <Breadcrumb :path="state.path" />
    </nav>
  </header>
  <main class="container-fluid">
    <article>
      <FilesList :path="state.path" :items="state.items" />
    </article>
  </main>
  <DirectoryMenu
    ref="directoryMenu"
    :path="state.path"
    @done="fetchItems"
    @upload="onUpload"
  />
</template>

<style scoped lang="scss">
header {
  position: sticky;
  top: 0;
  padding-right: var(--spacing);
  padding-left: var(--spacing);
  background-color: var(--background-color);
  box-shadow: 0 1px 0 var(--muted-border-color);
}
details {
  text-align: left;
}
</style>
