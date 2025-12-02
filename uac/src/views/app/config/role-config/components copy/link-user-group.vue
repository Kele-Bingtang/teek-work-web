<script setup lang="tsx" name="RoleLinkUserGroup">
import type { DialogFormProps, PageColumn } from "teek";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { ProPage } from "teek";
import {
  listUserGroupByRoleId,
  addUserGroupListToRole,
  removeRoleUserGroupLink,
} from "@/common/api/link/role-user-group-link";
import { usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./link-user-group-form-columns";

export interface LinkUserGroupProps {
  appId: string;
  roleId: string;
}

const props = defineProps<LinkUserGroupProps>();

const requestParam = reactive({ roleId: props.roleId });

// 监听 roleId，变化后修改关联的表格查询默认值
watchEffect(() => (requestParam.roleId = props.roleId));

// 表格列配置项
const columns: PageColumn<UserGroup.LinkInfo>[] = [
  { type: "selection", fixed: "left", width: 10 },
  { prop: "groupName", label: "用户组名", minWidth: 120, search: { el: "el-input", key: "userGroupName" } },
  { prop: "intro", label: "描述", minWidth: 120 },
  {
    prop: "ownerId",
    label: "负责人",
    minWidth: 160,
    search: { el: "el-input", key: "owner" },
    render: ({ row }) => {
      return (
        <>
          {row.ownerName} {row.ownerId}
        </>
      );
    },
  },
  { prop: "createTime", label: "创建时间", minWidth: 160 },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

// 新增、编辑弹框配置项
const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: useFormColumns(requestParam).columns,
  },
  id: ["linkId"],
  addApi: form =>
    addUserGroupListToRole({
      ...form,
      userGroupIds: form.userGroupIds,
      roleId: requestParam.roleId,
      appId: props.appId,
    }),
  removeApi: form => removeRoleUserGroupLink([form.linkId]),
  removeBatchApi: removeRoleUserGroupLink,
  disableAdd: !hasAuth("system:role:linkUserGroup"),
  disableEdit: !hasAuth("system:role:linkUserGroup"),
  disableRemove: !hasAuth("system:role:linkUserGroup"),
  disableRemoveBatch: !hasAuth("system:role:linkUserGroup"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 510,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div class="link-user-container">
    <ProPage
      :request-api="listUserGroupByRoleId"
      :init-request-params="requestParam"
      :columns
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 3, xl: 3 } }"
      :dialog-form-props
      row-key="linkId"
      height="100%"
      :init-show-search="false"
      :disabled-tool-button="!hasAuth('system:role:linkUserGroup') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
.link-user-container {
  height: 100%;
}
</style>
