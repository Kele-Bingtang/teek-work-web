import { http } from "@/common/http";

export namespace User {
  // 用户基本信息
  export interface Info {
    id: number; // 主键
    userId: string; // 用户 ID
    username: string; // 用户名
    nickname: string; // 用户昵称
    password: string; // 密码
    email: string; // 邮箱
    sex: number; // 性别（0 保密 1 男 2 女）
    birthday: string; // 生日
    phone: string; // 手机号码
    userStatus: number; // 状态（0 离线 1 在线）
    avatar: string; // 头像
    registerTime: string; //  注册时间
    loginIp: string; // 最后登录 IP
    loginTime: string; // 最后登录时间
    deptId: string; // 部门 ID
    status: number; // 状态
  }

  // 用户被关联信息，如被用户组关联、被角色关联
  export interface LinkInfo {
    userId: string; // 用户 ID
    username: string; // 用户名
    nickname: string; // 用户昵称
    linkId: number; // 关联 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    appId: string; // 应用 ID
    createTime: string; // 创建时间
  }

  // 用户关联角色信息（多个角色）
  export interface LinkRoles {
    userId: string; // 用户 ID
    roleIds: string[]; // 角色 ID
    validFrom: string; // 负责人 ID
    expireOn: string; // 负责人 username
    appId: string; // 应用 ID
  }

  // 用户关联用户组信息（多个用户组）
  export interface LinkUserGroup {
    linkId: number; // 关联 ID
    userId: string; // 用户 ID
    userGroupIds: string[]; // 用户组 ID
    validFrom: string; // 负责人 ID
    expireOn: string; // 负责人 username
    appId: string; // 应用 ID
  }

  // 用户穿梭框数据，如果 disabled 为 true，则禁选
  export interface BindSelect {
    userId: string; // 用户 ID
    username: string; // 用户名
    nickname: string; // 用户昵称
    disabled: boolean; // 是否禁用
  }
}

const baseUri = "/system/user";

export const list = (params?: Partial<User.Info>) => {
  return http.get<httpNs.Response<User.Info[]>>(`${baseUri}/list`, params);
};

export const listPage = (params: Partial<User.Info>) => {
  return http.get<httpNs.Page<User.Info[]>>(`${baseUri}/listPage`, params);
};

export const addUser = (data: User.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editUser = (data: RequiredKeyPartialOther<User.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeUser = (data: User.Info) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.userId],
    }
  );
};

export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: User.Info[] }) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${idList.join(",")}`,
    {},
    {
      data: dataList.map(item => item.userId),
    }
  );
};

/**
 * 用户导出
 */
export const exportExcel = (params: Partial<User.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
