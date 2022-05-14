<script setup lang="ts">
import { computed } from 'vue';
import { vScrollTo } from '../../directives';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'click', path: string): void;
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
        <a href="#" class="secondary" @click.prevent="emit('click', item.path)">
          <span v-if="item.path === '/'" class="icon">home</span>
          <span v-else>{{ item.name }}</span>
        </a>
      </li>
    </ul>
  </nav>
</template>
