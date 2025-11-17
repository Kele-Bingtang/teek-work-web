<script setup lang="ts">
import type { Component, ComponentPublicInstance } from "vue";
import type { TableInstance } from "element-plus";
import type { TableColumnCtx } from "element-plus/es/components/table/src/table-column/defaults";
import { ref, shallowRef, onBeforeMount, computed } from "vue";
import { ElMessage } from "element-plus";
import { Close } from "@element-plus/icons-vue";
import { useNamespace } from "@teek/composables";
import TransferSelect from "./index.vue";

defineOptions({ name: "TransferSelect" });

const ns = useNamespace("transfer-select");

export type TransferSelectInstance = Omit<
  InstanceType<typeof TransferSelect>,
  keyof ComponentPublicInstance | keyof TransferSelectProps
>;
type CommonObjType = Record<PropertyKey, any>;

export type TransferTableColumn<T extends CommonObjType = CommonObjType> = Partial<TableColumnCtx<T>>;

export interface TransferSelectProps<T extends CommonObjType = CommonObjType> {
  modelValue?: string | string[] | any; // v-model
  columns: TransferTableColumn<T>[]; // 表格列配置项
  id: string; // 一行的唯一标识，添加和移出选择项时用到
  data?: CommonObjType[] | any; // 表格数据
  requestApi?: (data?: any) => Promise<any>; // 请求数据的 api ==> 非必传
  requestParams?: CommonObjType; // 请求携带的参数
  multiple?: boolean; // 是否多选
  listIcon?: Component; // 选择内容的前缀
  closeIcon?: Component; // 移除内容的 Icon
}

// 接受父组件参数，配置默认值
const props = withDefaults(defineProps<TransferSelectProps>(), { multiple: true, closeIcon: Close });

type EmitProps = {
  (e: "update:modelValue", value: string | string[]): void; // 选择的 ids
  (e: "update:rows", value: CommonObjType | CommonObjType[]): void; // 选择的行数据
};

const emits = defineEmits<EmitProps>();
const tableData = ref([]);
const elTableRef = shallowRef<TableInstance>();

onBeforeMount(async () => {
  // 有数据就直接赋值，没有数据就执行请求函数
  if (props.data?.length) {
    tableData.value = props.data;
    return;
  }

  getDataList();
});

/**
 * @description 初始化选中项（可传入符合条件的数据，会自动激活选中）
 * 计算出来的 selectedList 一定是一个数组，如果 props 传入一个对象，也会变成 [对象]（转成数组）
 */
const selectedList = computed({
  get() {
    const { multiple, id, modelValue } = props;

    if (multiple && Array.isArray(modelValue)) {
      const selectedData: CommonObjType[] = [];
      tableData.value?.forEach(item => {
        if (modelValue.includes(item[id ?? props.columns[0]?.prop!])) {
          elTableRef.value?.toggleRowSelection(item, true);
          selectedData.push(item);
        } else elTableRef.value?.toggleRowSelection(item, false);
      });

      return selectedData || [];
    }

    let selectedData: CommonObjType[] = [];

    if (!multiple && Array.isArray(modelValue)) {
      selectedData = tableData.value?.filter(item => modelValue.includes(item[id ?? props.columns[0]?.prop!]));
    } else {
      selectedData = tableData.value?.filter(item => modelValue === item[id ?? props.columns[0]?.prop!]);
    }

    selectedData[0] && elTableRef.value?.toggleRowSelection(selectedData[0], true);
    return selectedData[0] ? [selectedData[0]] : [];
  },
  set(value) {
    const { multiple, id } = props;
    const selectedIds: string[] = [];
    value.forEach(item => item && selectedIds.push(item[id ?? props.columns[0]?.prop!]));
    emits("update:modelValue", multiple ? selectedIds : value?.length ? value[0][id ?? props.columns[0]?.prop!] : "");
    emits("update:rows", value);
  },
});

/**
 * @description 初始化数据
 */
const getDataList = async () => {
  if (props.requestApi) {
    const { data } = await props.requestApi({ ...props.requestParams });
    tableData.value = data;
  }
};

/**
 * @description 表格行点击事件
 */
const handleRowClick = (row: CommonObjType) => {
  if (row.disabled) return ElMessage.warning("该数据已被选中过，不允许再次选择");
  !props.multiple && (selectedList.value = [row]);
};

/**
 * @description 多选模式下，手动选中项发生变化时触发
 */
const handleSelect = (selected: CommonObjType[]) => {
  selectedList.value = selected;
};

/**
 * @description 取消选中回调
 */
const handleCancelSelect = (row: CommonObjType) => {
  selectedList.value = selectedList.value?.filter(
    (item: any) => item[props.id ?? props.columns[0].prop!] !== row[props.id ?? props.columns[0]?.prop!]
  );

  props.multiple && elTableRef.value?.toggleRowSelection(row, false);
};

/**
 * @description 清除按钮回调
 */
const handleClearSelect = () => {
  selectedList.value = [];
  elTableRef.value?.clearSelection();
};

defineExpose({ tableElement: elTableRef.value, getDataList });
</script>

<template>
  <div :class="ns.b()">
    <div :class="ns.e('left')">
      <el-table
        ref="elTableRef"
        :data="tableData"
        border
        show-overflow-tooltip
        @select="handleSelect"
        @row-click="handleRowClick"
      >
        <el-table-column v-if="multiple" type="selection" width="40" :selectable="(row: any) => !row.disabled" />
        <template v-for="item in columns" :key="item">
          <el-table-column :prop="item.prop" :label="item.label" v-bind="$attrs" />
        </template>
      </el-table>
    </div>

    <div :class="ns.e('right')">
      <div class="flx-justify-between">
        <div :class="ns.em('right', 'select')">
          选择数：
          <span :class="ns.e('select-num')">{{ selectedList?.length || "" }}</span>
        </div>
        <el-button link @click="handleClearSelect">清空</el-button>
      </div>

      <div :class="ns.em('right', 'selected')">
        <template v-if="selectedList?.length">
          <template v-for="item in selectedList" :key="item">
            <slot name="list" v-bind="item">
              <div :class="ns.e('item')">
                <slot>
                  <el-icon><component :is="listIcon" /></el-icon>
                </slot>
                <span :class="ns.e('item--name')">
                  <slot name="name" v-bind="item">{{ item[columns[0].prop!] }}</slot>
                </span>
                <el-button :icon="closeIcon" link @click="handleCancelSelect(item)"></el-button>
              </div>
            </slot>
          </template>
        </template>

        <el-empty v-else />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;

@include b(transfer-select) {
  display: flex;
  flex: 1;
  align-items: center;
  width: 100%;

  @include e(left) {
    display: flex;
    flex-direction: column;
    width: 50%;
    height: 450px;
    padding-right: 16px;
    border-right: 1px solid #dfdfdf;
  }

  @include e(right) {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    width: 50%;
    height: 450px;
    padding-left: 16px;

    @include m(select) {
      margin-bottom: 4px;
      line-height: 24px;
      color: #676767;
    }

    @include m(selected) {
      overflow: auto;
    }
  }

  @include e(select-num) {
    font-weight: 700;
  }

  @include e(item) {
    display: flex;
    align-items: center;
    height: 28px;

    @include m(name) {
      display: inline-block;
      flex: 1;
      margin-left: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      font-size: 12px;
      line-height: 22px;
      white-space: nowrap;
    }
  }
}
</style>
