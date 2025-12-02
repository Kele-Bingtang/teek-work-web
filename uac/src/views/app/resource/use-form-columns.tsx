import type { DialogFormColumn, ElFormProps } from "@teek/components";
import type { Resource } from "@/common/api/system/resource";
import { listResourceTreeSelectByApp } from "@/common/api/system/resource";
import { ElInput, ElOption, ElSelect, type FormRules } from "element-plus";
import { httpPrefix, httpsPrefix } from "@/common/config";
import { layoutFormColumns } from "./layout-columns";
import { iframeFormColumns } from "./iframe-columns";

const rules = reactive<FormRules>({
  appId: [{ required: true, message: "请选择 App", trigger: "blur" }],
  resourceCode: [{ required: true, message: "请输入资源编码", trigger: "blur" }],
  resourceName: [{ required: true, message: "请输入资源名称", trigger: "blur" }],
  path: [{ required: true, message: "请输入资源/路由地址", trigger: "blur" }],
});

export const resourceTypeEnum = [
  { value: "C", label: "目录", tagType: "warning" },
  { value: "M", label: "菜单", tagType: "primary" },
  { value: "F", label: "按钮", tagType: "info" },
];

export const commonEnum = [
  { value: 1, label: "是" },
  { value: 0, label: "否" },
];

export const elFormProps: ElFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (defaultValue: ComputedRef<string>) => {
  const columns: DialogFormColumn<Resource.Info>[] = [
    {
      prop: "base",
      label: "基础配置",
      el: "ElDivider",
      colProps: { span: 24 },
    },
    {
      prop: "resourceType",
      label: "资源类型",
      el: "el-radio-group",
      options: resourceTypeEnum,
      defaultValue: "C",
    },
    {
      prop: "parentId",
      label: model => (model.resourceType === "C" ? `上级目录` : "上级菜单"),
      el: "el-tree-select",
      elProps: {
        placeholder: "请选择 上级",
        filterable: true,
        valueKey: "id",
        showCheckbox: true,
        checkStrictly: true,
      },
      options: () => listResourceTreeSelectByApp({ appId: defaultValue.value }),
      hidden: model => model.parentId === "0",
    },
    {
      prop: "resourceCode",
      label: model => `${getLabel(model)}编码`,
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 编码(如 UserManage)" },
    },
    {
      prop: "resourceName",
      label: model => `${getLabel(model)}名称`,
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 名称（如用户管理）" },
    },
    {
      prop: "path",
      label: "路由地址",
      destroy: model => model.resourceType === "F",
      render: ({ model }) => {
        return (
          <>
            <ElInput
              v-model={model.path}
              placeholder="请输入 路由地址(如 user-manage)"
              v-slots={{
                prepend: () => (
                  <ElSelect v-model={model.pathPrefix} style="width: 120px">
                    <ElOption value="" />
                    <ElOption value={httpPrefix} />
                    <ElOption value={httpsPrefix} />
                  </ElSelect>
                ),
              }}
            ></ElInput>
          </>
        );
      },
    },
    {
      prop: "permission",
      label: "权限标识",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 权限标识（system:user:list）" },
      hidden: model => model.resourceType === "C",
    },
    {
      prop: "component",
      label: "组件路径",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 组件路径" },
      hidden: model => model.resourceType !== "M",
    },
    {
      prop: "icon",
      label: model => `${getLabel(model)}图标`,
      el: "icon-picker",
      elProps: { clearable: true, placeholder: "请选择 图标" },
      hidden: model => model.resourceType === "F",
    },
    {
      prop: "param",
      label: "路由参数",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 路由参数" },
      hidden: model => model.resourceType !== "M",
    },
    {
      prop: "orderNum",
      label: "显示顺序",
      el: "el-input-number",
      defaultValue: 1,
    },
    {
      prop: "intro",
      label: "介绍",
      el: "el-input",
      elProps: { type: "textarea", clearable: true, placeholder: "请输入 介绍" },
      colProps: { span: 24 },
    },
    {
      prop: "iframe",
      label: "META 配置",
      el: "ElDivider",
      destroy: model => model.resourceType === "F",
      colProps: { span: 24 },
    },
    {
      prop: "useMeta",
      label: "显示",
      el: "el-radio",
      destroy: model => model.resourceType === "F",
      options: commonEnum,
      defaultValue: 0,
    },

    ...layoutFormColumns,
    ...iframeFormColumns,
  ];

  const getLabel = (model: Resource.Info) => {
    if (model.resourceType === "C") return "目录";
    else if (model.resourceType === "M") return "菜单";
    else if (model.resourceType === "F") return "按钮";
  };

  return { columns };
};
