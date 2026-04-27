<script setup lang="tsx" name="ProjectSetting">
import type { Project } from "@/common/api/project";
import { WarnTriangleFilled } from "@element-plus/icons-vue";
import { useDialog, ProForm, message, HOME_NAME } from "teek";
import { useNamespace, useHandleData } from "@teek/composables";
import { ProjectKey, ProjectOnGetKey } from "@/common/config";
import { editProject, removeProject, transferProject } from "@/common/api/project";
import { listMyAllTeam } from "@/common/api/team";
import { rules, formColumns } from "@/views/team/components/project/form-columns";

const ns = useNamespace("project-setting");

const { open } = useDialog();

const projectInfo = inject(ProjectKey, ref());
const projectOnGetFunction = inject(ProjectOnGetKey, () => {});
const formModel = ref<Record<string, any>>({});
const isProjectOwner = computed(() => projectInfo.value?.projectRole === "管理员");

const baseInfo = computed(() => [
  { label: "项目 ID", value: projectInfo.value?.projectId },
  { label: "项目名称", value: projectInfo.value?.projectName },
  { label: "项目 URL", value: projectInfo.value?.baseUrl },
  { label: "项目简介", value: projectInfo.value?.description },
]);

const handleEdit = () => {
  formModel.value = { ...projectInfo.value };
  open({
    title: "编辑项目信息",
    height: 300,
    onConfirm: async () => {
      const res = await editProject({ ...formModel.value } as Project.ProjectUpdate);
      if (res.code === 200) {
        projectOnGetFunction();
        return message.success("编辑成功");
      }
    },
    render: () => (
      <ProForm
        v-model={formModel.value}
        el-form-props={{ rules }}
        columns={formColumns}
        notCleanModelKeys={["id", "projectId"]}
        showFooter={false}
      />
    ),
  });
};

const transferTeamId = reactive({
  teamId: "",
});

const handleTransferProject = () => {
  open({
    title: `移动项目 ${projectInfo.value?.projectName}`,
    height: 200,
    onConfirm: async () => {
      const res = await transferProject(projectInfo.value?.projectId || "", transferTeamId.teamId);
      if (res.code === 200) {
        projectOnGetFunction();
        return message.success("移动项目成功");
      }
    },
    render: () => (
      <ProForm
        v-model={transferTeamId}
        el-form-props={{ projectName: [{ required: true, message: "请选择 移动的团队名称", trigger: "blur" }] }}
        columns={[
          {
            prop: "teamId",
            label: "移动的团队名称",
            el: "el-select",
            options: async () => {
              const res = await listMyAllTeam();
              return {
                data: res.data.map(item => ({
                  value: item.teamId,
                  label: item.teamId === projectInfo.value?.teamId ? `${item.teamName}（当前团队）` : item.teamName,
                  disabled: item.teamId === projectInfo.value?.teamId || item.teamRole === 3,
                })),
              };
            },
          },
        ]}
        col-row
      />
    ),
  });
};

const router = useRouter();
const handleRemoveProject = () => {
  useHandleData("此操作将永久删除该项目，是否继续？", async () => {
    const res = await removeProject({ projectId: projectInfo?.value?.projectId || "" });
    if (res.code === 200) {
      router.push({ name: HOME_NAME });
      return message.success("删除成功");
    }
  });
};
</script>

<template>
  <el-space fill :class="`${ns.b()} w-full`">
    <el-descriptions title="基础信息" :column="1">
      <template #extra>
        <el-button plain auto-insert-space :disabled="!isProjectOwner" @click="handleEdit">编辑</el-button>
      </template>
      <el-descriptions-item
        v-for="item in baseInfo"
        :key="item.label"
        :label="item.label"
        label-class-name="w-125 "
        class-name="flex-1 flx-justify-between"
      >
        <span>{{ item.value }}</span>
      </el-descriptions-item>
    </el-descriptions>

    <!-- <el-descriptions title="基础信息" :column="1">
      <el-descriptions-item label="克隆项目" label-class-name="w-125 " class-name="flex-1 flx-justify-between">
        <span class="text-gray-400">克隆项目到当前团队或其他团队</span>
        <el-button plain auto-insert-space>克隆</el-button>
      </el-descriptions-item>
    </el-descriptions> -->

    <el-descriptions :column="1">
      <template #title>
        <el-space :size="5" class="text-danger">
          <el-icon><WarnTriangleFilled /></el-icon>
          <span>危险操作区域</span>
        </el-space>
      </template>

      <template v-if="isProjectOwner">
        <el-descriptions-item label="移动项目" label-class-name="w-125 fz-15" class-name="flex-1 flx-justify-between">
          <span class="text-gray-400">将项目移动到其他团队</span>
          <el-button plain auto-insert-space :disabled="!isProjectOwner" @click="handleTransferProject">移动</el-button>
        </el-descriptions-item>

        <el-descriptions-item label="删除项目" label-class-name="w-125 fz-15" class-name="flex-1 flx-justify-between">
          <span class="text-gray-400">务必谨慎，删除后项目不可以找回</span>
          <el-button
            plain
            auto-insert-space
            :disabled="!isProjectOwner || !projectInfo?.projectId"
            @click="handleRemoveProject()"
          >
            删除
          </el-button>
        </el-descriptions-item>
      </template>
    </el-descriptions>
  </el-space>
</template>

<style lang="scss" scoped>
@use "@teek/styles/mixins/bem" as *;
@use "@teek/styles/mixins/function" as *;

@include b(project-setting) {
  padding: 20px;
  background-color: cssVar(surface-bg-color-box);

  :deep(.el-descriptions__title) {
    font-size: 14px;
  }

  :deep(.el-descriptions__cell) {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 45px;
  }
}
</style>
