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
  <div class="files-list">
    <ul class="headers">
      <li>
        <SelectAll class="selected" :items="items" :hide-non-selected="false" />
        <span
          v-for="header in sort.headers(headers)"
          :key="header.key"
          :class="header.key"
        >
          <a
            href="#"
            class="secondary icons"
            @click="header.sort"
            :data-sort="header.direction"
          >
            {{ header.label }}
          </a>
        </span>
      </li>
    </ul>
    <ul class="items">
      <li v-for="item in sort.sorted(items)" :key="item.path">
        <span class="selected">
          <input type="checkbox" v-model="item.selected" />
        </span>
        <span class="name">
          <a href="#" class="secondary" @click.prevent="emit('click', item)">
            <FileIcon :item="item" />
            <span>{{ item.name }}</span>
          </a>
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
  display: table;
  min-width: 100%;

  .headers,
  .items {
    display: table-row-group;
    width: 100%;

    li {
      display: table-row;
      list-style: none;

      > * {
        display: table-cell;
        vertical-align: middle;
        background-color: var(--background-color);

        > * {
          padding: calc(var(--spacing) / 2);
        }

        &:first-child {
          padding-left: calc(var(--spacing) / 2);
        }

        &:last-child {
          padding-left: calc(var(--spacing) / 2);
        }

        &.selected {
          width: 1px;
        }

        &.name {
          width: 50%;
          a {
            display: flex;
            align-items: center;
            column-gap: 0.5em;
            text-decoration: none;
          }
        }
      }
    }
  }

  .headers {
    position: sticky;
    top: 0;
    white-space: nowrap;
    li {
      > * {
        border-bottom: var(--border-width) solid var(--table-border-color);
      }
    }
    a {
      display: flex;
      align-items: center;
      text-decoration: none;
      &:focus {
        background: inherit;
      }
      &::after {
        content: '';
        font-size: 1em;
        width: 1em;
        text-align: center;
      }
      &[data-sort='asc']::after {
        content: 'south';
      }
      &[data-sort='desc']::after {
        content: 'north';
      }
    }
  }

  .items {
    .lastModified,
    .mimeType,
    .size {
      white-space: nowrap;
    }
    .size {
      text-align: right;
    }
  }
}
</style>
