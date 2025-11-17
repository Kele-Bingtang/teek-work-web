import { http } from "@/common/http";

export namespace LoginLog {
  export interface Info {
    loginId: string; // 登录 ID
    userName: string; // 用户账号
    clientName: string; // 客户端名
    loginIp: string; // 登录 IP 地址
    loginLocation: string; // 登录地点
    browser: string; // 浏览器类型
    os: string; // 操作系统
    msg: string; // 提示消息
    status: number; // 状态（0 异常 1 正常 ）
    loginTime: string; // 登录时间
  }
}

const baseUri = "/monitor/loginLog";

export const listPage = (params: Partial<LoginLog.Info>) => {
  return http.get<httpNs.Response<LoginLog.Info[]>>(`${baseUri}/listPage`, params);
};

export const removeBatch = (idList: string[]) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${idList.join(",")}`);
};

export const cleanAllLog = () => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/clean`);
};

/**
 * 登录日志导出
 */
export const exportExcel = (params: Partial<LoginLog.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
