import type { App } from "@/common/api/application/app";
import { defineStore } from "pinia";

export const useDataStore = defineStore("dataStore", () => {
  const appInfo = ref<App.AppInfo>();

  const setAppInfo = (teamInfoParam: App.AppInfo) => {
    appInfo.value = teamInfoParam;
  };

  const clearAppInfo = () => {
    appInfo.value = undefined;
  };

  return {
    appInfo,
    setAppInfo,
    clearAppInfo,
  };
});
