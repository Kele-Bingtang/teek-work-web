<script setup lang="tsx" name="Role">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { Role } from "@/common/api/system/role";
import { ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, downloadByData, useNamespace } from "teek";
import { listPage, addRole, editRole, removeRole, removeBatch, exportExcel } from "@/common/api/system/role";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./use-form-columns";
import { listMenuIdsByRoleId } from "@/common/api/link/role-menu-link";

const ns = useNamespace("role");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const route = useRoute();

const { statusChange } = useChange(
  "roleName",
  "角色",
  (row, status) => editRole({ id: row.id, roleId: row.roleId, status }),
  () => proPageInstance.value?.search()
);

const initRequestParams = reactive({
  appId: route.params.appId as string,
});

const columns: PageColumn<Role.Info>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { type: "index", label: "#", width: 80 },
  { prop: "roleCode", label: "角色编码", search: { el: "el-input" } },
  { prop: "roleName", label: "角色名称", search: { el: "el-input" } },
  { prop: "ownerId", label: "负责人" },
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
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: useFormColumns().columns,
  },
  id: ["id", "roleId"],
  addApi: data => addRole({ ...data, appId: initRequestParams.appId }),
  editApi: editRole,
  removeApi: removeRole,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:role:add"),
  disableEdit: !hasAuth("system:role:edit"),
  disableRemove: !hasAuth("system:role:remove"),
  disableRemoveBatch: !hasAuth("system:role:remove"),
  clickEdit: async model => {
    // 编辑时获取该角色绑定的菜单 ID 集合
    const res = await listMenuIdsByRoleId(model.appId, model.roleId);
    model.selectedMenuIds = res.data || [];
  },
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
      downloadByData(res, `role_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proTableRef"
      :request-api="listPage"
      :columns
      :init-request-params="initRequestParams"
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:role:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>
