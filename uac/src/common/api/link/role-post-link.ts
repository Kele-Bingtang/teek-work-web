import type { Post } from "../system/post";
import type { Role } from "../system/role";
import { http } from "@/common/http";

const baseUri = "/system/rolePostLink";

// ------- 岗位关联角色相关 API（以岗位为主）-------

/**
 * 通过岗位 ID 查询角色列表
 */
export const listRoleLinkByPostId = (params: Partial<Role.LinkInfo & { postId: string }>) => {
  return http.get<httpNs.Page<Role.LinkInfo[]>>(`${baseUri}/listRoleLinkByPostId/${params.appId}/${params.postId}`, {
    ...params,
    appId: undefined,
    id: undefined,
  });
};

/**
 * 查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true
 */
export const listWithSelectedByPostId = (params: { appId: string; postId: string }) => {
  return http.get<httpNs.Response<Role.BindSelect[]>>(
    `${baseUri}/listWithSelectedByPostId/${params.appId}/${params.postId}`
  );
};

/**
 * 添加角色到岗位（多个角色）
 */
export const addRoleListToPost = (data: Post.LinkRoles) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addRoleListToPost`, data);
};

// ------- 角色关联岗位相关 API（以角色为主） -------

/**
 * 通过角色 ID 查询岗位列表（树形结构）
 */
export const listPostListByRoleId = (appId: string, roleId: string) => {
  return http.get<httpNs.Page<Post.LinkInfo[]>>(`${baseUri}/listPostListByRoleId/${appId}/${roleId}`);
};

/**
 * 添加岗位到角色（多个岗位）
 */
export const addPostListToRole = (data: Role.LinkPosts) => {
  return http.post<httpNs.Response<boolean>>(`${baseUri}/addPostListToRole`, data);
};

// ------- 公共 API -------

/**
 * 修改用户组和用户的关联信息
 */
export const editRolePostLink = (data: RequiredKeyPartialOther<Role.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editRolePostLink`, data);
};

/**
 * 将用户组移出角色
 */
export const removeRolePostLink = (data: Role.LinkInfo & { idList: string[]; dataList: Role.LinkInfo[] }) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRolePostLink/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeRolePostLink/${data.linkId}`);
};
