import { get } from '@/api/request'

// 拉取分类树并拍平为 { id, name } 选项列表
export async function getCategories(): Promise<any> {
  const res: any = await get('/categories')
  const tree: any[] = res.data ?? []
  const flat: any[] = []
  const walk = (nodes: any[], prefix: string) => {
    for (const n of nodes || []) {
      flat.push({ id: n.id, name: prefix + n.name })
      if (n.children && n.children.length) walk(n.children, prefix + n.name + ' / ')
    }
  }
  walk(tree, '')
  return { ...res, data: flat }
}