<script setup lang="tsx">
import type { DialogFormProps, PageColumn, ProPageInstance } from "teek";
import type { Resource } from "@/common/api/system/resource";
import { ElMessageBox, ElSwitch } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { ProPage, Icon, downloadByData, useNamespace } from "teek";
import { httpPrefix, httpsPrefix } from "@/common/config";
import {
  listResourceTreeTableByApp,
  addResource,
  editResource,
  removeResource,
  exportExcel,
} from "@/common/api/system/resource";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { resourceTypeEnum, elFormProps, useFormColumns } from "./use-form-columns";

const ns = useNamespace("resource");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const route = useRoute();

const { statusChange } = useChange(
  "resourceName",
  "资源",
  (row, status) => editResource({ id: row.id, resourceId: row.resourceId, parentId: row.parentId, status }),
  () => proPageInstance.value?.search()
);

const initRequestParams = reactive({
  appId: route.params.appId as string,
});

const columns: PageColumn<Resource.Info>[] = [
  { prop: "resourceName", label: "资源名称", align: "left", search: { el: "el-input" } },
  {
    prop: "icon",
    label: "图标",
    width: 60,
    render: ({ row }) => <Icon icon={row.icon}></Icon>,
  },
  { prop: "path", label: "组件路径", width: 200 },
  { prop: "permission", label: "权限标识", width: 180 },
  { prop: "resourceType", label: "类型", width: 80, options: resourceTypeEnum, el: "el-tag" },
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
  { prop: "orderNum", label: "排序", width: 80 },
  { prop: "intro", label: "介绍", width: 180 },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 200, fixed: "right" },
];

const installMeta = (data: any) => {
  if (!data.meta) return;

  const keys = Object.keys(data.meta);
  keys?.forEach(key => {
    if (data.meta[key] === "default") delete data.meta[key];
  });

  return data.meta;
};

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "50%",
    height: model => (model?.useMeta ? 700 : 500),
    top: "5vh",
    closeOnClickModal: false,
  },
  form: {
    elFormProps,
    columns: useFormColumns(computed(() => initRequestParams.appId)).columns,
    colProps: { span: 12 }, // 一行两个表单
  },
  id: ["id", "resourceId"],
  addApi: data => {
    const pathPrefix = data.pathPrefix;
    delete data.pathPrefix;

    return addResource({
      ...data,
      path: (pathPrefix || "") + (data.path || ""),
      meta: installMeta(data),
      appId: initRequestParams.appId,
    });
  },
  editApi: data => {
    const pathPrefix = data.pathPrefix;
    delete data.pathPrefix;

    return editResource({
      ...data,
      path: (pathPrefix || "") + (data.path || ""),
      meta: installMeta(data),
      appId: initRequestParams.appId,
    });
  },
  removeApi: removeResource,
  clickEdit: model => {
    if ([httpPrefix, httpsPrefix].find(item => model.path?.includes(item))) {
      model.pathPrefix = model.path.split("//")[0] + "//";
      model.path = model.path.split("//")[1];
    } else model.pathPrefix = "";

    // 初始化 meta 相关表单
    if (!model.meta) model.useMeta = 0;
    const m = { ...model.meta } as Recordable;
    // 删除资源内置的属性
    ["title", "icon", "rank"].forEach(key => delete m[key]);

    for (const key in m) {
      const val = m[key];
      if (val === "default") delete m[key];
    }
    model.useMeta = Object.keys(m).length ? 1 : 0;
  },
  disableAdd: !hasAuth("system:resource:add"),
  disableEdit: !hasAuth("system:resource:edit"),
  disableRemove: !hasAuth("system:resource:remove"),
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `resource_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listResourceTreeTableByApp"
      :columns
      :init-request-params="initRequestParams"
      :dialog-form-props
      :page-scope="false"
      :export-file
      :disabled-tool-button="!hasAuth('system:resource:export') ? ['export'] : []"
    >
      <template #operation-after="{ row, dialogFormInstance }">
        <el-button
          v-auth="['system:resource:add']"
          link
          size="small"
          :icon="Plus"
          @click="dialogFormInstance?.handleAdd({ parentId: row.resourceId })"
        >
          新增
        </el-button>
      </template>
    </ProPage>
  </div>
</template>
