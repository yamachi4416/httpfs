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
import FilePreview from './files/FilePreview.vue';
import FilesList from './files/FilesList.vue';
import FilesMenu, {
  OnActionDone,
  OnProgressAction,
} from './files/FilesMenu.vue';

const shared = injectSharedState();
const route = useRoute();
const router = useRouter();
const { t, te } = useI18n();

const path = ref(Array.from(route.params.path));
const items = ref([] as FsItem[]);
const parentPath = computed(() =>
  path.value.length > 0 ? `/${path.value.slice(0, -1).join('/')}` : ''
);
const error = ref(null as Error);

const itemMap = uniqueKeyMap(items, 'path');
const selectAll = selectAllable({ items });

const filesList = ref<InstanceType<typeof FilesList>>();
const filePreview = ref<InstanceType<typeof FilePreview>>();

async function fetchItems() {
  const selected = new Set(selectAll.items.map(item => item.path));
  try {
    error.value = null;
    items.value = await shared.withLoading(() =>
      fetchDirectoryItems(path.value).then(items => {
        items.forEach(item => {
          item.selected = selected.has(item.path);
        });
        return items;
      })
    );
  } catch (err) {
    items.value = [];
    error.value = err;
  }
}

async function previewItem(item: FsItem) {
  if (item.directory) {
    await router.push(item.path);
  } else {
    const files = filesList.value.items.filter(item => !item.directory);
    const index = files.indexOf(item);
    filePreview.value.open(files, index);
  }
}

const onActionDone: OnActionDone = async mtsts => {
  await fetchItems();
};

const onUploadProgress: OnProgressAction = (item, err?) => {
  if (err) return;
  const cdp = `/${path.value.join('/')}`;
  if (item.parent === cdp) {
    if (itemMap.value.has(item.path)) {
      const old = itemMap.value.get(item.path);
      Object.assign(old, item, { selected: old.selected });
    } else {
      items.value.push(item);
    }
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
  <div class="files">
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
              v-if="!error"
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
        v-if="!error"
        ref="filesList"
        :items="items"
        :headers="['selected', 'name', 'lastModified', 'mimeType', 'size']"
        :drag-select="true"
        @click="previewItem"
      />
      <div v-else class="error">
        <h3>
          {{
            te(`errors.${error.message}`)
              ? t(`errors.${error.message}`)
              : error.message
          }}
        </h3>
      </div>
    </main>
    <FilePreview ref="filePreview" />
  </div>
</template>

<style scoped lang="scss">
.files {
  @include fill-height;

  display: flex;
  flex-direction: column;

  & > header {
    z-index: 9;
    padding-right: var(--spacing);
    padding-left: calc(var(--spacing) / 2);
    background-color: var(--background-color);
    box-shadow: 0 1px 0 var(--muted-border-color);

    & > nav {
      & > ul {
        .back {
          display: flex;
        }
      }
    }
  }

  & > main {
    flex: 1;
    overflow: auto;

    .error {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      padding: var(--spacing);
    }
  }
}
</style>
