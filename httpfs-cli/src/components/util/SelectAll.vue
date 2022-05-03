<script setup lang="ts">
import { ref, computed, watch } from "vue";

const props = defineProps<{
  items: Array<{ selected: boolean }>;
}>();

const selectedItems = computed(() =>
  props.items.filter((item) => item.selected)
);

const selectedCount = computed(() => selectedItems.value.length);

const selectAll = ref(selectedCount.value === props.items?.length);

watch(
  () => selectedCount.value,
  (count) => {
    selectAll.value = count === props.items?.length;
  }
);

const changeSelectAll = () => {
  props.items?.forEach((item) => (item.selected = selectAll.value));
};
</script>

<template>
  <label v-if="selectedCount > 0">
    <input
      type="checkbox"
      :indeterminate="selectAll"
      v-model="selectAll"
      @change="changeSelectAll"
    />
    {{ selectedCount }} 件選択
  </label>
  <slot :items="selectedItems" :count="selectedCount"></slot>
</template>
