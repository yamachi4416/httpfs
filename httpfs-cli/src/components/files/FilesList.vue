<script setup lang="ts">
import { toRef } from 'vue';
import { useI18n } from 'vue-i18n';
import { computed } from '@vue/reactivity';
import { formatSize } from '../../functions/fmt';
import { FsItem } from '../../services/files';
import { selectAllable, sortable, SortableOptions } from '../../compositions';

import FileIcon from './fileslist/FileIcon.vue';

const props = withDefaults(
  defineProps<{
    items: FsItem[];
    sortOptions?: SortableOptions<FsItem>;
    headers?: (keyof FsItem)[];
  }>(),
  {
    items: () => [],
    sortOptions: () => ({ key: 'directory', direction: 'desc' }),
    headers: () => ['name', 'lastModified', 'mimeType', 'size'],
  }
);

const emit = defineEmits<{
  (e: 'click', item: FsItem);
  (e: 'dblclick', item: FsItem);
}>();

const { d, t } = useI18n();

const selectAll = selectAllable({ items: toRef(props, 'items') });
const sort = sortable(props.sortOptions);
const headers = computed(() => props.headers.map(key => ({ key })));
const items = computed(() => sort.sorted(props.items));

function format(item: FsItem, key: keyof FsItem) {
  const value = item[key];
  if (value instanceof Date) {
    return d(value, 'long');
  }
  if (key === 'size') {
    return formatSize(value as number);
  }
  return value;
}
</script>

<template>
  <div class="files-list table sticky">
    <ul class="thead">
      <li>
        <span
          v-for="header in sort.headers(headers)"
          :key="header.key"
          :class="header.key"
        >
          <span v-if="header.key === 'selected'">
            <input
              :disabled="!items?.length"
              type="checkbox"
              v-model="selectAll.value"
            />
          </span>
          <span v-else @click="header.sort">
            <span>{{ t(`fsItem.${header.key}`, header.key) }}</span>
            <span class="icons" :data-sort="header.direction"></span>
          </span>
        </span>
      </li>
    </ul>
    <ul class="tbody">
      <li
        v-for="item in items"
        :key="item.path"
        @click="emit('click', item)"
        @dblclick="emit('dblclick', item)"
      >
        <span v-for="col in headers" :class="col.key" :key="col.key">
          <span v-if="col.key === 'selected'" @click.stop @dblclick.stop>
            <input type="checkbox" v-model="item.selected" />
          </span>
          <span v-else-if="col.key === 'name'">
            <FileIcon :item="item" />
            <span>{{ item.name }}</span>
          </span>
          <span v-else>
            <span>{{ format(item, col.key) }}</span>
          </span>
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

    > * {
      cursor: pointer;

      > * > * {
        display: flex;
        column-gap: 0.5em;
        align-items: center;
      }
    }

    .selected {
      width: 1px;
      padding: 0;
      input[type='checkbox'] {
        margin: 0;
      }
    }

    .name {
      width: 50%;
    }
  }

  .tbody {
    .size > * {
      justify-content: flex-end;
    }
  }
}
</style>
