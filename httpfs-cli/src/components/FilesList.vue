<script lang="ts">
import { ref, watch, onBeforeMount, defineComponent } from "vue";
import { useRouter, useRoute } from "vue-router";
import { fetchDirectoryItems, downloadFile } from "../services/FilesService";
import FileUpload from "./FileUpload.vue";
import CreateDirectory from "./CreateDirectory.vue";

export default defineComponent({
  components: {
    FileUpload,
    CreateDirectory,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const path = ref(Array.from(route.params.path));
    const list = ref([]);

    const fetchItems = async () => {
      list.value = await fetchDirectoryItems(path.value);
    };

    onBeforeMount(async () => {
      await fetchItems();
    });

    watch(
      () => route.params.path,
      async (newPath) => {
        path.value = Array.from(newPath);
        await fetchItems();
      }
    );

    return {
      path,
      list,

      fetchItems,
      enterItem(item: any) {
        if (item.directory) {
          router.push({
            params: {
              path: [...path.value, item.name],
            },
          });
        } else {
          downloadFile(path.value, item.name);
        }
      },
    };
  },
});
</script>

<template>
  <div>{{ path.join("/") }}</div>
  <FileUpload :path="path" @done="fetchItems" />
  <CreateDirectory :path="path" @done="fetchItems" />
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
          <a href="#" @click.prevent="enterItem(item)">{{ item.name }}</a>
        </td>
        <td>{{ item.lastModified }}</td>
        <td>{{ item.mimeType }}</td>
        <td>{{ item.directory ? "" : item.size }}</td>
      </tr>
    </tbody>
  </table>
</template>
