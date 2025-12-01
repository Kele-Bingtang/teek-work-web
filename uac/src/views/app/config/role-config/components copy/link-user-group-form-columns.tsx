import type { DialogFormColumn, ElFormProps, TransferTableColumn } from "@teek/components";
import type { FormRules } from "element-plus";
import { User } from "@element-plus/icons-vue";
import { TransferSelect } from "teek";
import { listWithSelectedByRoleId } from "@/common/api/link/user-group-role-link";

const rules = reactive<FormRules>({
  groupIds: [{ required: true, message: "请选择用户组", trigger: "blur" }],
});

const transferSelectColumn: TransferTableColumn[] = [{ prop: "groupName", label: "用户组名" }];

export const elFormProps: ElFormProps = {
  inline: true,
  labelPosition: "top",
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (requestParams: { roleId: string }) => {
  const columns: DialogFormColumn<{ userGroupIds: string }>[] = [
    {
      prop: "userGroupIds",
      label: "用户组选择",
      render: ({ model }) => {
        return (
          <TransferSelect
            v-model={model.userGroupIds}
            columns={transferSelectColumn}
            request-api={listWithSelectedByRoleId}
            request-params={requestParams}
            multiple
            list-icon={User}
            id="groupId"
          ></TransferSelect>
        );
      },
    },
  ];

  return { columns };
};
