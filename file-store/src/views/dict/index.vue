<script setup lang="tsx" name="DictType">
import type { FeedbackFormProps, PageColumn } from "@teek/components";
import type { DictType } from "@/common/api/dictType";
import { ElLink, ElMessageBox } from "element-plus";
import { ProPage, BlankDrawer, PointTag, downloadByData } from "teek";
import { listPage, addDictType, editDictType, removeDictType, exportExcel } from "@/common/api/dictType";
import { baseOptions } from "@teek/config";
import { useNamespace } from "@teek/composables";
import { dictTypeElFormProps, useFormColumns } from "./use-form-columns";
import DictData from "./dict-data.vue";

const ns = useNamespace("dict-type");

const dictInfo = ref<DictType.DictTypeInfo>();
const drawer = ref(false);

const clickDictCode = (row: DictType.DictTypeInfo) => {
  dictInfo.value = row;
  drawer.value = true;
};

const isCascadeColorMap: Record<string, any> = {
  0: "#909399",
  1: "#395ae3",
};

const columns: PageColumn<DictType.DictTypeInfo>[] = [
  { type: "index", label: "#", width: 80 },
  {
    prop: "dictCode",
    label: "字典编码",
    search: { el: "el-input" },
    render: ({ row }) => {
      return (
        <>
          <ElLink type="primary" underline={false} onClick={() => clickDictCode(row)}>
            {row.dictCode}
          </ElLink>
        </>
      );
    },
  },
  { prop: "dictName", label: "字典名称", search: { el: "el-input" } },
  {
    prop: "isCascade",
    label: "是否级联",
    width: 110,
    options: baseOptions,
    search: { el: "el-input" },
    render: ({ row }: any) => {
      return (
        <>
          <PointTag color={isCascadeColorMap[row.isCascade]}></PointTag>
          {row._options.isCascade}
        </>
      );
    },
  },
  { prop: "intro", label: "描述" },
  { prop: "createTime", label: "创建时间", sortable: true },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const feedbackFormProps: FeedbackFormProps = {
  form: {
    elFormProps: dictTypeElFormProps,
    columns: useFormColumns(computed(() => "")).dictTypeColumns,
  },
  id: ["id"],
  addApi: addDictType,
  editApi: editDictType,
  removeApi: removeDictType,
  feedbackProps: {
    title: (_, status) => (status === "add" ? "新增" : "编辑"),
    width: "45%",
    height: 300,
    top: "5vh",
    closeOnClickModal: false,
  },
};

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `dictType_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <div :class="ns.e('table')">
      <ProPage :request-api="listPage" :columns="columns" :feedbackFormProps :exportFile></ProPage>
    </div>

    <BlankDrawer v-model="drawer" size="55%" title="字典数据配置">
      <div :class="ns.e('drawer-content')">
        <DictData :dict-code="dictInfo?.dictCode || ''" :is-cascade="dictInfo?.isCascade || 0" />
      </div>
    </BlankDrawer>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(dict-type) {
  display: flex;
  flex: 1;

  @include e(table) {
    height: 100%;

    :deep(.#{$el-namespace}-dialog__body) {
      margin-left: 20px;
    }
  }

  @include e(drawer-content) {
    width: 100%;
    height: 100%;
    background-color: #f0f2f5;
  }
}
</style>
