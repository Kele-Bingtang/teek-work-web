<script setup lang="tsx">
import type { DialogFormProps, ProPageInstance, PageColumn } from "@teek/components";
import type { User } from "@/common/api/user/user";
import { ElInput, ElMessageBox, ElSwitch } from "element-plus";
import { Key } from "@element-plus/icons-vue";
import { ProPage, downloadByData, useNamespace, useDialog } from "teek";
import { addUser, editUser, removeUser, removeBatch, listPage, exportExcel } from "@/common/api/user/user";
import { useChange, usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./use-form-columns";
import { useDictStore } from "@/pinia";
import { listDeptTreeList } from "@/common/api/system/dept";

const ns = useNamespace("user");

// 部门管理的用户列表需要传入
const props = defineProps<{ initRequestParams?: Recordable }>();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const newPassword = ref("");

const { hasAuth } = usePermission();

const { open } = useDialog();

const { statusChange } = useChange<User.UserInfo>(
  "username",
  "用户",
  (row, status) => editUser({ id: row.id, userId: row.userId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<User.UserInfo>[] = [
  { type: "selection", fixed: "left", width: 60 },
  { type: "index", label: "#", width: 60 },
  { prop: "username", label: "用户名称", width: 170, search: { el: "el-input" } },
  { prop: "nickname", label: "用户昵称", width: 170, search: { el: "el-input" } },
  {
    prop: "dept.deptName",
    label: "部门",
    width: 170,
    isFilterOptions: false,
    search: { el: "el-tree-select", prop: "deptId", elProps: { showCheckbox: true, checkStrictly: true } },
    options: () => listDeptTreeList(),
  },
  { prop: "phone", label: "手机号码", width: 130, search: { el: "el-input" } },
  { prop: "email", label: "邮箱", width: 170, search: { el: "el-input" } },
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
  { prop: "registerTime", width: 160, label: "注册时间" },
  { prop: "operation", label: "操作", width: 220, fixed: "right" },
];

const formColumns = useFormColumns(computed(() => props.initRequestParams?.deptId));

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: formColumns,
  },
  id: ["id", "userId"],
  addApi: addUser,
  editApi: editUser,
  editFilterKeys: ["dept", "disabled", "loginIp", "loginTime", "registerTime"],
  removeApi: removeUser,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:user:add"),
  disableEdit: !hasAuth("system:user:edit"),
  disableRemove: !hasAuth("system:user:remove"),
  disableRemoveBatch: !hasAuth("system:user:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 700,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const resetPassword = () => {
  open({
    title: "重置密码",
    onCancel: () => (newPassword.value = ""),
    onConfirm: handleConfirm,
    render: () => {
      return <ElInput v-model={newPassword.value} placeholder="请输入新密码"></ElInput>;
    },
  });
};

const handleConfirm = () => {
  if (!newPassword.value) {
    return;
  }
  // editOne({ id: row.id, userId: row.userId, password: newPassword.value }).then(() => {
  //   proPageInstance.value?.search();
  // });
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `user_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listPage"
      :init-request-params
      :columns
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:user:export') ? ['export'] : []"
    >
      <template #operation-after>
        <el-button v-auth="['system:user:passwordReset']" link size="small" :icon="Key" @click="resetPassword">
          重置密码
        </el-button>
      </template>
    </ProPage>
  </div>
</template>
