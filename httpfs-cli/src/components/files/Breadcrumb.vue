<script setup lang="ts">
import { computed } from 'vue';
import { vScrollTo } from '../../directives';

const props = defineProps<{
  path: string[];
}>();

const emit = defineEmits<{
  (e: 'click', item: ReturnType<typeof toItems>[0]): void;
}>();

function toItems(paths: string[]) {
  return [{ name: '', path: '/', encodedPath: '/' }].concat(
    paths.map((p, i) => {
      const cpath = paths.slice(0, i + 1);
      return {
        name: p,
        path: `/${cpath.join('/')}`,
        encodedPath: `/${cpath.map(encodeURIComponent).join('/')}`,
      };
    })
  );
}

const items = computed(() => toItems(Array.from(props.path)));
</script>

<template>
  <nav v-scroll-to="'right'" class="breadcrumb">
    <ul>
      <li v-for="item in items" :key="item.path" class="icons">
        <a href="#" class="secondary" @click.prevent="emit('click', item)">
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

  display: flex;
  padding: calc(var(--spacing) / 2) 0;
  overflow-x: scroll;
  overflow-y: hidden;
  white-space: nowrap;

  & > ul {
    display: flex;
    padding: 0;
    margin: 0;

    li {
      display: flex;
      padding: 0;
      margin: 0;

      &:not(:last-of-type)::after {
        color: var(--muted-color);
        content: 'play_arrow';
      }

      a {
        display: flex;
        text-decoration: none;

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
