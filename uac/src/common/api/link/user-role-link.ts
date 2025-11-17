import type { User } from "../system/user/user";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/userRoleLink";

/**
 * 查询某个用户所在的角色列表
 */
export const listRoleLinkByUserId = (params: { appId: string; userId: string }) => {
  return http.get<httpNs.Response<Role.LinkInfo[]>>(`${baseUri}/listRoleLinkByUserId/${params.appId}/${params.userId}`);
};

/**
 * 查询所有角色列表，如果角色绑定了用户，则 disabled 属性为 true
 */
export const listWithDisabledByUserId = (params: { appId: string; userId: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(
    `${baseUri}/listWithDisabledByUserId/${params.appId}/${params.userId}`
  );
};

/**
 * 通过角色 ID 查询用户列表
 */
export const listUserLinkByRoleId = (params: { roleId: string }) => {
  return http.get<httpNs.Response<User.LinkInfo[]>>(`${baseUri}/listUserLinkByRoleId/${params.roleId}`, {
    ...params,
    roleId: undefined,
  });
};

/**
 * 下拉查询用户列表，如果用户绑定了角色，则 disabled 属性为 true
 */
export const listWithDisabledByRoleId = (params: { roleId: string }) => {
  return http.get<httpNs.Response<User.BindSelect[]>>(`${baseUri}/listWithDisabledByRoleId/${params.roleId}`);
};

/**
 * 添加角色到用户（多个角色）
 */
export const addRolesToUser = (data: User.LinkRoles) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRolesToUser`, data);
};

/**
 * 添加用户到角色（多个用户）
 */
export const addUsersToRole = (data: Role.LinkUsers) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addUsersToRole`, data);
};

/**
 * 将用户移出角色
 */
export const removeUsersFromRole = (ids: string[]) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeUsersFromRole/${ids.join(",")}`);
};

/**
 * 修改用户和角色关联信息
 */
export const editUserRoleLinkInfo = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editUserRoleLink`, data);
};
