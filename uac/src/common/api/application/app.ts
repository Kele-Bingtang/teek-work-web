import { http } from "@/common/http";

export namespace App {
  export interface AppInfo {
    id: number; // 主键
    appId: string; // 应用 ID
    appCode: string; // 应用码
    appName: string; // 应用名
    intro: string; // 应用介绍
    orderNum: number; // 显示顺序
    status: number; // 状态
    createTime: string; // 创建时间
    clientId: string; // 客户端 ID
  }
  export interface AppTree {
    appId: string; // 应用 ID
    appName: string; // 应用名
  }
}

const baseUri = "/system/app";

export const getAppTreeList = () => {
  return http.get<httpNs.Response<App.AppTree[]>>(`${baseUri}/treeList`);
};

export const getOne = (appId: string) => {
  return http.get<httpNs.Response<App.AppInfo>>(`${baseUri}/getOne/${appId}`);
};

export const listApp = (params?: Partial<App.AppInfo>) => {
  return http.get<httpNs.Response<App.AppInfo[]>>(`${baseUri}/list`, params);
};

export const listAppPage = (params?: Partial<App.AppInfo>) => {
  return http.get<httpNs.Page<App.AppInfo[]>>(`${baseUri}/listPage`, params);
};

export const addApp = (data: App.AppInfo) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editApp = (data: RequiredKeyPartialOther<App.AppInfo, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeApp = (data: App.AppInfo) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.appId],
    }
  );
};

export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: App.AppInfo[] }) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${idList.join(",")}`,
    {},
    {
      data: dataList.map(item => item.appId),
    }
  );
};

/**
 * 应用导出
 */
export const exportExcel = (params: Partial<App.AppInfo>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
