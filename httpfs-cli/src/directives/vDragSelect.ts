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

  function patch(el: VDragSelectElement, item: Selectable) {
    el._dsh.item = item;
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
      el.addEventListener('mousedown', e => dst.mousedown(e, el._dsh.item));
      el.addEventListener('mouseover', e => dst.mouseover(e, el._dsh.item));
    },
    mounted(el, { value }) {
      patch(el, value);
    },
    updated(el, { value }) {
      patch(el, value);
    },
  } as ObjectDirective<VDragSelectElement>;
}
