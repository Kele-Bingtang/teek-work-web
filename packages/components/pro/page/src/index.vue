<script setup lang="ts">
import type { UnwrapRef } from "vue";
import type { TableInstance } from "element-plus";
import type { SearchColumn, ProSearchInstance } from "@teek/components/pro/search";
import type {
  ProTableInstance,
  OperationNamespace,
  SizeStyle,
  TableColumn,
  TableRow,
  TableScope,
  TableSizeEnum,
  UseSelectState,
  PageInfo,
} from "@teek/components/pro/table";
import type { ProPageEmits, ProPageProps } from "./types";
import { ref, computed, watchEffect, useTemplateRef, provide, toValue, unref, watch, useSlots } from "vue";
import { ElTooltip, ElButton } from "element-plus";
import { Delete, Edit, Search, Plus } from "@element-plus/icons-vue";
import { isEmpty, isFunction, isBoolean } from "@teek/utils";
import { useOptions, optionsMapKey } from "@teek/components/pro/use-options";
import { ProSearch } from "@teek/components/pro/search";
import { ProTable, defaultTooltipProps, lastProp } from "@teek/components/pro/table";
import { filterEmpty, flatColumnsFn, setProp } from "@teek/components/pro/helper";
import { useNamespace } from "@teek/composables";
import DialogForm from "./dialog-form.vue";

defineOptions({ name: "ProPage" });

const props = withDefaults(defineProps<ProPageProps>(), {
  columns: () => [],
  searchProps: () => ({}),
  initShowSearch: true,
  defaultValues: () => ({}),

  // ProTable 组件的 Props
  card: true,
  requestImmediate: true,
  toolButton: true,
  headerBackground: true,
  highlightCurrentRow: true,
  showHeader: true,
  pageScope: true,
  stripe: true,
  tooltipProps: () => defaultTooltipProps,
  exportFile: undefined,

  // DialogForm 配置项
  dialogFormProps: undefined,
});

const emits = defineEmits<ProPageEmits>();

const ns = useNamespace("pro-page");
const proSearchInstance = useTemplateRef<ProSearchInstance>("proSearchInstance");
const proTableInstance = useTemplateRef<ProTableInstance>("proTableInstance");
const dialogFormInstance = useTemplateRef<typeof DialogForm>("dialogFormInstance");

const slots = useSlots();

const searchSlots = computed(() =>
  Object.keys(slots)
    .filter(key => key.endsWith("-search"))
    .map(key => key.replace("-search", ""))
);

// 获取 ProTable 配置项
const proTableProps = computed(() => {
  const { columns, exportFile, ...rest } = props;

  // 如果 dialogFormProps 配置了 API，则开启对应的按钮
  const operationIndex = columns.findIndex(item => item.prop === (rest.operationProp || "operation"));
  if (operationIndex >= 0 && !toValue(columns[operationIndex].buttons)?.some(item => item.code === "native_edit")) {
    columns[operationIndex].buttons ??= [];
    toValue(columns[operationIndex].buttons)?.unshift(
      {
        text: "编辑",
        code: "native_edit",
        elProps: {
          type: "primary",
          size: "small",
          disabled: props.dialogFormProps?.disableEdit,
        },
        show: props.dialogFormProps?.editApi ? true : !!props.dialogFormProps?.useEdit,
        el: "el-link",
        icon: Edit,
        onClick: ({ row }) => dialogFormInstance.value?.handleEdit(row),
      },
      {
        text: "删除",
        code: "native_delete",
        elProps: {
          type: "danger",
          size: "small",
          disabled: props.dialogFormProps?.disableRemove,
        },
        confirm: {
          props: { title: "你确定删除吗?" },
        },
        show: props.dialogFormProps?.removeApi ? true : !!props.dialogFormProps?.useRemove,
        el: "el-link",
        icon: Delete,
        onConfirm: ({ row }) => dialogFormInstance.value?.handleRemove(row),
      }
    );
  }

  return {
    columns,
    ...filterEmpty(rest),
    searchProps: undefined,
    initShowSearch: undefined,
    dialogFormProps: undefined,
    exportProps: {
      // 加强 ProTable 的 exportFile 函数，支持传入搜索参数
      exportFile: exportFile ? (data: Record<string, any>[]) => exportFile?.(data, searchParams.value) : undefined,
      ...rest.exportProps,
    },
  };
});

