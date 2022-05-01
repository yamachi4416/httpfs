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
      v-slot:default="{ item }"
    >
      <td class="name">
        <div v-if="item.directory">
          <router-link :to="item.path">{{ item.name }}</router-link>
        </div>
        <div v-else>
          <a :href="item.endpoint" target="_blank">{{ item.name }}</a>
        </div>
      </td>
      <td class="lastModified">{{ formatDateTime(item.lastModified) }}</td>
      <td class="mimeType">{{ item.mimeType }}</td>
      <td class="size">{{ item.directory ? "" : formatSize(item.size) }}</td>
    </SortableTable>
  </figure>
</template>

<style>
td.size {
  text-align: right;
}
</style>
