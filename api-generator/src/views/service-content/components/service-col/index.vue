<script setup lang="tsx" name="ServiceCol">
import type { FeedbackFormProps, TableColumn, FormColumn, ProPageInstance } from "teek";
import type { ServiceCol } from "@/common/api/serviceCol";
import { Pointer, Delete, Files, Link, Plus, Minus } from "@element-plus/icons-vue";
import { oneDark } from "@codemirror/theme-one-dark";
import { sql } from "@codemirror/lang-sql";
import { ProPage, CodeMirror, message, useDialog } from "teek";
import {
  listServiceColPage,
  addServiceCol,
  editServiceCol,
  removeServiceCol,
  reGenCol,
  removeInvalidCol,
  editBatch,
  listByServiceId,
} from "@/common/api/serviceCol";
import { listSelectInProject } from "@/common/api/service";
import { ServiceKey, colTypeComponentForm, queryFilter } from "@/common/config";
import BatchOperate from "./batch-operate.vue";

const serviceInfo = inject(ServiceKey, ref());
const { open } = useDialog();

const initRequestParams = reactive({
  serviceId: serviceInfo.value?.serviceId,
  projectId: serviceInfo.value?.projectId,
});

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const model = ref<Partial<ServiceCol.ServiceColInfo>>({});
const clickRow = ref<Partial<ServiceCol.ServiceColInfo>>({}); // 点击某一行后缓存起来

const colTypeOptions = computed(() =>
  Object.keys(colTypeComponentForm).map((key, index) => ({ label: key, value: index }))
);
const queryFilterOptions = computed(() => queryFilter.map((item, index) => ({ label: item, value: index })));
const handleRowClick = (row: ServiceCol.ServiceColInfo) => {
  clickRow.value = row;
};

const handleRegenerate = async () => {
  const res = await reGenCol(serviceInfo.value?.serviceId || "");

  if (res.code === 200) {
    message.success(res.message);
    proPageInstance.value?.proTableInstance?.fetch();
  }
};

const handleDeleteInvalid = async () => {
  const res = await removeInvalidCol(serviceInfo.value?.serviceId || "");

  if (res.code === 200) {
    message.success(res.message);
    proPageInstance.value?.proTableInstance?.fetch();
  }
};

const batchOperate = shallowRef<typeof BatchOperate>();

const handleBatchOperate = () => {
  open({
    title: "批量操作",
    height: 300,
    render: () => (
      <BatchOperate
        ref={batchOperate}
        data={proPageInstance.value?.proTableInstance?.tableData as ServiceCol.ServiceColInfo[]}
      />
    ),
    onConfirm: async () => {
      const data = await batchOperate.value?.getData();

      if (data) {
        const res = await editBatch(data);
        if (res.code === 200) {
          proPageInstance.value?.proTableInstance?.fetch();
          return message.success("批量更新成功");
        }
      }
    },
  });
};

const handleToReport = () => {
  window.open(`/report/${serviceInfo.value?.serviceId}`);
};

const customDropDownList = ref<{ value: string; label: string }[]>([reactive({ value: "", label: "" })]);

const addCustomSelect = () => {
  customDropDownList.value.push(reactive({ value: "", label: "" }));
};
const removeCustomSelect = (index: number) => {
  customDropDownList.value.splice(index, 1);
};

const dropdownServiceColList = ref<ServiceCol.ServiceColInfo[]>([]);
const serviceSelectChange = async (value: string) => {
  if (!value) dropdownServiceColList.value = [];
  const res = await listByServiceId(value);
  if (res.code === 200) dropdownServiceColList.value = res.data;
};

const commonEnum = [
  { label: "不允许", value: 0 },
  { label: "允许", value: 1 },
];

const columns: TableColumn[] = [
  { type: "index", label: "#", width: 60 },
  { prop: "tableCol", label: "字段名", minWidth: 120 },
  { prop: "jsonCol", label: "请求名", minWidth: 120 },
  { prop: "reportCol", label: "报表名", minWidth: 120 },
  {
    prop: "isWhereKey",
    label: "主键",
    options: [
      { label: "不作为", value: 0 },
      { label: "作为", value: 1 },
    ],
  },
  { prop: "allowInsert", label: "新增", options: commonEnum, width: 100 },
  { prop: "allowUpdate", label: "更新", options: commonEnum, width: 100 },
  { prop: "allowRequest", label: "请求", options: commonEnum, width: 100 },
  { prop: "orderBy", label: "排序", width: 100 },
  { prop: "displaySeq", label: "返回顺序", width: 100 },
  { prop: "queryFilter", label: "筛选条件", options: queryFilterOptions.value, width: 100 },
  { prop: "defaultValue", label: "默认值", width: 110 },
  { prop: "colType", label: "类型", isFilterEnum: false },
  { prop: "colLength", label: "长度", width: 100 },
  { prop: "operation", label: "操作", width: 140, fixed: "right" },
];

