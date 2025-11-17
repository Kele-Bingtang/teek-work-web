import { http } from "@/common/http";

export namespace DictType {
  export interface Info {
    id: number; // 主键
    dictId: string; // 字典 ID
    dictCode: string; // 字典类型
    dictName: string; // 字典名称
    isCascade: number; // 是否开启级联（0 不开启，1 开启）
    appId: string; // 应用 ID
    [key: string]: any;
  }
}

const baseUri = "/system/dict/type";

export const list = (params: Partial<DictType.Info>) => {
  return http.get<httpNs.Response<DictType.Info[]>>(`${baseUri}/list`, params);
};

export const listPage = (params: Partial<DictType.Info>) => {
  return http.get<httpNs.Page<DictType.Info[]>>(`${baseUri}/listPage`, params);
};

export const addDictType = (data: DictType.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editDictType = (data: DictType.Info) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeDictType = (data: DictType.Info) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${data.id}`);
};

export const removeBatch = ({ idList }: { idList: string[] }) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${idList.join(",")}`);
};

/**
 * 字典类型导出
 */
export const exportExcel = (params: Partial<DictType.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
