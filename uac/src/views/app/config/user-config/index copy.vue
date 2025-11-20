<script setup lang="tsx" name="UserLink">
import type { UserGroup } from "@/common/api/system/user/user-group";
import type { User as UserType } from "@/common/api/system/user/user";
import type { App } from "@/common/api/application/app";
import type { Role } from "@/common/api/system/role";
import type { DescriptionProps, ListCardInstance } from "@/components";
import type { DialogFormInstance } from "./components/dialog-form.vue";
import type { TreeKey } from "element-plus";
import { ElRow, ElCol } from "element-plus";
import { Plus, User } from "@element-plus/icons-vue";
import { TreeFilter, message, useNamespace } from "teek";
import { list } from "@/common/api/system/user/user";
import { addRolesToUser } from "@/common/api/link/user-role-link";
import { addUserGroupsToUser } from "@/common/api/link/user-group-user-link";
import { getAppTreeList } from "@/common/api/application/app";
import {
  listUserGroupByUserId,
  listWithSelectedByUserId as listUserGroupWithDisabledByUserId,
  removeUserFromUserGroup,
  editUserGroupUserLink,
} from "@/common/api/link/user-group-user-link";
import {
  editUserRoleLink,
  listRoleLinkByUserId,
  listWithSelectedByUserId as listRoleWithDisabledByUserId,
  removeUsersFromRole,
} from "@/common/api/link/user-role-link";
import { Description, ListCard } from "@/components";
import { usePermission } from "@/composables";
import DialogForm from "./components/dialog-form.vue";

const ns = useNamespace("user-link");

const { hasAuth } = usePermission();

const userGroupListCardInstance = useTemplateRef<ListCardInstance>("userGroupListCardInstance");
const roleListCardInstance = useTemplateRef<ListCardInstance>("roleListCardInstance");
const userGroupDialogFormInstance = useTemplateRef<DialogFormInstance>("userGroupDialogFormInstance");
const roleDialogFormInstance = useTemplateRef<DialogFormInstance>("roleDialogFormInstance");

const userInfo = ref<UserType.Info>();

const descriptionData = ref<DescriptionProps>({
  title: "",
  data: [],
});
const appId = ref("");
const appTreeList = ref<App.TreeList[]>([]);

onBeforeMount(() => {
  getAppTreeList().then(res => {
    appId.value = res.data[0]?.appId;
    appTreeList.value = res.data;
  });
});

// 点击用户列表的回调
const handleTreeChange = (_: string | TreeKey[], data: UserType.Info) => {
  userInfo.value = data;
  descriptionData.value.title = data.nickname;
  descriptionData.value.data = [
    { label: "用户名", value: data.username },
    { label: "性别", value: data.sex === 1 ? "男" : data.sex === 2 ? "女" : "保密" },
    { label: "电话", value: data.phone },
    { label: "邮箱", value: data.email },
    { label: "注册时间", value: data.registerTime, span: 4 },
  ];
};

// 点击新增用户组按钮的回调
const handleAddUserGroup = () => userGroupDialogFormInstance.value?.openAdd();

// 点击新增角色按钮的回调
const handleAddRole = () => roleDialogFormInstance.value?.openAdd();

// 新增用户组的弹框确认回调
const userGroupConfirm = async (form: any, status: "add" | "edit", callback: () => void) => {
  if (status === "add") {
    addUserGroupsToUser({
      ...form,
      userGroupIds: form.transferIds,
      appId: appId.value,
      userId: userInfo.value?.userId || "",
    }).then((res: any) => {
      if (res.status === "success") {
        message.success("新增成功");
        // 刷新外面的用户组列表
        userGroupListCardInstance.value?.getDataList();
        // 触发 dialog 的回调，关闭 dialog，清除 dialog 的数据
        callback();
      }
    });
  } else {
    editUserGroupUserLink(form).then((res: any) => {
      if (res.status === "success") {
        message.success("修改成功");
        // 刷新外面的用户组列表
        userGroupListCardInstance.value?.getDataList();
        // 触发 dialog 的回调，关闭 dialog，清除 dialog 的数据
        callback();
      }
    });
  }
};

// 新增角色的弹框确认回调
const roleConfirm = async (form: any, status: "add" | "edit", callback: () => void) => {
  if (status === "add") {
    addRolesToUser({
      ...form,
      roleIds: form.transferIds,
      appId: appId.value,
      userId: userInfo.value?.userId || "",
    }).then((res: any) => {
      if (res.status === "success") {
        message.success("新增成功");
        // 刷新外面的用户组列表
        roleListCardInstance.value?.getDataList();
        // 触发 dialog 的回调，关闭 dialog，清除 dialog 的数据
        callback();
      }
    });
  } else {
    editUserRoleLink(form).then((res: any) => {
      if (res.status === "success") {
        message.success("修改成功");
        // 刷新外面的用户组列表
        roleListCardInstance.value?.getDataList();
        // 触发 dialog 的回调，关闭 dialog，清除 dialog 的数据
        callback();
      }
    });
  }
};

