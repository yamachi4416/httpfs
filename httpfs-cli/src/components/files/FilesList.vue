<script setup lang="ts">
import { FsItem } from "../../services/FilesService";
import { formatSize, formatDateTime } from "../../functions/fmt";
import { sortable } from "../util/Sortable";

defineProps<{
  items: FsItem[];
}>();

const headers = [
  { key: "name", label: "名前" },
  { key: "lastModified", label: "更新日時" },
  { key: "mimeType", label: "タイプ" },
  { key: "size", label: "サイズ" },
];

const sort = sortable({ key: "name", direction: "asc" });
</script>

<template>
  <figure>
    <table>
      <thead>
        <tr>
          <th v-for="header in sort.headers(headers)" :key="header.key">
            <label @click="header.sort" :data-sort="header.direction">
              {{ header.label }}
            </label>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in sort.sorted(items)"
          :key="item.path"
          :data-selected="item.selected"
        >
          <td class="name">
            <div>
              <input type="checkbox" v-model="item.selected" />
              <router-link v-if="item.directory" :to="item.path"
                >{{ item.name }}
              </router-link>
              <a v-else target="_blank" :href="item.endpoint">{{
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
      </tbody>
    </table>
  </figure>
</template>

<style scoped lang="scss">
th label {
  display: flex;
  cursor: pointer;
  white-space: nowrap;

  &::after {
    content: "";
    display: block;
    width: 2em;
    text-align: center;
  }

  &[data-sort="asc"]::after {
    content: "↓";
  }

  &[data-sort="desc"]::after {
    content: "↑";
  }
}
td.name {
  > div {
    display: flex;
    align-items: center;
    a {
      flex: 1;
    }
  }
}

td.size {
  text-align: right;
}
</style>
