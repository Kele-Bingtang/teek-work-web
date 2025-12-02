<script setup lang="tsx">
import type { DialogFormProps, ProPageInstance, PageColumn, DialogFormColumn, ElFormProps } from "teek";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { dayjs, ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, downloadByData } from "teek";
import { useChange, usePermission } from "@/composables";
import { useDictStore } from "@/pinia";
import { exportExcel } from "@/common/api/system/user/user-group";
import {
  addUserGroupListToUser,
  editUserGroupUserLink,
  listUserGroupByUserId,
  listWithSelectedByUserId,
  removeUserGroupUserLink,
} from "@/common/api/link/user-group-user-link";

const props = defineProps<{ userId?: string }>();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const initRequestParams = reactive({ userId: props.userId || "" });

watchEffect(() => {
  if (props.userId) initRequestParams.userId = props.userId;
});

const { statusChange } = useChange<UserGroup.Info>(
  "groupName",
  "用户组",
  (row, status) => editUserGroupUserLink({ id: row.linkId, status }),
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
    userGroupIds: [{ required: true, message: "请选择用户组", trigger: "blur" }],
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
    prop: "userGroupIds",
    label: "用户组",
    el: "el-transfer",
    options: () => (initRequestParams.userId ? listWithSelectedByUserId({ userId: initRequestParams.userId }) : []),
    elProps: {
      props: { key: "groupId", label: "groupName" },
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

    return addUserGroupListToUser({ ...model, ...initRequestParams });
  },
  editApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return editUserGroupUserLink({ ...model, id: model.linkId });
  },
  removeApi: removeUserGroupUserLink,
  removeBatchApi: removeUserGroupUserLink,
  editFilterKeys: ["userGroupIds"],
  apiFilterKeys: ["user", "createTime"],
  clickEdit: model => {
    // 根据 expireOn 计算 expireOnNum，如果计算不是整数，则走 custom
    const limit = dayjs(model.expireOn).diff(dayjs(model.validFrom), "month");
    if (limit % 1 !== 0) model.expireOnNum = -1;
    else model.expireOnNum = limit;
  },
  disableAdd: !hasAuth("system:userGroup:add"),
  disableEdit: !hasAuth("system:userGroup:edit"),
  disableRemove: !hasAuth("system:userGroup:remove"),
  disableRemoveBatch: !hasAuth("system:userGroup:remove"),
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
  <ProPage
    ref="proPageInstance"
    :request-api="listUserGroupByUserId"
    :init-request-params
    :request-immediate="false"
    :columns
    :dialog-form-props
    row-key="linkId"
    :export-file
    :disabled-tool-button="!hasAuth('system:userGroup:export') ? ['export'] : []"
  ></ProPage>
</template>
