<script setup lang="tsx" name="UserGroup">
import type { DialogFormProps, ProPageInstance, PageColumn } from "teek";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { ElMessageBox, type TreeKey } from "element-plus";
import {
  listPage,
  addUserGroup,
  editUserGroup,
  removeUserGroup,
  removeBatch,
  exportExcel,
} from "@/common/api/system/user/user-group";
import { ProPage, TreeFilter, downloadByData, useNamespace } from "teek";
import { getAppTreeList } from "@/common/api/application/app";
import { usePermission } from "@/composables";
import { Description } from "@/components";
import { elFormProps, useFormColumns } from "./use-form-columns";
import LinkUser from "./components/link-user.vue";
import LinkRole from "./components/link-role.vue";

const ns = useNamespace("user-group");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const requestParam = reactive({ appId: "" });

const activeName = ref("User");
const clickRowInfo = ref<UserGroup.Info>();

const descriptionData = ref([{ label: "负责人：", value: "", span: 7 }]);

// 表格行点击回调
const handleRowClick = (row: UserGroup.Info) => {
  clickRowInfo.value = row;
  descriptionData.value[0].value = row?.ownerId ? `${row?.ownerName} ${row?.ownerId}` : "";
};

// ProTable 获取数据后的回调
const transformData = (data: UserGroup.Info[]) => {
  clickRowInfo.value = clickRowInfo.value || data[0] || undefined;
  descriptionData.value[0].value = data[0]?.ownerId ? `${data[0]?.ownerName} ${data[0]?.ownerId}` : "";
  data[0] && proPageInstance.value?.proTableInstance?.getElTableInstance()?.setCurrentRow(data[0]);
};

// 表格列配置项
const columns: PageColumn<UserGroup.Info>[] = [
  { type: "selection", fixed: "left", width: 10 },
  { prop: "groupName", label: "用户组名", minWidth: 120, search: { el: "el-input" } },
  { prop: "intro", label: "描述", minWidth: 120 },
  {
    prop: "ownerId",
    label: "负责人",
    minWidth: 160,
    render: ({ row }) => {
      return (
        <>
          {row.ownerName} {row.ownerId}
        </>
      );
    },
  },
  { prop: "createTime", label: "创建时间", minWidth: 160 },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

// 新增、编辑弹框配置项
const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: useFormColumns().columns,
  },
  id: ["id", "groupId"],
  addApi: form => addUserGroup({ ...form, appId: requestParam.appId }),
  beforeAdd: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  editApi: editUserGroup,
  beforeEdit: form => {
    form.ownerId = form.user?.username;
    form.ownerName = form.user?.nickname;
  },
  disableAdd: !hasAuth("system:userGroup:add"),
  disableEdit: !hasAuth("system:userGroup:edit"),
  disableRemove: !hasAuth("system:userGroup:remove"),
  disableRemoveBatch: !hasAuth("system:userGroup:remove"),
  removeApi: removeUserGroup,
  removeBatchApi: removeBatch,
  apiFilterKeys: ["user", "createTime"],
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 150,
    top: "5vh",
    closeOnClickModal: false,
  },
};

// 选择 App 清单节点回调
const handleTreeChange = (value: string | TreeKey[]) => {
  requestParam.appId = value + "";
};

type TabEnum = {
  name: string;
  label: string;
  components: any;
  elProps: ComputedRef<{
    appId?: string;
    groupId?: string;
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
        userGroupId: clickRowInfo.value?.groupId,
      };
    }),
  },
  {
    name: "Role",
    label: "关联角色",
    components: LinkRole,
    elProps: computed(() => {
      return {
        appId: requestParam.appId,
        userGroupId: clickRowInfo.value?.groupId,
      };
    }),
  },
];

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `userGroup_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      :class="ns.e('tree')"
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

    <div :class="ns.e('box')">
      <ProPage
        ref="proPageInstance"
        :request-api="listPage"
        :init-request-params="requestParam"
        :request-immediate="false"
        :columns
        :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 3, xl: 3 } }"
        :dialog-form-props
        @row-click="handleRowClick"
        :transform-data="transformData"
        highlight-current-row
        :export-file
        :disabled-tool-button="!hasAuth('system:userGroup:export') ? ['export'] : []"
      ></ProPage>
    </div>

    <div :class="ns.e('link')" v-if="clickRowInfo">
      <Description :title="clickRowInfo?.groupName" :data="descriptionData"></Description>

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
@use "@teek/styles/mixins/namespace" as *;

@include b(user-group) {
  display: flex;
  height: 100%;

  @include e(tree) {
    width: 10%;
  }

  @include e(box) {
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
