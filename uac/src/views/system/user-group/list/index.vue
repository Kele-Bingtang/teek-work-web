<script setup lang="tsx" name="UserGroup">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { UserGroup } from "@/common/api/user/userGroup";
import { ElMessageBox } from "element-plus";
import {
  listPage,
  addUserGroup,
  editUserGroup,
  removeUserGroup,
  removeBatch,
  exportExcel,
} from "@/common/api/user/userGroup";
import { ProPage, downloadByData, useNamespace } from "teek";
import { usePermission } from "@/composables";
import { useDictStore } from "@/pinia";
import { elFormProps, formColumns } from "./use-form-columns";

const ns = useNamespace("user-group");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

// 表格列配置项
const columns: PageColumn<UserGroup.UserGroupInfo>[] = [
  { type: "selection", fixed: "left", width: 10 },
  { prop: "groupName", label: "用户组名", minWidth: 120, search: { el: "el-input" } },
  { prop: "groupId", label: "用户组编码", minWidth: 120, search: { el: "el-input" } },
  {
    prop: "groupType",
    label: "用户组类型",
    width: 100,
    search: { el: "el-select" },
    optionField: { value: "dictValue", label: "dictLabel" },
    options: () => useDictStore().getDictData("sys_group_type"),
  },
  { prop: "intro", label: "描述", minWidth: 120 },
  {
    prop: "ownerId",
    label: "负责人",
    minWidth: 160,
    formatValue: (_, { row }) => `${row.ownerName} ${row.ownerId}`,
  },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

// 新增、编辑弹框配置项
const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: formColumns,
  },
  id: ["id", "groupId"],
  addApi: addUserGroup,
  beforeAdd: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  editApi: editUserGroup,
  beforeEdit: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  disableAdd: !hasAuth("system:userGroup:add"),
  disableEdit: !hasAuth("system:userGroup:edit"),
  disableRemove: !hasAuth("system:userGroup:remove"),
  disableRemoveBatch: !hasAuth("system:userGroup:remove"),
  removeApi: removeUserGroup,
  removeBatchApi: removeBatch,
  apiFilterKeys: ["user", "createTime"],
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 250,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `userGroup_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listPage"
      :columns
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:userGroup:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>
