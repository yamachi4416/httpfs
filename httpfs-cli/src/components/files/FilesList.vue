<script setup lang="ts">
import { ref, watch, onBeforeMount } from "vue";
import { fetchDirectoryItems, FsItem } from "../../services/FilesService";

const props = defineProps<{
  path: string[];
  reload: Boolean;
}>();

const emit = defineEmits<{
  (e: "select", item: FsItem): void;
  (e: "done", items: FsItem[]): void;
}>();

const list = ref([]);

const fetchItems = async () => {
  list.value = await fetchDirectoryItems(props.path);
  emit("done", list.value);
};

onBeforeMount(async () => await fetchItems());

watch(
  () => props.path,
  async () => await fetchItems()
);

watch(
  () => props.reload,
  async (reload) => {
    if (reload) {
      await fetchItems();
    }
  }
);

const formatDateTime = (str: string): string => {
  if (!str) {
    return "";
  }
  const date = new Date(str);
  const yyyy = date.getFullYear();
  const MM = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mm = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}/${MM}/${dd} ${hh}:${mm}:${ss}`;
};

const formatSize = (size: number): string => {
  let s = size;
  let u = "";
  for (u of ["B", "KB", "MB", "GB"]) {
    if (s < 1024) {
      break;
    }
    s = Math.round(s / 1024);
  }
  return `${s} ${u}`
};
</script>

<template>
  <figure>
    <table>
      <thead>
        <th>名前</th>
        <th>更新日時</th>
        <th>タイプ</th>
        <th>サイズ</th>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.name">
          <td>
            <div>
              <a href="#" @click.prevent="$emit('select', item)">{{
                item.name
              }}</a>
            </div>
          </td>
          <td>{{ formatDateTime(item.lastModified) }}</td>
          <td>{{ item.mimeType }}</td>
          <td>{{ item.directory ? "" : formatSize(item.size || 0) }}</td>
        </tr>
      </tbody>
    </table>
  </figure>
</template>
