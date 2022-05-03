<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  path: string[];
}>();

const items = computed(() => {
  const paths = Array.from(props.path);
  return [{ name: "ROOT", path: "/" }].concat(
    paths.map((p, i) => ({
      name: p,
      path: `/${paths.slice(0, i + 1).join("/")}`,
    }))
  );
});

const listItems = computed(() => {
  return items.value.slice(0, items.value.length - 1);
});

const lastItem = computed(() => {
  return items.value[items.value.length - 1] || { name: "" };
});
</script>

<template>
  <ul>
    <li v-for="item in listItems">
      <router-link :to="item.path">{{ item.name }}</router-link>
    </li>
    <li>
      <span>{{ lastItem.name }}</span>
    </li>
  </ul>
</template>

<style scoped lang="scss">
ul {
  li {
    display: flex;
    padding-right: 0;
    &:not(:last-of-type)::after {
      content: "▸";
      color: var(--muted-color);
      padding-left: var(--nav-element-spacing-horizontal);
    }
  }
}
</style>
