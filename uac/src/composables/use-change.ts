import { ElMessageBox } from "element-plus";
import { useDictStore } from "@/pinia";
import { message } from "@teek/utils";
import { findItemNested } from "@teek/components/pro/helper";

export const useChange = <T extends Recordable = Recordable>(
  name: string,
  desc: string,
  editApi: (params: any, status: number) => Promise<httpNs.Response<boolean>>,
  listApi: () => Promise<any> | undefined
) => {
  const statusChange = async (value: string | number | boolean, row: T) => {
    const statusEnum = await useDictStore().getDictData("sys_normal_status");
    const filterData = findItemNested(value + "", statusEnum.data, "dictValue", "");

    if (!filterData?.dictLabel) {
      row.status = 1;
      message.warning("不存在状态");
      return;
    }

    ElMessageBox.confirm(
      `确认要 <span style="color: teal">${filterData?.dictLabel}</span> 【${row[name]}】${desc}吗`,
      "系统提示",
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    )
      .then(() => {
        editApi(row, value as number)
          .then(res => {
            if (res.status === "success") message.success("修改成功") && nextTick(() => listApi && listApi());
            else {
              statusRecover(value as number, row);
            }
          })
          .catch(() => statusRecover(value as number, row));
      })
      .catch(() => statusRecover(value as number, row));
  };

  const statusRecover = (value: number, row: any) => {
    if (value === 0) return (row.status = 1);
    if (value === 1) return (row.status = 0);
  };

  return {
    statusChange,
  };
};
