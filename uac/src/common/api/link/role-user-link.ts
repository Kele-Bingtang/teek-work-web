import type { User } from "../system/user/user";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/roleUserLink";

// ------- 用户关联角色相关 API（以用户为主）-------

/**
 * 查询某个用户所在的角色列表
 */
export const listRoleLinkByUserId = (params: Partial<Role.LinkInfo>) => {
  return http.get<httpNs.Response<Role.LinkInfo[]>>(`${baseUri}/listRoleLinkByUserId/${params.appId}/${params.id}`, {
    ...params,
    appId: undefined,
    id: undefined,
  });
};

/**
 * 查询所有角色列表，如果角色绑定了用户，则 disabled 属性为 true
 */
export const listWithSelectedByUserId = (params: { appId: string; id: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(
    `${baseUri}/listWithSelectedByUserId/${params.appId}/${params.id}`
  );
};

/**
 * 添加角色到用户（多个角色）
 */
export const addRoleListToUser = (data: User.LinkRoles) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRoleListToUser`, data);
};

// ------- 角色关联用户相关 API（以角色为主）-------

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
export const listWithSelectedByRoleId = (params: { roleId: string }) => {
  return http.get<httpNs.Response<User.BindSelect[]>>(`${baseUri}/listWithSelectedByRoleId/${params.roleId}`);
};

/**
 * 添加用户到角色（多个用户）
 */
export const addUserListToRole = (data: Role.LinkUsers) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addUserListToRole`, data);
};

// ------- 公共 API -------

/**
 * 修改用户和角色关联信息
 */
export const editRoleUserLink = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editRoleUserLink`, data);
};

/**
 * 将用户移出角色
 */
export const removeRoleUserLink = (data: Role.LinkInfo & { idList: string[]; dataList: Role.LinkInfo[] }) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleUserLink/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleUserLink/${data.linkId}`);
};
