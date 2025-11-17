<script setup lang="tsx">
import type { DialogFormColumn, DialogFormProps, ElFormProps, PageColumn, ProPageInstance } from "@teek/components";
import type { User } from "@/common/api/system/user/user";
import { dayjs, ElSwitch } from "element-plus";
import { ProPage, useNamespace } from "teek";
import {
  listUserLinkByGroupId,
  listWithDisabledByGroupId,
  addUsersToGroup,
  editUserGroupUserLink,
  removeUserFromUserGroup,
} from "@/common/api/link/user-group-user-link";
import { useChange, usePermission } from "@/composables";
import { useDictStore } from "@/pinia";

const ns = useNamespace("user-group-user-link");

const props = defineProps<{ userGroupId?: string }>();

const initRequestParams = reactive({ userGroupId: props.userGroupId });

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

// 监听 userGroupId，变化后修改关联的表格查询默认值
watchEffect(() => {
  if (props.userGroupId) initRequestParams.userGroupId = props.userGroupId;
});

const { statusChange } = useChange<User.UserInfo>(
  "username",
  "用户",
  (row, status) => editUserGroupUserLink({ id: row.linkId, validFrom: row.validFrom, expireOn: row.expireOn, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<User.UserInfo>[] = [
  { type: "selection", fixed: "left", width: 60 },
  { type: "index", label: "#", width: 60 },
  { prop: "username", label: "用户名称", search: { el: "el-input" } },
  { prop: "nickname", label: "用户昵称", search: { el: "el-input" } },
  { prop: "validFrom", label: "生效时间", minWidth: 120 },
  { prop: "expireOn", label: "过期时间", minWidth: 120 },
  {
    prop: "status",
    width: 160,
    label: "是否有效",
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
  { prop: "createTime", width: 160, label: "注册时间" },
  { prop: "operation", label: "操作", width: 220, fixed: "right" },
];

const elFormProps: ElFormProps = {
  labelWidth: 80,
  rules: {
    userIds: [{ required: true, message: "请选择用户", trigger: "blur" }],
    validFrom: [{ required: true, message: "请选择生效时间", trigger: "blur" }],
    expireOnNum: [{ required: true, message: "请选择期限", trigger: "blur" }],
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
    label: "用户列表",
    el: "el-transfer",
    options: () =>
      initRequestParams.userGroupId ? listWithDisabledByGroupId({ userGroupId: initRequestParams.userGroupId }) : [],
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
  form: {
    elFormProps,
    columns: formColumns,
  },
  id: ["id", "linkId"],
  addApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return addUsersToGroup({
      ...model,
      userGroupId: initRequestParams.userGroupId,
    });
  },
  editApi: model => {
    if (model.expireOnNum !== -1) {
      model.expireOn = dayjs(model.validFrom).add(model.expireOnNum, "month").format("YYYY-MM-DD");
      delete model.expireOnNum;
    }

    return editUserGroupUserLink({ ...model, id: model.linkId });
  },
  clickEdit: model => {
    // 根据 expireOn 计算 expireOnNum，如果计算不是整数，则走 custom
    const limit = dayjs(model.expireOn).diff(dayjs(model.validFrom), "month");
    if (limit % 1 !== 0) model.expireOnNum = -1;
    else model.expireOnNum = limit;
  },
  editFilterKeys: ["userIds"],
  removeApi: model => removeUserFromUserGroup([model.linkId]),
  removeBatchApi: removeUserFromUserGroup,
  disableAdd: !hasAuth("system:userGroup:linkUser"),
  disableEdit: !hasAuth("system:userGroup:linkUser"),
  disableRemove: !hasAuth("system:userGroup:linkUser"),
  disableRemoveBatch: !hasAuth("system:userGroup:linkUser"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "50%",
    height: (_, status) => (status === "add" ? 450 : 170),
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listUserLinkByGroupId"
      :init-request-params
      :request-immediate="false"
      :columns
      :dialog-form-props
      :card="false"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/namespace" as *;

@include b(user-group-user-link) {
  :deep(.#{$el-namespace}-transfer) {
    --el-transfer-panel-width: 300px;
  }
}
</style>
