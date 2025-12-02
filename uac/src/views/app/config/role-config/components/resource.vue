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

export interface LinkResourceProps {
  roleId?: string;
}

const props = withDefaults(defineProps<LinkResourceProps>(), {
  roleId: "",
});
const route = useRoute();

const data = ref<Resource.TreeList[]>([]);
const form = ref<{ selectedResourceIds: string[] }>({ selectedResourceIds: [] });
const selectedResourceIds = ref<string[]>([]);

const initRequestParams = reactive({
  appId: route.params.appId as string,
});

const columns: FormColumn[] = [
  {
    prop: "selectedResourceIds",
    label: "",
    options: () => listResourceTreeSelectByApp({ appId: initRequestParams.appId }),
    el: "tree",
    elProps: { nodeKey: "value", search: true, checkbox: true },
  },
];

const initTreeData = async () => {
  const [treeData, resourceIds] = await Promise.all([
    listResourceListByRoleId(initRequestParams.appId, props.roleId),
    listResourceIdsByRoleId(initRequestParams.appId, props.roleId),
  ]);

  data.value = treeData.data || [];
  form.value.selectedResourceIds = resourceIds.data;
  selectedResourceIds.value = resourceIds.data;
};

watch(() => props.roleId, initTreeData, { immediate: true });

const { open } = useDialog();

const handleEdit = () => {
  form.value.selectedResourceIds = selectedResourceIds.value;
  open({
    title: "编辑资源",
    height: 500,
    onCancel: handleCancel,
    onConfirm: handleConfirm,
    render: () => {
      return <ProForm v-model={form.value} columns={columns} showFooter={false} style="padding: 10px" />;
    },
  });
};

const handleCancel = () => {
  form.value = { selectedResourceIds: [] };
};

const handleConfirm = async () => {
  await addResourceToRole({
    roleId: props.roleId,
    appId: initRequestParams.appId,
    resourceIds: form.value.selectedResourceIds,
  });
  initTreeData();
};
</script>

<template>
  <div>
    <div style="display: flex; align-items: center; margin-bottom: 10px">
      <el-button v-auth="['system:role:linkResource']" type="primary" @click="handleEdit">编辑</el-button>
      <el-button @click="initTreeData()">刷新</el-button>
      <el-alert title="绿色代表已授权资源，黑色代表未授权资源" :closable="false" style="margin: 0 10px" />
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
