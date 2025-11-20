import { http } from "@/common/http";

export namespace Role {
  // 角色基本信息
  export interface Info {
    id: number; // 主键
    roleId: string; // 角色 ID
    roleCode: string; // 角色码
    roleName: string; // 角色名
    status: number; // 状态
    orderNum: number; // 显示顺序
    intro: string; // 角色介绍
    appId: string; // 应用 ID
    createTime: string; // 创建时间
    selectedMenuIds: string[]; // 选中的菜单 ID
  }

  // 角色被关联数据，如被用户关联、被用户组关联
  export interface LinkInfo {
    id: number;
    roleId: string; // 角色 ID
    roleName: string; // 角色名
    roleCode: string; // 角色码
    linkId: number; // 关联 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    appId: string; // 应用 ID
    status: number; // 状态
  }

  // 角色关联用户组信息（多个用户组）
  export interface LinkUserGroups {
    userGroupIds: string[]; // 用户组 ID
    roleId: string; // 角色 ID
    appId: string; // 应用 ID
  }

  // 添加用户到角色（多个用户）
  export interface LinkUsers {
    id: number;
    roleId: string[]; // 角色 ID
    userIds: string; // 用户 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    appId: string; // 应用 ID
    expireOnNum?: string; // 过期期限
  }

  // 角色被用户组关联数据（没有生效和过期时间）
  export interface UserGroupLinkRoleVO {
    id: number;
    roleId: string; // 角色 ID
    roleName: string; // 角色名
    roleCode: string; // 角色码
    linkId: number; // 关联 ID
    appId: string; // 应用 ID
    createTime: string; // 创建时间
  }

  // 角色穿梭框数据，如果 disabled 为 true，则禁选
  export interface BindSelect {
    roleId: string; // 角色 ID
    roleName: string; // 角色名
    roleCode: string; // 角色码
    disabled: boolean; // 是否禁用
  }
}

const baseUri = "/system/role";

/**
 * 通过条件查询角色列表
 */
export const list = (params: Partial<Role.Info>) => {
  return http.get<httpNs.Response<Role.Info[]>>(`${baseUri}/list`, params);
};

/**
 * 通过条件查询角色列表（支持分页）
 */
export const listPage = (params: Partial<Role.Info>) => {
  return http.get<httpNs.Page<Role.Info[]>>(`${baseUri}/listPage`, params);
};

/**
 * 新增一个角色
 */
export const addRole = (data: Role.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

/**
 * 修改一个角色
 */
export const editRole = (data: RequiredKeyPartialOther<Role.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

/**
 * 删除一个角色
 */
export const removeRole = (data: Role.Info) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.roleId],
    }
  );
};

/**
 * 通过主键批量删除角色列表
 */
export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: Role.LinkInfo[] }) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${idList.join(",")}`,
    {},
    {
      data: dataList.map(item => item.roleId),
    }
  );
};

/**
 * 角色导出
 */
export const exportExcel = (params: Partial<Role.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
