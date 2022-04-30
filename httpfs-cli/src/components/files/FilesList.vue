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
          <td>{{ item.lastModified }}</td>
          <td>{{ item.mimeType }}</td>
          <td>{{ item.directory ? "" : item.size }}</td>
        </tr>
      </tbody>
    </table>
  </figure>
</template>
