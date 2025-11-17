import { http } from "@/common/http";

export namespace Dept {
  export interface Info {
    id: number;
    deptId: string; // 部门 ID
    parentId: string; // 父级部门 ID
    parentName: string; // 父级部门名字
    ancestors: string; // 祖级列表
    deptName: string; // 部门名
    icon: string; // 部门图标
    orderNum: number; // 部门显示顺序
    userCount: string; // 部门用户数量
    leader: string; // 部门负责人
    phone: string; // 负责电话
    email: string; // 邮箱
    intro: string; // 部门介绍
    level: string; // 部门层级
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

const baseUri = "/system/dept";

export const list = (params?: Partial<Dept.Info>) => {
  return http.get<httpNs.Response<Dept.Info[]>>(`${baseUri}/list`, params);
};

export const listPage = (params?: Partial<Dept.Info>) => {
  return http.get<httpNs.Page<Dept.Info[]>>(`${baseUri}/listPage`, params);
};

/**
 * 查询部门树列表
 */
export const listDeptTreeList = () => {
  return http.get<httpNs.Response<Dept.TreeList[]>>(`${baseUri}/treeList`);
};

/**
 * 查询部门树表格
 */
export const listDeptTreeTable = () => {
  return http.get<httpNs.Response<Dept.TreeTable[]>>(`${baseUri}/treeTable`);
};

export const addDept = (data: Dept.Info) => {
  return http.post<httpNs.Response<boolean>>(baseUri, data);
};

export const editDept = (data: RequiredKeyPartialOther<Dept.Info, "id">) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const removeDept = (data: Dept.Info) => {
  return http.delete<httpNs.Response<boolean>>(`${baseUri}/${data.id}/${data.deptId}`);
};

/**
 * 部门导出
 */
export const exportExcel = (params: Partial<Dept.Info>) => {
  return http.post<any>(`${baseUri}/export`, params, { responseType: "blob" });
};
