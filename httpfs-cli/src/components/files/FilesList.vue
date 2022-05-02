<script setup lang="ts">
import { FsItem } from "../../services/FilesService";
import { formatSize, formatDateTime } from "../../functions/fmt";
import SortableTable from "../util/SortableTable.vue";

const props = defineProps<{
  items: FsItem[];
}>();

const headers = [
  { key: "name", label: "名前" },
  { key: "lastModified", label: "更新日時" },
  { key: "mimeType", label: "タイプ" },
  { key: "size", label: "サイズ" },
];
</script>

<template>
  <figure>
    <SortableTable
      :items="props.items"
      :headers="headers"
      id-key="path"
      default-key="name"
      default-direction="asc"
      v-slot:default="{ item }: { item: FsItem }"
    >
      <tr :data-selected="item.selected">
        <td class="name">
          <div>
            <input type="checkbox" v-model="item.selected" />
            <router-link v-if="item.directory" class="secondary" :to="item.path"
              >{{ item.name }}
            </router-link>
            <a v-else class="secondary" target="_blank" :href="item.endpoint">{{
              item.name
            }}</a>
          </div>
        </td>
        <td class="lastModified">{{ formatDateTime(item.lastModified) }}</td>
        <td class="mimeType">{{ item.mimeType }}</td>
        <td class="size">
          {{ item.directory ? "" : formatSize(item.size) }}
        </td>
      </tr>
    </SortableTable>
  </figure>
</template>

<style>
tr[data-selected="true"] {
  cursor: pointer;
  background-color: var(--table-row-stripped-background-color);
}

td,
th {
  white-space: nowrap;
}
td.name > div {
  display: flex;
  align-items: center;
  column-gap: 0.375em;
}
td.name a {
  flex: 1;
  text-decoration: none;
}
td.size {
  text-align: right;
}
</style>
