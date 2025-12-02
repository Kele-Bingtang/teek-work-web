<script setup lang="ts">
import type { App } from "@/common/api/application/app";
import { useNamespace } from "@/composables";
import { listApp } from "@/common/api/application/app";
import { useDataStore } from "@/pinia";

// ElTabs 组件配置项
const tabs = [
  { label: "我的项目", name: "all" },
  { label: "我创建的", name: "creator" },
  { label: "我加入的", name: "joiner" },
];

const ns = useNamespace("app");

const activeName = ref("all");
const appList = ref<App.Info[]>([]);

const dataStore = useDataStore();
const router = useRouter();

const initApp = async () => {
  const res = await listApp();
  if (res.status === "success") appList.value = res.data;
};

onMounted(initApp);
const switchTab = () => {};

const handleAppClick = (item: App.Info) => {
  dataStore.setInfo(item);
  router.push(`/resource-manage/${item.appId}`);
};
</script>

<template>
  <div :class="ns.b()" class="tk-card-minimal">
    <el-tabs type="border-card" v-model="activeName" @tab-change="switchTab">
      <el-tab-pane v-for="item in tabs" :key="item.name" :label="item.label" :name="item.name" :lazy="true">
        <el-row :gutter="10">
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6" v-for="item in appList" :key="item.id">
            <div class="tk-card-minimal app-card" @click="handleAppClick(item)">
              <div :class="ns.e('code')">{{ item.appCode }} ({{ item.appName }})</div>
              <div :class="ns.e('intro')" class="mle">{{ item.intro }}</div>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/function" as *;

@include b(app) {
  .app-card {
    height: 120px;
    cursor: pointer;
  }

  @include e(code) {
    margin-bottom: 10px;
    font-size: 18px;
  }

  @include e(intro) {
    font-size: 14px;
    color: cssVar(text-gray-600);
  }
}
</style>
