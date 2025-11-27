import type { FormRules } from "element-plus";
import type { DialogFormColumn, ElFormProps, TransferTableColumn } from "@teek/components";
import { User } from "@element-plus/icons-vue";
import { listWithSelectedByGroupId } from "@/common/api/link/user-group-role-link";
import { TransferSelect } from "@teek/components";

const rules = reactive<FormRules>({
  roleIds: [{ required: true, message: "请选择角色名", trigger: "blur" }],
});

const transferSelectColumn: TransferTableColumn[] = [{ prop: "roleName", label: "角色名称" }];

export const elFormProps: ElFormProps = {
  labelPosition: "top",
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (requestParams: { userGroupId: string }) => {
  const columns: DialogFormColumn<{ roleIds: string[] }>[] = [
    {
      prop: "roleIds",
      label: "角色选择",
      render: ({ model }) => {
        return (
          <TransferSelect
            v-model={model.roleIds}
            columns={transferSelectColumn}
            request-api={listWithSelectedByGroupId}
            request-params={requestParams}
            multiple
            list-icon={User}
            id="roleId"
          ></TransferSelect>
        );
      },
    },
  ];

  return {
    columns,
  };
};
