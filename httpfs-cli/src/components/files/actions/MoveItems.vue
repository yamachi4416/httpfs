<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { reactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { FsItem, fetchDirectoryItems } from '../../../services/FilesService';
import Modal from '../../util/Modal.vue';
import FilesListPanel from '../fileslist/FilesListPanel.vue';
import Breadcrumb from '../Breadcrumb.vue';

const { t } = useI18n();

const state = reactive({
  show: false,
  paths: null as string[],
  items: [] as FsItem[],
});

const current = computed(() => state.paths?.join('/'));

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
  state.show = false;
}

function enterItem(item: FsItem) {
  if (item.directory) {
    state.paths = [...item.paths];
  }
}

watch(
  () => current.value,
  async (value, oldValue) => {
    if (value !== oldValue) {
      state.items = [];
      if (value != null) {
        state.items = await fetchDirectoryItems(state.paths);
      }
    }
  }
);

defineExpose({
  open(path: string[]) {
    state.show = true;
    state.paths = [...path];
  },
  close,
});
</script>

<template>
  <Modal
    class="move-items"
    :transision="'slide'"
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
      <div class="move-items-panel-body">
        <FilesListPanel
          :items="state.items"
          :sort-options="{ key: 'directory', direction: 'desc' }"
          @click="enterItem"
        />
      </div>
      <nav class="move-items-panel-footer">
        <a href="#" tabindex="1" role="link" @click.prevent="close">
          {{ t('actions.cancel') }}
        </a>
        <span class="devider"></span>
        <a href="#" @click.prevent="">決定</a>
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
    }

    &-footer {
    }
  }
}
</style>
