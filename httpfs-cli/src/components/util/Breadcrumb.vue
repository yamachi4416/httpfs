<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  path: string[];
}>();

const items = computed(() => {
  const paths = Array.from(props.path);
  return [{ name: "", path: "/" }].concat(
    paths.map((p, i) => ({
      name: p,
      path: `/${paths.slice(0, i + 1).join("/")}`,
    }))
  );
});
</script>

<template>
  <nav :class="$style.breadcrumb">
    <ul>
      <li v-for="item in items" :key="item.path">
        <router-link :to="item.path">{{ item.name }}</router-link>
      </li>
    </ul>
  </nav>
</template>

<style module lang="scss">
.breadcrumb {
  > ul {
    li {
      display: flex;
      padding-right: 0;

      &:not(:last-of-type)::after {
        content: "▸";
        color: var(--muted-color);
        padding-left: var(--nav-element-spacing-horizontal);
      }

      &:first-of-type {
        a::before {
          content: "ROOT";
        }
      }

      a[aria-current="page"] {
        color: var(--secondary) !important;
      }
    }
  }
}
</style>
