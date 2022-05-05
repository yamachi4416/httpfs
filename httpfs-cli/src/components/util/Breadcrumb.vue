<script setup lang="ts">
import { computed } from 'vue';
import { vScrollTo } from '../../directives/vScrollTo';

const props = defineProps<{
  path: string[];
}>();

const items = computed(() => {
  const paths = Array.from(props.path);
  return [{ name: '', path: '/' }].concat(
    paths.map((p, i) => ({
      name: p,
      path: `/${paths.slice(0, i + 1).join('/')}`,
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
