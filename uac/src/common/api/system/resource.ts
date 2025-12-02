import { http } from "@/common/http";

export namespace Resource {
  export interface Info {
    id: number; // id
    resourceId: string; // 资源 ID
    resourceCode: string; // 资源编码
    resourceName: string; // 资源名
    parentId: string; // 父级资源 ID
    pathPrefix: string; // 资源地址前缀
    path: string; // 资源地址
    icon: string; // 图标
    orderNum: number; // 显示顺序
    permission: string; // 资源权限
    resourceType: string; // 资源类型（C目录 M资源 F按钮）
    component: string; // 组件路径
    meta: MetaProps; // 资源前端额外配置
    intro: string; // 资源介绍
    appId: string; // 应用 ID
    status: number; // 状态
    createTime: string; // 创建时间
    children: Info[]; // 子数据
  }

  export interface TreeList {
    id: string;
    label: string;
    parentId: string;
    weight: number;
    icon: string;
    children: TreeList[];
    value: string;
  }

  export interface TreeTable extends Info {
    children: TreeTable[];
  }
}

const baseUri = "/system/resource";

export const listRoutes = (appId: string) => {
  return http.get<httpNs.Response<RouterConfigRaw[]>>(`${baseUri}/listRoutes/${appId}`);
};

export const listResourceTreeTableByApp = (params: { appId: string }) => {
  return http.get<httpNs.Response<Resource.Info[]>>(`${baseUri}/treeTable`, params);
};

export const listResourceTreeSelectByApp = (params: { appId: string }) => {
  return http.get<httpNs.Response<Resource.Info[]>>(`${baseUri}/treeSelect`, params);
};

export const addResource = (data: Resource.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editResource = (data: RequiredKeyPartialOther<Resource.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeResource = (data: Resource.Info) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${data.id}/${data.resourceId}`);
};

/**
 * 资源导出
 */
export const exportExcel = (params: Partial<Resource.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
