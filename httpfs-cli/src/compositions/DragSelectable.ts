import { Ref, shallowReactive } from 'vue';

interface Selectable {
  selected: boolean;
}

export interface DragSelectableOptions {
  items: Ref<Selectable[]>;
  nop?: boolean;
}

interface DragSelectable {
  mousedown(event: MouseEvent, item: Selectable): void;
  mouseover(event: MouseEvent, item: Selectable): void;
  get working(): boolean;
}

export function dragSelectable(options: DragSelectableOptions): DragSelectable {
  if (options.nop) {
    return {
      mousedown: () => {},
      mouseover: () => {},
      get working() {
        return working();
      },
    };
  }

  const items = options.items;

  const state = shallowReactive<{
    start: Selectable;
    end: Selectable;
  }>({
    start: null,
    end: null,
  });

  function working() {
    return state.start != null;
  }

  function clearState() {
    state.start = null;
    state.end = null;
  }

  function selectItems() {
    const sIdx = items.value.indexOf(state.start);
    const eIdx = items.value.indexOf(state.end);
    const start = Math.min(sIdx, eIdx);
    const end = Math.max(sIdx, eIdx);
    items.value.forEach((item, idx) => {
      item.selected = start <= idx && idx <= end;
    });
  }

  function mousedown(event: MouseEvent, item: Selectable) {
    if (item && !item.selected) {
      state.start = item;
      state.end = item;
      selectItems();
      const mouseup = (event: MouseEvent) => {
        selectItems();
        setTimeout(clearState);
      };
      window.addEventListener('mouseup', mouseup, { once: true });
    }
  }

  function mouseover(event: MouseEvent, item: Selectable) {
    if (item && state.start != null) {
      state.end = item;
      selectItems();
    }
  }

  return {
    mousedown,
    mouseover,
    get working() {
      return working();
    },
  };
}
