<script setup lang="tsx" name="File">
import type { TreeKey } from "element-plus";
import type { ProPageInstance, PageColumn, TableRow, TreeFilterInstance } from "@teek/components";
import type { File } from "@/common/api/file";
import { View, Delete } from "@element-plus/icons-vue";
import { TreeFilter, ProPage, downloadByData, message } from "teek";
import { listAppModule, listFilePage, removeFile, removeBatchFile } from "@/common/api/file";
import { download } from "@/common/api/download";
import { useNamespace, useHandleData } from "@teek/composables";

const ns = useNamespace("app");

const treeFilterInstance = useTemplateRef<TreeFilterInstance>("treeFilterInstance");
const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const route = useRoute();

const initRequestParams = reactive({
  appId: route.params.appId as string,
  appModule: "",
});

const handleDeleteBatch = () => {
  useHandleData("文件将会被删除，确定删除吗？", async () => {
    const selectedList = proPageInstance.value?.proTableInstance?.tableMainInstance?.selectedList || [];
    const fileKeyList = selectedList.map(item => item.fileKey);

    const res = await removeBatchFile(route.params.appId as string, fileKeyList);
    if (res.code === 200) {
      proPageInstance.value?.clearSelection();
      proPageInstance.value?.search();
      message.success("删除成功");
    }
  });
};

const handlePriView = (row: TableRow<File.FileInfo>) => {
  window.open(`${import.meta.env.VITE_API_URL}/download/${row.appId}/${row.fileKey}`);
};

const handleDelete = (row: TableRow<File.FileInfo>) => {
  useHandleData("文件将会被删除，确定删除吗？", async () => {
    const res = await removeFile(row.appId, row.fileKey);
    if (res.code === 200) {
      proPageInstance.value?.search();
      message.success("删除成功");
    }
  });
};

const columns: PageColumn<File.FileInfo>[] = [
  { type: "selection", fixed: "left", width: 80 },
  {
    prop: "fileKey",
    label: "附件标识",
    search: { el: "el-input" },
    width: 280,
    el: "el-link",
    elProps: row => {
      return {
        onClick: () =>
          download(row.appId, row.fileKey)
            .then(res => downloadByData(res, row.fileName))
            .catch(() => message.error("下载失败，后台错误或 AppId 未认证")),
      };
    },
  },
  { prop: "fileName", label: "源附件名称", search: { el: "el-input" }, width: 200 },
  { prop: "filePath", label: "附件存储路径", width: 280 },
  { prop: "fileType", label: "附件类型", width: 100 },
  { prop: "fileSize", label: "附件大小（KB）", width: 160 },
  {
    prop: "expireTime",
    label: "失效时间",
    width: 160,
    search: { el: "el-select", key: "expire" },
    isFilterOptions: false,
    options: [
      { value: true, label: "是" },
      { value: false, label: "否" },
    ],
  },
  { prop: "createTime", label: "创建时间", width: 160 },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const handleTreeChange = (nodeId: string | TreeKey[]) => {
  initRequestParams.appModule = nodeId + "";
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      ref="treeFilterInstance"
      title="模块清单"
      :requestApi="() => listAppModule(route.params.appId as string)"
      @change="handleTreeChange"
      id="appModule"
      label="appModule"
      defaultFirst
    >
      <template #default="{ node }">
        <Icon v-if="node.data.icon" :icon="node.data.icon"></Icon>
        <span>{{ node.label }}</span>
      </template>
    </TreeFilter>

    <div :class="ns.e('role-table')">
      <ProPage
        ref="proPageInstance"
        :request-api="listFilePage"
        :columns="columns"
        :init-request-params="initRequestParams"
        :request-immediate="false"
        :search-props="{ searchCols: { xs: 1, sm: 1, md: 2, lg: 4, xl: 4 } }"
      >
        <template #head-left>
          <el-button
            type="danger"
            :icon="Delete"
            plain
            @click="handleDeleteBatch"
            :disabled="!proPageInstance?.proTableInstance?.tableMainInstance?.isSelected"
          >
            批量删除
          </el-button>
        </template>
        <template #operation="{ row }">
          <el-button link type="primary" :icon="View" @click="handlePriView(row)">预览</el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </ProPage>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(app) {
  display: flex;
  height: 100%;

  @include e(role-table) {
    width: calc(100% - 230px);
    height: 100%;
  }
}
</style>
