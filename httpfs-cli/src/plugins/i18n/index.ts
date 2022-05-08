import { createI18n, I18nOptions, IntlDateTimeFormats } from 'vue-i18n';
import messages from '@intlify/vite-plugin-vue-i18n/messages';

const datetimeFormats = {
  ja: {
    long: {
      dateStyle: 'medium',
      timeStyle: 'medium',
    },
  },
} as IntlDateTimeFormats;

export default createI18n({
  legacy: false,
  availableLocales: ['ja'],
  locale: 'ja',
  fallbackLocale: 'ja',
  datetimeFormats,
  messages
} as I18nOptions);
