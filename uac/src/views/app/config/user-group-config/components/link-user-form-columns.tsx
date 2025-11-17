import type { FormRules } from "element-plus";
import type { DialogFormColumn } from "@teek/components";
import type { UserGroup } from "@/common/api/system/user/user-group";
import { ElOption, ElSelect, ElDatePicker, ElRow, ElCol, dayjs } from "element-plus";
import { listWithDisabledByGroupId } from "@/common/api/link/user-group-user-link";
import { useDictStore } from "@/pinia";

const rules = reactive<FormRules>({
  userIds: [{ required: true, message: "请选择用户", trigger: "blur" }],
  validFrom: [{ required: true, message: "请选择生效时间", trigger: "blur" }],
  expireOn: [{ required: true, message: "请选择过期时间", trigger: "blur" }],
});

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const useFormColumns = (requestParams: { userGroupId: string }) => {
  const { getDictData } = useDictStore();

  // 选择时长后，计算出过期时间
  const selectChange = (form: any, value: number) => {
    if (!form || value === undefined) return;
    if (value === -1) form.expireOn = dayjs().add(99, "year").format("YYYY-MM-DD");
    else form.expireOn = dayjs().add(value, "year").format("YYYY-MM-DD");
  };

  const columns: DialogFormColumn<UserGroup.LinkUsers>[] = [
    {
      prop: "userIds",
      label: "用户选择",
      el: "user-select",
      elProps: { requestApi: listWithDisabledByGroupId, requestParams: requestParams, multiple: true },
      destroyIn: ["edit"],
    },
    {
      prop: "validFrom",
      label: "生效时间",
      el: "el-date-picker",
      elProps: { clearable: true, placeholder: "请选择生效时间" },
    },
    {
      prop: "expireOn",
      label: "过期时间",
      options: () => getDictData("sys_expire_on"),
      render: ({ model, options }) => {
        return (
          <ElRow gutter={10} class="flex-1">
            <ElCol span={12}>
              <ElSelect
                v-model={model.expireOnNum}
                placeholder="请选择时长"
                style={{ width: "100%" }}
                onChange={(val: string) => selectChange(model, Number(val))}
                clearable
              >
                {options?.map((item: any) => (
                  <ElOption key={item.dictValue} label={item.dictLabel} value={item.dictValue} />
                ))}
              </ElSelect>
            </ElCol>
            <ElCol span={12}>
              <ElDatePicker
                v-model={model.expireOn}
                type="date"
                placeholder="请选择过期时间"
                style={{ width: "100%" }}
                value-format="YYYY-MM-DD"
              />
            </ElCol>
          </ElRow>
        );
      },
    },
  ];

  return { columns };
};
