<script setup lang="ts">
import { computed, onBeforeMount, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import {
  injectSharedState,
  selectAllable,
  uniqueKeyMap,
} from '../compositions';
import { fetchDirectoryItems, FsItem } from '../services/files';
import Breadcrumb from './files/Breadcrumb.vue';
import FilesList from './files/FilesList.vue';
import FilesMenu, {
  OnActionDone,
  OnProgressAction,
} from './files/FilesMenu.vue';

const shared = injectSharedState();
const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const path = ref(Array.from(route.params.path));
const items = ref([] as FsItem[]);
const parentPath = computed(() =>
  path.value.length > 0 ? `/${path.value.slice(0, -1).join('/')}` : ''
);

const itemMap = uniqueKeyMap(items, 'path');
const selectAll = selectAllable({ items });

async function fetchItems() {
  const selected = new Set<string>(selectAll.items.map(item => item.path));
  const newItems = await shared.withLoading(() =>
    fetchDirectoryItems(path.value)
  );
  newItems.forEach(item => {
    item.selected = selected.has(item.path);
  });
  items.value = newItems;
}

async function enterItem(item: FsItem) {
  if (item.directory) {
    await router.push(item.path);
  } else {
    window.open(item.endpoint, '_blank');
  }
}

const onActionDone: OnActionDone = async mtsts => {
  await fetchItems();
};

const onUploadProgress: OnProgressAction = (item, err?) => {
  if (err) return;
  const cdp = `/${path.value.join('/')}`;
  if (item.parent === cdp) {
    itemMap.value.has(item.path)
      ? Object.assign(itemMap.value.get(item.path), item)
      : items.value.push(item);
  }
};

const onMoveProgress: OnProgressAction = (item, err?) => {
  if (err) return;
  const map = itemMap.value;
  if (map.has(item.path)) {
    const selected = map.get(item.path);
    const idx = items.value.indexOf(selected);
    if (idx !== -1) {
      items.value.splice(idx, 1);
    }
  }
};

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
            <FilesMenu
              ref="filesMenu"
              :path="path"
              :items="items"
              @done="onActionDone"
              @upload-progress="onUploadProgress"
              @move-progress="onMoveProgress"
              @cancel="fetchItems"
            />
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
