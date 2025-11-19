<script setup lang="ts" name="ServiceMain">
import type { TreeKey } from "element-plus";
import type { TreeFilterInstance } from "teek";
import { TreeFilter, mittBus, useNamespace } from "teek";
import { listCategory } from "@/common/api/category";
import { FolderOpened } from "@element-plus/icons-vue";
import Service from "./service.vue";

const ns = useNamespace("service-main");

const categoryId = ref("");

const treeFilterInstance = useTemplateRef<TreeFilterInstance>("treeFilterInstance");

const handleTreeChange = (nodeId: string | TreeKey[]) => {
  categoryId.value = nodeId as string;
};

mittBus.on("initTreeData", () => {
  treeFilterInstance.value?.initTreeData();
});
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      ref="treeFilterInstance"
      title="目录"
      :requestApi="listCategory"
      @change="handleTreeChange"
      id="categoryId"
      label="categoryName"
      defaultFirst
    >
      <template #default="{ node }">
        <div class="flx-center">
          <Icon :icon="FolderOpened" class="mr-1.5"></Icon>
          <span>{{ node.label }}</span>
        </div>
      </template>
    </TreeFilter>

    <Service v-if="categoryId" :categoryId />
    <el-empty v-else description="目录暂无服务" />
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(service-main) {
  display: flex;
  height: 100%;
}
</style>