const dropdownService = reactive({ service: "", value: "", label: "" });
const dropdownSql = ref("");

const formColumns: FormColumn[] = [
  { prop: "base", label: "基本配置", el: "ElDivider" },
  { prop: "tableCol", label: "字段名称", el: "el-input" },
  { prop: "jsonCol", label: "请求名称", el: "el-input" },
  { prop: "reportCol", label: "报表名称", el: "el-input" },
  {
    prop: "isWhereKey",
    label: "where 条件",
    el: "el-select",
    defaultValue: 0,
    options: [
      { label: "不作为", value: 0 },
      { label: "作为", value: 1 },
    ],
  },
  { prop: "allowInsert", label: "新增", el: "el-select", options: commonEnum, defaultValue: 1 },
  { prop: "allowUpdate", label: "更新", el: "el-select", options: commonEnum, defaultValue: 1 },
  { prop: "allowRequest", label: "请求", el: "el-select", options: commonEnum, defaultValue: 1 },
  { prop: "queryFilter", label: "筛选条件", el: "el-select", defaultValue: 0, options: queryFilterOptions.value },
  { prop: "orderBy", label: "排序顺序", el: "el-input-number", defaultValue: 99 },
  {
    prop: "defaultValue",
    label: "默认值",
    el: "el-input",
    hidden: () => model.value.isWhereKey === 0,
    elProps: {
      placeholder: "新增时的默认值",
    },
  },
  { prop: "colType", label: "字段类型", el: "el-select", defaultValue: "String", options: colTypeOptions.value },
  { prop: "report", label: "报表配置", el: "ElDivider" },
  {
    prop: "colAlign",
    label: "列对齐",
    el: "el-select",
    options: [
      { label: "左对齐", value: 1 },
      { label: "居中", value: 2 },
      { label: "右对齐", value: 3 },
    ],
    defaultValue: 1,
  },
  { prop: "allowShowInReport", label: "是否在报表显示", el: "el-select", options: commonEnum, defaultValue: 1 },
  { prop: "allowShowInDetail", label: "是否在弹框显示", el: "el-select", options: commonEnum, defaultValue: 1 },
  { prop: "displaySeq", label: "返回出现顺序", el: "el-input-number", defaultValue: 99 },
  { prop: "reportColWidth", label: "报表列宽度", el: "el-input-number", defaultValue: -1 },
  { prop: "detailColWidth", label: "弹框组件宽度", el: "el-input-number", defaultValue: -1 },
  {
    prop: "dropDownType",
    label: "下拉模板",
    el: "el-radio-group",
    options: [
      { value: "local", label: "本地下拉配置" },
      { value: "service", label: "从其他服务获取" },
      { value: "sql", label: "从 SQL 里获取" },
    ],
    defaultValue: "local",
  },
  {
    prop: "dropdownList",
    label: "",
    render: () => (
      <div class="relative">
        <el-button
          type="primary"
          icon={Plus}
          circle
          onClick={() => addCustomSelect()}
          class="mr-11 absolute left-[-70px]"
        />
        {customDropDownList.value.map((item, index) => (
          <div class="flex mb-2">
            <el-input v-model={item.value} placeholder="请输入存储内容（value）" class="mr-4" />
            <el-input v-model={item.label} placeholder="请输入展示内容（label）" class="mr-4" />
            <el-button type="danger" icon={Minus} circle onClick={() => removeCustomSelect(index)} />
          </div>
        ))}
      </div>
    ),
    hidden: model => model.dropDownType !== "local",
  },
  {
    prop: "dropdownService",
    label: "",
    el: "el-select",
    options: () => listSelectInProject(serviceInfo.value?.projectId || "", serviceInfo.value?.serviceId || ""),
    hidden: model => model.dropDownType !== "service",
    render: ({ options }) => {
      return (
        <div>
          <el-select
            v-model={dropdownService.service}
            placeholder="请选择接口"
            clearable
            onChange={serviceSelectChange}
          >
            {options.map(item => (
              <el-option label={item.serviceName} value={item.serviceId} />
            ))}
          </el-select>
          <div class="flex mt-2">
            <el-select v-model={dropdownService.value} placeholder="存储内容字段：Value" clearable>
              {dropdownServiceColList.value.map(item => (
                <el-option label={item.jsonCol} value={item.colId} />
              ))}
            </el-select>
            <el-select v-model={dropdownService.label} placeholder="展示内容内容字段：Label" clearable>
              {dropdownServiceColList.value.map(item => (
                <el-option label={item.jsonCol} value={item.colId} />
              ))}
            </el-select>
          </div>
        </div>
      );
    },
  },
  {
    prop: "dropdownSql",
    label: "",
    render: () => {
      return <CodeMirror v-model={dropdownSql.value} localTheme={oneDark} lang={sql()} height={300} fullScreen />;
    },
    hidden: model => model.dropDownType !== "sql",
  },
];

