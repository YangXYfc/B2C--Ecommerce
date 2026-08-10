// Mock: 管理员统计看板

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export async function getDashboard() {
  await delay()
  return {
    code: 200,
    data: {
      totalUsers: 7,
      totalMerchants: 3,
      totalOrders: 115,
      totalSales: 289600.00,
      salesTrend: [
        { date: '08-04', amount: 35600 },
        { date: '08-05', amount: 42100 },
        { date: '08-06', amount: 38900 },
        { date: '08-07', amount: 45200 },
        { date: '08-08', amount: 39700 },
        { date: '08-09', amount: 43800 },
        { date: '08-10', amount: 44300 },
      ],
      categoryDistribution: [
        { name: '手机数码', value: 42 },
        { name: '家用电器', value: 18 },
        { name: '服饰鞋包', value: 30 },
        { name: '食品生鲜', value: 10 },
      ],
    },
  }
}
