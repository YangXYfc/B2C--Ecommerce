import { post } from '@/api/request'

export async function uploadImage(file: File): Promise<string> {
  const body = new FormData()
  body.append('file', file)
  const response: any = await post('/files/images', body, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data.url
}
