import type { Resource } from "../system/resource";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/roleResourceLink";

export const listResourceListByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<Resource.TreeList[]>>(`${baseUri}/listResourceListByRoleId/${appId}/${roleId}`);
};

export const listResourceIdsByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Response<string[]>>(`${baseUri}/listResourceIdsByRoleId/${appId}/${roleId}`);
};

/**
 * 添加资源到角色（多个资源）
 */
export const addResourceToRole = (data: Role.LinkResources) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addResourceToRole`, data);
};

/**
 * 将用户组移出角色
 */
export const removeRoleResourceLink = (data: Role.LinkInfo & { idList: string[]; dataList: Role.LinkInfo[] }) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleResourceLink/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRoleResourceLink/${data.linkId}`);
};
