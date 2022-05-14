<script setup lang="ts">
import { onBeforeMount, computed, watch, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';

import { fetchDirectoryItems, deleteItems, FsItem } from '../services/files';
import { selectAllable } from '../compositions';

import Modal from './ui/Modal.vue';
import FilesList from './files/FilesList.vue';
import Breadcrumb from './files/Breadcrumb.vue';
import FileUpload from './files/actions/FileUpload.vue';
import CreateDirectory from './files/actions/CreateDirectory.vue';
import MoveItems from './files/actions/MoveItems.vue';

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const path = ref(Array.from(route.params.path));
const items = ref([] as FsItem[]);
const parentPath = computed(() =>
  path.value.length > 0 ? `/${path.value.slice(0, -1).join('/')}` : ''
);

const openMenu = ref(false);
const selectAll = selectAllable({ items });
const createDirectory = ref<InstanceType<typeof CreateDirectory>>();
const fileUpload = ref<InstanceType<typeof FileUpload>>();
const moveItems = ref<InstanceType<typeof MoveItems>>();

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

function onUpload(uploads: FsItem[], failures: FsItem[], err?: Error) {
  console.log(uploads, failures, err);
  if (err) {
    // TODO
    return;
  }

  const cdp = parentPath.value || '/';
  const map = new Map<string, FsItem>();
  items.value.forEach((item: FsItem) => map.set(item.path, item));
  uploads.forEach(item => {
    if (item.parent === cdp) {
      map.has(item.path)
        ? Object.assign(map.get(item.path), item)
        : items.value.push(item);
    }
  });
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

const menuItems = [
  {
    name: 'deleteFiles',
    get show() {
      return selectAll.any;
    },
    click: () => deleteSelectedItems(selectAll.items),
  },
  {
    name: 'moveItems',
    get show() {
      return selectAll.any;
    },
    click: () => moveItems.value?.open(path.value, selectAll.items),
  },
  {
    name: 'createDirectory',
    get show() {
      return !selectAll.any;
    },
    click: () => createDirectory.value?.open(),
  },
  {
    name: 'uploadFiles',
    get show() {
      return !selectAll.any;
    },
    click: () => fileUpload.value?.open(),
  },
];
</script>

<template>
  <div class="files fill-height">
    <header>
      <nav>
        <ul>
          <li v-if="!selectAll.any">
            <router-link
              class="back secondary"
              v-if="parentPath"
              :to="parentPath"
            >
              <span class="icon">arrow_back_ios_new</span>
            </router-link>
          </li>
          <li>
            <label v-show="selectAll.any">
              <input type="checkbox" v-model="selectAll.value" />
              {{ t('messages.selectedCount', [selectAll.count]) }}
            </label>
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
      <Breadcrumb :path="path" @click="p => router.push(p)" />
    </header>

    <main>
      <FilesList
        :items="items"
        :headers="['selected', 'name', 'lastModified', 'mimeType', 'size']"
        @click="item => item.directory && enterItem(item)"
        @dblclick="item => enterItem(item)"
      />
    </main>

    <Teleport to="body">
      <FileUpload
        ref="fileUpload"
        :path="path"
        @close="fetchItems"
        @done="fetchItems"
        @upload="onUpload"
      />
      <CreateDirectory ref="createDirectory" :path="path" @done="fetchItems" />
      <MoveItems ref="moveItems" @close="fetchItems" @done="fetchItems" />
      <Modal :show="openMenu" transision="scale" @close="openMenu = false">
        <ul class="card" :class="$style.menu">
          <li v-for="item in menuItems.filter(i => i.show)" :key="item.name">
            <a
              href="#"
              class="secondary"
              @click.prevent="(openMenu = false), item.click()"
            >
              {{ t(`actions.${item.name}`) }}
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
