<script setup lang="ts">
import { ref, computed, watch, withDefaults } from 'vue';

const props = withDefaults(
  defineProps<{
    items: Array<{ selected: boolean }>;
  }>(),
  {
    items: () => [],
  }
);

const items = computed(() => props.items?.filter(item => item.selected) || []);
const count = computed(() => items.value.length);
const value = ref(count.value > 0 && count.value === props.items?.length);

watch(
  () => items.value.length,
  length => {
    value.value = length > 0 && length === props.items.length;
  }
);

const changeValue = () => {
  if (props.items.length > 0) {
    props.items?.forEach(item => (item.selected = value.value));
  } else {
    value.value = false;
  }
};

defineExpose({
  items,
  count,
  value,
});
</script>

<template>
  <label>
    <input
      :disabled="props.items.length === 0"
      type="checkbox"
      v-model="value"
      @change="changeValue"
    />
    <slot :items="items" :count="count" :value="value"></slot>
  </label>
</template>
