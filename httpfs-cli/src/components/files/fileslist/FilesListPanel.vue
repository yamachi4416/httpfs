<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { formatSize } from '../../../functions/fmt';
import { FsItem } from '../../../services/FilesService';
import { sortable, SortableOptions } from '../../util/Sortable';
import FileIcon from './FileIcon.vue';
import SelectAll from '../../util/SelectAll.vue';
import { computed } from '@vue/reactivity';

const props = withDefaults(
  defineProps<{
    items: FsItem[];
    sortOptions?: SortableOptions;
    headers?: ('selected' | 'name' | 'lastModified' | 'mimeType' | 'size')[];
  }>(),
  {
    items: () => [],
    sortOptions: () => ({ key: 'name', direction: 'asc' }),
    headers: () => ['name', 'lastModified', 'mimeType', 'size'],
  }
);

const emit = defineEmits<{
  (e: 'click', item: FsItem);
  (e: 'dblclick', item: FsItem);
}>();

const { d, t } = useI18n();

const headers = computed(() => props.headers.map(key => ({ key })));
const sort = computed(() => sortable(props.sortOptions));
const items = computed(() => sort.value.sorted(props.items));
</script>

<template>
  <div class="files-list table sticky">
    <ul class="thead">
      <li>
        <template v-for="header in sort.headers(headers)">
          <SelectAll
            v-if="header.key === 'selected'"
            :key="`${header.key}-all`"
            :class="header.key"
            :items="items"
          />
          <span
            v-else
            @click="header.sort"
            :key="`${header.key}`"
            :class="header.key"
          >
            <span>
              <span>{{ t(`fsItem.${header.key}`, header.key) }}</span>
              <span class="icons" :data-sort="header.direction"></span>
            </span>
          </span>
        </template>
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
          <span v-else-if="['lastModified'].includes(col.key)">
            <span>{{ d(item[col.key], 'long') }}</span>
          </span>
          <span v-else-if="col.key === 'size'">
            <span>{{ item.directory ? '' : formatSize(item.size) }}</span>
          </span>
          <span v-else>
            <span>{{ item[col.key] }}</span>
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
      input[type="checkbox"] {
        margin: 0;
      }
    }

    .name {
      width: 50%;
    }
  }

  .tbody {
    .selected {
      padding: 0;
    }
    .size > * {
      justify-content: flex-end;
    }
  }
}
</style>
