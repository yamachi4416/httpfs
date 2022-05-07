import { createApp } from 'vue';
import router from './router';
import modals from './plugins/modals';
import App from './App.vue';
import {} from './assets/scss/main.scss';

const app = createApp(App);

app.use(router);
app.use(modals);

app.mount('#app');
