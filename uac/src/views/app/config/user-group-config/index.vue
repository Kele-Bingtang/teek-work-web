<script setup lang="tsx" name="UserLink">
import type { TreeKey } from "element-plus";
import type { DescriptionColumn, TabColumn } from "teek";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/user/user-group";
import { useDictStore } from "@/pinia";
import {
  listWithSelectedByGroupId,
  listRoleLinkByGroupId,
  addRoleListToUserGroup,
  editRoleUserGroupLink,
  removeRoleUserGroupLink,
} from "@/common/api/link/role-user-group-link";
import User from "@/views/system/user-group/user/components/link-user.vue";
import Role from "../common/role.vue";

const ns = useNamespace("user-group-link");

const descriptionData = reactive({
  title: "",
  data: {} as UserGroup.Info,
  columns: [] as DescriptionColumn[],
});

const tabColumns: TabColumn[] = [
  {
    prop: "Role",
    label: "已有角色",
    el: Role,
    elProps: computed(() => {
      return {
        id: descriptionData.data.groupId,
        requestImmediate: false,
        listWithSelectedApi: listWithSelectedByGroupId,
        listApi: listRoleLinkByGroupId,
        addApi: addRoleListToUserGroup,
        editApi: editRoleUserGroupLink,
        removeApi: removeRoleUserGroupLink,
        removeBatchApi: removeRoleUserGroupLink,
      };
    }),
  },
  {
    prop: "User",
    label: "已有用户",
    el: User,
    elProps: computed(() => ({
      userGroupId: descriptionData.data.groupId,
    })),
    lazy: true,
  },
];

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: UserGroup.Info) => {
  descriptionData.title = data.groupName;
  descriptionData.data = data;
  descriptionData.columns = [
    { prop: "groupName", label: "用户组名:" },
    { prop: "groupId", label: "用户组编码:" },
    {
      prop: "groupType",
      label: "用户组类型:",
      options: () => useDictStore().getDictData("sys_group_type"),
      optionField: { value: "dictValue", label: "dictLabel" },
    },
    { prop: "ownerId", label: "负责人:", formatValue: (_, { data }) => `${data.ownerName} ${data.ownerId}` },
    { prop: "intro", label: "描述:" },
    { prop: "createTime", label: "创建时间:" },
  ];
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="用户组列表"
      :request-api="list"
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="groupId"
      label="groupName"
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
