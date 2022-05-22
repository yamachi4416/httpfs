import { Ref, shallowReactive } from 'vue';

interface Selectable {
  selected: boolean;
}

interface DragSelectableOptions {
  items: Ref<Selectable[]>;
  nop?: boolean;
}

interface DragSelectable {
  mousedown(item: Selectable): void;
  mouseover(item: Selectable): void;
}

export function dragSelectable(options: DragSelectableOptions): DragSelectable {
  if (options.nop) {
    return {
      mousedown: item => {},
      mouseover: item => {},
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

  function mousedown(item: Selectable) {
    if (!item.selected) {
      state.start = item;
      state.end = item;
      selectItems();
      const mouseup = (event: MouseEvent) => {
        event.preventDefault();
        selectItems();
        clearState();
        window.removeEventListener('mouseup', mouseup);
      };
      window.addEventListener('mouseup', mouseup);
    }
  }

  function mouseover(item: Selectable) {
    if (state.start != null) {
      state.end = item;
      selectItems();
    }
  }

  return {
    mousedown,
    mouseover,
  };
}
