import { ObjectDirective } from 'vue';

function scrollTo(el: HTMLElement, value: string) {
  const scrollOptions = {} as ScrollToOptions;

  if (value?.includes('top')) {
    scrollOptions.top = 0;
  } else if (value?.includes('bottom')) {
    scrollOptions.top = el.scrollHeight;
  }

  if (value?.includes('left')) {
    scrollOptions.left = 0;
  } else if (value?.includes('right')) {
    scrollOptions.left = el.scrollWidth;
  }

  el.scrollTo(scrollOptions);
}

export const vScrollTo: ObjectDirective<HTMLElement> = {
  mounted(el, { value }) {
    scrollTo(el, value);
  },
  updated(el, { value }) {
    scrollTo(el, value);
  },
};

export default vScrollTo;
