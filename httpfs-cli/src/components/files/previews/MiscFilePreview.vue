<script setup lang="ts">
import { FsItem } from '../../../services/files';
import FileIcon from '../fileslist/FileIcon.vue';
import Waiting from '../../ui/Waiting.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const props = defineProps<{
  item: FsItem;
}>();
</script>

<template>
  <div class="misc-file-preview">
    <Waiting class="misc-file-preview-content" :waiting="!props.item">
      <FileIcon class="misc-file-preview-icon" :item="props.item" />
      <p>{{ t('messages.notSupportFilePreview') }}</p>
      <a href="#" @click.prevent="props.item?.download()">
        {{ t('actions.download') }}
      </a>
    </Waiting>
  </div>
</template>

<style scoped lang="scss">
.misc-file-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;

  &-content {
    display: flex;
    flex-direction: column;
    padding: 2em;
    text-align: center;

    a {
      text-decoration: none;
    }
  }

  &-icon {
    align-items: center;
    justify-content: center;
    font-size: 10em;
  }
}
</style>
