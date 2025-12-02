<script setup lang="tsx">
import type { FormColumn } from "teek";
import type { Resource } from "@/common/api/system/resource";
import { ProForm, Tree, useDialog } from "teek";
import { listResourceTreeSelectByApp } from "@/common/api/system/resource";
import {
  listResourceListByRoleId,
  listResourceIdsByRoleId,
  addResourceToRole,
} from "@/common/api/link/role-resource-link";

export interface LinkMenuProps {
  appId: string;
  id: number;
  roleId: string;
}

const props = defineProps<LinkMenuProps>();

const data = ref<Resource.TreeList[]>([]);
const form = ref<{ selectedResourceIds: string[] }>({ selectedResourceIds: [] });
const selectedResourceIds = ref<string[]>([]);

const initTreeData = async (appId = props.appId, roleId = props.roleId) => {
  const [treeData, resourceIds] = await Promise.all([
    listResourceListByRoleId(appId, roleId),
    listResourceIdsByRoleId(props.appId, props.roleId),
  ]);
  data.value = treeData.data || [];
  form.value.selectedResourceIds = resourceIds.data;
  selectedResourceIds.value = resourceIds.data;
};

watchEffect(() => initTreeData(props.appId, props.roleId));

const { open } = useDialog();

const handleEdit = () => {
  form.value.selectedResourceIds = selectedResourceIds.value;
  open({
    title: "编辑菜单",
    height: 500,
    onCancel: handleCancel,
    onConfirm: handleConfirm,
    render: () => {
      return <ProForm v-model={form.value} elFormProps={{ labelWidth: 80 }} columns={columns} />;
    },
  });
};

const handleCancel = () => {
  form.value = { selectedResourceIds: [] };
};

const handleConfirm = async () => {
  await addResourceToRole({
    roleId: props.roleId,
    appId: props.appId,
    selectedResourceIds: form.value.selectedResourceIds,
  });
  initTreeData();
};

const columns: FormColumn[] = [
  {
    prop: "selectedResourceIds",
    label: "",
    el: "el-tree",
    options: () => listResourceTreeSelectByApp({ appId: props.appId }),
    elProps: { nodeKey: "value", search: true, checkbox: true },
  },
];
</script>

<template>
  <div style="display: flex; align-items: center; margin-bottom: 10px">
    <el-button v-auth="['system:role:linkResource']" type="primary" @click="handleEdit">编辑</el-button>
    <el-button @click="initTreeData()">刷新</el-button>
    <el-alert title="蓝色代表已关联的菜单，黑色代表未关联的菜单" :closable="false" style="margin: 0 10px" />
  </div>
  <Tree :data="data" node-key="value" checkbox search :select="false">
    <template #default="{ data }">
      <span :class="data.class">{{ data.label }}</span>
    </template>
  </Tree>
</template>

<style lang="scss" scoped>
.selected {
  color: var(--el-color-primary);
}
</style>
