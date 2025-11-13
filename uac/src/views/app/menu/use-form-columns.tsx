import { listMenuTreeSelectByApp, type Menu } from "@/common/api/system/menu";
import type { DialogFormColumn } from "@teek/components";
import { ElInput, ElOption, ElSelect, type FormRules } from "element-plus";
import { httpPrefix, httpsPrefix } from "@/common/config";
import { layoutFormColumns } from "./layout-columns";
import { iframeFormColumns } from "./iframe-columns";

const rules = reactive<FormRules>({
  appId: [{ required: true, message: "请选择 App", trigger: "blur" }],
  menuCode: [{ required: true, message: "请输入菜单编码", trigger: "blur" }],
  menuName: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
  path: [{ required: true, message: "请输入菜单/路由地址", trigger: "blur" }],
});

export const menuTypeEnum = [
  { value: "C", label: "目录" },
  { value: "M", label: "菜单" },
  { value: "F", label: "按钮" },
];

export const commonEnum = [
  { value: 1, label: "是" },
  { value: 0, label: "否" },
];

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (defaultValue: ComputedRef<string>) => {
  const columns: DialogFormColumn<Menu.MenuInfo>[] = [
    {
      prop: "base",
      label: "基础配置",
      el: "ElDivider",
    },
    {
      prop: "menuType",
      label: "菜单类型",
      el: "el-radio-group",
      options: menuTypeEnum,
      defaultValue: "C",
    },
    {
      prop: "parentId",
      label: model => (model.menuType === "C" ? `上级目录` : "上级菜单"),
      el: "el-tree-select",
      elProps: {
        placeholder: "请选择 上级",
        filterable: true,
        valueKey: "id",
        showCheckbox: true,
        checkStrictly: true,
      },
      options: () => listMenuTreeSelectByApp({ appId: defaultValue.value }),
      hidden: model => model.parentId === "0",
    },
    {
      prop: "menuCode",
      label: model => `${getLabel(model)}编码`,
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 编码(如 UserManage)" },
    },
    {
      prop: "menuName",
      label: model => `${getLabel(model)}名称`,
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 名称（如用户管理）" },
    },
    {
      prop: "path",
      label: "路由地址",
      destroy: model => model.menuType === "F",
      render: ({ model }) => {
        return (
          <>
            <ElInput
              v-model={model.path}
              placeholder="请输入 路由地址(如 user-manage)"
              v-slots={{
                prepend: () => {
                  return (
                    <ElSelect v-model={model.pathPrefix} style="width: 120px">
                      <ElOption value="" />
                      <ElOption value={httpPrefix} />
                      <ElOption value={httpsPrefix} />
                    </ElSelect>
                  );
                },
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
      hidden: model => model.menuType === "C",
    },
    {
      prop: "component",
      label: "组件路径",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 组件路径" },
      hidden: model => model.menuType !== "M",
    },
    {
      prop: "icon",
      label: model => `${getLabel(model)}图标`,
      el: "icon-picker",
      elProps: { clearable: true, placeholder: "请选择 图标" },
      hidden: model => model.menuType === "F",
    },
    {
      prop: "param",
      label: "路由参数",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 路由参数" },
      hidden: model => model.menuType !== "M",
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
    },
    {
      prop: "iframe",
      label: "META 配置",
      el: "ElDivider",
      destroy: model => model.menuType === "F",
    },
    {
      label: "显示",
      prop: "useMeta",
      el: "el-radio",
      destroy: model => model.menuType === "F",
      options: commonEnum,
      defaultValue: 0,
    },

    ...layoutFormColumns,
    ...iframeFormColumns,
  ];

  const getLabel = (model: Menu.MenuInfo) => {
    if (model.menuType === "C") return "目录";
    else if (model.menuType === "M") return "菜单";
    else if (model.menuType === "F") return "按钮";
  };

  return { columns };
};
