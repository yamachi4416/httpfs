<script setup lang="ts">
import { computed } from '@vue/reactivity';
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { selectAllable } from '../../compositions';
import { FsItem, MultiStatus } from '../../services/files';
import Modal from '../ui/Modal.vue';
import CreateDirectory from './actions/CreateDirectory.vue';
import FileDelete from './actions/FileDelete.vue';
import FileUpload from './actions/FileUpload.vue';
import MoveItems from './actions/MoveItems.vue';
import RenameItem from './actions/RenameItem.vue';

const props = withDefaults(
  defineProps<{
    path: string[];
    items: FsItem[];
  }>(),
  {
    path: () => [],
    items: () => [],
  }
);

const emit = defineEmits<{
  (e: 'cancel');
  (e: 'done');
  (e: 'upload-progress', item: FsItem, err?: Error);
  (e: 'upload-done', mtsts: MultiStatus[]);
  (e: 'move-progress', item: FsItem, err?: Error);
  (e: 'move-done', mtsts: MultiStatus[]);
  (e: 'delete-done', names: string[]);
  (e: 'mkdir-done', item: FsItem);
  (e: 'rename-done', item: FsItem);
}>();

const { t } = useI18n();
const items = computed(() => props.items);
const openMenu = ref(false);
const selectAll = selectAllable({ items });
const createDirectory = ref<InstanceType<typeof CreateDirectory>>();
const fileUpload = ref<InstanceType<typeof FileUpload>>();
const moveItems = ref<InstanceType<typeof MoveItems>>();
const renameItem = ref<InstanceType<typeof RenameItem>>();
const fileDelete = ref<InstanceType<typeof FileDelete>>();

const menuItems = [
  {
    name: 'createDirectory',
    icon: 'create_new_folder',
    get show() {
      return true;
    },
    click: () => createDirectory.value?.open(props.path),
  },
  {
    name: 'uploadFiles',
    icon: 'upload_file',
    get show() {
      return true;
    },
    click: () => fileUpload.value?.open(props.path),
  },
  {
    name: 'renameItem',
    icon: 'drive_file_rename_outline',
    get show() {
      return selectAll.count === 1;
    },
    click: () => renameItem.value?.open(props.path, selectAll.items[0]),
  },
  {
    name: 'moveItems',
    icon: 'drive_file_move',
    get show() {
      return selectAll.any;
    },
    click: () => moveItems.value?.open(props.path, selectAll.items),
  },
  {
    name: 'deleteFiles',
    icon: 'delete',
    get show() {
      return selectAll.any;
    },
    click: () => fileDelete.value?.open(props.path, selectAll.items),
  },
];

function done(...p: any[]) {
  emit('done');
  close();
}

function open() {
  openMenu.value = true;
}

function close() {
  openMenu.value = false;
}

function cancel() {
  if (selectAll.any) {
    emit('cancel');
  }
  close();
}

defineExpose({
  open,
  close,
});
</script>

<script lang="ts">
export type OnProgressAction = (
  item: FsItem,
  err?: Error
) => void | Promise<void>;
export type OnActionDone = (mtsts: MultiStatus[]) => void | Promise<void>;
</script>

<template>
  <a href="#" class="icon secondary" @click.prevent="open">
    more_vert
    <Teleport to="body">
      <FileUpload
        ref="fileUpload"
        @progress="(item, err) => emit('upload-progress', item, err)"
        @done="mtsts => done(emit('upload-done', mtsts))"
        @close="cancel"
      />
      <CreateDirectory
        ref="createDirectory"
        @done="item => done(emit('mkdir-done', item))"
      />
      <MoveItems
        ref="moveItems"
        @progress="(item, err) => emit('move-progress', item, err)"
        @done="mtsts => done(emit('move-done', mtsts))"
        @close="cancel"
      />
      <RenameItem
        ref="renameItem"
        @done="item => done(emit('rename-done', item))"
      />
      <FileDelete
        ref="fileDelete"
        @done="() => done(emit('delete-done', []))"
      />
      <Modal :show="openMenu" transision="scale" @close="close">
        <ul class="files-menu">
          <li v-for="item in menuItems.filter(i => i.show)" :key="item.name">
            <a
              href="#"
              class="secondary"
              @click.prevent="(openMenu = false), item.click()"
            >
              <span class="icon">{{ item.icon }}</span>
              <span>{{ t(`actions.${item.name}`) }}</span>
            </a>
          </li>
        </ul>
      </Modal>
    </Teleport>
  </a>
</template>

<style scoped lang="scss">
.files-menu {
  @include card;

  --block-spacing-vertical: 1rem;

  position: absolute;
  top: var(--block-spacing-vertical);
  right: var(--block-spacing-horizontal);
  border-radius: calc(var(--border-radius) * 2);
  transform-origin: top right;

  li,
  a {
    width: 100%;
    margin: 0;
    white-space: nowrap;
    list-style: none;
  }

  li {
    padding: 0;
  }

  a {
    display: inline-flex;
    column-gap: 0.5em;
    align-items: center;
    text-decoration: none;
  }
}
</style>
