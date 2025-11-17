<template>
  <div :class="prefixClass">
    <el-descriptions title="基本信息" :class="`${prefixClass}__cache-descriptions`" :column="5" border>
      <template #extra>
        <el-button type="info" link @click="dialogVisible = true">详情</el-button>
      </template>
      <el-descriptions-item label="Redis 版本">{{ cacheInfoVO.info?.redis_version }}</el-descriptions-item>
      <el-descriptions-item label="运行模式">
        {{ cacheInfoVO.info?.redis_mode === "standalone" ? "单机" : "集群" }}
      </el-descriptions-item>
      <el-descriptions-item label="端口">{{ cacheInfoVO.info?.tcp_port }}</el-descriptions-item>
      <el-descriptions-item label="客户端数量">{{ cacheInfoVO.info?.connected_clients }}</el-descriptions-item>
      <el-descriptions-item label="运行时间(天)">{{ cacheInfoVO.info?.uptime_in_days }}</el-descriptions-item>
      <el-descriptions-item label="使用内存">{{ cacheInfoVO.info?.used_memory_human }}</el-descriptions-item>
      <el-descriptions-item label="使用 CPU">{{ cacheInfoVO.info?.used_cpu_user_children }}</el-descriptions-item>
      <el-descriptions-item label="内存配置">7</el-descriptions-item>
      <el-descriptions-item label="AOF 是否开启">
        {{ cacheInfoVO.info?.aof_enabled === "1" ? "是" : "否" }}
      </el-descriptions-item>
      <el-descriptions-item label="RDB 是否成功执行">
        {{ cacheInfoVO.info?.rdb_bgsave_in_progress === "0" ? "成功" : "失败" }}
      </el-descriptions-item>
      <el-descriptions-item label="Key 数量">{{ cacheInfoVO.dbSize }}</el-descriptions-item>
      <el-descriptions-item label="网络入口出口">
        {{ cacheInfoVO.info?.instantaneous_input_kbps }}kps / {{ cacheInfoVO.info?.instantaneous_output_kbps }}kps
      </el-descriptions-item>
    </el-descriptions>

    <el-dialog v-model="dialogVisible" title="详情" width="50%">
      {{ cacheInfoVO.info }}
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">退出</el-button>
      </template>
    </el-dialog>

    <el-row :gutter="10">
      <el-col :span="12">
        <CommandChart
          v-if="cacheInfoVO.commandStats"
          :data="cacheInfoVO.commandStats"
          :class="`${prefixClass}__chart-item`"
        />
      </el-col>
      <el-col :span="12">
        <MemoryChart
          v-if="cacheInfoVO.info?.used_memory_human"
          :value="cacheInfoVO.info?.used_memory_human"
          :class="`${prefixClass}__chart-item`"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="CacheMonitor">
import { list, type Cache } from "@/api/monitor/cache";
import CommandChart from "./components/CommandChart.vue";
import MemoryChart from "./components/MemoryChart.vue";
import { useDesign } from "@work/hooks";

const { getPrefixClass } = useDesign();
const prefixClass = getPrefixClass("cache-monitor");

const cacheInfoVO = ref<Cache.CacheInfo>({
  info: undefined,
  dbSize: 0,
  commandStats: undefined,
});

const dialogVisible = ref(false);

onMounted(() => {
  list().then(res => {
    if (res.status === "success") {
      cacheInfoVO.value = res.data;
    }
  });
});
</script>

<style lang="scss" scoped>
$prefix-class: #{$admin-namespace}-cache-monitor;

.#{$prefix-class} {
  flex: 1;

  &__cache-descriptions {
    padding: 10px;
    margin-bottom: 10px;
    background-color: #ffffff;
  }

  &__chart-item {
    padding-top: 10px;
    background-color: #ffffff;
  }
}
</style>
