<script setup lang="tsx" name="LinkMenu">
import type { FormColumn } from "teek";
import type { Menu } from "@/common/api/system/menu";
import { ProForm, Tree, useDialog } from "teek";
import { listMenuTreeSelectByApp } from "@/common/api/system/menu";
import { listMenuListByRoleId, listMenuIdsByRoleId, addMenusToRole } from "@/common/api/link/role-menu-link";

export interface LinkMenuProps {
  appId: string;
  id: number;
  roleId: string;
}

const props = defineProps<LinkMenuProps>();

const data = ref<Menu.TreeList[]>([]);
const form = ref<{ selectedMenuIds: string[] }>({ selectedMenuIds: [] });
const selectedMenuIds = ref<string[]>([]);

const initTreeData = async (appId = props.appId, roleId = props.roleId) => {
  const [treeData, menuIds] = await Promise.all([
    listMenuListByRoleId(appId, roleId),
    listMenuIdsByRoleId(props.appId, props.roleId),
  ]);
  data.value = treeData.data || [];
  form.value.selectedMenuIds = menuIds.data;
  selectedMenuIds.value = menuIds.data;
};

watchEffect(() => initTreeData(props.appId, props.roleId));

const { open } = useDialog();

const handleEdit = () => {
  form.value.selectedMenuIds = selectedMenuIds.value;
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
  form.value = { selectedMenuIds: [] };
};

const handleConfirm = async () => {
  await addMenusToRole({
    roleId: props.roleId,
    appId: props.appId,
    selectedMenuIds: form.value.selectedMenuIds,
  });
  initTreeData();
};

const columns: FormColumn[] = [
  {
    prop: "selectedMenuIds",
    label: "",
    el: "el-tree",
    options: () => listMenuTreeSelectByApp({ appId: props.appId }),
    elProps: { nodeKey: "value", search: true, checkbox: true },
  },
];
</script>

<template>
  <div style="display: flex; align-items: center; margin-bottom: 10px">
    <el-button v-auth="['system:role:linkMenu']" type="primary" @click="handleEdit">编辑</el-button>
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
