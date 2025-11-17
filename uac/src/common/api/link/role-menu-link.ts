import type { Menu } from "../system/menu";
import { http } from "@/common/http";

// 添加部门到角色（多个部门）
export interface RoleLinkMenu {
  roleId: string; // 角色 ID
  appId: string; // 应用 ID
  selectedMenuIds: string[]; // 部门 ID
}

const baseUri = "/system/roleMenuLink";

export const listMenuListByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<Menu.TreeList[]>>(`${baseUri}/listMenuListByRoleId/${appId}/${roleId}`);
};

export const listMenuIdsByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<string[]>>(`${baseUri}/listMenuIdsByRoleId/${appId}/${roleId}`);
};

/**
 * 添加菜单到角色（多个菜单）
 */
export const addMenusToRole = (data: RoleLinkMenu) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addMenusToRole`, data);
};
