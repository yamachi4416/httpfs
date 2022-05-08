<script setup lang="ts">
import { FsItem } from '../../services/FilesService';
import FilesListPanel from './fileslist/FilesListPanel.vue';

const props = defineProps<{
  items: FsItem[];
}>();

const emit = defineEmits<{
  (e: 'click', item: FsItem): void;
}>();

</script>

<template>
  <FilesListPanel
    :items="props.items"
    :sort-options="{ key: 'name', direction: 'asc' }"
    :headers="['selected', 'name', 'lastModified', 'mimeType', 'size']"
    @dblclick="(item) => emit('click', item)"
  />
</template>
