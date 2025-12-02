<script setup lang="tsx" name="RoleLink">
import type { TreeKey } from "element-plus";
import type { DescriptionColumn, TabColumn } from "teek";
import type { Role } from "@/common/api/system/role";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/role";
import LinkUser from "./components/user.vue";
import LinkUserGroup from "./components/user-group.vue";
import LinkResource from "./components/resource.vue";
import LinkDept from "./components/dept.vue";

const ns = useNamespace("role-link");

const descriptionData = reactive({
  title: "",
  data: {} as Role.Info,
  columns: [] as DescriptionColumn[],
});

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: Role.Info) => {
  descriptionData.title = data.roleName;
  descriptionData.data = data;
  descriptionData.columns = [
    { prop: "roleName", label: "角色名称:" },
    { prop: "roleCode", label: "角色编码:" },
    { prop: "ownerId", label: "负责人:" },
    { prop: "createTime", label: "创建时间:" },
  ];
};

const tabColumns: TabColumn[] = [
  {
    name: "User",
    label: "授权用户",
    el: LinkUser,
    elProps: computed(() => {
      return {
        roleId: descriptionData.data.roleId,
      };
    }),
  },
  {
    name: "UserGroup",
    label: "授权用户组",
    el: LinkUserGroup,
    elProps: computed(() => {
      return {
        roleId: descriptionData.data.roleId,
      };
    }),
    lazy: true,
  },
  {
    name: "Menu",
    label: "授权资源",
    el: LinkResource,
    elProps: computed(() => {
      return {
        roleId: descriptionData.data.roleId,
      };
    }),
    lazy: true,
  },
  {
    name: "Dept",
    label: "授权部门",
    el: LinkDept,
    elProps: computed(() => {
      return {
        roleId: descriptionData.data.roleId,
      };
    }),
    lazy: true,
  },
  // {
  //   name: "Post",
  //   label: "授权岗位",
  //   el: LinkPost,
  //   elProps: computed(() => {
  //     return {
  //       roleId: descriptionData.data.roleId,
  //     };
  //   }),
  //   lazy: true,
  // },
];
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="角色列表"
      :requestApi="list"
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="roleId"
      label="roleName"
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

@include b(role-link) {
  display: flex;
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
