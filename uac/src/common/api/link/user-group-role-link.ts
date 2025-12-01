import type { UserGroup } from "../system/user/user-group";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/userGroupRoleLink";

/**
 * 查询某个角色绑定的用户组列表
 */
export const listUserGroupByRoleId = (params: { roleId: string }) => {
  return http.get<httpNs.Response<UserGroup.LinkInfo[]>>(`${baseUri}/listUserGroupByRoleId/${params.roleId}`, {
    ...params,
    roleId: undefined,
  });
};

/**
 * 通过用户组 ID 查询角色列表
 */
export const listRoleLinkByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Response<Role.LinkInfo[]>>(`${baseUri}/listRoleLinkByGroupId/${params.userGroupId}`, {
    ...params,
    userGroupId: undefined,
  });
};

/**
 * 查询所有用户组列表，如果用户组绑定角色，则 disabled 属性为 true
 */
export const listWithSelectedByRoleId = (params: { roleId: string }) => {
  return http.get<httpNs.Response<UserGroup.BindSelect[]>>(`${baseUri}/listWithSelectedByRoleId/${params.roleId}`);
};

/**
 * 查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true
 */
export const listWithSelectedByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(`${baseUri}/listWithSelectedByGroupId/${params.userGroupId}`);
};

/**
 * 添加角色到用户组（多个角色）
 */
export const addRolesToUserGroup = (data: UserGroup.LinkRoles) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRolesToUserGroup`, data);
};

/**
 * 添加角色到用户组（多个用户组）
 */
export const addUserGroupsToRole = (data: Role.LinkUserGroups) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addUserGroupsToRole`, data);
};

/**
 * 修改用户组和用户的关联信息
 */
export const editUserGroupRoleLink = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editUserGroupRoleLink`, data);
};

/**
 * 将用户组移出角色
 */
export const removeUserGroupFromRole = (ids: string[]) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeUserGroupFromRole/${ids.join(",")}`);
};
