<script setup lang="ts">
import { ref, computed } from "vue";

interface IMenuItem {
  name: string;
  click: () => void;
}

const props = defineProps<{
  path: string[],
  menues: IMenuItem[]
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

const menuDetails = ref<HTMLDetailsElement>();

const clickMenu = (menu: { click: () => void }) => {
  menuDetails.value.open = false;
  menu.click();
};
</script>

<template>
  <nav>
    <ul class="breadcrumb">
      <li v-for="item in listItems">
        <router-link :to="item.path">{{ item.name }}</router-link>
      </li>
      <li>
        <details ref="menuDetails" role="list">
          <summary aria-haspopup="listbox" role="link">
            {{ lastItem.name }}
          </summary>
          <ul role="listbox">
            <li v-for="menu in menues">
              <a @click.prevent="clickMenu(menu)">{{ menu.name }}</a>
            </li>
          </ul>
        </details>
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
