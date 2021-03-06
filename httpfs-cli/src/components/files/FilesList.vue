<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { useI18n } from 'vue-i18n';
import { selectAllable, sortable, SortableOptions } from '../../compositions';
import { vDragSelectDirective } from '../../directives';
import { formatSize } from '../../functions/fmt';
import { FsItem } from '../../services/files';
import FileIcon from './fileslist/FileIcon.vue';

const props = withDefaults(
  defineProps<{
    items: FsItem[];
    sortOptions?: SortableOptions<FsItem>;
    headers?: (keyof FsItem)[];
    bindItem?: (item: FsItem) => BindeItemResult;
    dragSelect?: boolean;
  }>(),
  {
    items: () => [],
    sortOptions: () => ({ idKey: 'path', key: 'directory', direction: 'desc' }),
    headers: () => ['name', 'lastModified', 'mimeType', 'size'],
    bindItem: () => undefined,
    dragSelect: false,
  }
);

const emit = defineEmits<{
  (e: 'click', item: FsItem);
  (e: 'dblclick', item: FsItem);
}>();

const { d, t } = useI18n();

const sort = sortable(props.sortOptions);
const headers = computed(() => props.headers.map(key => ({ key })));
const items = computed(() => sort.sorted(props.items));
const selectAll = selectAllable({ items });
const vDragSelect = vDragSelectDirective({ items, nop: !props.dragSelect });

function format(item: FsItem, key: keyof FsItem) {
  const value = item[key];
  if (value instanceof Date) {
    return d(value, 'long');
  }
  if (key === 'size') {
    return item.directory ? '' : formatSize(value as number);
  }
  return value;
}

defineExpose({
  items
})
</script>

<script lang="ts">
type BindeItemResult =
  | {
      class?: 'disabled';
    }
  | undefined;
export type FileListBindItems = (item: FsItem) => BindeItemResult;
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
        v-bind="props.bindItem(item)"
        v-drag-select="item"
        @click="emit('click', item)"
        @dblclick="emit('dblclick', item)"
      >
        <span v-for="col in headers" :class="col.key" :key="col.key">
          <span
            v-if="col.key === 'selected'"
            @click.stop
            @dblclick.stop
            @mousedown.stop
          >
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
  @include table;
  
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

    .disabled {
      opacity: 0.5;
    }
  }
}
</style>
