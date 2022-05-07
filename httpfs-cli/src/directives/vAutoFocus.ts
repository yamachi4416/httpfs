import { ObjectDirective } from 'vue';

export const vAutoFocus: ObjectDirective<HTMLElement> = {
  mounted(el, { value }) {
    if (value === true || value == null) {
      if (document.activeElement !== el) {
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
};
