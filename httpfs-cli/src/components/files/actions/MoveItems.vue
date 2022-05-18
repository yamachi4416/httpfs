<script setup lang="ts">
import { computed, reactive, toRef, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { injectSharedState, uniqueKeyMap } from '../../../compositions';
import {
  fetchDirectoryItems,
  FsItem,
  moveItems,
  MultiStatus,
} from '../../../services/files';
import Modal from '../../ui/Modal.vue';
import Breadcrumb from '../Breadcrumb.vue';
import FilesList, { FileListBindItems } from '../FilesList.vue';

const { t } = useI18n();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'progress', item: FsItem, err?: Error);
  (e: 'done', mtsts: MultiStatus[]): void;
}>();

const shared = injectSharedState();

const state = reactive({
  show: false,
  loading: true,
  start: null as string[],
  paths: null as string[],
  items: [] as FsItem[],
  targets: [] as FsItem[],
});

const current = computed(() => state.paths?.join('/'));
const canMove = computed(
  () =>
    state.start?.join('/') !== current.value &&
    !isTargetChild(`/${current.value}`)
);
const targetsMap = uniqueKeyMap(toRef(state, 'targets'), 'path');

function clear() {
  state.show = false;
  state.start = null;
  state.paths = null;
  state.loading = true;
  state.items = [];
}

function back() {
  if (state.paths.length > 0) {
    state.paths.pop();
  }
}

function movePath(path: string) {
  if (path !== current.value) {
    state.paths = path.split('/').filter(p => p);
  }
}

function close() {
  clear();
  emit('close');
}

function isTargetChild(path: string): boolean {
  return state.targets
    .filter(target => target.directory)
    .some(dir => `${path}/`.startsWith(`${dir.path}/`));
}

function enterItem(item: FsItem) {
  if (item.directory && !isTargetChild(item.path)) {
    state.paths = [...item.paths];
  }
}

async function move() {
  if (state.loading || !canMove.value) {
    return;
  }

  const mtsts = await shared.withLoading(() =>
    moveItems({
      path: state.start,
      destination: current.value,
      items: state.targets,
    }).finally(() => {
      state.loading = false;
    })
  );

  if (mtsts.some(({ isError }) => isError)) {
    const map = targetsMap.value;
    mtsts.forEach(mtst => {
      if (!mtst.isError && map.has(mtst.item.path)) {
        map.get(mtst.item.path).selected = false;
        state.items.push(mtst.item);
      }
      emit('progress', mtst.item, mtst.toException());
    });
  } else {
    clear();
    emit('done', mtsts);
  }
}

const bindItems: FileListBindItems = item => {
  if (!item.directory) {
    return { class: 'disabled' };
  }
  if (isTargetChild(item.path)) {
    return { class: 'disabled' };
  }
};

watch(
  () => current.value,
  async (value, oldValue) => {
    if (value !== oldValue) {
      state.items = [];
      if (value != null) {
        state.loading = true;
        state.items = await fetchDirectoryItems(state.paths).finally(() => {
          state.loading = false;
        });
      }
    }
  }
);

defineExpose({
  open(path: string[], targets: FsItem[]) {
    clear();
    state.show = true;
    state.start = [...path];
    state.paths = [...path];
    state.targets = [...targets]
  },
  close,
});
</script>

<template>
  <Modal
    class="move-items"
    :transision="'scale'"
    :show="state.show"
    @close="close"
  >
    <article class="move-items-panel card">
      <div class="move-items-panel-header">
        <a
          href="#"
          class="move-items-panel-header-back secondary"
          @click.prevent="back"
        >
          <span class="icon">arrow_back_ios_new</span>
        </a>
        <Breadcrumb
          class="move-items-panel-header-title"
          :path="state.paths"
          @click="movePath"
        />
      </div>
      <div class="move-items-panel-body" :aria-busy="state.loading">
        <FilesList
          :items="state.items"
          :sort-options="{ key: 'directory', direction: 'desc' }"
          :bind-item="bindItems"
          @click="enterItem"
        />
      </div>
      <nav class="move-items-panel-footer">
        <a href="#" tabindex="1" role="link" @click.prevent="close">
          {{ t('actions.cancel') }}
        </a>
        <span class="devider"></span>
        <a href="#" :class="{ secondary: !canMove }" @click.prevent="move">
          {{ t('actions.decision') }}
        </a>
      </nav>
    </article>
  </Modal>
</template>

<style scoped lang="scss">
.move-items {
  padding: var(--spacing);

  &-panel {
    margin: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    &-header {
      display: flex;
      column-gap: var(--spacing);

      &-back {
        display: flex;
        align-items: center;
        text-decoration: none;
      }

      &-title {
        flex: 1;
      }
    }

    &-body {
      flex: 1;
      overflow: auto;
      display: block;
      &[aria-busy='true'] {
        display: flex;
        align-items: center;
        justify-content: center;
        * {
          display: none;
        }
      }
    }

    &-footer {
    }
  }
}
</style>
