import { Plugin } from 'vue';
import { Router } from 'vue-router';

export interface Modal {
  close(): boolean;
  add(): void;
  remove(): void;
}

const modals = [] as Modal[];

function addModal(modal: Modal) {
  modals.push(modal);
}

function removeModal(modal: Modal) {
  const idx = modals.findIndex(m => m === modal);
  if (idx !== -1) {
    modals.splice(idx, 1);
  }
}

function closeModal(): boolean {
  const modal = modals.pop();
  if (modal) {
    return modal.close();
  }
  return false;
}

export function defineModal(close: () => boolean): Modal {
  return {
    close,
    add() {
      addModal(this);
    },
    remove() {
      removeModal(this);
    },
  };
}

export default {
  install(app) {
    const router = app.config.globalProperties.$router as Router;

    router.beforeEach((to, from, next) => {
      if (closeModal()) {
        next(false);
      } else {
        next();
      }
    });

    window?.addEventListener('keydown', function (event) {
      if (event.key === 'Escape') {
        if (closeModal()) {
          event.preventDefault();
        }
      }
    });
  },
} as Plugin;
