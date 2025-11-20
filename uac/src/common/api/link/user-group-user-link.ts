import type { User } from "../system/user/user";
import type { UserGroup } from "../system/user/user-group";
import { http } from "@/common/http";

const baseUri = "/system/userGroupUserLink";

/**
 * 通过用户组 ID 查询用户列表
 */
export const listUserLinkByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Page<User.LinkInfo[]>>(`${baseUri}/listUserLinkByGroupId/${params.userGroupId}`, params);
};

/**
 * 查询某个用户所在的用户组列表
 */
export const listUserGroupByUserId = (params: { userId: string }) => {
  return http.get<httpNs.Response<UserGroup.LinkInfo[]>>(`${baseUri}/listUserGroupByUserId/${params.userId}`, params);
};

/**
 * 下拉查询用户列表，如果用户绑定了用户组，则 disabled 属性为 true
 */
export const listWithSelectedByGroupId = (params: { userGroupId: string }) => {
  return http.get<httpNs.Response<User.BindSelect[]>>(`${baseUri}/listWithSelectedByGroupId/${params.userGroupId}`);
};

/**
 * 查询所有用户组列表，如果用户组存在用户，则 disabled 属性为 true
 */
export const listWithSelectedByUserId = (params: { userId: string }) => {
  return http.get<httpNs.Response<UserGroup.BindSelect[]>>(`${baseUri}/listWithSelectedByUserId/${params.userId}`);
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
export const addUsersToGroup = (data: UserGroup.LinkInfo) => {
  return http.post<httpNs.Response<string>>(`${baseUri}/addUsersToGroup`, data);
};

/**
 * 将用户移出项目组
 */
export const removeUserFromUserGroup = (
  data: UserGroup.LinkInfo & { idList: string[]; dataList: UserGroup.LinkInfo[] }
) => {
  // 批量删除
  if (data.idList) {
    return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeUserFromUserGroup/${data.idList.join(",")}`);
  }

  // 单行删除
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/removeUserFromUserGroup/${data.linkId}`);
};

/**
 * 修改用户组和用户的关联信息
 */
export const editUserGroupUserLink = (data: RequiredKeyPartialOther<UserGroup.LinkInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/editUserGroupUserLink`, data);
};
