import type { FormRules } from "element-plus";
import type { DialogFormColumn } from "teek";
import type { UserGroup } from "@/common/api/user/userGroup";
import type { User } from "@/common/api/user/user";
import { UserSelect } from "teek";
import { list } from "@/common/api/user/user";

const rules = reactive<FormRules>({
  groupName: [{ required: true, message: "请输入用户组名", trigger: "blur" }],
});

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = () => {
  const columns: DialogFormColumn<UserGroup.UserGroupInfo & { user: User.UserInfo }>[] = [
    {
      prop: "groupName",
      label: "用户组名",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 用户组名" },
    },
    {
      prop: "owner",
      label: "负责人",
      renderUseProp: ["ownerId", "user"],
      render: ({ model }) => {
        return (
          <UserSelect v-model={model.ownerId} v-model:user={model.user} requestApi={list} id="username"></UserSelect>
        );
      },
    },
    {
      prop: "intro",
      label: "描述",
      el: "el-input",
      elProps: { type: "textarea", clearable: true, placeholder: "请输入 介绍" },
    },
  ];

  return { columns };
};
