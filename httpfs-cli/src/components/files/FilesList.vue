<script setup lang="ts">
import { FsItem } from '../../services/FilesService';
import { formatSize, formatDateTime } from '../../functions/fmt';
import { sortable } from '../util/Sortable';
import SelectAll from '../util/SelectAll.vue';
import FileIcon from './fileslist/FileIcon.vue';

defineProps<{
  items: FsItem[];
}>();

const emit = defineEmits<{
  (e: 'click', item: FsItem): void;
}>();

const headers = [
  { key: 'name', label: '名前' },
  { key: 'lastModified', label: '更新日時' },
  { key: 'mimeType', label: 'タイプ' },
  { key: 'size', label: 'サイズ' },
];

const sort = sortable({ key: 'name', direction: 'asc' });
</script>

<template>
  <div class="files-list table sticky">
    <ul class="thead">
      <li>
        <SelectAll class="selected" :items="items" />
        <span
          v-for="header in sort.headers(headers)"
          :key="header.key"
          :class="header.key"
        >
          <span @click="header.sort">
            <span>{{ header.label }}</span>
            <span class="icons" :data-sort="header.direction"></span>
          </span>
        </span>
      </li>
    </ul>
    <ul class="tbody">
      <li
        v-for="item in sort.sorted(items)"
        :key="item.path"
        @click="emit('click', item)"
      >
        <span class="selected" @click.stop>
          <input type="checkbox" v-model="item.selected" />
        </span>
        <span class="name">
          <span>
            <FileIcon :item="item" />
            <span>{{ item.name }}</span>
          </span>
        </span>
        <span class="lastModified">
          <span>{{ formatDateTime(item.lastModified) }}</span>
        </span>
        <span class="mimeType">
          <span>{{ item.mimeType }}</span>
        </span>
        <span class="size">
          <span>{{ item.directory ? '' : formatSize(item.size) }}</span>
        </span>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
.files-list {
  .thead,
  .tbody {
    white-space: nowrap;

    * > * > * {
      display: flex;
      column-gap: 0.5em;
      align-items: center;
    }

    .selected {
      width: 1px;
    }

    .name {
      width: 50%;
    }
  }

  .thead {
    > * > * > * {
      cursor: pointer;
    }
  }

  .tbody {
    > * {
      cursor: pointer;

      .name {
        white-space: initial;
      }

      .size > * {
        justify-content: flex-end;
      }
    }
  }
}
</style>
