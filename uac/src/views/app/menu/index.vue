<script setup lang="tsx" name="Menu">
import type { DialogFormProps, TableColumn, ProPageInstance } from "teek";
import type { Menu } from "@/common/api/system/menu";
import { ElMessageBox, ElSwitch } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { ProPage, Icon, downloadByData, useNamespace } from "teek";
import { httpPrefix, httpsPrefix } from "@/common/config";
import { listMenuTreeTableByApp, addMenu, editMenu, removeMenu, exportExcel } from "@/common/api/system/menu";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { menuTypeEnum, elFormProps, useFormColumns } from "./use-form-columns";

const ns = useNamespace("menu");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const route = useRoute();

const { statusChange } = useChange(
  "menuName",
  "菜单",
  (row, status) => editMenu({ id: row.id, menuId: row.menuId, parentId: row.parentId, status }),
  () => proPageInstance.value?.search()
);

const initRequestParams = reactive({
  appId: route.params.appId as string,
});

const columns: TableColumn<Menu.MenuInfo>[] = [
  { prop: "menuName", label: "菜单名称", align: "left", search: { el: "el-input" } },
  {
    prop: "icon",
    label: "图标",
    width: 60,
    render: ({ row }) => <Icon icon={row.icon}></Icon>,
  },
  { prop: "orderNum", label: "排序", width: 80 },
  { prop: "component", label: "组件路径", width: 200 },
  { prop: "permission", label: "权限标识", width: 180 },
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
  { prop: "menuType", label: "类型", width: 80, options: menuTypeEnum },
  { prop: "intro", label: "介绍", width: 180 },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 200, fixed: "right" },
];

const installMeta = (data: any) => {
  if (data.meta) {
    const keys = Object.keys(data.meta);
    keys?.forEach((key: any) => {
      if (data.meta[key] === "default") delete data.meta[key];
    });
    return data.meta;
  }
};

const { hasAuth } = usePermission();

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps,
    columns: useFormColumns(computed(() => initRequestParams.appId)).columns,
    notCleanModelKeys: ["meta"],
  },
  id: ["id", "menuId"],
  addApi: data =>
    addMenu({
      ...data,
      path: (data.pathPrefix || "") + (data.path || ""),
      meta: installMeta(data),
      appId: initRequestParams.appId,
    }),
  editApi: data =>
    editMenu({
      ...data,
      path: (data.pathPrefix || "") + (data.path || ""),
      meta: installMeta(data),
      appId: initRequestParams.appId,
    }),
  removeApi: removeMenu,
  clickEdit: model => {
    if ([httpPrefix, httpsPrefix].find(item => model.path?.includes(item))) {
      model.pathPrefix = model.path.split("//")[0] + "//";
      model.path = model.path.split("//")[1];
    } else model.pathPrefix = "";

    if (!model.meta) model.useMeta = 0;
    const m = { ...model.meta } as Recordable;
    ["title", "icon", "rank"].forEach(key => delete m[key]);

    for (const key in m) {
      const val = m[key];
      if (val === "default") delete m[key];
    }
    model.useMeta = Object.keys(m).length ? 1 : 0;
  },
  disableAdd: !hasAuth("system:menu:add"),
  disableEdit: !hasAuth("system:menu:edit"),
  disableRemove: !hasAuth("system:menu:remove"),
  apiFilterKeys: ["pathPrefix"],
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 670,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `menu_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <div :class="ns.em('table', 'empty')" v-if="!initRequestParams.appId">
      <el-empty description="请先选择一个应用" />
    </div>
    <ProPage
      ref="proPageInstance"
      v-show="initRequestParams.appId"
      :request-api="listMenuTreeTableByApp"
      :columns
      :init-request-params="initRequestParams"
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 2, lg: 3, xl: 3 } }"
      :dialog-form-props
      :border="false"
      :page-scope="false"
      :export-file
      :disabled-tool-button="!hasAuth('system:menu:export') ? ['export'] : []"
    >
      <template #operation-after="{ row, dialogFormInstance }">
        <el-button
          v-auth="['system:menu:add']"
          link
          size="small"
          :icon="Plus"
          @click="dialogFormInstance?.handleAdd({ parentId: row.menuId })"
        >
          新增
        </el-button>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/function" as *;

@include b(menu) {
  @include m(empty) {
    display: flex;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-color: cssVar(bg-color);
  }
}
</style>
