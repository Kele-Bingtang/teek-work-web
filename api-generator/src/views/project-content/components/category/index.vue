<script setup lang="ts" name="Category">
import type { FeedbackFormProps, TableColumn, FormColumn } from "teek";
import { ProTable, mittBus } from "teek";
import { listCategoryPage, addCategory, editCategory, removeCategory } from "@/common/api/category";
import { ProjectKey } from "@/common/config";

const projectInfo = inject(ProjectKey, ref());

const teamId = computed(() => projectInfo.value?.teamId);
const projectId = computed(() => projectInfo.value?.projectId);

const initRequestParams = reactive({ teamId: teamId.value, projectId: projectId.value });

const tableColumns: TableColumn[] = [
  { type: "index", label: "#", width: 80 },
  { prop: "categoryName", label: "目录名称", search: { el: "el-input" } },
  { prop: "categoryCode", label: "目录编码", search: { el: "el-input" } },
  {
    prop: "isMain",
    label: "主目录",
    tag: true,
    options: [
      { value: 0, label: "否", tagEl: "el-check-tag", tagType: "primary" },
      { value: 1, label: "是", tagEl: "el-check-tag", tagType: "success" },
    ],
  },
  { prop: "orderNum", label: "排序" },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const formColumns: FormColumn[] = [
  { prop: "categoryCode", label: "目录编码", el: "el-input" },
  { prop: "categoryName", label: "目录名称", el: "el-input" },
  { prop: "orderNum", label: "排序", el: "el-input-number", defaultValue: 0 },
];

const elFormProps = {
  labelWidth: 80,
  rules: {
    categoryCode: [{ required: true, message: "请输入目录编码", trigger: "blur" }],
    categoryName: [{ required: true, message: "请输入目录名称", trigger: "blur" }],
  },
};

const feedbackFormProps: FeedbackFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "categoryId"],
  addApi: data => addCategory({ ...data, ...initRequestParams }),
  editApi: data => editCategory({ ...data, ...initRequestParams }),
  removeApi: removeCategory,
  disableAdd: projectInfo.value?.projectRole === "只读成员",
  disableEdit: projectInfo.value?.projectRole === "只读成员",
  disableRemove: projectInfo.value?.projectRole === "只读成员",
  afterConfirm: () => {
    mittBus.emit("initTreeData");
  },
  feedbackProps: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "30%",
    height: 200,
    top: "5vh",
    closeOnClickModal: false,
  },
};
</script>

<template>
  <ProTable
    :request-api="listCategoryPage"
    :init-request-params
    :columns="tableColumns"
    :feedback-form-props
  ></ProTable>
</template>
