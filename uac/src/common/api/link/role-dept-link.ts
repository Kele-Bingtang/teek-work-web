import type { Dept } from "../system/dept";
import type { Role } from "../system/role";
import { http } from "@/common/http";

// 添加部门到角色（多个部门）
export interface RoleLinkDept {
  roleId: string; // 角色 ID
  appId: string; // 应用 ID
  deptIds: string[]; // 部门 ID
}

const baseUri = "/system/roleDeptLink";

// ------- 部门关联角色相关 API（以部门为主）-------

/**
 * 通过部门 ID 查询角色列表
 */
export const listRoleLinkByDeptId = (params: { appId: string; deptId: string }) => {
  return http.get<httpNs.Response<Role.LinkInfo[]>>(
    `${baseUri}/listRoleLinkByDeptId/${params.appId}/${params.deptId}`,
    { ...params, appId: undefined, deptId: undefined }
  );
};

/**
 * 查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true
 */
export const listWithSelectedByDeptId = (params: { deptId: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(`${baseUri}/listWithSelectedByDeptId/${params.deptId}`);
};

/**
 * 添加角色到部门（多个角色）
 */
export const addRoleListToDept = (data: RoleLinkDept) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRoleListToDept`, data);
};

// ------- 角色关联部门相关 API（以角色为主） -------

/**
 * 通过角色 ID 查询部门列表（树形结构）
 */
export const listDeptListByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<Dept.TreeList[]>>(`${baseUri}/listDeptListByRoleId/${appId}/${roleId}`);
};

/**
 * 通过角色 ID 查询部门 ID 列表
 */
export const listDeptIdsByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<string[]>>(`${baseUri}/listDeptIdsByRoleId/${appId}/${roleId}`);
};

/**
 * 添加部门到角色（多个部门）
 */
export const addDeptListToRole = (data: RoleLinkDept) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addDeptListToRole`, data);
};

// ------- 公共 API -------

/**
 * 修改用户组和用户的关联信息
 */
export const editRoleDeptLink = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editRoleDeptLink`, data);
};

/**
 * 将用户组移出角色
 */
export const removeDeptRoleLink = (data: Role.LinkInfo & { idList: string[]; dataList: Role.LinkInfo[] }) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeDeptRoleLink/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeDeptRoleLink/${data.linkId}`);
};
