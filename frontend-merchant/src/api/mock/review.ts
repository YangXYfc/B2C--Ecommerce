// Mock: 评价管理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const reviews: any[] = [
  { id: 1, productName: '轻薄本 Air 14 锐龙版 16GB+512GB', productImage: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=100&q=80', userName: '李四', rating: 5, content: '笔记本很轻薄，屏幕素质非常好，2.8K OLED显示效果惊艳。续航日常使用7小时左右，整体满意。', isAnonymous: false, merchantReply: '感谢您的支持！', merchantReplyTime: '2026-07-05 10:00', createdAt: '2026-07-04 16:00:00' },
  { id: 2, productName: '智选 Pro 5G 手机 12GB+256GB 钛空灰', productImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&q=80', userName: '王五', rating: 4, content: '手机运行流畅，拍照效果很好，就是续航可以再加强。', isAnonymous: false, merchantReply: null, createdAt: '2026-07-05 14:00:00' },
  { id: 3, productName: '65W GaN氮化镓充电器 三口快充', productImage: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=100&q=80', userName: '张**', rating: 5, content: '充电速度很快，体积小巧方便携带，非常满意！', isAnonymous: true, merchantReply: null, createdAt: '2026-07-06 11:00:00' },
  { id: 4, productName: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', productImage: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=100&q=80', userName: '赵六', rating: 3, content: '性价比不错，但屏幕色彩一般。', isAnonymous: false, merchantReply: null, createdAt: '2026-07-07 09:00:00' },
]

export async function getReviewList() {
  await delay()
  return { code: 200, data: { list: reviews, total: reviews.length } }
}

export async function replyReview(id: number, reply: string) {
  await delay()
  const r = reviews.find(item => item.id === id)
  if (r) { r.merchantReply = reply; r.merchantReplyTime = new Date().toISOString() }
  return { code: 200, message: '回复成功' }
}
