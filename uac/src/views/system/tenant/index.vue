<script setup lang="tsx" name="Tenant">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { Tenant } from "@/common/api/system/tenant";
import { ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, Icon, downloadByData, useNamespace } from "teek";
import { listPage, addTenant, editTenant, removeTenant, removeBatch, exportExcel } from "@/common/api/system/tenant";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, formColumns } from "./form-columns";

const ns = useNamespace("tenant");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const { statusChange } = useChange<Tenant.TenantInfo>(
  "tenantName",
  "租户",
  (row, status) => editTenant({ id: row.id, tenantId: row.tenantId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<Tenant.TenantInfo>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { prop: "tenantId", label: "租户编号", search: { el: "el-input" } },
  { prop: "tenantName", label: "企业名称", search: { el: "el-input" } },
  { prop: "icon", label: "企业图标", width: 100, render: ({ row }) => row.icon && <Icon icon={row.icon}></Icon> },
  { prop: "contactUserName", label: "联系人" },
  { prop: "contactPhone", label: "联系电话" },
  { prop: "founder", label: "企业创始人" },
  { prop: "licenseNumber", label: "社会信用代码" },
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
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "tenantId"],
  addApi: addTenant,
  editApi: editTenant,
  removeApi: removeTenant,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:tenant:add"),
  disableEdit: !hasAuth("system:tenant:edit"),
  disableRemove: !hasAuth("system:tenant:remove"),
  disableRemoveBatch: !hasAuth("system:tenant:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 450,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `tenant_${new Date().getTime()}.xlsx`);
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
      class="pro-table"
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:tenant:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(tenant) {
  height: 100%;
}
</style>
