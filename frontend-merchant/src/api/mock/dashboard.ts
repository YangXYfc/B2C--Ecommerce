// Mock: 商家看板数据

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export async function getDashboard() {
  await delay()
  return {
    code: 200,
    data: {
      todaySales: 12890.50,
      todayOrders: 47,
      pendingShip: 12,
      activeProducts: 36,
      salesTrend: [
        { date: '08-04', amount: 8900 },
        { date: '08-05', amount: 11200 },
        { date: '08-06', amount: 10200 },
        { date: '08-07', amount: 13500 },
        { date: '08-08', amount: 9800 },
        { date: '08-09', amount: 14200 },
        { date: '08-10', amount: 12890 },
      ],
      orderDistribution: [
        { name: '待支付', value: 5 },
        { name: '待发货', value: 12 },
        { name: '已发货', value: 18 },
        { name: '已收货', value: 35 },
        { name: '已评价', value: 42 },
        { name: '已取消', value: 3 },
      ],
    },
  }
}
