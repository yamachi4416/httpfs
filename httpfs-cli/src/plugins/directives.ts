import { Plugin } from 'vue';
import { vAutoFocus } from '../directives';

export default {
  install(app) {
    app.directive('auto-focus', vAutoFocus);
  },
} as Plugin;
