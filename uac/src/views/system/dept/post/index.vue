<script setup lang="tsx" name="UserInfo">
import type { TreeKey } from "element-plus";
import { TreeFilter, useNamespace } from "teek";
import { listDeptTreeList } from "@/common/api/system/dept";
import Post from "../../post/index.vue";

const ns = useNamespace("dept-post");

const initRequestParams = reactive({
  deptId: "",
});

const handleTreeChange = (nodeId: string | TreeKey[]) => {
  initRequestParams.deptId = nodeId + "";
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter :requestApi="listDeptTreeList" @change="handleTreeChange" id="value" class="user-tree">
      <template #default="{ node }">
        <Icon v-if="node.data.icon" :icon="node.data.icon" class="icon"></Icon>
        <span>{{ node.label }}</span>
      </template>
    </TreeFilter>

    <Post :init-request-params="initRequestParams" />
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(dept-post) {
  display: flex;

  .icon {
    margin-right: 5px;
    vertical-align: -2px;
  }

  .user-tree {
    min-width: 220px;
  }

  .tk-user {
    overflow: hidden;
  }
}
</style>
