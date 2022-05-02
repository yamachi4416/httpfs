<script setup lang="ts">
import { computed } from "@vue/reactivity";
import { reactive } from "vue";
import { comparatorKey } from "../../functions/util";
import { FsItem } from "../../services/FilesService";

const props = defineProps<{
  items: Array<{}>;
  idKey: string;
  defaultKey?: string;
  defaultDirection?: "asc" | "desc";
  headers: Array<{ key: string; label: string }>;
}>();

const state = reactive({
  key: props.defaultKey || "",
  direction: props.defaultDirection || "",
});

const items = computed(() => {
  const key = state.key;
  const dir = state.direction === "desc" ? -1 : 1;
  const keys = [key];
  if (key !== props.idKey) {
    keys.push(props.idKey);
  }
  const comparator = comparatorKey(...keys);
  return Array.from(props.items).sort((a, b) => {
    return comparator(a, b) * dir;
  }) as FsItem[];
});

const sortBy = (key: string) => {
  if (state.key === key) {
    state.direction = state.direction === "desc" ? "asc" : "desc";
  } else {
    state.key = key;
    state.direction = "asc";
  }
};
</script>

<template>
  <table>
    <thead>
      <th v-for="header in props.headers" :key="header.key">
        <label
          @click="sortBy(header.key)"
          :class="{ [state.direction]: state.key === header.key }"
        >
          <span>{{ header.label }}</span>
        </label>
      </th>
    </thead>
    <tbody>
      <slot v-for="item in items" :key="item[props.idKey]" :item="item"></slot>
    </tbody>
  </table>
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
</style>
