<script setup lang="ts">
import type { TreeKey } from "element-plus";
import type { UserGroup } from "@/common/api/system/user/user-group";
import type { DescriptionColumn, TabColumn } from "teek";
import { TreeFilter, ProDescriptions, ProTabs, useNamespace } from "teek";
import { list } from "@/common/api/system/user/user-group";
import LinkUser from "./components/link-user.vue";
import { useDictStore } from "@/pinia";

const ns = useNamespace("user-group-user");

const descriptionColumn: DescriptionColumn[] = [
  { prop: "groupName", label: "用户组名称" },
  { prop: "groupName", label: "用户组编码" },
  { prop: "ownerId", label: "负责人" },
  { prop: "intro", label: "用户组描述" },
  {
    prop: "groupType",
    label: "用户组类型",
    optionField: { value: "dictValue", label: "dictLabel" },
    options: () => useDictStore().getDictData("sys_group_type"),
  },
  { prop: "createTime", label: "创建时间" },
];

const descriptionData = ref<UserGroup.Info>();

const activeName = ref("User");

const tabColumns: TabColumn[] = [
  {
    prop: "User",
    label: "用户清单",
    el: LinkUser,
    elProps: computed(() => ({
      userGroupId: descriptionData.value?.groupId,
    })),
  },
];

// 选择 用户组 清单节点回调
const handleTreeChange = (_: string | TreeKey[], data: UserGroup.Info) => {
  descriptionData.value = data;
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
      :enable-total="false"
      default-first
    ></TreeFilter>

    <div :class="ns.e('right')">
      <ProDescriptions
        v-if="descriptionData"
        title="用户组详情"
        :columns="descriptionColumn"
        :data="descriptionData"
        card
        border
      />

      <ProTabs v-model="activeName" :columns="tabColumns" card style="margin-top: 10px" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(user-group-user) {
  display: flex;
  height: 100%;

  @include e(right) {
    flex: 1;
  }
}
</style>
