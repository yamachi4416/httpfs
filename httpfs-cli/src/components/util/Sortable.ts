import { reactive } from "vue";
import { comparatorKey } from "../../functions/util";

interface SortableOptions {
  idKey?: string;
  key?: string;
  direction?: "asc" | "desc";
}

export function sortable(options: SortableOptions) {
  const state = reactive<SortableOptions>({
    ...{ idKey: options.key },
    ...options,
  });

  function sorted<T>(items: T[]) {
    const key = state.key;
    const dir = state.direction === "desc" ? -1 : 1;
    const keys = [key];
    if (key !== state.idKey) {
      keys.push(state.idKey);
    }
    const comparator = comparatorKey(...keys);
    return Array.from(items).sort((a, b) => {
      return comparator(a, b) * dir;
    }) as T[];
  }

  function sortBy(key: string) {
    if (state.key === key) {
      state.direction = state.direction === "asc" ? "desc" : "asc";
    } else {
      state.key = key;
      state.direction = "asc";
    }
  }

  function headers<T extends { key: string }>(items: T[]) {
    return items.map((item) => ({
      ...item,
      sort() {
        return sortBy(item.key);
      },
      get direction(): null | "asc" | "desc" {
        return item.key === state.key ? state.direction : null;
      },
    }));
  }

  return {
    sorted,
    sortBy,
    headers,
    isSortKey(key: string) {
      return key === state.key;
    },
    get key() {
      return state.key;
    },
    get direction() {
      return state.direction;
    },
  };
}

export default {
  sortable,
};
