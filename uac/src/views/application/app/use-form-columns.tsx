import type { FormRules } from "element-plus";
import type { App } from "@/common/api/application/app";
import type { DialogFormColumn } from "@teek/components";
import { UserSelect } from "@teek/components";
import { list } from "@/common/api/user/user";
import { useDictStore } from "@/pinia";

const rules = reactive<FormRules>({
  clientId: [{ required: true, message: "请选择客户端", trigger: "blur" }],
  appCode: [{ required: true, message: "请输入应用编码", trigger: "blur" }],
  appName: [{ required: true, message: "请输入应用名称", trigger: "blur" }],
  appType: [{ required: true, message: "请选择应用类型", trigger: "blur" }],
});

export const elFormProps = {
  labelWidth: 100,
  rules: rules,
};
export const useFormColumns = (options: ComputedRef<any>, defaultValue: ComputedRef<string>) => {
  const columns: DialogFormColumn<App.AppInfo>[] = [
    {
      prop: "clientId",
      label: "所属客户端",
      el: "el-select",
      options,
      optionField: { value: "clientId", label: "clientName" },
      defaultValue: defaultValue,
      elProps: model => ({ clearable: true, placeholder: "请选择 所属客户端", disabled: !!model.clientId }),
    },
    {
      prop: "appCode",
      label: "应用编码",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 角色编码" },
    },
    {
      prop: "appName",
      label: "应用名称",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 角色名称" },
    },
    {
      prop: "appType",
      label: "应用类型",
      el: "el-select",
      options: () => useDictStore().getDictData("sys_app_type"),
      optionField: { value: "dictValue", label: "dictLabel" },
    },
    {
      prop: "owner",
      label: "负责人",
      render: ({ model }) => {
        return (
          <UserSelect v-model={model.ownerId} v-model:user={model.user} requestApi={list} id="username"></UserSelect>
        );
      },
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
  ];

  return {
    columns,
  };
};
