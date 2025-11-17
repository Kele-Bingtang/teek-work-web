import type { Dept } from "../system/dept";
import { http } from "@/common/http";

// 添加部门到角色（多个部门）
export interface RoleLinkDept {
  roleId: string; // 角色 ID
  appId: string; // 应用 ID
  selectedDeptIds: string[]; // 部门 ID
}

const baseUri = "/system/roleDeptLink";

/**
 * 通过角色 ID 查询部门列表（树形结构
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
export const addDeptsToRole = (data: RoleLinkDept) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addDeptsToRole`, data);
};
