<script setup lang="ts">
import { computed } from "vue";
import { vScrollTo } from "../../directives/vScrollTo";

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
  <nav v-scroll-to="'right'" class="breadcrumb">
    <ul>
      <li v-for="item in items" :key="item.path" class="icons">
        <router-link :to="item.path" class="secondary">
          <span v-if="item.path === '/'" class="icon">home</span>
          <span v-else>{{ item.name }}</span>
        </router-link>
      </li>
    </ul>
  </nav>
</template>

<style scoped lang="scss">
.breadcrumb {
  overflow-x: scroll;
  white-space: nowrap;
  padding: calc(var(--nav-element-spacing-vertical) / 2)
    var(--nav-element-spacing-horizontal);

  > ul {
    li {
      display: flex;
      padding: 0;

      &:not(:last-of-type)::after {
        content: "play_arrow";
        color: var(--muted-color);
      }

      a {
        display: flex;

        &[aria-current="page"] {
          color: var(--secondary) !important;
        }

        &:focus {
          background-color: inherit;
        }
      }
    }
  }
}
</style>
