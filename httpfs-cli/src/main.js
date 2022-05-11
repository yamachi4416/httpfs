import { createApp } from 'vue';
import router from './plugins/router';
import modals from './plugins/modals';
import i18n from './plugins/i18n'
import App from './App.vue';
import {} from './assets/scss/main.scss';

const app = createApp(App);

app.use(router);
app.use(modals);
app.use(i18n);

app.mount('#app');
