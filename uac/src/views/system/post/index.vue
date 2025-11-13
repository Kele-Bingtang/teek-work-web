<script setup lang="tsx" name="Post">
import type { DialogFormProps, ProPageInstance, PageColumn } from "@teek/components";
import type { Post } from "@/common/api/system/post";
import { ElMessageBox, ElSwitch } from "element-plus";
import { ProPage, downloadByData, useNamespace } from "teek";
import { listPage, addPost, editPost, removePost, removeBatch, exportExcel } from "@/common/api/system/post";
import { listDeptTreeList } from "@/common/api/system/dept";
import { useDictStore } from "@/pinia";
import { useChange, usePermission } from "@/composables";
import { elFormProps, useFormColumns } from "./use-form-columns";

// 部门管理的用户列表需要传入
const props = defineProps<{ initRequestParams?: Recordable }>();

const ns = useNamespace("post");

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const { statusChange } = useChange<Post.PostInfo>(
  "postName",
  "岗位",
  (row, status) => editPost({ id: row.id, postId: row.postId, status }),
  () => proPageInstance.value?.search()
);

const columns: PageColumn<Post.PostInfo>[] = [
  { type: "selection", fixed: "left", width: 80 },
  { prop: "postCode", label: "岗位编码", search: { el: "el-input" } },
  { prop: "postName", label: "岗位名称", search: { el: "el-input" } },
  {
    prop: "deptId",
    label: "所属部门",
    search: { el: "el-tree-select", elProps: { showCheckbox: true, checkStrictly: true } },
    options: () => listDeptTreeList(),
  },
  { prop: "orderNum", label: "排序" },
  {
    prop: "status",
    label: "状态",
    optionField: { value: "dictValue", label: "dictLabel" },
    options: () => useDictStore().getDictData("sys_normal_status"),
    search: { el: "el-select" },
    render: ({ row }) => {
      return (
        <>
          {row.status !== undefined && (
            <ElSwitch
              v-model={row.status}
              activeValue={1}
              inactiveValue={0}
              activeText="启用"
              inactiveText="停用"
              inlinePrompt
              onChange={value => statusChange(value, row)}
            />
          )}
        </>
      );
    },
  },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", width: 160, fixed: "right" },
];

const { hasAuth } = usePermission();

const formColumns = useFormColumns(computed(() => props.initRequestParams?.deptId));

const dialogFormProps: DialogFormProps = {
  form: { elFormProps, columns: formColumns },
  id: ["id", "postId"],
  addApi: addPost,
  editApi: editPost,
  removeApi: removePost,
  removeBatchApi: removeBatch,
  disableAdd: !hasAuth("system:post:add"),
  disableEdit: !hasAuth("system:post:edit"),
  disableRemove: !hasAuth("system:post:remove"),
  disableRemoveBatch: !hasAuth("system:post:remove"),
  dialog: {
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
      downloadByData(res, `post_${new Date().getTime()}.xlsx`);
    });
  });
};
</script>

<template>
  <div :class="ns.b()">
    <ProPage
      ref="proPageInstance"
      :request-api="listPage"
      :init-request-params
      :columns
      :dialog-form-props
      :export-file
      :disabled-tool-button="!hasAuth('system:post:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>
