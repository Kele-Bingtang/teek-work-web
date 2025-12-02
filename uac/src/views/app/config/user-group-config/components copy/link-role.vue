<script setup lang="tsx" name="UserGroupLinkRole">
import type { DialogFormProps, PageColumn } from "teek";
import type { Role } from "@/common/api/system/role";
import { ProTable } from "teek";
import { elFormProps, useFormColumns } from "./link-role-form-columns";
import {
  listRoleLinkByGroupId,
  removeRoleUserGroupLink,
  addRoleListToUserGroup,
} from "@/common/api/link/role-user-group-link";
import { usePermission } from "@/composables";

export interface LinkRoleProps {
  appId: string;
  userGroupId: string;
}

const props = defineProps<LinkRoleProps>();

const requestParam = reactive({ userGroupId: props.userGroupId });

// 监听 userGroupId，变化后修改关联的表格查询默认值
watchEffect(() => (requestParam.userGroupId = props.userGroupId));

// 表格列配置项
const columns: PageColumn<Role.LinkInfo>[] = [
  { type: "selection", fixed: "left", width: 10 },
  { prop: "roleName", label: "角色名称", minWidth: 120, search: { el: "el-input" } },
  { prop: "roleCode", label: "角色编码", minWidth: 120, search: { el: "el-input" } },
  { prop: "createTime", label: "创建时间", minWidth: 160 },
  { prop: "operation", label: "操作", width: 100, fixed: "right" },
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
    addRoleListToUserGroup({
      ...form,
      roleIds: form.roleIds,
      userGroupId: requestParam.userGroupId,
      appId: props.appId,
    }),
  addFilterKeys: ["role"],
  removeApi: form => removeRoleUserGroupLink([form.linkId]),
  removeBatchApi: removeRoleUserGroupLink,
  disableAdd: !hasAuth("system:userGroup:linkRole"),
  disableEdit: !hasAuth("system:userGroup:linkRole"),
  disableRemove: !hasAuth("system:userGroup:linkRole"),
  disableRemoveBatch: !hasAuth("system:userGroup:linkRole"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 520,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div class="link-role-container">
    <ProTable
      :request-api="listRoleLinkByGroupId"
      :init-request-params="requestParam"
      :columns
      :search-cols="{ xs: 1, sm: 1, md: 3, lg: 3, xl: 3 }"
      :dialog-form-props
      row-key="linkId"
      height="100%"
      :init-show-search="false"
      :disabled-tool-button="!hasAuth('system:userGroup:linkRole') ? ['export'] : []"
    ></ProTable>
  </div>
</template>

<style lang="scss" scoped>
.link-role-container {
  height: 100%;
}
</style>
