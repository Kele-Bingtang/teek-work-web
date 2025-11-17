import { http } from "@/common/http";

export namespace Menu {
  export interface Info {
    id: number; // id
    menuId: string; // 菜单 ID
    menuCode: string; // 菜单编码
    menuName: string; // 菜单名
    parentId: string; // 父级菜单 ID
    pathPrefix: string; // 菜单地址前缀
    path: string; // 菜单地址
    icon: string; // 图标
    orderNum: number; // 显示顺序
    permission: string; // 菜单权限
    menuType: string; // 菜单类型（C目录 M菜单 F按钮）
    component: string; // 组件路径
    meta: MetaProps; // 菜单前端额外配置
    intro: string; // 菜单介绍
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

const baseUri = "/system/menu";

export const listRoutes = (appId: string) => {
  return http.get<httpNs.Response<RouterConfigRaw[]>>(`${baseUri}/listRoutes/${appId}`);
};

export const listMenuTreeTableByApp = (params: { appId: string }) => {
  return http.get<httpNs.Response<Menu.Info[]>>(`${baseUri}/treeTable`, params);
};

export const listMenuTreeSelectByApp = (params: { appId: string }) => {
  return http.get<httpNs.Response<Menu.Info[]>>(`${baseUri}/treeSelect`, params);
};

export const addMenu = (data: Menu.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editMenu = (data: RequiredKeyPartialOther<Menu.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeMenu = (data: Menu.Info) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${data.id}/${data.menuId}`);
};

/**
 * 菜单导出
 */
export const exportExcel = (params: Partial<Menu.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