const initShowSearch = ref(true);

watchEffect(() => (initShowSearch.value = toValue(props.initShowSearch)));

const { optionsMap, initOptionsMap } = useOptions();
const { flatColumns, searchParams, searchDefaultParams, searchColumns } = usePageSearchInit();

provide(optionsMapKey, optionsMap);

// 计算初始化查询参数
const initRequestParams = computed(() => ({ ...searchDefaultParams.value, ...props.initRequestParams }));

/**
 *  页面搜索数据初始化
 */
function usePageSearchInit() {
  const searchParams = ref<Record<string, any>>({});
  const searchDefaultParams = ref<Record<string, any>>({});

  // 定时器
  let timer: ReturnType<typeof setTimeout> | null = null;

  // 扁平化 columns，为了过滤搜索配置项
  const flatColumns = computed(() => flatColumnsFn(props.columns));

  // 组装 ProSearch 配置项
  const searchColumns = computed(() => {
    const filterColumns = flatColumns.value.filter(item => item.search);
    const searchColumns: SearchColumn[] = [];

    filterColumns.forEach(async column => {
      // Table 默认查询参数初始化
      const prop = lastProp(column.search?.prop ?? column.prop ?? "");
      const defaultValue = unref(column.search?.defaultValue) ?? props.defaultValues[prop];

      if (!isEmpty(defaultValue)) {
        console.log(defaultValue);
        if (!isFunction(defaultValue)) setSearchParams(prop, defaultValue);
        else {
          setSearchParams(prop, await defaultValue({ model: searchParams.value, optionsMap: optionsMap.value, prop }));
        }
      }

      // 组装搜索表单配置项
      const searchColumn: SearchColumn = {
        ...column.search,
        el: column.search?.el || ((column.search?.options ?? column.options) ? "ElSelect" : "ElInput"),
        grid: {
          offset: column.search?.offset,
          span: column.search?.span,
          xs: column.search?.xs,
          sm: column.search?.sm,
          md: column.search?.md,
          lg: column.search?.lg,
          xl: column.search?.xl,
          ...column.search?.grid,
        },
        prop,
        label: column.search?.label ?? column.label,
        beforeSearch: undefined,
        options: undefined, // proPage 已经处理 options，无需传给 ProForm 再次处理
        optionField: column.optionField,
        optionsProp: column.optionsProp,
      };
      searchColumns.push(searchColumn);
    });

    return searchColumns;
  });

  // 搜索表单初始化参数
  const setSearchParams = (prop: string, value: unknown) => {
    searchParams.value[prop] = value;
    searchDefaultParams.value[prop] = value;
  };

  watch(
    flatColumns,
    newValue => {
      // 防抖：防止初始化时连续执行
      if (timer) {
        clearTimeout(timer);
        timer = null;
      }

      timer = setTimeout(async () => {
        for (const column of newValue)
          initOptionsMap(column.search?.options ?? column.options, column.search?.prop || column.prop || "");
      }, 1);
    },
    { deep: true, immediate: true }
  );

  return { flatColumns, searchParams, searchDefaultParams, searchColumns };
}

// ---------- ProSearch 事件监听 ----------

const handleSearch = (searchModel: Record<string, any>) => {
  const newSearchModel = { ...searchModel };

  // 触发每个配置项的 beforeSearch
  for (const column of flatColumns.value) {
    if (!column.search?.beforeSearch) continue;

    const prop = lastProp(column.search?.prop ?? column.prop ?? "");
    const newSearchValue = column.search?.beforeSearch(newSearchModel[prop], newSearchModel, column);

    // 如果返回 false，则不执行查询
    if (newSearchValue === false) return;

    newSearchValue && setProp(newSearchModel, prop, newSearchValue);
  }

  // ProSearch 已经自动清除空值，因此传入 false
  proTableInstance.value?.search(newSearchModel, false);
  emits("search", newSearchModel);
};
const handleReset = (searchModel: Record<string, any>) => {
  proTableInstance.value?.reset(searchModel, false);
  emits("reset", searchModel);
};

