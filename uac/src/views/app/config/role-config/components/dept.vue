<script setup lang="tsx">
import type { FormColumn } from "teek";
import type { Dept } from "@/common/api/system/dept";
import { listDeptTreeList } from "@/common/api/system/dept";
import { listDeptListByRoleId, listDeptIdsByRoleId, addDeptListToRole } from "@/common/api/link/role-dept-link";
import { ProForm, Tree, useDialog } from "teek";

export interface LinkDeptProps {
  roleId?: string;
}

const props = withDefaults(defineProps<LinkDeptProps>(), {
  roleId: "",
});
const route = useRoute();

const data = ref<Dept.TreeList[]>([]);
const form = ref<{ selectedDeptIds: string[] }>({ selectedDeptIds: [] });
const selectedDeptIds = ref<string[]>([]);

const initRequestParams = reactive({
  appId: route.params.appId as string,
});

const columns: FormColumn[] = [
  {
    prop: "selectedDeptIds",
    label: "",
    options: () => listDeptTreeList(),
    el: "tree",
    elProps: { nodeKey: "value", search: true, checkbox: true },
  },
];

const initTreeData = async () => {
  const [treeData, deptIds] = await Promise.all([
    listDeptListByRoleId(initRequestParams.appId, props.roleId),
    listDeptIdsByRoleId(initRequestParams.appId, props.roleId),
  ]);
  data.value = treeData.data || [];
  form.value.selectedDeptIds = deptIds.data;
  selectedDeptIds.value = deptIds.data;
};

watch(() => props.roleId, initTreeData, { immediate: true });

const { open } = useDialog();

const handleEdit = () => {
  form.value.selectedDeptIds = selectedDeptIds.value;
  open({
    title: "编辑部门",
    height: 500,
    onCancel: handleCancel,
    onConfirm: handleConfirm,
    render: () => {
      return <ProForm v-model={form.value} columns={columns} showFooter={false} style="padding: 10px" />;
    },
  });
};

const handleCancel = () => {
  form.value = { selectedDeptIds: [] };
};

const handleConfirm = async () => {
  await addDeptListToRole({
    roleId: props.roleId,
    appId: initRequestParams.appId,
    deptIds: form.value.selectedDeptIds,
  });
  initTreeData();
};
</script>

<template>
  <div>
    <div style="display: flex; align-items: center; margin-bottom: 10px">
      <el-button v-auth="['system:role:linkDept']" type="primary" @click="handleEdit">编辑</el-button>
      <el-button @click="initTreeData()">刷新</el-button>
      <el-alert title="绿色代表已授权部门，黑色代表未授权部门" :closable="false" style="margin: 0 10px" />
    </div>
    <Tree :data="data" node-key="value" checkbox search :select="false">
      <template #default="{ data }">
        <span :class="data.class">{{ data.label }}</span>
      </template>
    </Tree>
  </div>
</template>

<style lang="scss" scoped>
.selected {
  color: var(--el-color-success);
}
</style>
