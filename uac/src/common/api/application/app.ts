import { http } from "@/common/http";

export namespace App {
  export interface Info {
    id: number; // 主键
    appId: string; // 应用 ID
    appCode: string; // 应用码
    appName: string; // 应用名
    appType: string; // 应用类型
    ownerId: string; // 负责人 ID
    ownerName: string; // 负责人 username
    intro: string; // 应用介绍
    orderNum: number; // 显示顺序
    status: number; // 状态
    updateBy: string; // 更新人
    updateById: string; // 更新人 id
    updateTime: string; // 更新时间
    clientId: string; // 客户端 ID
    user?: string;
  }
  export interface TreeList {
    appId: string; // 应用 ID
    appName: string; // 应用名
  }
}

const baseUri = "/system/app";

export const getAppTreeList = () => {
  return http.get<httpNs.Response<App.TreeList[]>>(`${baseUri}/treeList`);
};

export const getOne = (appId: string) => {
  return http.get<httpNs.Response<App.Info>>(`${baseUri}/getOne/${appId}`);
};

export const listApp = (params?: Partial<App.Info>) => {
  return http.get<httpNs.Response<App.Info[]>>(`${baseUri}/list`, params);
};

export const listAppPage = (params?: Partial<App.Info>) => {
  return http.get<httpNs.Page<App.Info[]>>(`${baseUri}/listPage`, params);
};

export const addApp = (data: App.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editApp = (data: RequiredKeyPartialOther<App.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeApp = (data: App.Info) => {
  return http.delete<httpNs.Response<boolean>>(
    `${baseUri}/${data.id}`,
    {},
    {
      data: [data.appId],
    }
  );
};

export const removeBatch = ({ idList, dataList }: { idList: string[]; dataList: App.Info[] }) => {
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
export const exportExcel = (params: Partial<App.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
