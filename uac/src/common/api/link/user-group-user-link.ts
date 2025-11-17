import type { User } from "../system/user/user";
import type { UserGroup } from "../system/user/user-group";
import { http } from "@/common/http";

// 添加部门到角色（多个部门）
export interface RoleLinkMenu {
  roleId: string; // 角色 ID
  appId: string; // 应用 ID
  selectedMenuIds: string[]; // 部门 ID
}

const baseUri = "/system/userGroupUserLink";

/**
 * 通过用户组 ID 查询用户列表
 */
export const listUserLinkByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Page<User.LinkInfo[]>>(`${baseUri}/listUserLinkByGroupId/${params.userGroupId}`, {
    ...params,
    userGroupId: undefined,
  });
};

/**
 * 查询某个用户所在的用户组列表
 */
export const listUserGroupByUserId = (params: { appId: string; userId: string }) => {
  return http.get<httpNs.Response<UserGroup.LinkUserInfo[]>>(
    `${baseUri}/listUserGroupByUserId/${params.appId}/${params.userId}`
  );
};

/**
 * 下拉查询用户列表，如果用户绑定了用户组，则 disabled 属性为 true
 */
export const listWithDisabledByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Response<User.BindSelect[]>>(`${baseUri}/listWithDisabledByGroupId/${params.userGroupId}`);
};

/**
 * 查询所有用户组列表，如果用户组存在用户，则 disabled 属性为 true
 */
export const listWithDisabledByUserId = (params: { appId: string; userId: string }) => {
  return http.get<httpNs.Response<UserGroup.BindSelect[]>>(
    `${baseUri}/listWithDisabledByUserId/${params.appId}/${params.userId}`
  );
};

/**
 * 添加用户组到用户（多个用户组）
 */
export const addUserGroupsToUser = (data: User.LinkUserGroup) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addUserGroupsToUser`, data);
};

/**
 * 添加用户到用户组（多个用户）
 */
export const addUsersToGroup = (data: UserGroup.LinkUserInfo) => {
  return http.post<httpNs.Response<string>>(`${baseUri}/addUsersToGroup`, data);
};

/**
 * 将用户移出项目组
 */
export const removeUserFromUserGroup = (ids: string[]) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeUserFromUserGroup/${ids.join(",")}`);
};

/**
 * 修改用户组和用户的关联信息
 */
export const editUserGroupUserLink = (data: RequiredKeyPartialOther<UserGroup.LinkUserInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editUserGroupUserLink`, data);
};
