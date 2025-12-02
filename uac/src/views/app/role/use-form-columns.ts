import type { FormRules } from "element-plus";
import type { Role } from "@/common/api/system/role";
import type { DialogFormColumn } from "@teek/components";
import { listResourceTreeSelectByApp } from "@/common/api/system/resource";
import { listResourceIdsByRoleId } from "@/common/api/link/role-resource-link";
import { list } from "@/common/api/system/user/user";

const rules = reactive<FormRules>({
  appId: [{ required: true, message: "请选择 App", trigger: "blur" }],
  roleCode: [{ required: true, message: "请输入角色编码", trigger: "blur" }],
  roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
});

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = () => {
  const route = useRoute();

  const columns: DialogFormColumn<Role.Info>[] = [
    {
      prop: "roleCode",
      label: "角色编码",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 角色编码" },
    },
    {
      prop: "roleName",
      label: "角色名称",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 角色名称" },
    },
    {
      prop: "ownerId",
      label: "责任人",
      el: "user-select",
      elProps: { requestApi: list, multiple: true },
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
      prop: "selectedResourceIds",
      label: "资源分配",
      el: "tree",
      defaultValue: async (model: Record<string, any>) => {
        if (!model.appId) return [];
        const res = await listResourceIdsByRoleId(model.appId, model.roleId);
        return res.data || [];
      },
      options: () => (route.params.appId ? listResourceTreeSelectByApp({ appId: route.params.appId as string }) : []),
      elProps: { nodeKey: "value", search: true, checkbox: true },
    },
  ];

  return { columns };
};
