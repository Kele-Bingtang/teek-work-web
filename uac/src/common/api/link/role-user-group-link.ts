import type { UserGroup } from "../system/user/user-group";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/roleUserGroupLink";

// ------- 用户组关联角色相关 API（以用户组为主）-------

/**
 * 通过用户组 ID 查询角色列表
 */
export const listRoleLinkByGroupId = (params: Partial<Role.Info & { userGroupId: string }>) => {
  return http.get<httpNs.Page<Role.LinkInfo[]>>(
    `${baseUri}/listRoleLinkByGroupId/${params.appId}/${params.userGroupId}`,
    {
      ...params,
      appId: undefined,
      id: undefined,
    }
  );
};

/**
 * 查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true
 */
export const listWithSelectedByGroupId = (params: { appId: string; userGroupId: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(
    `${baseUri}/listWithSelectedByGroupId/${params.appId}/${params.userGroupId}`
  );
};

/**
 * 添加角色到用户组（多个角色）
 */
export const addRoleListToUserGroup = (data: UserGroup.LinkRoles) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRoleListToUserGroup`, data);
};

// ------- 角色关联用户组相关 API（以角色为主）-------

/**
 * 查询某个角色绑定的用户组列表
 */
export const listUserGroupByRoleId = (params: Partial<UserGroup.Info & { appId: string; roleId: string }>) => {
  return http.get<httpNs.Page<UserGroup.LinkInfo[]>>(
    `${baseUri}/listUserGroupByRoleId/${params.appId}/${params.roleId}`,
    { ...params, appId: undefined, roleId: undefined }
  );
};

/**
 * 查询所有用户组列表，如果用户组绑定角色，则 disabled 属性为 true
 */
export const listWithSelectedByRoleId = (params: { appId: string; roleId: string }) => {
  return http.get<httpNs.Response<UserGroup.BindSelect[]>>(
    `${baseUri}/listWithSelectedByRoleId/${params.appId}/${params.roleId}`
  );
};

/**
 * 添加角色到用户组（多个用户组）
 */
export const addUserGroupListToRole = (data: Role.LinkUserGroups) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addUserGroupListToRole`, data);
};

// ------- 公共 API -------

/**
 * 修改用户组和用户的关联信息
 */
export const editRoleUserGroupLink = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editRoleUserGroupLink`, data);
};

/**
 * 将用户组移出角色
 */
export const removeRoleUserGroupLink = (data: Role.LinkInfo & { idList: string[]; dataList: Role.LinkInfo[] }) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleUserGroupLink/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleUserGroupLink/${data.linkId}`);
};
