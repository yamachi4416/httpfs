<script setup lang="ts">
import { computed, reactive } from "vue";
import { FsItem } from "../../services/FilesService";

const props = defineProps<{
  items: FsItem[];
}>();

const state = reactive({
  sort: {
    key: "name",
    direction: "asc",
  },
});

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
  let s = size || 0;
  let u = "";
  for (u of ["B", "KB", "MB", "GB"]) {
    if (s < 1024) {
      break;
    }
    s = Math.round(s / 1024);
  }
  return `${s} ${u}`;
};

const items = computed(() => {
  const compare = (key: string, item1: FsItem, item2: FsItem): number => {
    const v1 = item1[key];
    const v2 = item2[key];
    let cp = 0;
    if (v1 == null || v2 == null) {
      cp = v1 == v2 ? 0 : v1 == null ? 1 : -1;
    } else {
      cp = v1 == v2 ? 0 : v1 < v2 ? -1 : 1;
    }
    return direction === "desc" ? cp * -1 : cp;
  };

  const { key, direction } = state.sort;
  return Array.from(props.items).sort((a, b) => {
    const cp = compare(key, a, b);
    if (cp === 0 && key !== "name") {
      return compare("name", a, b);
    }
    return cp;
  });
});

const sortBy = (key: string) => {
  if (state.sort.key === key) {
    state.sort.direction = state.sort.direction === "desc" ? "asc" : "desc";
  } else {
    state.sort.key = key;
    state.sort.direction = "asc";
  }
};

</script>

<template>
  <figure>
    <table>
      <thead>
        <th
          v-for="item in [
            ['name', '名前'],
            ['lastModified', '更新日時'],
            ['mimeType', 'タイプ'],
            ['size', 'サイズ'],
          ]"
        >
          <label
            @click="sortBy(item[0])"
            :class="{ [state.sort.direction]: state.sort.key === item[0] }"
          >
            <span>{{ item[1] }}</span>
          </label>
        </th>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item.path">
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
          <td class="size">
            {{ item.directory ? "" : formatSize(item.size) }}
          </td>
        </tr>
      </tbody>
    </table>
  </figure>
</template>

<style>
th label {
  cursor: pointer;
  display: flex;
}
th label::after {
  content: "";
  display: block;
  width: 1rem;
}
th label.asc::after {
  content: "↓";
}
th label.desc::after {
  content: "↑";
}
td.size {
  text-align: right;
}
</style>
