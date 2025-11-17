<script setup lang="tsx" name="LoginLog">
import type { ProPageInstance, PageColumn } from "@teek/components";
import { ProPage, downloadByData, useNamespace } from "teek";
import { ElMessageBox, ElMessage } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import { listPage, removeBatch, cleanAllLog, type LoginLog, exportExcel } from "@/common/api/monitor/login-log";
import { usePermission } from "@/composables";

const ns = useNamespace("login-log");

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

const columns: PageColumn<LoginLog.Info>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { prop: "loginId", label: "登录编号", width: 170 },
  { prop: "username", label: "用户账号", search: { el: "el-input" } },
  { prop: "clientName", label: "客户端名称", width: 100, search: { el: "el-input" } },
  { prop: "loginIp", label: "登录 IP 地址", search: { el: "el-input" } },
  { prop: "loginLocation", label: "登录地点", search: { el: "el-input" } },
  { prop: "browser", label: "浏览器类型", search: { el: "el-input" } },
  { prop: "os", label: "操作系统" },
  { prop: "msg", label: "提示消息" },
  {
    prop: "status",
    label: "状态",
    width: 100,
    tag: true,
    options: [
      { value: "0", label: "失败", tagEl: "el-check-tag", tagType: "danger" },
      { value: "1", label: "成功", tagEl: "el-check-tag", tagType: "success" },
    ],
    search: { el: "el-select", order: 3 },
  },
  {
    prop: "loginTime",
    label: "登录时间",
    sortable: "custom",
    search: {
      el: "el-date-picker",
      order: 3,
      elProps: { type: "daterange" },
    },
  },
];

const exportFile = (_: Record<string, any>[], searchParam: Record<string, any>) => {
  ElMessageBox.confirm("确认导出吗？", "温馨提示", { type: "warning" }).then(() => {
    exportExcel(searchParam).then(res => {
      downloadByData(res, `loginLog_${new Date().getTime()}.xlsx`);
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
      :disabled-tool-button="!hasAuth('system:loginLog:export') ? ['export'] : []"
    >
      <template #tableHeader="scope">
        <el-button
          v-auth="['system:loginLog:remove']"
          type="danger"
          :icon="Delete"
          plain
          @click="handleDeleteBatch(scope)"
          :disabled="!scope?.isSelected"
        >
          批量删除
        </el-button>
        <el-button v-auth="['system:loginLog:clear']" type="danger" :icon="Delete" plain @click="handleClean()">
          清除
        </el-button>
      </template>
    </ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(login-log) {
  height: 100%;
}
</style>
