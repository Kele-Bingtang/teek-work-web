import type { FormRules } from "element-plus";
import type { FeedbackFormColumn } from "teek";
import type { Service } from "@/common/api/service";

const rules: FormRules = {
  appId: [{ required: true, message: "请选择 App", trigger: "blur" }],
  dictCode: [{ required: true, message: "请输入字典编码", trigger: "blur" }],
  dictName: [{ required: true, message: "请输入字典名称", trigger: "blur" }],
};

export const elFormProps = {
  labelWidth: 80,
  rules: rules,
};

export const formColumns: FeedbackFormColumn<Service.ServiceInfo>[] = [
  {
    prop: "serviceName",
    label: "服务名称",
    el: "el-input",
    elProps: { placeholder: "请输入 服务名称" },
  },
  {
    prop: "reportTitle",
    label: "报表名称",
    el: "el-input",
    elProps: { placeholder: "请输入 报表名称" },
  },
  {
    prop: "serviceUrl",
    label: "服务地址",
    el: "el-input",
    elProps: { placeholder: "请输入 服务名称" },
  },
  {
    prop: "status",
    label: "服务状态",
    el: "el-select",
    options: [
      { value: 0, label: "禁用" },
      { value: 1, label: "启用" },
    ],
    elProps: { placeholder: "请选择 服务状态" },
  },
  {
    prop: "isAuth",
    label: "是否认证",
    el: "el-select",
    options: [
      { value: 0, label: "禁用" },
      { value: 1, label: "启用" },
    ],
    elProps: { placeholder: "请选择 是否认证" },
  },
  {
    prop: "selectSql",
    label: "SQL",
    el: "el-input",
  },
  {
    prop: "description",
    label: "服务描述",
    el: "el-input",
    elProps: { type: "textarea" },
  },
  {
    prop: "insertTable",
    label: "Insert Table",
    el: "el-select",
    options: [{ value: "Table1", label: "Table1" }],
    elProps: { placeholder: "请选择接支持插入的表名" },
  },
  {
    prop: "updateTable",
    label: "Update Table",
    el: "el-select",
    options: [{ value: "Table1", label: "Table1" }],
    elProps: { placeholder: "请选择接支持更新的表名" },
  },
  {
    prop: "deleteTable",
    label: "Delete Table",
    el: "el-select",
    options: [{ value: "Table1", label: "Table1" }],
    elProps: { placeholder: "请选择接支持删除的表名" },
  },
];
