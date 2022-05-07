<script setup lang="ts">
import { onBeforeMount, computed, watch, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  fetchDirectoryItems,
  deleteItems,
  FsItem,
} from '../services/FilesService';

import FilesList from './files/FilesList.vue';
import DirectoryMenu from './files/DirectoryMenu.vue';
import Breadcrumb from './files/Breadcrumb.vue';
import SelectAll from './util/SelectAll.vue';
import Modal from './util/Modal.vue';

const route = useRoute();
const router = useRouter();

const path = ref(Array.from(route.params.path));
const items = ref([] as FsItem[]);
const parentPath = computed(() => {
  if (path.value.length > 0) {
    return `/${path.value.slice(0, path.value.length - 1).join('/')}`;
  }
  return '';
});
const openMenu = ref(false);

const selectAll = ref<InstanceType<typeof SelectAll>>();
const directoryMenu = ref<InstanceType<typeof DirectoryMenu>>();

async function fetchItems() {
  openMenu.value = false;
  items.value = await fetchDirectoryItems(path.value);
}

async function enterItem(item: FsItem) {
  if (item.directory) {
    await router.push(item.path);
  } else {
    window.open(item.endpoint, '_blank');
  }
}

async function onUpload(uploads: FsItem[]) {
  const map = new Map<string, FsItem>();
  items.value.forEach((item: FsItem) => map.set(item.path, item));
  uploads.forEach(item =>
    map.has(item.path)
      ? Object.assign(map.get(item.path), item)
      : items.value.push(item)
  );
}

async function deleteSelectedItems(deletes: FsItem[]) {
  await deleteItems(path.value, deletes).finally(
    async () => await fetchItems()
  );
}

watch(
  () => route.params.path,
  async newPath => {
    path.value = Array.from(newPath);
    await fetchItems();
  }
);

onBeforeMount(async () => await fetchItems());
</script>

<template>
  <div class="files fill-height">
    <header>
      <nav>
        <ul>
          <li v-if="(selectAll?.count || 0) === 0">
            <router-link
              class="back secondary"
              v-if="parentPath"
              :to="parentPath"
            >
              <span class="icon">arrow_back_ios_new</span>
            </router-link>
          </li>
          <li>
            <SelectAll
              v-show="(selectAll?.count || 0) > 0"
              ref="selectAll"
              :items="items"
              v-slot="{ count }"
            >
              {{ count }} 件選択
            </SelectAll>
          </li>
        </ul>
        <ul></ul>
        <ul>
          <li>
            <a href="#" class="icon secondary" @click.prevent="openMenu = true">
              more_vert
            </a>
          </li>
        </ul>
      </nav>
      <Breadcrumb :path="path" />
    </header>

    <main>
      <FilesList :path="path" :items="items" @click="enterItem" />
    </main>

    <DirectoryMenu
      ref="directoryMenu"
      :path="path"
      @done="fetchItems"
      @upload="onUpload"
    />

    <Teleport to="body">
      <Modal :show="openMenu" transision="scale" @close="openMenu = false">
        <ul class="card" :class="$style.menu">
          <li v-if="selectAll?.count > 0">
            <a
              href="#"
              class="secondary"
              @click.prevent="deleteSelectedItems(selectAll?.items)"
            >
              削除
            </a>
          </li>
          <li v-if="selectAll?.count == 0">
            <a
              href="#"
              class="secondary"
              @click.prevent="directoryMenu?.openCreateDirectory()"
            >
              フォルダ作成
            </a>
          </li>
          <li v-if="selectAll?.count == 0">
            <a
              href="#"
              class="secondary"
              @click.prevent="directoryMenu?.openFileUpload()"
            >
              ファイル追加
            </a>
          </li>
        </ul>
      </Modal>
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
.files {
  display: flex;
  flex-direction: column;

  > header {
    z-index: 9;
    padding-right: var(--spacing);
    padding-left: calc(var(--spacing) / 2);
    background-color: var(--background-color);
    box-shadow: 0 1px 0 var(--muted-border-color);

    > nav {
      > ul {
        .back {
          display: flex;
        }
      }
    }
  }

  > main {
    flex: 1;
    overflow: auto;
  }
}
</style>

<style module lang="scss">
.menu {
  --block-spacing-vertical: 1rem;

  position: absolute;
  top: var(--block-spacing-vertical);
  right: var(--block-spacing-horizontal);
  transform-origin: top right;
  border-radius: calc(var(--border-radius) * 2);

  li,
  a {
    width: 100%;
    white-space: nowrap;
    margin: 0;
  }

  li {
    padding: 0;
  }

  a {
    text-decoration: none;
  }
}
</style>
