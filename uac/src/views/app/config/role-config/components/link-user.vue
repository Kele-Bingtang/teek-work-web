<script setup lang="tsx" name="RoleLinkUser">
import type { DialogFormProps, PageColumn } from "teek";
import type { User } from "@/common/api/system/user/user";
import { ProPage } from "teek";
import { addUsersToRole, editUserRoleLink, removeUsersFromRole } from "@/common/api/link/user-role-link";
import { listUserLinkByRoleId } from "@/common/api/link/user-role-link";
import { usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./link-user-form-columns";

export interface LinkUserProps {
  appId: string;
  roleId: string;
}

const props = defineProps<LinkUserProps>();

const requestParam = reactive({ roleId: props.roleId });

// 监听 roleId，变化后修改关联的表格查询默认值
watchEffect(() => (requestParam.roleId = props.roleId));

// 表格列配置项
const columns: PageColumn<User.Info>[] = [
  { type: "selection", fixed: "left", width: 10 },
  { prop: "username", label: "用户名称", minWidth: 120, search: { el: "el-input" } },
  { prop: "nickname", label: "用户昵称", minWidth: 120, search: { el: "el-input" } },
  { prop: "validFrom", label: "生效时间", minWidth: 120 },
  { prop: "expireOn", label: "过期时间", minWidth: 120 },
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
    addUsersToRole({
      ...form,
      userIds: form.userIds,
      roleId: requestParam.roleId,
      appId: props.appId,
    }),
  editApi: form => editUserRoleLink({ ...form, id: form.linkId }),
  editFilterKeys: ["userId", "appId", "userIds"],
  removeApi: form => removeUsersFromRole([form.linkId]),
  removeBatchApi: removeUsersFromRole,
  disableAdd: !hasAuth("system:role:linkUser"),
  disableEdit: !hasAuth("system:role:linkUser"),
  disableRemove: !hasAuth("system:role:linkUser"),
  disableRemoveBatch: !hasAuth("system:role:linkUser"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 200,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div class="link-user-container">
    <ProPage
      :request-api="listUserLinkByRoleId"
      :init-request-params="requestParam"
      :columns
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 3, xl: 3 } }"
      :dialog-form-props
      row-key="linkId"
      height="100%"
      :init-show-search="false"
      :disabled-tool-button="!hasAuth('system:role:linkUser') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
.link-user-container {
  height: 100%;
}
</style>
