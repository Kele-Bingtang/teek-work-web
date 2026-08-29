<script setup lang="tsx" name="DataSource">
import type { FeedbackFormProps, FormColumn, PageColumn } from "@teek/components";
import type { DataSource } from "@/common/api/dataSource";
import { Loading } from "@element-plus/icons-vue";
import { useNamespace, ProPage, isArray, message } from "teek";
import {
  listDataSourcePage,
  addDataSource,
  editDataSource,
  removeDataSource,
  testConnect,
} from "@/common/api/dataSource";
import { useDictStore } from "@/pinia";

const ns = useNamespace("dataSource");

const route = useRoute();
const { getDictData } = useDictStore();

const initRequestParams = { teamId: route.meta.params?.teamId };

const handleConnect = async (row: DataSource.DataSourceInfo & { dataSourceTypeDriveClass: string }) => {
  if (!row.dataSourceTypeDriveClass || !row.jdbcUrl) return message.warning("请先填写数据源连接信息");
  const model = { ...row };
  parseDataSourceTypeDriveClass(model);
  const res = await testConnect(model);
  if (res.code === 200) message.success("测试连接成功。2s 后可以再次测试连接，请勿一直重复点击！");
};

const columns: PageColumn<DataSource.DataSourceInfo>[] = [
  { type: "index", label: "#", width: 50 },
  { prop: "dataSourceName", label: "数据源名称", width: 200, search: { el: "el-input" } },
  {
    prop: "dataSourceType",
    label: "数据源类型",
    width: 120,
    search: { el: "el-select" },
    options: async () => getDictData("data_source_type", true),
    optionField: { value: "label", label: "label" },
  },
  { prop: "jdbcUrl", label: "链接地址", align: "left" },
  { prop: "driverClassName", label: "驱动类名", width: 250, align: "left" },
  { prop: "username", label: "账号", width: 160 },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "设置", width: 190 },
];

const formColumns: FormColumn[] = [
  { prop: "dataSourceName", label: "数据源名称", el: "el-input" },
  {
    prop: "dataSourceTypeDriveClass",
    label: "数据源类型",
    el: "el-cascader",
    options: () => getDictData("data_source_type", true),
    optionField: { value: "label", label: "label" },
  },
  {
    prop: "jdbcUrl",
    label: "数据源 URL",
    el: "el-input",
    elProps: { type: "textarea", placeholder: "请输入 数据源 URL" },
  },
  { prop: "username", label: "数据源账号", el: "el-input" },
  { prop: "password", label: "数据源密码", el: "el-input" },
  {
    prop: "description",
    label: "数据源描述",
    el: "el-input",
    elProps: { type: "textarea", placeholder: "请输入 团队描述" },
  },
];

const parseDataSourceTypeDriveClass = (model: Record<string, any>) => {
  const { dataSourceTypeDriveClass } = model;
  if (isArray(dataSourceTypeDriveClass)) {
    model.dataSourceType = dataSourceTypeDriveClass[0];
    model.driverClassName = dataSourceTypeDriveClass[1];
    delete model.dataSourceTypeDriveClass;
  }
};

const elFormProps = {
  labelWidth: 100,
  rules: {
    dataSourceName: [{ required: true, message: "请输入数据源名称", trigger: "blur" }],
    dataSourceType: [{ required: true, message: "请输入数据源类型", trigger: "blur" }],
    jdbcUrl: [{ required: true, message: "请输入数据源地址", trigger: "blur" }],
  },
};

const feedbackFormProps: FeedbackFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "dataSourceId"],
  addApi: data => addDataSource({ ...data, ...initRequestParams }),
  editApi: data => editDataSource({ ...data, ...initRequestParams }),
  removeApi: removeDataSource,
  onAdd: model => {
    // dataSourceTypeDriveClass 是级联选择器的值，绑定了后端需要的 dataSourceType 和 driverClassName
    parseDataSourceTypeDriveClass(model);
    return false;
  },
  onEdit: model => {
    // dataSourceTypeDriveClass 是级联选择器的值，绑定了后端需要的 dataSourceType 和 driverClassName
    parseDataSourceTypeDriveClass(model);
    return false;
  },
  feedbackProps: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 400,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      :request-api="listDataSourcePage"
      :init-request-params
      :columns
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 2, lg: 5, xl: 5 } }"
      :feedback-form-props
    >
      <template #operation-after="{ row }">
        <el-button link type="primary" size="small" :icon="Loading" v-throttle="() => handleConnect(row)">
          测试
        </el-button>
      </template>
      <template #dialog-footer-before="{ model }">
        <el-button type="primary" :icon="Loading" v-throttle="() => handleConnect(model)">测试连接</el-button>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/namespace" as *;

@include b(dataSource) {
  flex: 1;

  :deep(.#{$teek-namespace}-pro-search) {
    padding: 0;
    margin: 0;
    border: none;
    box-shadow: none;
  }

  :deep(.#{$teek-namespace}-pro-table) {
    padding: 0;
    border: none;
    box-shadow: none;
  }
}
</style>
