<script setup lang="ts">
import { ref, computed, watch } from "vue";

const props = defineProps<{
  items: Array<{ selected: boolean }>;
}>();

const items = computed(() => props.items.filter((item) => item.selected));
const count = computed(() => items.value.length);
const value = ref(count.value === props.items?.length);

watch(
  () => count.value,
  (count) => {
    value.value = count === props.items?.length;
  }
);

const changeValue = () => {
  props.items?.forEach((item) => (item.selected = value.value));
};

defineExpose({
  items,
  count,
  value,
});
</script>

<template>
  <label v-if="count > 0">
    <input
      type="checkbox"
      :indeterminate="value"
      v-model="value"
      @change="changeValue"
    />
    {{ count }} 件選択
  </label>
</template>
