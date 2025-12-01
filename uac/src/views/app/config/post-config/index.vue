<script setup lang="tsx">
import type { TreeKey } from "element-plus";
import type { DescriptionColumn, TabColumn } from "teek";
import type { Post } from "@/common/api/system/post";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/post";
import LinkRole from "./components/role.vue";

const ns = useNamespace("user-group-link");

const descriptionData = reactive({
  title: "",
  data: {} as Post.Info,
  columns: [] as DescriptionColumn[],
});

const tabColumns: TabColumn[] = [
  {
    prop: "Role",
    label: "已有角色",
    el: LinkRole,
    elProps: computed(() => {
      return {
        deptId: descriptionData.data.postId,
      };
    }),
  },
];

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: Post.Info) => {
  descriptionData.title = data.postName;
  descriptionData.data = data;
  descriptionData.columns = [
    { prop: "postCode", label: "岗位编码:" },
    { prop: "postName", label: "岗位名称:" },
    { prop: "createTime", label: "创建时间:" },
  ];
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="岗位列表"
      :request-api="list"
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="postId"
      label="postName"
      default-first
    ></TreeFilter>

    <el-card shadow="never" :class="[ns.e('right'), ns.join('card-minimal')]">
      <ProDescriptions
        :title="descriptionData.title"
        :columns="descriptionData.columns"
        :data="descriptionData.data"
        :column="5"
        :class="ns.e('descriptions')"
      />

      <ProTabs :columns="tabColumns" />
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/namespace" as *;

@include b(user-group-link) {
  display: flex;
  width: 100%;
  height: 100%;

  @include e(right) {
    width: 100%;

    @include e(descriptions) {
      :deep(.#{$el-namespace}-descriptions__header) {
        margin-bottom: 10px;
      }
    }
  }
}
</style>
