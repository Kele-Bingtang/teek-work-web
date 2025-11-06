<script setup lang="tsx" name="App">
import type { DialogFormProps, ProPageInstance, PageColumn, TreeFilterInstance } from "teek";
import type { TreeKey } from "element-plus";
import { ElMessageBox, ElSwitch } from "element-plus";
import { TreeFilter, ProPage, downloadByData, useNamespace } from "teek";
import { getClientTreeList } from "@/common/api/application/client";
import {
  listAppPage,
  addApp,
  editApp,
  removeApp,
  removeBatch,
  type App,
  exportExcel,
} from "@/common/api/application/app";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./use-form-columns";

const ns = useNamespace("app");

const treeFilterInstance = useTemplateRef<TreeFilterInstance>("treeFilterInstance");
const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const { statusChange } = useChange(
  "appName",
  "应用",
  (row, status) => editApp({ id: row.id, clientId: row.clientId, status }),
  () => proPageInstance.value?.getTableList()
);

const initRequestParams = reactive({
  clientId: "",
});

const columns: PageColumn<App.AppInfo>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { prop: "appName", label: "应用名称", search: { el: "el-input" } },
  { prop: "appCode", label: "应用编码", search: { el: "el-input" } },
  {
    prop: "appType",
    label: "应用类型",
    search: { el: "el-select" },
    options: () => useDictStore().getDictData("sys_app_type"),
    optionField: { value: "dictValue", label: "dictLabel" },
  },
  {
    prop: "status",
    label: "状态",
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
  { prop: "orderNum", label: "显示顺序" },
  { prop: "updateById", label: "更新人", formatValue: (_, { row }) => `${row.updateBy} ${row.updateById}` },
  { prop: "updateTime", label: "更新时间" },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: useFormColumns(
      computed(() => treeFilterInstance.value?.treeData),
      computed(() => initRequestParams.clientId)
    ).columns,
  },
  id: ["id", "appId"],
  addApi: addApp,
  beforeAdd: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  editApi: data => editApp({ ...data, clientId: initRequestParams.clientId || data.clientId }),
  beforeEdit: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  removeApi: removeApp,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:app:add"),
  disableEdit: !hasAuth("system:app:edit"),
  disableRemove: !hasAuth("system:app:remove"),
  disableRemoveBatch: !hasAuth("system:app:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 400,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const handleTreeChange = (nodeId: string | TreeKey[]) => {
  initRequestParams.clientId = nodeId + "";
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `app_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      ref="treeFilterInstance"
      title="客户端清单"
      :requestApi="getClientTreeList"
      @change="handleTreeChange"
      id="clientId"
      label="clientName"
      class="role-tree"
    >
      <template #default="{ node }">
        <Icon v-if="node.data.icon" :icon="node.data.icon"></Icon>
        <span>{{ node.label }}</span>
      </template>
    </TreeFilter>

    <div :class="ns.e('role-table')">
      <ProPage
        ref="proPageInstance"
        :request-api="listAppPage"
        :columns
        :init-request-params="initRequestParams"
        :dialogFormProps="dialogFormProps"
        :export-file
        :disabled-tool-button="!hasAuth('system:app:export') ? ['export'] : []"
      ></ProPage>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(app) {
  display: flex;

  .role-tree {
    min-width: 220px;
  }
}
</style>
