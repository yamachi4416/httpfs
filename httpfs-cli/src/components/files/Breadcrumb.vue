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
      path: `/${paths
        .slice(0, i + 1)
        .map(p => encodeURIComponent(p))
        .join('/')}`,
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

<style scoped lang="scss">
.breadcrumb {
  --scrollbar-width: 8px;
  --scrollbar-height: 5px;

  &::-webkit-scrollbar {
    width: var(--scrollbar-width);
    height: var(--scrollbar-height);
  }

  &::-webkit-scrollbar-thumb {
    background-color: transparent;
    border-radius: var(--scrollbar-height);
  }

  &:hover,
  &:focus {
    &::-webkit-scrollbar-thumb {
      background-color: var(--muted-border-color);
    }
  }

  padding: calc(var(--nav-element-spacing-vertical) / 2)
    var(--nav-element-spacing-horizontal)
    calc(var(--nav-element-spacing-vertical) / 2 - var(--scrollbar-height))
    var(--nav-element-spacing-horizontal);
  overflow-x: scroll;
  overflow-y: hidden;
  white-space: nowrap;

  > ul {
    li {
      display: flex;
      padding: 0;

      &:not(:last-of-type)::after {
        color: var(--muted-color);
        content: 'play_arrow';
      }

      a {
        display: flex;

        &[aria-current='page'] {
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
