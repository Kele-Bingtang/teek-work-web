<script setup lang="tsx">
import type { DialogFormProps, ProPageInstance, PageColumn, DialogFormColumn, ElFormProps } from "teek";
import type { Role } from "@/common/api/system/role";
import { dayjs, ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, downloadByData } from "teek";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { exportExcel } from "@/common/api/system/role";
import {
  listRoleLinkByDeptId,
  addDeptListToRole,
  listWithSelectedByDeptId,
  editRoleDeptLink,
  removeDeptRoleLink,
} from "@/common/api/link/role-dept-link";

const props = defineProps<{ deptId?: string }>();
const route = useRoute();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const initRequestParams = reactive({
  appId: route.params.appId as string,
  deptId: props.deptId || "",
});

watchEffect(() => {
  if (props.deptId) initRequestParams.deptId = props.deptId;
});

const { statusChange } = useChange(
  "roleName",
  "角色",
  (row, status) => editRoleDeptLink({ id: row.linkId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<Role.Info>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { type: "index", label: "#", width: 80 },
  { prop: "roleCode", label: "角色编码", search: { el: "el-input" } },
  { prop: "roleName", label: "角色名称", search: { el: "el-input" } },
  { prop: "ownerId", label: "负责人" },
  { prop: "validFrom", label: "生效时间", minWidth: 120 },
  { prop: "expireOn", label: "过期时间", minWidth: 120 },
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
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const elFormProps: ElFormProps = {
  labelWidth: 80,
  rules: {
    validFrom: [{ required: true, message: "请选择生效时间", trigger: "blur" }],
    expireOnNum: [{ required: true, message: "请选择期限", trigger: "blur" }],
    roleIds: [{ required: true, message: "请选择角色", trigger: "blur" }],
  },
};

const formColumns: DialogFormColumn[] = [
  {
    prop: "validFrom",
    label: "生效时间",
    el: "el-date-picker",
    elProps: { placeholder: "请选择生效时间" },
    defaultValue: dayjs().format("YYYY-MM-DD"),
  },
  {
    prop: "expireOnNum",
    label: "期限",
    el: "el-radio-group",
    options: [
      { label: "一个月", value: 1 },
      { label: "三个月", value: 3 },
      { label: "半年", value: 6 },
      { label: "一年", value: 12 },
      { label: "三年", value: 36 },
      { label: "长期", value: 120 },
      { label: "自定义", value: -1 },
    ],
    defaultValue: 36,
  },
  {
    prop: "expireOn",
    label: "过期时间",
    el: "el-date-picker",
    elProps: { placeholder: "请选择过期时间" },
    hidden: model => model.expireOnNum !== -1,
  },
  {
    prop: "roleIds",
    label: "角色",
    el: "el-transfer",
    options: () => (initRequestParams.deptId ? listWithSelectedByDeptId(initRequestParams) : []),
    elProps: {
      props: { key: "roleId", label: "roleName" },
      filterable: true,
      titles: ["Source", "Target"],
    },
    destroyIn: ["edit"],
  },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: (_, status) => (status === "add" ? 470 : 170),
    top: "5vh",
    closeOnClickModal: false,
  },
  form: { elFormProps, columns: formColumns },
  id: ["id", "linkId"],
  addApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return addDeptListToRole({ ...model, ...initRequestParams });
  },
  editApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return editRoleDeptLink({ ...model, id: model.linkId });
  },
  removeApi: removeDeptRoleLink,
  removeBatchApi: removeDeptRoleLink,
  clickEdit: model => {
    // 根据 expireOn 计算 expireOnNum，如果计算不是整数，则走 custom
    const limit = dayjs(model.expireOn).diff(dayjs(model.validFrom), "month");
    if (limit % 1 !== 0) model.expireOnNum = -1;
    else model.expireOnNum = limit;
  },
  disableAdd: !hasAuth("system:role:add"),
  disableEdit: !hasAuth("system:role:edit"),
  disableRemove: !hasAuth("system:role:remove"),
  disableRemoveBatch: !hasAuth("system:role:remove"),
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
  <ProPage
    :request-api="listRoleLinkByDeptId"
    :columns
    :init-request-params
    :request-immediate="false"
    :dialog-form-props
    row-key="linkId"
    :export-file
    :disabled-tool-button="!hasAuth('system:role:export') ? ['export'] : []"
  ></ProPage>
</template>