const elFormProps = {
  labelWidth: 110,
  rules: {
    tableCol: [{ required: true, message: "请输入字段名称", trigger: "blur" }],
    jsonCol: [{ required: true, message: "请输入请求名称", trigger: "blur" }],
    reportCol: [{ required: true, message: "请输入报表名称", trigger: "blur" }],
  },
};

const getDropdownConfig = (data: any) => {
  if (!data.dropdownList) return;

  let dropdownConfig: { type: string; value: any } | {} = {};
  if (data.dropDownType === "local") {
    if (data.dropdownList.length > 0) dropdownConfig = { type: "local", value: customDropDownList.value };
  }
  if (data.dropDownType === "service") {
    if (dropdownService.service) dropdownConfig = { type: "service", value: { ...dropdownService } };
  }
  if (data.dropDownType === "sql") {
    if (dropdownSql.value) dropdownConfig = { type: "sql", value: dropdownSql.value };
  }

  // 重置数据为初始值
  customDropDownList.value = [reactive({ value: "", label: "" })];
  dropdownService.service = "";
  dropdownService.value = "";
  dropdownService.label = "";
  dropdownSql.value = "";

  return dropdownConfig;
};

const feedbackFormProps: FeedbackFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "colId"],
  addApi: data => {
    const dropdownConfig = getDropdownConfig(data);

    return addServiceCol({
      ...data,
      ...initRequestParams,
      categoryId: serviceInfo.value?.categoryId,
      dropdownConfig,
      teamId: serviceInfo.value?.teamId,
    });
  },
  editApi: data => {
    const dropdownConfig = getDropdownConfig(data);

    return editServiceCol({
      ...data,
      ...initRequestParams,
      dropdownConfig,
      categoryId: serviceInfo.value?.categoryId,
      teamId: serviceInfo.value?.teamId,
    });
  },
  removeApi: removeServiceCol,
  clickEdit: model => {
    const dropdownConfig = model.dropdownConfig;
    if (!dropdownConfig) return;

    model.dropDownType = dropdownConfig?.type;

    if (dropdownConfig?.type === "local") customDropDownList.value = dropdownConfig.value;
    else if (dropdownConfig?.type === "service") {
      serviceSelectChange(dropdownConfig.value.service);
      dropdownService.service = dropdownConfig.value.service;
      dropdownService.value = dropdownConfig.value.value;
      dropdownService.label = dropdownConfig.value.label;
    } else if (dropdownConfig?.type === "sql") dropdownSql.value = dropdownConfig.value;
  },
  feedbackProps: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "50%",
    height: 700,
    top: "2vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <ProPage
    ref="proPageInstance"
    :request-api="listServiceColPage"
    :init-request-params
    highlight-current-row
    :columns
    :feedback-form-props
    @row-click="handleRowClick"
  >
    <template #head-left-after>
      <el-button
        v-waves
        type="primary"
        plain
        :icon="Pointer"
        :disabled="serviceInfo?.projectRole === '只读成员'"
        @click="handleRegenerate"
      >
        生成新增字段
      </el-button>
      <el-button
        v-waves
        type="danger"
        plain
        :icon="Delete"
        :disabled="serviceInfo?.projectRole === '只读成员'"
        @click="handleDeleteInvalid"
      >
        删除失效字段
      </el-button>
      <el-button
        v-waves
        type="success"
        plain
        :icon="Files"
        :disabled="serviceInfo?.projectRole === '只读成员'"
        @click="handleBatchOperate"
      >
        批量操作
      </el-button>
      <el-button v-waves type="info" plain :icon="Link" @click="handleToReport">跳转至报表</el-button>
    </template>
  </ProPage>
</template>
