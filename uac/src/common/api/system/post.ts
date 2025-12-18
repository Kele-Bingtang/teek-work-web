import { http } from "@/common/http";

export namespace Post {
  export interface Info {
    id: number; // 主键
    postId: string; // 岗位 ID
    postCode: string; // 岗位编码
    postName: string; // 岗位名称
    orderNum: number; // 显示顺序
    intro: string; // 岗位介绍
    status: number; // 状态
    createTime: string; // 创建时间
  }

  // 与角色关联信息
  export interface LinkInfo {
    postId: string; // 岗位 ID
    postCode: string; // 岗位编码
    postName: string; // 岗位名称
    linkId: number; // 关联 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    status: number; // 状态
    createTime: string; // 创建时间
  }

  // 添加资源到角色（多个岗位）
  export interface LinkRoles {
    postId: string; // 岗位 ID
    roleIds: string[]; // 角色 ID
    validFrom: string; // 生效时间
    expireOn: string; // 过期时间
    appId: string; // 应用 ID
  }

  export interface UserSelectPost {
    postIds: string[];
    postList: Info[];
  }
}

const baseUri = "/system/post";

export const list = (params?: Partial<Post.Info>) => {
  return http.get<httpNs.Response<Post.Info[]>>(`${baseUri}/list`, params);
};

export const listPage = (params?: Partial<Post.Info>) => {
  return http.get<httpNs.Page<Post.Info[]>>(`${baseUri}/listPage`, params);
};

/**
 * 查询岗位列表和已选择的岗位列表
 */
export const userSelectPostList = (userId: string) => {
  return http.get<httpNs.Response<Post.UserSelectPost>>(`${baseUri}/userSelectPostList/${userId}`);
};

export const addPost = (data: Post.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editPost = (data: RequiredKeyPartialOther<Post.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removePost = (data: Post.Info) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.postId],
    }
  );
};

export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: Post.Info[] }) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${idList.join(",")}`,
    {},
    {
      data: dataList.map(item => item.postId),
    }
  );
};

/**
 * 岗位导出
 */
export const exportExcel = (params: Partial<Post.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
