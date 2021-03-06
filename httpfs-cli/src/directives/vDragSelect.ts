import { ObjectDirective } from 'vue';
import { dragSelectable, DragSelectableOptions } from '../compositions';

interface Selectable {
  selected: boolean;
}

type EventValue = Function | Function[];

interface VDragSelectElement extends HTMLElement {
  _vei: { onClick: { value: EventValue } };
  _dsh: { item: Selectable };
}

export function vDragSelectDirective(options: DragSelectableOptions) {
  if (options.nop) {
    return {};
  }

  const dst = dragSelectable(options);

  function patch(el: VDragSelectElement) {
    const invokers = el._vei;
    if (invokers?.onClick?.value) {
      const handlers = Array.isArray(invokers.onClick.value)
        ? invokers.onClick.value
        : [invokers.onClick.value];
      invokers.onClick.value = (event: MouseEvent) => {
        if (!dst.working) {
          handlers.forEach(handler => handler(event));
        }
      };
    }
  }

  return {
    beforeMount(el, { value }) {
      el._dsh = { item: value };
      dst.addEvents(el, () => el._dsh?.item);
    },
    mounted(el, { value }) {
      el._dsh.item = value;
      patch(el);
    },
    updated(el, { value }) {
      el._dsh.item = value;
      patch(el);
    },
  } as ObjectDirective<VDragSelectElement>;
}
