<script setup lang="ts">
import { FsItem } from '../../services/FilesService';
import { formatSize } from '../../functions/fmt';
import { sortable } from '../util/Sortable';
import SelectAll from '../util/SelectAll.vue';
import FileIcon from './fileslist/FileIcon.vue';
import { useI18n } from 'vue-i18n';

defineProps<{
  items: FsItem[];
}>();

const { d, t } = useI18n();

const emit = defineEmits<{
  (e: 'click', item: FsItem): void;
}>();

const headers = ['name', 'lastModified', 'mimeType', 'size'].map(key => ({
  key,
}));

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
            <span>{{ t(`fsItem.${header.key}`, header.key) }}</span>
            <span class="icons" :data-sort="header.direction"></span>
          </span>
        </span>
      </li>
    </ul>
    <ul class="tbody">
      <li
        v-for="item in sort.sorted(items)"
        :key="item.path"
        @dblclick="emit('click', item)"
      >
        <span class="selected" @click.stop @dblclick.stop>
          <input type="checkbox" v-model="item.selected" />
        </span>
        <span class="name">
          <span>
            <FileIcon :item="item" />
            <span>{{ item.name }}</span>
          </span>
        </span>
        <span class="lastModified">
          <span>{{ d(item.lastModified, 'long') }}</span>
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
    user-select: none;

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

      .size > * {
        justify-content: flex-end;
      }
    }
  }
}
</style>
