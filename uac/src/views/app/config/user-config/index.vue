<script setup lang="tsx" name="UserLink">
import type { User } from "@/common/api/system/user/user";
import type { DescriptionColumn, TabColumn } from "teek";
import type { TreeKey } from "element-plus";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/user/user";
import { useDictStore } from "@/pinia";
import { simplifyText } from "@/common/utils";
import {
  listWithSelectedByUserId,
  listRoleLinkByUserId,
  addRoleListToUser,
  editRoleUserLink,
  removeRoleUserLink,
} from "@/common/api/link/role-user-link";
import UserGroup from "./components/user-group.vue";
import Role from "../common/role.vue";

const ns = useNamespace("user-link");

const avatarBgColor = [
  "#f56a00",
  "#e6a23c",
  "#6f7ad3",
  "#1989fa",
  "#5cb87a",
  "#e6a23c",
  "#f56a00",
  "#e6a23c",
  "#6f7ad3",
  "#1989fa",
  "#5cb87a",
];

const userInfo = ref<User.Info>();

const descriptionData = reactive({
  title: "",
  avatarBgColor: "",
  data: {} as User.Info,
  columns: [] as DescriptionColumn[],
});

const tabColumns: TabColumn[] = [
  {
    prop: "UserGroup",
    label: "已加入群组",
    el: UserGroup,
    elProps: computed(() => ({
      userId: descriptionData.data.userId,
    })),
  },
  {
    prop: "Role",
    label: "已有角色",
    lazy: true,
    el: Role,
    elProps: computed(() => ({
      id: descriptionData.data.userId,
      listWithSelectedApi: listWithSelectedByUserId,
      listApi: listRoleLinkByUserId,
      addApi: addRoleListToUser,
      editApi: editRoleUserLink,
      removeApi: removeRoleUserLink,
      removeBatchApi: removeRoleUserLink,
    })),
  },
];

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: User.Info & { avatarBgColor: string }) => {
  userInfo.value = data;
  descriptionData.title = data.nickname || data.username;
  descriptionData.avatarBgColor = data.avatarBgColor;
  descriptionData.data = data;
  descriptionData.columns = [
    { prop: "username", label: "用户名:" },
    {
      prop: "sex",
      label: "性别:",
      options: () => useDictStore().getDictData("sys_user_sex"),
      optionField: { value: "dictValue", label: "dictLabel" },
    },
    { prop: "phone", label: "电话:" },
    { prop: "email", label: "邮箱:" },
    { prop: "registerTime", label: "注册时间:" },
  ];
};

const transformData = (data: Recordable) => {
  return data?.map((item: any, index: number) => {
    // 添加头像背景色
    item.avatarBgColor = avatarBgColor[index % avatarBgColor.length];
    return item;
  });
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="用户列表"
      :requestApi="list"
      :transform-data
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="userId"
      label="nickname"
      default-first
      :show-total="false"
      show-num
      :class="ns.e('user')"
    >
      <template #default="{ node }">
        <el-avatar
          style="margin-right: 10px; font-size: 16px"
          :size="34"
          :style="{ backgroundColor: node.data.avatarBgColor }"
        >
          {{ simplifyText(node.data.nickname || node.data.username) }}
        </el-avatar>
        <span>{{ node.label || node.data.username }}</span>
      </template>
    </TreeFilter>

    <el-card shadow="never" :class="[ns.e('right'), ns.join('card-minimal')]">
      <div class="flx-align-center">
        <el-avatar :size="56" style="font-size: 28px" :style="{ backgroundColor: descriptionData.avatarBgColor }">
          {{ simplifyText(descriptionData.title) }}
        </el-avatar>

        <ProDescriptions
          :title="descriptionData.title"
          :columns="descriptionData.columns"
          :data="descriptionData.data"
          :column="5"
          :class="ns.e('descriptions')"
        />
      </div>

      <ProTabs :columns="tabColumns" />
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/namespace" as *;

@include b(user-link) {
  display: flex;
  width: 100%;
  height: 100%;

  @include e(user) {
    width: 300px;

    :deep(.#{$el-namespace}-tree-node__content) {
      height: 46px !important;
      padding-left: 10px !important;

      .#{$el-namespace}-tree-node__expand-icon {
        display: none;
      }
    }
  }

  @include e(right) {
    width: 100%;

    @include e(descriptions) {
      flex: 1;
      padding-left: 16px;

      :deep(.#{$el-namespace}-descriptions__header) {
        margin-bottom: 10px;
      }
    }
  }
}
</style>
