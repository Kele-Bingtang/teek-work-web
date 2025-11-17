import { http } from "@/common/http";

export namespace Cache {
  export interface Info {
    info: any; // 缓存统计信息
    dbSize: number; // 缓存中可用键 Key 的总数
    commandStats: any; // 缓存完整信息
  }
}

const baseUri = "/monitor/cache";

export const list = () => {
  return http.get<httpNs.Response<Cache.Info>>(`${baseUri}`);
};
