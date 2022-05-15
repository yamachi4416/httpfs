import { createApp } from 'vue';
import App from './App.vue';
import {} from './assets/scss/main.scss';
import { useSharedState } from './compositions';
import i18n from './plugins/i18n';
import modals from './plugins/modals';
import router from './plugins/router';

const app = createApp(App);

app.use(router);
app.use(modals);
app.use(i18n);
useSharedState(app);

app.mount('#app');
