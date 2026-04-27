<script setup lang="ts" name="ServiceContent">
import { type Service } from "@/common/api/service";
import { message, useNamespace } from "teek";
import { ServiceKey } from "@/common/config";
import { getServiceByServiceId } from "@/common/api/service";
import { getMyProjectRole } from "@/common/api/projectMember";
import { CircuitBreaking, ResponseConfig, ResponseData, ServiceCol } from "./components";

const ns = useNamespace("service-main");

const route = useRoute();

const serviceInfo = ref<Service.ServiceInfo>();

onMounted(async () => {
  // 初始化服务信息，给子组件使用
  const serviceId = route.params.serviceId as string;

  const res = await getServiceByServiceId(serviceId);
  if (!res.data) return message.error("服务不存在");

  // 获取项目角色
  const projectRoleRes = await getMyProjectRole(res.data.projectId);
  if (!projectRoleRes.data || projectRoleRes.data === "禁止访问") return message.error("您没有权限访问该项目");

  serviceInfo.value = res.data;
});

provide(ServiceKey, serviceInfo);

const serviceTabActiveName = ref("数据响应");
const serviceTabList = [
  { label: "数据响应", name: "数据响应", lazy: false, component: ResponseData },
  { label: "数据列配置项", name: "数据列配置项", lazy: true, component: ServiceCol },
  { label: "降级响应", name: "降级响应", lazy: true, component: CircuitBreaking },
  { label: "响应配置", name: "项目设置", lazy: true, component: ResponseConfig },
];
</script>

<template>
  <div :class="ns.b()">
    <el-tabs v-model="serviceTabActiveName">
      <el-tab-pane v-for="tab in serviceTabList" :key="tab.name" :label="tab.label" :name="tab.name" :lazy="tab.lazy">
        <component :is="tab.component" class="pt-2.5"></component>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/function" as *;

@include b(service-main) {
  flex: 1;

  .el-tabs {
    display: flex;

    :deep(.el-tabs__content, .el-tab-pane) {
      flex: 1;
    }

    :deep(.el-tab-pane) {
      height: 100%;
    }
  }

  :deep(.el-tabs__header) {
    padding: 0 15px;
    margin: 0;
    background-color: cssVar(surface-bg-color-box);
  }
}
</style>
