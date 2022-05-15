import { App, inject, InjectionKey, reactive } from 'vue';

function getSharedState() {
  const state = reactive({
    loading: false,
  });

  return {
    get loading() {
      return state.loading;
    },
    async withLoading(func: () => any) {
      try {
        state.loading = true;
        await func();
      } finally {
        state.loading = false;
      }
    },
  };
}

type SharedStateType = ReturnType<typeof getSharedState>;
const SharedStateKey: InjectionKey<SharedStateType> = Symbol('SharedState');

export function useSharedState(app: App<any>) {
  app.provide(SharedStateKey, getSharedState());
}

export function injectSharedState() {
  return inject(SharedStateKey);
}
