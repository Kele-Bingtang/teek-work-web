<script setup lang="tsx" name="RoleLink">
import type { DialogFormProps, ProPageInstance, TableColumn, TreeFilterInstance } from "teek";
import type { TreeKey } from "element-plus";
import { ElMessageBox, ElSwitch } from "element-plus";
import { TreeFilter, ProPage, downloadByData } from "teek";
import { getAppTreeList } from "@/common/api/application/app";
import { listPage, addRole, editRole, removeRole, removeBatch, type Role, exportExcel } from "@/common/api/system/role";
import { elFormProps, formColumns } from "@/views/app/role/use-form-columns";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { Description } from "@/components";
import { useNamespace } from "@teek/composables";
import LinkUser from "./components/link-user.vue";
import LinkUserGroup from "./components/link-user-group.vue";
import LinkMenu from "./components/link-menu.vue";
import LinkDept from "./components/link-dept.vue";

const ns = useNamespace("role-link");

const treeFilterInstance = useTemplateRef<TreeFilterInstance>("treeFilterInstance");
const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const activeName = ref("User");
const clickRowInfo = ref<Role.RoleInfo>();
const requestParam = reactive({ appId: "" });

const { statusChange } = useChange(
  "roleName",
  "角色",
  (row, status) => editRole({ id: row.id, roleId: row.roleId, status }),
  () => proPageInstance.value?.search()
);

// 表格行点击回调
const handleRowClick = (row: Role.RoleInfo) => {
  clickRowInfo.value = row;
};

// ProTable 获取数据后的回调
const transformData = (data: Role.RoleInfo[]) => {
  clickRowInfo.value = data[0] || undefined;
  data[0] && proPageInstance.value?.proTableInstance?.getElTableInstance()?.setCurrentRow(data[0]);
};

const columns: TableColumn<Role.RoleInfo>[] = [
  { type: "selection", fixed: "left", width: 40 },
  { prop: "roleCode", label: "角色编码", search: { el: "el-input" } },
  { prop: "roleName", label: "角色名称", search: { el: "el-input" } },
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
  { prop: "operation", label: "操作", width: 130, fixed: "right" },
];

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: formColumns,
  },
  id: ["id", "roleId", "appId"],
  addApi: addRole,
  editApi: editRole,
  removeApi: removeRole,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:role:add"),
  disableEdit: !hasAuth("system:role:edit"),
  disableRemove: !hasAuth("system:role:remove"),
  disableRemoveBatch: !hasAuth("system:role:remove"),
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 450,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const handleTreeChange = (nodeId: string | TreeKey[]) => {
  requestParam.appId = nodeId + "";
};

type TabEnum = {
  name: string;
  label: string;
  components: any;
  elProps: ComputedRef<{
    appId?: string;
    id?: number;
    roleId?: string;
  }>;
};

const tabEnums: TabEnum[] = [
  {
    name: "User",
    label: "关联用户",
    components: LinkUser,
    elProps: computed(() => {
      return {
        appId: requestParam.appId,
        roleId: clickRowInfo.value?.roleId,
      };
    }),
  },
  {
    name: "UserGroup",
    label: "关联用户组",
    components: LinkUserGroup,
    elProps: computed(() => {
      return {
        appId: requestParam.appId,
        roleId: clickRowInfo.value?.roleId,
      };
    }),
  },
  {
    name: "Menu",
    label: "关联菜单",
    components: LinkMenu,
    elProps: computed(() => {
      return {
        appId: requestParam.appId,
        id: clickRowInfo.value?.id,
        roleId: clickRowInfo.value?.roleId,
      };
    }),
  },
  {
    name: "Dept",
    label: "关联部门",
    components: LinkDept,
    elProps: computed(() => {
      return {
        appId: requestParam.appId,
        roleId: clickRowInfo.value?.roleId,
      };
    }),
  },
];

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
    <TreeFilter
      :class="ns.e('tree')"
      ref="treeFilterInstance"
      title="App 清单"
      :requestApi="getAppTreeList"
      @change="handleTreeChange"
      id="appId"
      label="appName"
      :enable-total="false"
      default-first
    >
      <template #default="{ node }">
        <Icon v-if="node.data.icon" :icon="node.data.icon"></Icon>
        <span>{{ node.label }}</span>
      </template>
    </TreeFilter>

    <div :class="ns.e('role')">
      <ProPage
        ref="proPageInstance"
        :request-api="listPage"
        :init-request-params="requestParam"
        :request-immediate="false"
        :columns
        :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 3, xl: 3 } }"
        :dialog-form-props
        :border="false"
        @row-click="handleRowClick"
        :transform-data="transformData"
        highlight-current-row
        :export-file
        :disabled-tool-button="!hasAuth('system:role:export') ? ['export'] : []"
      ></ProPage>
    </div>

    <div v-if="clickRowInfo" :class="ns.e('link')">
      <Description :title="clickRowInfo?.roleName"></Description>

      <el-tabs v-model="activeName" style="height: calc(100% - 70px)">
        <template v-for="item in tabEnums" :key="item.name">
          <el-tab-pane lazy :label="item.label" :name="item.name" style="height: 100%">
            <component :is="item.components" v-bind="item.elProps.value"></component>
          </el-tab-pane>
        </template>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(role-link) {
  display: flex;
  height: 100%;

  @include e(tree) {
    width: 10%;
  }

  @include e(role) {
    width: 45%;
    padding-right: 12px;
    border-right: 1px solid #dfdfdf;
  }

  @include e(link) {
    width: 45%;
    padding-left: 12px;
    background-color: #ffffff;

    :deep(.#{$el-namespace}-tabs__content) {
      height: calc(100% - 60px);
    }
  }
}
</style>
