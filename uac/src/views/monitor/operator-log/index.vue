<script setup lang="tsx" name="OperatorLog">
import type { ProPageInstance, PageColumn } from "teek";
import type { OperaLog } from "@/common/api/monitor/opera-log";
import { ElMessageBox, ElMessage } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import { ProPage, downloadByData, useNamespace } from "teek";
import { listPage, removeBatch, cleanAllLog, exportExcel } from "@/common/api/monitor/opera-log";
import { useDictStore } from "@/pinia";
import { usePermission } from "@/composables";

const ns = useNamespace("operator-log");

const { hasAuth } = usePermission();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const initRequestParams = reactive({
  orderRuleList: [{}],
});

const handleDeleteBatch = async (scope: any) => {
  ElMessageBox.confirm(`删除所选信息?`, "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    draggable: true,
  }).then(async () => {
    removeBatch(scope.selectedListIds)
      .then(res => {
        if (res.status === "success") {
          ElMessage.success("批量删除成功");
          proPageInstance.value?.clearSelection();
          proPageInstance.value?.search();
        } else {
          ElMessage.success("批量删除失败");
        }
      })
      .catch(err => {
        ElMessage.success("批量删除失败");
        console.log(err);
      });
  });
};

const handleClean = () => {
  ElMessageBox.confirm(`清除全部信息?`, "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    draggable: true,
  }).then(async () => {
    cleanAllLog()
      .then(res => {
        if (res.status === "success") {
          ElMessage.success("清除日志成功");
          proPageInstance.value?.clearSelection();
          proPageInstance.value?.search();
        } else {
          ElMessage.success("清除日志失败");
        }
      })
      .catch(err => {
        ElMessage.success("清除日志失败");
        console.log(err);
      });
  });
};

const sortChange = (data: { column: any; prop: string; order: any }) => {
  initRequestParams.orderRuleList = [{ column: data.prop, type: data.order === "descending" ? "desc" : "asc" }];
};

const columns: PageColumn<OperaLog.Info>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { prop: "operaId", label: "操作编号", width: 170 },
  { prop: "title", label: "模块标题", search: { el: "el-input" } },
  {
    prop: "businessType",
    label: "操作类型",
    width: 100,
    tag: true,
    search: { el: "el-select" },
    optionField: { value: "dictValue", label: "dictLabel" },
    options: () => useDictStore().getDictData("sys_opera_business_type"),
  },
  { prop: "operaName", label: "操作人员", search: { el: "el-input" } },
  { prop: "deptName", label: "操作人部门" },
  { prop: "operaIp", label: "操作 IP 地址", search: { el: "el-input" } },
  { prop: "operaLocation", label: "操作地点", search: { el: "el-input" } },
  {
    prop: "status",
    label: "状态",
    width: 100,
    tag: true,
    options: [
      { value: "0", label: "失败", tagEl: "el-check-tag", tagType: "danger" },
      { value: "1", label: "成功", tagEl: "el-check-tag", tagType: "success" },
    ],
    search: { el: "el-select" },
  },
  {
    prop: "operaTime",
    label: "操作时间",
    sortable: "custom",
    search: {
      el: "el-date-picker",
      order: 3,
      elProps: { type: "daterange" },
    },
  },
  { prop: "costTime", label: "消耗时间/毫秒", search: { el: "el-input" } },
];

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `operatorLog_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listPage"
      :init-request-params="initRequestParams"
      :columns
      :search-props="{ searchCols: { xs: 1, sm: 1, md: 3, lg: 5, xl: 5 } }"
      @sort-change="sortChange"
      :default-sort="{ prop: 'loginTime', order: 'descending' }"
      :export-file
      :disabled-tool-button="!hasAuth('system:operaLog:export') ? ['export'] : []"
    >
      <template #tableHeader="scope">
        <el-button
          v-auth="['system:operaLog:remove']"
          type="danger"
          :icon="Delete"
          plain
          @click="handleDeleteBatch(scope)"
          :disabled="!scope?.isSelected"
        >
          批量删除
        </el-button>
        <el-button v-auth="['system:operaLog:clear']" type="danger" :icon="Delete" plain @click="handleClean()">
          清除
        </el-button>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(operator-login) {
  height: 100%;
}
</style>
