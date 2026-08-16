<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail, shipOrder, orderStatusMap } from '@/api/order'
import { ElMessage } from 'element-plus'

const route = useRoute()
const order = ref<any>(null)
const shipDialog = ref(false)
const shipForm = ref({ logisticsCompany: '', logisticsNo: '' })
const shipLoading = ref(false)

onMounted(async () => {
  const res: any = await getOrderDetail(Number(route.params.id))
  order.value = res.data
})

async function handleShip() {
  if (!shipForm.value.logisticsCompany || !shipForm.value.logisticsNo) {
    ElMessage.warning('请填写物流信息')
    return
  }
  shipLoading.value = true
  await shipOrder(order.value.id, shipForm.value)
  ElMessage.success('发货成功')
  order.value.status = 2
  order.value.logisticsCompany = shipForm.value.logisticsCompany
  order.value.logisticsNo = shipForm.value.logisticsNo
  shipDialog.value = false
  shipLoading.value = false
}
</script>

<template>
  <div class="page-container" v-if="order">
    <div class="page-header"><h2>订单详情</h2></div>

    <el-card style="margin-bottom:16px">
      <template #header>订单信息</template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="order.status === 1 ? 'warning' : 'info'">{{ orderStatusMap[order.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ order.payAmount || order.totalAmount }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-bottom:16px">
      <template #header>收货地址</template>
      <p>{{ order.addressSnapshot || '-' }}</p>
    </el-card>

    <el-card style="margin-bottom:16px">
      <template #header>商品信息</template>
      <el-table :data="order.items" border>
        <el-table-column label="图片" width="80">
          <template #default="{ row: item }">
            <img :src="item.productImage" style="width:50px;height:50px;object-fit:cover" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品" min-width="200" />
        <el-table-column prop="skuName" label="规格" width="160" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row: item }">¥{{ item.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="subtotal" label="小计" width="100">
          <template #default="{ row: item }">¥{{ item.subtotal }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header>物流信息</template>
      <div v-if="order.logisticsCompany">
        <p>物流公司：{{ order.logisticsCompany }}</p>
        <p>物流单号：{{ order.logisticsNo }}</p>
      </div>
      <div v-else-if="order.status === 1" style="text-align:center;padding:16px">
        <el-button type="primary" @click="shipDialog = true">去发货</el-button>
      </div>
      <div v-else style="color:#909399">暂无物流信息</div>
    </el-card>

    <el-dialog v-model="shipDialog" title="发货" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.logisticsCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialog = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>