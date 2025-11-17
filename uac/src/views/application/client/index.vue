<script setup lang="tsx" name="Client">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { Client } from "@/common/api/application/client";
import { ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, downloadByData, useNamespace } from "teek";
import {
  listPage,
  addClient,
  editClient,
  removeClient,
  removeBatch,
  exportExcel,
} from "@/common/api/application/client";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, formColumns } from "./form-columns";

const ns = useNamespace("client");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const { statusChange } = useChange(
  "clientName",
  "客户端",
  (row, status) => editClient({ id: row.id, clientId: row.clientId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<Client.Info>[] = [
  { type: "selection", fixed: "left", width: 40 },
  { prop: "clientId", label: "客户端 ID", width: 270, search: { el: "el-input" } },
  { prop: "clientKey", label: "客户端 Key", width: 160, search: { el: "el-input" } },
  { prop: "clientSecret", label: "客户端秘钥", width: 270, search: { el: "el-input" } },
  { prop: "clientName", label: "客户端名称", width: 160, search: { el: "el-input" } },
  {
    prop: "grantTypeList",
    label: "授权类型",
    align: "left",
    minWidth: 120,
    options: () => useDictStore().getDictData("sys_grant_type"),
    tag: true,
    optionField: { value: "dictValue", label: "dictLabel" },
    search: {
      el: "el-select",
      elProps: { multiple: true, collapseTags: true, collapseTagsTooltip: true, maxCollapseTags: 2 },
    },
  },
  { prop: "activeTimeout", label: "Token 活跃超时时间", width: 170 },
  { prop: "timeout", label: "Token 固定超时时间", width: 170 },
  {
    prop: "status",
    label: "状态",
    width: 80,
    optionField: { value: "dictValue", label: "dictLabel" },
    options: () => useDictStore().getDictData("sys_normal_status"),
    search: { el: "el-select" },
    render: ({ row }) => {
      return (
        <>
          {row.status !== undefined && (
            <ElSwitch
              v-model={row.status}
              activeValue={1}
              inactiveValue={0}
              activeText="启用"
              inactiveText="停用"
              inlinePrompt
              onChange={value => statusChange(value, row)}
            />
          )}
        </>
      );
    },
  },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "clientId"],
  addApi: addClient,
  editApi: editClient,
  removeApi: removeClient,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:client:add"),
  disableEdit: !hasAuth("system:client:edit"),
  disableRemove: !hasAuth("system:client:remove"),
  disableRemoveBatch: !hasAuth("system:client:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 350,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `client_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns">
    <ProPage
      ref="proPageInstance"
      :request-api="listPage"
      :columns
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:client:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(client) {
  height: 100%;
}
</style>
