import type { FormProps, FormRules } from "element-plus";
import type { DialogFormColumn } from "@teek/components";
import type { DictType } from "@/common/api/system/dict-type";
import { listDataTreeList, type DictData } from "@/common/api/system/dict-data";
import { useDictStore } from "@/pinia";
import { baseOptions } from "@teek/config";

const dictTypeRules = reactive<FormRules>({
  appId: [{ required: true, message: "请选择 App", trigger: "blur" }],
  dictCode: [{ required: true, message: "请输入字典编码", trigger: "blur" }],
  dictName: [{ required: true, message: "请输入字典名称", trigger: "blur" }],
});

const dictDataRules = reactive<FormRules>({
  dictValue: [{ required: true, message: "请输入字典键值", trigger: "blur" }],
  dictLabel: [{ required: true, message: "请输入字典标签", trigger: "blur" }],
});

export const dictTypeElFormProps: Partial<FormProps> = {
  labelPosition: "top",
  labelWidth: 80,
  rules: dictTypeRules,
};

export const dictDataElFormProps = {
  labelWidth: 100,
  rules: dictDataRules,
};

export const useFormColumns = (defaultValue: ComputedRef<string>, isCascade?: ComputedRef<number>) => {
  const { getDictData } = useDictStore();

  const dictTypeColumns: DialogFormColumn<DictType.Info>[] = [
    {
      prop: "dictCode",
      label: "字典编码",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 字典编码" },
    },
    {
      prop: "dictName",
      label: "字典名称",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 字典名称" },
    },
    {
      prop: "isCascade",
      label: "开启级联",
      el: "el-radio-group",
      options: baseOptions,
      defaultValue: 0,
      elProps: { clearable: true, placeholder: "请选择 是否开启级联" },
    },
    {
      prop: "intro",
      label: "描述",
      el: "el-input",
      elProps: { type: "textarea", clearable: true, placeholder: "请输入 描述" },
    },
  ];

  const dictDataColumns: DialogFormColumn<DictData.Info>[] = [
    {
      prop: "parentId",
      label: "上级字典",
      el: "el-tree-select",
      elProps: {
        placeholder: "请选择 上级字典",
        filterable: true,
        valueKey: "id",
        showCheckbox: true,
        checkStrictly: true,
      },
      options: () => listDataTreeList({ dictCode: defaultValue.value }),
      hidden: model => model.parentId === "0",
      destroy: () => !isCascade?.value,
    },
    {
      prop: "dictCode",
      label: "字典编码",
      el: "el-input",
      defaultValue: defaultValue,
      elProps: { disabled: true },
    },
    {
      prop: "dictValue",
      label: "字典键值",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 字典键值" },
    },
    {
      prop: "dictLabel",
      label: "字典标签",
      el: "el-input",
      elProps: { clearable: true, placeholder: "请输入 字典标签" },
    },
    {
      prop: "dictSort",
      label: "字典排序",
      el: "el-input-number",
      defaultValue: 1,
    },
    {
      prop: "tagEl",
      label: "tag 标签",
      el: "el-select",
      options: () => getDictData("sys_dict_tag_el"),
      optionField: { value: "dictValue", label: "dictLabel" },
      elProps: { placeholder: "请选择 tag 标签" },
    },
    {
      prop: "tagType",
      label: "tag 类型",
      el: "el-select",
      destroy: model => !model.tagEl,
      options: () => getDictData("sys_dict_tag_type"),
      optionField: { value: "dictValue", label: "dictLabel" },
      elProps: { placeholder: "请选择 tag 类型" },
    },
    {
      prop: "tagEffect",
      label: "tag 主题",
      el: "el-select",
      destroy: model => model.tagEl !== "el-tag",
      options: () => getDictData("sys_dict_tag_effect"),
      optionField: { value: "dictValue", label: "dictLabel" },
      elProps: { placeholder: "请选择 tag 主题" },
    },
    {
      prop: "tagAttributes",
      label: "tag 额外属性",
      el: "el-input",
      hidden: model => model.tagEl !== "el-tag",
      elProps: { clearable: true, placeholder: "请输入 tag 额外属性" },
    },
    {
      prop: "isDefault",
      label: "是否默认选中",
      el: "el-select",
      defaultValue: "N",
      options: [
        { value: "Y", label: "选中" },
        { value: "N", label: "不选中" },
      ],
      elProps: { clearable: true, placeholder: "请选择 是否默认选中" },
    },
  ];

  return {
    dictTypeColumns,
    dictDataColumns,
  };
};
