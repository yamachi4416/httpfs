import { Ref, shallowReactive } from 'vue';

interface Selectable {
  selected: boolean;
}

export interface DragSelectableOptions {
  items: Ref<Selectable[]>;
  nop?: boolean;
}

interface DragSelectable {
  addEvents(el: HTMLElement, item: Selectable): void;
  get working(): boolean;
}

type Events = MouseEvent | PointerEvent;

export function dragSelectable(options: DragSelectableOptions): DragSelectable {
  if (options.nop) {
    return {
      addEvents() {},
      get working() {
        return working();
      },
    };
  }

  const items = options.items;

  const state = shallowReactive<{
    start: Selectable;
    end: Selectable;
    working: boolean;
  }>({
    start: null,
    end: null,
    working: false,
  });

  function working() {
    return state.working;
  }

  function clearState() {
    state.start = null;
    state.end = null;
    state.working = false;
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

  function down(event: Events, item: Selectable) {
    if (!item) return;
    if (event.ctrlKey) {
      state.working = true;
      item.selected = !item.selected;
      const up = () => {
        setTimeout(clearState);
      };
      window.addEventListener('pointerup', up, { once: true });
    } else if (!item.selected) {
      state.working = true;
      state.start = item;
      state.end = item;
      selectItems();
      const up = () => {
        selectItems();
        setTimeout(clearState);
      };
      window.addEventListener('pointerup', up, { once: true });
    }
  }

  function over(event: Events, item: Selectable) {
    if (item && state.start != null) {
      state.end = item;
      selectItems();
    }
  }

  return {
    addEvents(el: HTMLElement, item: Selectable) {
      el.addEventListener('pointerdown', e => down(e, item));
      el.addEventListener('pointerover', e => over(e, item));
    },
    get working() {
      return working();
    },
  };
}
