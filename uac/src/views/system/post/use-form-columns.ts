import type { FormRules } from "element-plus";
import type { Post } from "@/common/api/system/post";
import type { DialogFormColumn } from "@teek/components";
import { listDeptTreeList } from "@/common/api/system/dept";

const rules = reactive<FormRules>({
  deptId: [{ required: true, message: "请选择部门", trigger: "blur" }],
  postCode: [{ required: true, message: "请输入岗位编码", trigger: "blur" }],
  postName: [{ required: true, message: "请输入岗位名称", trigger: "blur" }],
});

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (defaultValue?: ComputedRef<string>) => {
  const columns: DialogFormColumn<Post.Info>[] = [
    {
      prop: "deptId",
      label: "部门",
      el: "el-tree-select",
      defaultValue: defaultValue,
      elProps: {
        clearable: true,
        defaultExpandAll: true,
        placeholder: "请选择 部门",
        showCheckbox: true,
        checkStrictly: true,
      },
      options: () => listDeptTreeList(),
    },
    {
      prop: "postCode",
      label: "岗位编码",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 角色编码" },
    },
    {
      prop: "postName",
      label: "岗位名称",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 领导" },
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
  return columns;
};
