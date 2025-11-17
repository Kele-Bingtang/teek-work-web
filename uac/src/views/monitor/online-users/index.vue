<script setup lang="tsx" name="OnlineUsers">
import type { ProPageInstance, PageColumn } from "teek";
import type { OnlineUser } from "@/common/api/monitor/online-user";
import { ProPage, useNamespace } from "teek";
import { forceLogout, listPage } from "@/common/api/monitor/online-user";
import { listDeptTreeList } from "@/common/api/system/dept";
import { useUserStore } from "@/pinia";
import { usePermission } from "@/composables";

const ns = useNamespace("online-users");

const { hasAuth } = usePermission();

const proPageInstance = useTemplateRef<ProPageInstance>("proPageInstance");

const initRequestParams = reactive({
  orderRuleList: [{}],
});

const sortChange = (data: { column: any; prop: string; order: any }) => {
  initRequestParams.orderRuleList = [{ column: data.prop, type: data.order === "descending" ? "desc" : "asc" }];
};

const { userInfo } = useUserStore();

const columns: PageColumn<OnlineUser.Info>[] = [
  { prop: "username", label: "用户账号", search: { el: "el-input" } },
  { prop: "nickname", label: "用户昵称" },
  { prop: "deptId", label: "所属部门", options: () => listDeptTreeList() },
  { prop: "clientName", label: "客户端名称" },
  { prop: "loginIp", label: "登录 IP 地址" },
  { prop: "loginLocation", label: "登录地点" },
  { prop: "browser", label: "浏览器类型" },
  { prop: "os", label: "操作系统", width: 280 },
  { prop: "loginTime", label: "登录时间", sortable: "custom" },
  {
    prop: "operation",
    label: "操作",
    width: 100,
    fixed: "right",
    render: ({ row }) => (
      <el-popconfirm title={`你确定强制 ${row.nickname} 用户下线吗?`} onConfirm={() => forceLogout(row.username)}>
        {{
          reference: () => (
            <el-button
              v-auth={["system:onlineUsers:forceLogout"]}
              link
              type="danger"
              disabled={row.username === userInfo.username}
            >
              强退{" "}
            </el-button>
          ),
        }}
      </el-popconfirm>
    ),
  },
];
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
      :disabled-tool-button="!hasAuth('system:onlineUsers:export') ? ['export'] : []"
    ></ProPage>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(online-users) {
  height: 100%;
}
</style>
