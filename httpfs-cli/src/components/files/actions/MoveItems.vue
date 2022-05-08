<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { reactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { FsItem, fetchDirectoryItems } from '../../../services/FilesService';
import Modal from '../../util/Modal.vue';
import FilesListPanel from '../fileslist/FilesListPanel.vue';

const { t } = useI18n();

const state = reactive({
  show: false,
  paths: null as string[],
  items: [] as FsItem[],
});

const path = computed(() => state.paths?.join('/'));

function back() {
  if (state.paths.length > 0) {
    state.paths.pop();
  }
}

function close() {
  state.show = false;
}

watch(
  () => path.value,
  async (value, oldValue) => {
    if (value !== oldValue) {
      if (value != null) {
        state.items = [];
        state.items = await fetchDirectoryItems(state.paths);
      } else {
        state.items = [];
      }
    }
  }
);

function enterItem(item: FsItem) {
  if (item.directory) {
    state.paths = [...item.paths];
  }
}

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
        <span class="move-items-panel-header-title">
          {{ state.paths.join(' / ') }}
        </span>
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
    min-width: 90%;
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
