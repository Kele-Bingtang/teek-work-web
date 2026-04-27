<script setup lang="ts" name="Team">
import { useNamespace } from "teek";
import { DataSource, Members, Project, TeamSetting } from "./components";

const ns = useNamespace("team");

const route = useRoute();

const teamTabActiveName = ref("团队项目");

const teamTabList = [
  { label: "团队项目", name: "团队项目", lazy: false, component: Project },
  { label: "成员/权限", name: "成员/权限", lazy: true, component: Members },
  { label: "团队设置", name: "团队设置", lazy: true, component: TeamSetting },
  {
    label: "数据源设置",
    name: "数据源设置",
    lazy: true,
    component: DataSource,
    hidden: route.meta.params?.teamRole !== "所有者",
  },
];
</script>

<template>
  <el-card :class="ns.b()" shadow="never">
    <template #header>
      <el-space>
        <span>{{ route.meta.params?.teamName }}</span>
        <ElTag type="warning">{{ route.meta.params?.teamRole }}</ElTag>
      </el-space>
    </template>
    <el-tabs v-model="teamTabActiveName">
      <template v-for="tab in teamTabList" :key="tab.name">
        <el-tab-pane v-if="!tab.hidden" :label="tab.label" :name="tab.name" :lazy="tab.lazy">
          <component :is="tab.component" class="p-10"></component>
        </el-tab-pane>
      </template>
    </el-tabs>
  </el-card>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(team) {
  flex: 1;
  padding: 10px;
  background-color: cssVar(surface-bg-color-box);

  :deep(.el-card__header) {
    padding: 10px;
    border-bottom: none;
  }

  :deep(.el-card__body) {
    padding: 0 10px;
  }
}
</style>
