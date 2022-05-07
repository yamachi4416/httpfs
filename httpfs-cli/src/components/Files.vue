<script setup lang="ts">
import { onBeforeMount, reactive, computed, watch, ref } from 'vue';
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

const route = useRoute();
const router = useRouter();

const state = reactive({
  path: Array.from(route.params.path),
  items: [] as FsItem[],
  parentPath: computed(() => {
    const { path } = state;
    if (path.length > 0) {
      return `/${path.slice(0, path.length - 1).join('/')}`;
    }
    return '';
  }),
});

const selectAll = ref<InstanceType<typeof SelectAll>>();
const menuDetaileElement = ref<HTMLDetailsElement>();
const directoryMenu = ref<InstanceType<typeof DirectoryMenu>>();

async function fetchItems() {
  if (menuDetaileElement?.value) {
    menuDetaileElement.value.open = false;
  }
  state.items = await fetchDirectoryItems(state.path);
}

async function enterItem(item: FsItem) {
  if (item.directory) {
    await router.push(item.path);
  } else {
    window.open(item.endpoint, '_blank');
  }
}

async function onUpload(items: FsItem[]) {
  const map = new Map<string, FsItem>();
  state.items.forEach((item: FsItem) => map.set(item.path, item));
  items.forEach(item =>
    map.has(item.path)
      ? Object.assign(map.get(item.path), item)
      : state.items.push(item)
  );
}

async function deleteSelectedItems(items: FsItem[]) {
  await deleteItems(state.path, items).finally(async () => await fetchItems());
}

watch(
  () => route.params.path,
  async newPath => {
    state.path = Array.from(newPath);
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
              v-if="state.parentPath"
              :to="state.parentPath"
            >
              <span class="icon">arrow_back_ios_new</span>
            </router-link>
          </li>
          <li>
            <SelectAll
              v-show="(selectAll?.count || 0) > 0"
              ref="selectAll"
              :items="state.items"
              v-slot="{ count }"
            >
              {{ count }} 件選択
            </SelectAll>
          </li>
        </ul>
        <ul></ul>
        <ul>
          <details ref="menuDetaileElement" role="list" dir="rtl">
            <summary role="link" class="secondary">
              <span class="icon">more_vert</span>
            </summary>
            <ul>
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
      <Breadcrumb :path="state.path" />
    </header>

    <main>
      <FilesList :path="state.path" :items="state.items" @click="enterItem" />
    </main>

    <DirectoryMenu
      ref="directoryMenu"
      :path="state.path"
      @done="fetchItems"
      @upload="onUpload"
    />
  </div>
</template>

<style scoped lang="scss">
.files {
  display: flex;
  flex-direction: column;

  > header {
    z-index: 9;
    padding-right: calc(var(--spacing) / 2);
    padding-left: calc(var(--spacing) / 2);
    background-color: var(--background-color);
    box-shadow: 0 1px 0 var(--muted-border-color);

    > nav {
      > ul {
        .back {
          display: flex;
        }

        > details {
          text-align: left;
          margin-right: var(--spacing);
          margin-bottom: 0;
          a {
            cursor: pointer;
          }

          > summary {
            cursor: pointer;
            &::-webkit-details-marker {
              display: none;
            }
            &::after {
              display: none;
            }
          }
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
