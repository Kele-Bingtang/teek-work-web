<script setup lang="tsx" name="Dept">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { Dept } from "@/common/api/system/dept";
import { ElMessageBox, ElSwitch } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { ProPage, downloadByData, useNamespace } from "teek";
import { listDeptTreeTable, addDept, editDept, removeDept, exportExcel } from "@/common/api/system/dept";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, formColumns } from "./form-columns";

const ns = useNamespace("dept");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const { statusChange } = useChange<Dept.TreeTable>(
  "deptName",
  "部门",
  (row, status) => editDept({ id: row.id, deptId: row.deptId, parentId: row.parentId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<Dept.TreeTable>[] = [
  { prop: "deptName", label: "部门名称", align: "left", search: { el: "el-input" } },
  { prop: "orderNum", label: "排序", width: 80 },
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
  { prop: "userCount", label: "成员数量", width: 100 },
  { prop: "leader", label: "领导", width: 120 },
  { prop: "phone", label: "电话", width: 120 },
  { prop: "email", label: "邮箱", width: 120 },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 200, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "deptId"],
  addApi: addDept,
  editApi: editDept,
  removeApi: removeDept,
  disableAdd: !hasAuth("system:dept:add"),
  disableEdit: !hasAuth("system:dept:edit"),
  disableRemove: !hasAuth("system:dept:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 400,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `dept_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listDeptTreeTable"
      :columns
      :dialog-form-props
      :page-scope="false"
      :export-file
      :disabled-tool-button="!hasAuth('system:dept:export') ? ['export'] : []"
    >
      <template #operation-after="{ row, dialogFormInstance }">
        <el-button
          v-auth="['system:dept:add']"
          link
          size="small"
          :icon="Plus"
          @click="dialogFormInstance?.handleAdd({ parentId: row.deptId })"
        >
          新增
        </el-button>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(dept) {
  height: 100%;
}
</style>
