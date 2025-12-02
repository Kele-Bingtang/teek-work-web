<script setup lang="tsx" name="RoleLinkUserGroup">
import type { DialogFormColumn, DialogFormProps, ElFormProps, PageColumn, ProPageInstance } from "teek";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { dayjs, ElSwitch } from "element-plus";
import { ProPage } from "teek";
import {
  listUserGroupByRoleId,
  addUserGroupListToRole,
  removeRoleUserGroupLink,
  listWithSelectedByRoleId,
  editRoleUserGroupLink,
} from "@/common/api/link/role-user-group-link";
import { useChange, usePermission } from "@/composables";
import { useDictStore } from "@/pinia";

export interface LinkUserGroupProps {
  roleId?: string;
}

const props = defineProps<LinkUserGroupProps>();
const route = useRoute();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const initRequestParams = reactive({
  appId: route.params.appId as string,
  roleId: props.roleId,
});

// 监听 roleId，变化后修改关联的表格查询默认值
watchEffect(() => (initRequestParams.roleId = props.roleId));

const { statusChange } = useChange<UserGroup.Info>(
  "groupName",
  "用户组",
  (row, status) => editRoleUserGroupLink({ id: row.linkId, status }),
  () => proPageInstance.value?.search()
);

// 表格列配置项
const columns: PageColumn<UserGroup.Info>[] = [
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
  { prop: "validFrom", label: "生效时间", minWidth: 120 },
  { prop: "expireOn", label: "过期时间", minWidth: 120 },
  {
    prop: "ownerId",
    label: "负责人",
    minWidth: 160,
    formatValue: (_, { row }) => `${row.ownerName} ${row.ownerId}`,
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
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const elFormProps: ElFormProps = {
  labelWidth: 80,
  rules: {
    validFrom: [{ required: true, message: "请选择生效时间", trigger: "blur" }],
    expireOnNum: [{ required: true, message: "请选择期限", trigger: "blur" }],
    userIds: [{ required: true, message: "请选择用户", trigger: "blur" }],
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
    prop: "userIds",
    label: "用户",
    el: "el-transfer",
    options: () => (initRequestParams.roleId ? listWithSelectedByRoleId({ roleId: initRequestParams.roleId }) : []),
    elProps: {
      props: { key: "userId", label: "nickname" },
      filterable: true,
      titles: ["Source", "Target"],
    },
    destroyIn: ["edit"],
  },
];

const { hasAuth } = usePermission();

// 新增、编辑弹框配置项
const dialogFormProps: DialogFormProps = {
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "50%",
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

    return addUserGroupListToRole({ ...model, ...initRequestParams });
  },
  editApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return editRoleUserGroupLink({ ...model, id: model.linkId });
  },
  removeApi: removeRoleUserGroupLink,
  removeBatchApi: removeRoleUserGroupLink,
  clickEdit: model => {
    // 根据 expireOn 计算 expireOnNum，如果计算不是整数，则走 custom
    const limit = dayjs(model.expireOn).diff(dayjs(model.validFrom), "month");
    if (limit % 1 !== 0) model.expireOnNum = -1;
    else model.expireOnNum = limit;
  },
  disableAdd: !hasAuth("system:role:linkUserGroup"),
  disableEdit: !hasAuth("system:role:linkUserGroup"),
  disableRemove: !hasAuth("system:role:linkUserGroup"),
  disableRemoveBatch: !hasAuth("system:role:linkUserGroup"),
};
</script>

<template>
  <ProPage
    ref="proPageInstance"
    :request-api="listUserGroupByRoleId"
    :init-request-params
    :columns
    :dialog-form-props
    row-key="linkId"
    :disabled-tool-button="!hasAuth('system:role:linkUserGroup') ? ['export'] : []"
  ></ProPage>
</template>
