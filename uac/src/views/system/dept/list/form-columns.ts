import type { Dept } from "@/common/api/system/dept";
import type { DialogFormColumn } from "@teek/components";
import type { FormRules } from "element-plus";
import { useFormRules } from "@/composables";
import { listDeptTreeList } from "@/common/api/system/dept";
import { list } from "@/common/api/system/user/user";

const { validatePhone } = useFormRules();

const rules = reactive<FormRules>({
  deptName: [{ required: true, message: "请输入部门名称", trigger: "blur" }],
  phone: [{ validator: validatePhone, trigger: "blur" }],
  email: [{ type: "email", message: "请输入正确的邮箱", trigger: ["blur", "change"] }],
});

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const formColumns: DialogFormColumn<Dept.TreeTable>[] = [
  {
    prop: "parentId",
    label: "上级部门",
    el: "el-tree-select",
    elProps: {
      placeholder: "请选择 上级部门",
      filterable: true,
      valueKey: "id",
      defaultExpandAll: true,
      showCheckbox: true,
      checkStrictly: true,
    },
    options: () => listDeptTreeList(),
    hidden: form => form.parentId === "0",
  },
  {
    prop: "deptName",
    label: "部门名称",
    el: "el-input",
    elProps: { clearable: true, placeholder: "请输入 角色编码" },
  },
  {
    prop: "leader",
    label: "领导",
    el: "user-select",
    elProps: { requestApi: list, placeholder: "请输入 领导" },
  },
  {
    prop: "phone",
    label: "电话",
    el: "el-input",
    elProps: { clearable: true, placeholder: "请输入 电话" },
  },
  {
    prop: "email",
    label: "邮箱",
    el: "el-input",
    elProps: { clearable: true, placeholder: "请输入 邮箱" },
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
