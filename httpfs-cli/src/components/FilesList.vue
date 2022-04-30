<script lang="ts">
import { ref, watch, onBeforeMount, defineComponent } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  fetchDirectoryItems,
  createDirectory,
  downloadFile,
} from "../services/FilesService";
import FileUpload from "./FileUpload.vue";

export default defineComponent({
  components: {
    FileUpload,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const file = ref<HTMLInputElement>();
    const path = ref(Array.from(route.params.path));
    const list = ref([]);
    const dirname = ref("");

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
      file,
      path,
      dirname,
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

      async createDirectory() {
        await createDirectory(path.value, dirname.value).finally(() => {
          dirname.value = "";
        });
        await fetchItems();
      },
    };
  },
});
</script>

<template>
  <div>{{ path.join("/") }}</div>
  <FileUpload :path="path" @done="fetchItems" />
  <div>
    <input type="text" v-model="dirname" />
    <button @click="createDirectory" :disabled="!dirname">フォルダ作成</button>
  </div>
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
