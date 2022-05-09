import { shallowReactive } from 'vue';
import { comparatorKey } from '../../functions/util';

export interface SortableOptions<T> {
  idKey?: keyof T;
  key?: keyof T;
  direction?: 'asc' | 'desc';
}

export function sortable<T>(options: SortableOptions<T>) {
  type Keys = keyof T;

  const state = shallowReactive<SortableOptions<T>>({
    ...{ idKey: options.key },
    ...options,
  });

  function sorted(items: T[]) {
    const { key } = state;
    const dir = state.direction === 'desc' ? -1 : 1;
    const keys = [key];
    if (key !== state.idKey) {
      keys.push(state.idKey);
    }
    const comparator = comparatorKey(...keys);
    return Array.from(items).sort((a, b) => comparator(a, b) * dir) as T[];
  }

  function sortBy(key: Keys) {
    if (state.key === key) {
      state.direction = state.direction === 'asc' ? 'desc' : 'asc';
    } else {
      state.key = key;
      state.direction = 'asc';
    }
  }

  function headers<H extends { key: Keys }>(items: H[]) {
    return items.map(item => ({
      ...item,
      sort() {
        return sortBy(item.key);
      },
      get direction(): null | 'asc' | 'desc' {
        return item.key === state.key ? state.direction : null;
      },
    }));
  }

  return {
    sorted,
    sortBy,
    headers,
    isSortKey(key: Keys) {
      return key === state.key;
    },
    get key() {
      return state.key;
    },
    get direction() {
      return state.direction;
    },
    get options() {
      return state;
    },
  };
}

export default {
  sortable,
};
