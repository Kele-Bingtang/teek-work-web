<script setup lang="ts" name="DictData">
import type { DialogFormProps, PageColumn } from "@teek/components";
import type { DictData } from "@/common/api/system/dict-data";
import { ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { ProPage, downloadByData } from "teek";
import {
  listPage,
  listDataTreeTable,
  addDictData,
  editDictData,
  removeDictData,
  exportExcel,
} from "@/common/api/system/dict-data";
import { dictDataElFormProps, useFormColumns } from "./use-form-columns";

export interface DictDataProps {
  dictCode: string;
  isCascade: number;
}

// 接受父组件参数，配置默认值
const props = defineProps<DictDataProps>();
const route = useRoute();

const initRequestParams = reactive({
  dictCode: computed(() => props.dictCode),
  appId: route.params.appId as string,
});

const columns: PageColumn<DictData.Info>[] = [
  { type: "index", label: "#", width: 80 },
  { prop: "dictLabel", label: "字典标签", align: "left", search: { el: "el-input" } },
  { prop: "dictValue", label: "字典键值" },
  { prop: "dictSort", label: "字典排序" },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: computed(() => (props.isCascade ? 200 : 160)) },
];

const dialogFormProps: DialogFormProps = {
  form: {
    elFormProps: dictDataElFormProps,
    columns: useFormColumns(
      computed(() => props.dictCode),
      computed(() => props.isCascade)
    ).dictDataColumns,
  },
  dialog: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 350,
    top: "5vh",
    closeOnClickModal: false,
  },
  id: ["id"],
  addApi: params => addDictData({ ...params, appId: initRequestParams.appId }),
  editApi: editDictData,
  removeApi: removeDictData,
  beforeEdit: form => {
    if (form.tagEl === undefined) form.tagEl = "";
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel({ ...searchParam, dictCode: props.dictCode }).then(res => {
      downloadByData(res, `dictData_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <ProPage
    :request-api="!isCascade ? listPage : listDataTreeTable"
    :columns
    :init-request-params="initRequestParams"
    :page-scope="!isCascade"
    :dialog-form-props
    :export-file
    :indent="0"
  >
    <template #operation-after="{ row, dialogFormInstance }" v-if="isCascade">
      <el-button link size="small" :icon="Plus" @click="dialogFormInstance?.handleAdd({ parentId: row.dataId })">
        新增
      </el-button>
    </template>
  </ProPage>
</template>
