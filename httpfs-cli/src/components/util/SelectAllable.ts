import { computed, Ref, ref, watch } from 'vue';

interface Selectable {
  selected: boolean;
}

interface SelectableOptions {
  items: Ref<Selectable[]>;
}

export function selectAllable(options: SelectableOptions) {
  const value = ref(false);
  const items = options.items;
  const selectedItems = computed(
    () => items.value?.filter(item => item.selected) ?? []
  );
  const count = computed(() => selectedItems.value.length);
  const any = computed(() => items.value.some(item => item.selected));

  watch(
    () => count.value,
    count => {
      value.value = count > 0 && count === items.value.length;
    }
  );

  return {
    get value() {
      return value.value;
    },
    set value(val) {
      items.value.forEach(item => {
        item.selected = val;
      });
      value.value = val;
    },
    get items() {
      return selectedItems.value;
    },
    get any() {
      return any.value;
    },
    get count() {
      return count.value;
    },
  };
}
