<script setup lang="ts" name="CacheMonitor">
import type { Cache } from "@/common/api/monitor/cache";
import { useNamespace } from "@teek/composables";
import { list } from "@/common/api/monitor/cache";
import CommandChart from "./components/command-chart.vue";
import MemoryChart from "./components/memory-chart.vue";

const ns = useNamespace("cache-monitor");

const cacheInfo = ref<Cache.Info>({
  info: undefined,
  dbSize: 0,
  commandStats: undefined,
});

const dialogVisible = ref(false);

onMounted(() => {
  list().then(res => {
    if (res.status === "success") {
      cacheInfo.value = res.data;
    }
  });
});
</script>

<template>
  <div :class="ns.b()">
    <el-descriptions title="基本信息" :class="ns.e('cache-descriptions')" :column="5" border>
      <template #extra>
        <el-button type="info" link @click="dialogVisible = true">详情</el-button>
      </template>
      <el-descriptions-item label="Redis 版本">{{ cacheInfo.info?.redis_version }}</el-descriptions-item>
      <el-descriptions-item label="运行模式">
        {{ cacheInfo.info?.redis_mode === "standalone" ? "单机" : "集群" }}
      </el-descriptions-item>
      <el-descriptions-item label="端口">{{ cacheInfo.info?.tcp_port }}</el-descriptions-item>
      <el-descriptions-item label="客户端数量">{{ cacheInfo.info?.connected_clients }}</el-descriptions-item>
      <el-descriptions-item label="运行时间(天)">{{ cacheInfo.info?.uptime_in_days }}</el-descriptions-item>
      <el-descriptions-item label="使用内存">{{ cacheInfo.info?.used_memory_human }}</el-descriptions-item>
      <el-descriptions-item label="使用 CPU">{{ cacheInfo.info?.used_cpu_user_children }}</el-descriptions-item>
      <el-descriptions-item label="内存配置">7</el-descriptions-item>
      <el-descriptions-item label="AOF 是否开启">
        {{ cacheInfo.info?.aof_enabled === "1" ? "是" : "否" }}
      </el-descriptions-item>
      <el-descriptions-item label="RDB 是否成功执行">
        {{ cacheInfo.info?.rdb_bgsave_in_progress === "0" ? "成功" : "失败" }}
      </el-descriptions-item>
      <el-descriptions-item label="Key 数量">{{ cacheInfo.dbSize }}</el-descriptions-item>
      <el-descriptions-item label="网络入口出口">
        {{ cacheInfo.info?.instantaneous_input_kbps }}kps / {{ cacheInfo.info?.instantaneous_output_kbps }}kps
      </el-descriptions-item>
    </el-descriptions>

    <el-dialog v-model="dialogVisible" title="详情" width="50%">
      {{ cacheInfo.info }}
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">退出</el-button>
      </template>
    </el-dialog>

    <el-row :gutter="10">
      <el-col :span="12">
        <CommandChart v-if="cacheInfo.commandStats" :data="cacheInfo.commandStats" :class="ns.e('chart-item')" />
      </el-col>
      <el-col :span="12">
        <MemoryChart
          v-if="cacheInfo.info?.used_memory_human"
          :value="cacheInfo.info?.used_memory_human"
          :class="ns.e('chart-item')"
        />
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(cache-monitor) {
  height: 100%;

  @include e(cache-descriptions) {
    padding: 10px;
    margin-bottom: 10px;
    background-color: #ffffff;
  }

  @include e(chart-item) {
    padding-top: 10px;
    background-color: #ffffff;
  }
}
</style>
