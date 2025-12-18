import { http } from "@/common/http";

export namespace UserGroup {
  // 用户组基本信息
  export interface Info {
    id: number; // 主键
    groupId: string; // 用户组 ID
    groupName: string; // 用户组名
    groupType: string; // 用户组类型
    ownerId: string; // 负责人 ID
    ownerName: string; // 负责人 username
    intro: string; // 用户组描述
    status: number; // 状态
    createTime: string; // 创建时间
  }

  // 与用户关联信息
  export interface LinkInfo {
    id: number;
    groupId: string; // 用户组 ID
    groupName: string; // 用户组名
    linkId: number; // 关联 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    ownerName: string; // 负责人 nickname
    ownerId: string; // 负责人 username
    status: number; // 状态
    createTime: string; // 创建时间
  }

  // 用户组关联用户信息（多个用户）
  export interface LinkUsers {
    userIds: string[]; // 用户 ID
    userGroupId: string; // 用户组 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
  }

  // 用户组关联角色信息（多个角色）
  export interface LinkRoles {
    roleIds: string[]; // 角色 ID
    userGroupId: string; // 用户组 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    appId: string; // 应用 ID
  }

  // 用户组穿梭框数据，如果 disabled 为 true，则禁选
  export interface BindSelect {
    groupId: string;
    groupName: string;
    disabled: boolean;
  }

  export interface TreeList {
    groupId: string;
    groupName: string;
  }
}

const baseUri = "/system/userGroup";

export const list = (params: Partial<UserGroup.Info>) => {
  return http.get<httpNs.Response<UserGroup.Info[]>>(`${baseUri}/list`, params);
};

export const listPage = (params: Partial<UserGroup.Info>) => {
  return http.get<httpNs.Page<UserGroup.Info[]>>(`${baseUri}/listPage`, params);
};

export const getAppTreeList = () => {
  return http.get<httpNs.Page<UserGroup.TreeList[]>>(`${baseUri}/treeList`);
};

export const addUserGroup = (data: UserGroup.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editUserGroup = (data: RequiredKeyPartialOther<UserGroup.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeUserGroup = (data: UserGroup.Info) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.groupId],
    }
  );
};

export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: UserGroup.Info[] }) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${idList.join(",")}`,
    {},
    {
      data: dataList.map(item => item.groupId),
    }
  );
};

/**
 * 用户组导出
 */
export const exportExcel = (params: Partial<UserGroup.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
