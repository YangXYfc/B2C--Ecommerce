<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { uploadImage } from '@/api/file'

const props = withDefaults(defineProps<{
  modelValue: string | string[]
  multiple?: boolean
  limit?: number
}>(), {
  multiple: false,
  limit: 1,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | string[]]
}>()

const uploading = ref(false)
const values = computed(() => Array.isArray(props.modelValue)
  ? props.modelValue.filter(Boolean)
  : (props.modelValue ? [props.modelValue] : []))

function validate(file: File) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5 MB')
    return false
  }
  return true
}

async function handleUpload(options: UploadRequestOptions) {
  const file = options.file as File
  if (!validate(file)) return
  uploading.value = true
  try {
    const url = await uploadImage(file)
    const next = props.multiple ? [...values.value, url].slice(0, props.limit) : url
    emit('update:modelValue', next)
    options.onSuccess?.({ url })
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error((error as Error).message || '图片上传失败')
  } finally {
    uploading.value = false
  }
}

function remove(index: number) {
  if (props.multiple) emit('update:modelValue', values.value.filter((_, itemIndex) => itemIndex !== index))
  else emit('update:modelValue', '')
}
</script>

<template>
  <div class="image-uploader">
    <div v-if="values.length" class="previews">
      <div v-for="(url, index) in values" :key="url" class="preview-item">
        <img :src="url" alt="已上传图片" />
        <button type="button" aria-label="移除图片" @click="remove(index)">×</button>
      </div>
    </div>
    <el-upload
      v-if="values.length < limit"
      accept="image/jpeg,image/png,image/webp"
      :multiple="multiple"
      :show-file-list="false"
      :http-request="handleUpload"
    >
      <el-button :loading="uploading" plain>{{ uploading ? '上传中' : (values.length ? '继续上传' : '选择图片') }}</el-button>
    </el-upload>
    <span class="hint">支持 JPG、PNG、WebP，单张不超过 5 MB</span>
  </div>
</template>

<style scoped>
.image-uploader { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }
.previews { display: flex; flex-wrap: wrap; gap: 10px; }
.preview-item { position: relative; width: 104px; height: 104px; overflow: hidden; border: 1px solid #e5e7eb; border-radius: 10px; background: #f7f8fa; }
.preview-item img { width: 100%; height: 100%; object-fit: cover; }
.preview-item button { position: absolute; top: 5px; right: 5px; width: 24px; height: 24px; padding: 0; border: 0; border-radius: 50%; color: #fff; background: rgba(0,0,0,.58); cursor: pointer; }
.hint { width: 100%; color: #909399; font-size: 12px; }
</style>