const handleSearchRegister = (proSearchInstance: ProSearchInstance | null) => {
  emits("searchRegister", proSearchInstance);
};

// ---------- ProTable 事件监听 ----------

const handleTableRegister = (proTableInstance: ProTableInstance | null, elTableInstance: TableInstance | null) => {
  emits("tableRegister", proTableInstance, elTableInstance);
};

const handleSelectionChange = (useSelectReturn: UnwrapRef<UseSelectState>, index?: number) => {
  emits("selectionChange", useSelectReturn, index);
};

const handleSizeChange = (size: TableSizeEnum, style: SizeStyle) => {
  emits("sizeChange", size, style);
};

const handlePaginationChange = (pageInfo: PageInfo) => {
  emits("paginationChange", pageInfo);
};

const handleDragSortEnd = (newIndex: number, oldIndex: number) => {
  emits("dragSortEnd", newIndex, oldIndex);
};

/**
 * 执行过滤搜索
 */
const handleFilter = (filterModel: Record<string, any>, filterValue: unknown, prop: string) => {
  emits("filter", filterModel, filterValue, prop);
};
/**
 * 执行过滤清除
 */
const handleFilterClear = (prop: string) => {
  emits("filterClear", prop);
};
/**
 * 执行过滤重置
 */
const handleFilterReset = () => {
  emits("filterReset");
};

const handleFormChange = (fromValue: unknown, prop: TableColumn["prop"], scope: TableScope) => {
  emits("formChange", fromValue, prop || "", scope);
};

const handleButtonClick = (params: OperationNamespace.ButtonsCallBackParams) => {
  emits("buttonClick", params);
};

const handleConfirm = (params: OperationNamespace.ButtonsCallBackParams) => {
  emits("buttonConfirm", params);
};

const handleCancel = (params: OperationNamespace.ButtonsCallBackParams) => {
  emits("buttonCancel", params);
};

const handleLeaveCellEdit = (row: TableRow, column: TableColumn) => {
  emits("leaveCellEdit", row, column);
};

const expose = {
  searchParams,
  searchDefaultParams,
  proSearchInstance,
  proTableInstance,
  dialogFormInstance,

  // 在这里添加暴露常用方法，也可以直接通过 proSearchInstance、proTableInstance 获取实例对象调用方法
  search: () => proSearchInstance.value?.search(),
  reset: () => proSearchInstance.value?.reset(),
  toggleCollapse: () => proSearchInstance.value?.toggleCollapse(),
  getTableData: () => proTableInstance.value?.tableData,
  getPageInfo: () => proTableInstance.value?.pageInfo,
  setSearchParams: (params: Record<string, any>) => {
    Object.entries(params).forEach(([key, value]) => {
      setProp(searchParams.value, key, value);
    });
  },
  clearSearchParams: () => (searchParams.value = {}),
  clearSelection: () => proTableInstance.value?.tableMainInstance?.clearSelection(),
};

defineExpose(expose);
</script>

