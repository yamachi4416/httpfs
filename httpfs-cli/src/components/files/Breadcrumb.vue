<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  path: string[],
}>();

const items = computed(() => {
  const paths = Array.from(props.path);
  return [{ name: "TOP", path: "/" }].concat(
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
  <nav>
    <ul class="breadcrumb">
      <li v-for="item in listItems">
        <router-link :to="item.path">{{ item.name }}</router-link>
      </li>
      <li>
        <slot :item="lastItem"></slot>
      </li>
    </ul>
  </nav>
</template>

<style scoped>
.breadcrumb > li {
  padding-right: 0;
}
.breadcrumb > li:not(:last-child)::after {
  content: "/";
  padding-left: var(--nav-element-spacing-horizontal);
}
</style>
