import { ObjectDirective } from 'vue';

interface VAutoFocus extends HTMLElement {
  _v_auto_focus_state: {
    prevFocusEl?: HTMLElement;
  };
}

export const vAutoFocus: ObjectDirective<VAutoFocus> = {
  mounted(el, { value }) {
    el._v_auto_focus_state = {};
    if (value === true || value == null) {
      const prevFocusEl = document.activeElement;
      if (prevFocusEl !== el) {
        if (prevFocusEl instanceof HTMLElement) {
          el._v_auto_focus_state.prevFocusEl = prevFocusEl;
        }
        el.focus();
      }
    }
  },
  updated(el, { value, oldValue }) {
    if (!value === !oldValue) return;
    if (value === true || value == null) {
      if (document.activeElement !== el) {
        el.focus();
      }
    }
  },
  beforeUnmount(el) {
    const prevFocusEl = el._v_auto_focus_state.prevFocusEl;
    if (prevFocusEl && document.contains(prevFocusEl)) {
      prevFocusEl.focus();
    }
    delete el._v_auto_focus_state;
  },
};