// 编辑用户组的回调
const userGroupEdit = (item: UserGroup.LinkInfo) => {
  userGroupDialogFormInstance.value?.openEdit({ id: item.linkId, validFrom: item.validFrom, expireOn: item.expireOn });
};

// 删除用户组的回调
const userGroupDelete = (item: UserGroup.LinkInfo) => {
  removeUserFromUserGroup([item.linkId + ""]).then((res: any) => {
    if (res.status === "success") {
      message.success("删除成功");
      userGroupListCardInstance.value?.getDataList();
    }
  });
};

// 编辑角色的回调
const roleEdit = (item: Role.LinkInfo) => {
  roleDialogFormInstance.value?.openEdit({ id: item.linkId, validFrom: item.validFrom, expireOn: item.expireOn });
};

// 删除角色的回调
const roleDelete = (item: Role.LinkInfo) => {
  removeUsersFromRole([item.linkId + ""]).then((res: any) => {
    if (res.status === "success") {
      message.success("删除成功");
      roleListCardInstance.value?.getDataList();
    }
  });
};
</script>

<template>
  <div :class="ns.b()">
    <TreeFilter
      title="用户列表"
      :requestApi="list"
      @change="(value, data: any) => handleTreeChange(value, data)"
      id="userId"
      label="username"
      style="width: 300px"
      default-first
    >
      <template #default="{ node }">
        <el-icon style="margin-right: 10px"><User /></el-icon>
        <span>{{ node.label }}{{ node.data.nickname ? `（${node.data.nickname}）` : "" }}</span>
      </template>
    </TreeFilter>

    <div :class="ns.e('right')">
      <Description :title="descriptionData.title" :data="descriptionData.data">
        <template #extra>
          <div>
            <el-select
              v-model="appId"
              placeholder="请选择 App 清单"
              size="small"
              style="width: 120px; margin-right: 10px"
            >
              <el-option v-for="item in appTreeList" :key="item.appId" :label="item.appName" :value="item.appId" />
            </el-select>
          </div>
        </template>
      </Description>

      <el-row :gutter="10" :class="ns.em('right', 'body')" v-if="userInfo">
        <el-col :span="6" style="height: 100%">
          <ListCard
            ref="userGroupListCardInstance"
            title="用户组"
            :request-api="listUserGroupByUserId"
            :request-params="{ appId: appId, userId: userInfo?.userId }"
            value="groupId"
            label="groupName"
            @edit="userGroupEdit"
            @remove="userGroupDelete"
            :use-edit="hasAuth('system:user:edit')"
            :use-remove="hasAuth('system:user:remove')"
          >
            <template #extra>
              <el-button v-auth="['system:user:add']" link :icon="Plus" @click="handleAddUserGroup">新增</el-button>
            </template>
          </ListCard>
        </el-col>
        <el-col :span="6" style="height: 100%">
          <ListCard
            ref="roleListCardInstance"
            title="角色"
            :data="[]"
            :request-api="listRoleLinkByUserId"
            :request-params="{ appId: appId, userId: userInfo?.userId }"
            value="roleId"
            label="roleName"
            @edit="roleEdit"
            @remove="roleDelete"
            :use-edit="hasAuth('system:user:edit')"
            :use-remove="hasAuth('system:user:remove')"
          >
            <template #extra>
              <el-button v-auth="['system:user:add']" link :icon="Plus" @click="handleAddRole">新增</el-button>
            </template>
          </ListCard>
        </el-col>
      </el-row>

      <DialogForm
        ref="userGroupDialogFormInstance"
        id="groupId"
        :transfer-select-column="[{ prop: 'groupName', label: '用户组名' }]"
        :transfer-api="listUserGroupWithDisabledByUserId"
        :request-params="{ appId: appId, userId: userInfo?.userId }"
        transfer-placeholder="用户组"
        @confirm="userGroupConfirm"
      />

      <DialogForm
        ref="roleDialogFormInstance"
        id="roleId"
        :transfer-select-column="[{ prop: 'roleName', label: '角色名' }]"
        :transfer-api="listRoleWithDisabledByUserId"
        :request-params="{ appId: appId, userId: userInfo?.userId }"
        transfer-placeholder="角色"
        @confirm="roleConfirm"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(user-link) {
  display: flex;
  width: 100%;
  height: 100%;

  @include e(right) {
    width: 100%;
    padding: 10px;
    background-color: #ffffff;

    @include m(body) {
      display: flex;
      height: calc(100vh - 215px);

      :deep(.#{$el-namespace}-menu-item) {
        height: 32px;
      }
    }
  }
}
</style>
