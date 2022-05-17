import { computed, Ref } from 'vue';

export function uniqueKeyMap<T, Key extends keyof T>(items: Ref<T[]>, key: Key) {
  return computed(() => {
    const map = new Map<T[Key], T>();
    items.value.forEach(item => map.set(item[key], item));
    return map;
  });
}
