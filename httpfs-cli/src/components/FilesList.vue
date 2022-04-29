<script lang="ts">
import { ref, watch, onBeforeMount, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import FileService from "../services/file-service";

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();

    const file = ref<HTMLInputElement>();
    const path = ref(Array.from(route.params.path));
    const list = ref([]);
    const dirname = ref("");

    onBeforeMount(async () => {
      list.value = await FileService.list(path.value);
    });

    watch(
      () => route.params.path,
      async (newPath) => {
        path.value = Array.from(newPath);
        list.value = await FileService.list(path.value);
      }
    );

    const enterItem = (item: any) => {
      if (item.directory) {
        router.push({
          params: {
            path: [...path.value, item.name],
          },
        });
      } else {
        FileService.download(path.value, item.name);
      }
    };

    const fileUpload = async () => {
      const files = file.value.files;
      await FileService.upload(path.value, files).finally(() => {
        file.value.value = null;
      });
      list.value = await FileService.list(path.value);
    };

    const createDirectory = async () => {
      await FileService.createDirectory(path.value, dirname.value).finally(
        () => {
          dirname.value = "";
        }
      );
      list.value = await FileService.list(path.value);
    };

    return {
      file,
      path,
      list,
      dirname,
      enterItem,
      fileUpload,
      createDirectory,
    };
  },
};
</script>

<template>
  <div>{{ path.join("/") }}</div>
  <div>
    <input ref="file" multiple="true" type="file" @change="fileChange" />
    <button @click="fileUpload">
      アップロード
    </button>
  </div>
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
