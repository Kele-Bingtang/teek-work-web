import type { DialogFormColumn, ElFormProps } from "@teek/components";
import type { User } from "@/common/api/system/user/user";
import type { FormRules } from "element-plus";
import { useDictStore } from "@/pinia";
import { listDeptTreeList } from "@/common/api/system/dept";
import { userSelectPostList } from "@/common/api/system/post";
import { useFormRules } from "@/composables";

const { validatePassword, validatePhone } = useFormRules();

const rules = reactive<FormRules>({
  username: [{ required: true, message: "请输入用户名称", trigger: "blur" }],
  password: [{ required: true, validator: validatePassword, trigger: "blur" }],
  deptId: [{ required: true, message: "请选择部门", trigger: "blur" }],
  phone: [{ validator: validatePhone, trigger: "blur" }],
  email: [{ type: "email", message: "请输入正确的邮箱", trigger: ["blur", "change"] }],
});

export const elFormProps: ElFormProps = {
  labelPosition: "top",
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (defaultValue?: ComputedRef<string>) => {
  const { getDictData } = useDictStore();

  const columns: DialogFormColumn<User.Info>[] = [
    {
      prop: "username",
      label: "用户名称",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 用户名称" },
    },
    {
      prop: "status",
      label: "状态",
      defaultValue: "1",
      el: "el-select",
      optionField: { value: "dictValue", label: "dictLabel" },
      options: () => getDictData("sys_normal_status"),
    },
    {
      prop: "nickname",
      label: "用户昵称",
      el: "el-input",
      hiddenIn: ["edit"],
      elProps: { clearable: true, placeholder: "请输入 用户昵称" },
    },
    {
      prop: "password",
      label: "密码",
      el: "el-input",
      hiddenIn: ["edit"],
      defaultValue: "123456",
      elProps: { clearable: true, placeholder: "请输入 密码", type: "password", showPassword: true },
    },
    {
      prop: "sex",
      label: "性别",
      el: "el-select",
      valueFormat: "string",
      options: () => getDictData("sys_user_sex"),
      optionField: { value: "dictValue", label: "dictLabel" },
      elProps: { clearable: true, placeholder: "请选择 性别" },
    },
    {
      prop: "birthday",
      label: "生日",
      el: "el-date-picker",
      elProps: { clearable: true, placeholder: "请选择 生日" },
    },
    {
      prop: "email",
      label: "邮箱",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 邮箱" },
    },
    {
      prop: "phone",
      label: "电话号码",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 电话号码" },
    },
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
      prop: "postIds",
      label: "岗位",
      el: "el-select",
      defaultValue: ({ optionsMap }) => optionsMap?.get("postId")?.postIds,
      options: async ({ model }) => {
        const res = await userSelectPostList(model!.userId);
        return res.data.postList;
      },
      optionField: { value: "postId", label: "postName" },
      elProps: {
        clearable: true,
        multiple: true,
        collapseTags: true,
        collapseTagsTooltip: true,
        maxCollapseTags: 1,
        placeholder: "请选择 岗位",
      },
    },
  ];

  return columns;
};
