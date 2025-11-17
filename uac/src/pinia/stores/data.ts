import type { App } from "@/common/api/application/app";
import { defineStore } from "pinia";

export const useDataStore = defineStore("dataStore", () => {
  const appInfo = ref<App.Info>();

  const setInfo = (teamInfoParam: App.Info) => {
    appInfo.value = teamInfoParam;
  };

  const clearInfo = () => {
    appInfo.value = undefined;
  };

  return {
    appInfo,
    setInfo,
    clearInfo,
  };
});
