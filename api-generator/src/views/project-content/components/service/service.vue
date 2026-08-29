<script setup lang="tsx" name="Service">
import type { FeedbackFormProps, FeedbackFormColumn, TableColumn, ProPageInstance } from "teek";
import { listServicePage, addService, editService, removeService, type Service } from "@/common/api/service";
import { ProjectKey } from "@/common/config";
import { ElTooltip, ElTag } from "element-plus";
import { ProPage, message, PointTag, useDialog } from "teek";
import { useNamespace, useClipboard } from "@teek/composables";
import { Search, MoreFilled, Edit } from "@element-plus/icons-vue";
import { useDictStore } from "@/pinia";
import { getReportByServiceId } from "@/common/api/report";
import ReportForm from "./report-form.vue";

const ns = useNamespace("service");

const { copy } = useClipboard();
const { open } = useDialog();

const props = defineProps<{ categoryId: string }>();

const router = useRouter();
const { getDictData } = useDictStore();

const projectInfo = inject(ProjectKey, ref());
const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");
const initRequestParams = reactive({ categoryId: "", projectId: "" });

watch(
  () => [projectInfo.value, props.categoryId],
  () => {
    initRequestParams.projectId = projectInfo.value?.projectId || "";
    initRequestParams.categoryId = props.categoryId;
  }
);

const handleCopyLink = (row: Service.ServiceInfo) => {
  copy(`${import.meta.env.VITE_API_URL}/generic-api/list${projectInfo.value?.baseUrl}${row.serviceUrl}`);
  message.success("复制成功");
};

const handleCommand = (command: string, row: Service.ServiceInfo) => {
  if (command === "handleEditReport") {
    editReport(row);
  } else if (command === "handleSearchServiceData") {
    handleSearchServiceData(row);
  }
};

const reportFormInstance = useTemplateRef<InstanceType<typeof ReportForm>>("reportFormInstance");

const editReport = async (row: Service.ServiceInfo) => {
  const res = await getReportByServiceId(row.serviceId);
  if (res.code === 200) {
    open({
      title: "报表编辑",
      render: () => <ReportForm ref={reportFormInstance} reportInfo={res.data} />,
      onConfirm: async () => {
        const result = await reportFormInstance.value?.handleEditReport();
        if (result) {
          proPageInstance.value?.search();
          return message.success("更新成功");
        }
        message.error("更新失败");
      },
    });
  }
};

const handleSearchServiceData = (row: Service.ServiceInfo) => {
  window.open(
    `/serviceQuery/${row.serviceId}/${row.serviceName}?query=/generic-api/list${projectInfo.value?.baseUrl}${row.serviceUrl}&secretKey=${projectInfo.value?.secretKey}`
  );
};

const statusColor: Recordable = {
  0: "var(--el-color-info)",
  1: "var(--el-color-primary)",
};

const tableColumns: TableColumn[] = [
  { type: "index", label: "#", width: 80 },
  {
    prop: "serviceName",
    label: "接口名称",
    width: 180,
    search: { el: "el-input" },
    el: "el-link",
    elProps: row => ({ onClick: () => router.push(`/service/${row.serviceId}/${row.serviceName}`) }),
  },
  {
    prop: "reportTitle",
    label: "报表名称",
    width: 180,
    el: "el-link",
    elProps: row => ({
      type: "info",
      onClick: () => window.open(`/report/${row.serviceId}`),
    }),
  },
  {
    prop: "status",
    label: "接口状态",
    width: 100,
    options: () => getDictData("sys_normal_status"),
    optionField: { value: "dictValue", label: "dictLabel" },
    search: { el: "el-select" },
    render: ({ row, displayValue }) => <PointTag color={statusColor[row.status]}>{displayValue}</PointTag>,
  },
  {
    prop: "serviceUrl",
    label: "接口地址",
    render: ({ row }) => (
      <ElTooltip effect="dark" content="点击复制完整地址" placement="top">
        <ElTag type="primary" onClick={() => handleCopyLink(row)} class="cursor-pointer">
          {row.serviceUrl}
        </ElTag>
      </ElTooltip>
    ),
  },
  { prop: "description", label: "接口描述" },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: 280, fixed: "right" },
];

const formColumns: FeedbackFormColumn<Service.ServiceInfo>[] = [
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
    elProps: { placeholder: "请输入 服务地址" },
    slots: {
      prepend: () => projectInfo.value?.baseUrl,
    },
  },
  {
    prop: "status",
    label: "服务状态",
    el: "el-select",
    defaultValue: 1,
    options: [
      { value: 0, label: "下线" },
      { value: 1, label: "上线" },
    ],
    elProps: { placeholder: "请选择 服务状态" },
  },
  {
    prop: "isAuth",
    label: "是否认证",
    el: "el-select",
    defaultValue: 0,
    options: [
      { value: 0, label: "禁用" },
      { value: 1, label: "启用" },
    ],
    elProps: { placeholder: "请选择 是否认证" },
  },
  {
    prop: "description",
    label: "服务描述",
    el: "el-input",
    elProps: { type: "textarea" },
  },
];

const feedbackFormProps: FeedbackFormProps = {
  form: {
    elFormProps: {
      labelWidth: 80,
      rules: {
        serviceName: [{ required: true, message: "请选择服务名称", trigger: "blur" }],
        serviceUrl: [{ required: true, message: "请输入服务地址", trigger: "blur" }],
        status: [{ required: true, message: "请选择服务状态", trigger: "blur" }],
        isAuth: [{ required: true, message: "请选择是否认证", trigger: "blur" }],
      },
    },
    columns: formColumns,
  },
  id: ["id", "serviceId"],
  addApi: data =>
    addService({
      ...data,
      categoryId: props.categoryId,
      projectId: projectInfo.value?.projectId,
      teamId: projectInfo.value?.teamId,
    }),
  editApi: data => editService({ ...data, projectId: projectInfo.value?.projectId }),
  removeApi: removeService,
  disableAdd: projectInfo.value?.projectRole === "只读成员",
  disableEdit: projectInfo.value?.projectRole === "只读成员",
  disableRemove: projectInfo.value?.projectRole === "只读成员",
  feedbackProps: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 250,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listServicePage"
      :columns="tableColumns"
      :init-request-params
      :requestImmediate="false"
      :feedback-form-props
    >
      <template #operation-after="{ row }">
        <el-dropdown @command="command => handleCommand(command, row)" class="align-middle">
          <el-button link type="info" :icon="MoreFilled">更多</el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="handleEditReport" :icon="Edit">报表编辑</el-dropdown-item>
              <el-dropdown-item command="handleSearchServiceData" :icon="Search">服务查询</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(service) {
  flex: 1;
}
</style>
