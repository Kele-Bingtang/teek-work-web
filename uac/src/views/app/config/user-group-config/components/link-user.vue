<script setup lang="tsx" name="UserGroupLinkUser">
import type { DialogFormProps, PageColumn } from "teek";
import type { User } from "@/common/api/system/user/user";
import { ProPage } from "teek";
import {
  listUserLinkByGroupId,
  addUsersToGroup,
  removeUserFromUserGroup,
  editUserGroupUserLink,
} from "@/common/api/link/user-group-user-link";
import { usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./link-user-form-columns";

export interface LinkUserProps {
  appId: string;
  userGroupId: string;
}

const props = defineProps<LinkUserProps>();

const requestParam = reactive({ userGroupId: props.userGroupId });

// 监听 userGroupId，变化后修改关联的表格查询默认值
watchEffect(() => (requestParam.userGroupId = props.userGroupId));

// 表格列配置项
const columns: PageColumn<User.LinkInfo>[] = [
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
    addUsersToGroup({
      ...form,
      userIds: form.userIds,
      userGroupId: requestParam.userGroupId,
      appId: props.appId,
    }),
  editApi: form => editUserGroupUserLink({ ...form, id: form.linkId }),
  editFilterKeys: ["userId", "appId", "userIds"],
  removeApi: form => removeUserFromUserGroup([form.linkId]),
  removeBatchApi: removeUserFromUserGroup,
  disableAdd: !hasAuth("system:userGroup:linkUser"),
  disableEdit: !hasAuth("system:userGroup:linkUser"),
  disableRemove: !hasAuth("system:userGroup:linkUser"),
  disableRemoveBatch: !hasAuth("system:userGroup:linkUser"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 170,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div class="link-user-container">
    <ProPage
      :request-api="listUserLinkByGroupId"
      :init-request-params="requestParam"
      :columns
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 3, xl: 3 } }"
      :dialog-form-props
      row-key="linkId"
      height="100%"
      :init-show-search="false"
      :disabled-tool-button="!hasAuth('system:userGroup:linkUser') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
.link-user-container {
  height: 100%;
}
</style>
