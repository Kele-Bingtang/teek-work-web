<script setup lang="tsx">
import type { TreeKey } from "element-plus";
import type { DescriptionColumn, TabColumn } from "teek";
import type { Dept } from "@/common/api/system/dept";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/dept";
import {
  listWithSelectedByDeptId,
  listRoleLinkByDeptId,
  addDeptListToRole,
  editRoleDeptLink,
  removeDeptRoleLink,
} from "@/common/api/link/role-dept-link";
import Role from "../common/role.vue";

const ns = useNamespace("user-group-link");

const descriptionData = reactive({
  title: "",
  data: {} as Dept.Info,
  columns: [] as DescriptionColumn[],
});

const tabColumns: TabColumn[] = [
  {
    prop: "Role",
    label: "已有角色",
    el: Role,
    elProps: computed(() => {
      return {
        id: descriptionData.data.deptId,
        requestImmediate: false,
        listWithSelectedApi: listWithSelectedByDeptId,
        listApi: listRoleLinkByDeptId,
        addApi: addDeptListToRole,
        editApi: editRoleDeptLink,
        removeApi: removeDeptRoleLink,
        removeBatchApi: removeDeptRoleLink,
      };
    }),
  },
];

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: Dept.Info) => {
  descriptionData.title = data.deptName;
  descriptionData.data = data;
  descriptionData.columns = [
    { prop: "deptName", label: "部门名称:" },
    { prop: "deptId", label: "部门编码:" },
    { prop: "leader", label: "领导:" },
    { prop: "phone", label: "电话:" },
    { prop: "email", label: "邮箱:" },
    { prop: "createTime", label: "创建时间:" },
  ];
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="部门列表"
      :request-api="list"
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="deptId"
      label="deptName"
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