<template>
  <div :class="ns.b()">
    <ProSearch
      ref="proSearchInstance"
      v-show="initShowSearch"
      v-model="searchParams"
      :columns="searchColumns"
      :card
      v-bind="searchProps"
      @search="handleSearch"
      @reset="handleReset"
      @register="handleSearchRegister"
    >
      <template v-if="$slots['action']" #action="scope">
        <slot name="action" v-bind="scope" />
      </template>

      <template v-for="slot in searchSlots" #[slot]="scope">
        <slot :name="`${slot}-search`" v-bind="scope" />
      </template>
    </ProSearch>

    <ProTable
      ref="proTableInstance"
      v-bind="{ ...$attrs, ...proTableProps }"
      :request-params="searchParams"
      :init-request-params="initRequestParams"
      @selection-change="handleSelectionChange"
      @size-change="handleSizeChange"
      @pagination-change="handlePaginationChange"
      @drag-sort-end="handleDragSortEnd"
      @filter="handleFilter"
      @filter-clear="handleFilterClear"
      @filter-reset="handleFilterReset"
      @form-change="handleFormChange"
      @button-click="handleButtonClick"
      @confirm="handleConfirm"
      @cancel="handleCancel"
      @leave-cell-edit="handleLeaveCellEdit"
      @register="handleTableRegister"
    >
      <template #head-tool-after>
        <el-tooltip
          v-if="(toolButton === true || (!isBoolean(toolButton) && toolButton?.includes('search'))) && columns.length"
          content="隐藏/展开搜索"
          v-bind="tooltipProps"
        >
          <el-button
            :disabled="disabledToolButton?.includes('search')"
            :icon="Search"
            @click="initShowSearch = !initShowSearch"
            class="head__tool-button"
          />
        </el-tooltip>

        <slot name="head-tool-after" />
      </template>

      <!-- 拓展 ProTable 顶栏左侧按钮，适配 DialogForm 的 API -->
      <template #head-left="{ selectedListIds, selectedList, isSelected }">
        <slot name="head-left" v-bind="{ selectedListIds, selectedList, isSelected }">
          <slot name="head-left-before" v-bind="{ selectedListIds, selectedList, isSelected }" />

          <slot name="add" v-bind="{ selectedListIds, selectedList, isSelected, dialogFormInstance }">
            <el-button
              v-if="dialogFormProps?.addApi ? true : dialogFormProps?.useAdd"
              type="primary"
              :icon="Plus"
              @click="dialogFormInstance?.handleAdd()"
              :disabled="dialogFormProps?.disableAdd"
            >
              新增
            </el-button>
          </slot>
          <slot name="removeBatch" v-bind="{ selectedListIds, selectedList, isSelected, dialogFormInstance }">
            <el-button
              v-if="dialogFormProps?.removeBatchApi ? true : dialogFormProps?.useRemoveBatch"
              type="danger"
              :icon="Delete"
              plain
              @click="
                dialogFormInstance?.handleRemoveBatch(selectedListIds, selectedList, () => {
                  proTableInstance?.tableMainInstance?.clearSelection();
                })
              "
              :disabled="dialogFormProps?.disableRemoveBatch || !isSelected"
            >
              批量删除
            </el-button>
          </slot>
        </slot>

        <slot name="head-left-after" v-bind="{ selectedListIds, selectedList, isSelected }" />
      </template>

      <template v-if="$slots['operation-before']" #operation-before="scope">
        <slot name="operation-before" v-bind="{ ...scope, dialogFormInstance }" />
      </template>

      <template v-if="$slots['operation-after']" #operation-after="scope">
        <slot name="operation-after" v-bind="{ ...scope, dialogFormInstance }" />
      </template>

      <template
        v-for="slot in Object.keys($slots).filter(
          key => !['head-tool-after', 'head-left', 'operation-before', 'operation-after'].includes(key)
        )"
        #[slot]="scope"
      >
        <slot :name="slot" v-bind="scope" />
      </template>
    </ProTable>

    <!-- Dialog 表单 -->
    <DialogForm
      ref="dialogFormInstance"
      v-if="dialogFormProps"
      v-bind="{
        ...dialogFormProps,
        afterConfirm: (status, result) => {
          result && handleSearch(searchParams);
          dialogFormProps?.afterConfirm && dialogFormProps.afterConfirm(status, result);
        },
      }"
    >
      <template v-for="slot in Object.keys($slots)" #[slot]="scope">
        <slot :name="slot" v-bind="scope" />
      </template>
    </DialogForm>
  </div>
</template>

<style lang="scss" scoped>
@use "./index";
</style>
