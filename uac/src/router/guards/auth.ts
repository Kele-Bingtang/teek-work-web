import type { Router } from "vue-router";
import { message } from "teek";
import { serviceConfig, LOGIN_URL } from "@/common/config";
import { useRouteFn } from "@/composables";
import { useRouteStore, useUserStore, useDataStore } from "@/pinia";
import { resetRouter } from "..";
import { getOne } from "@/common/api/application/app";

export const createAuthGuard = (router: Router) => {
  /**
   * 路由跳转开始
   */
  router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();
    const routeStore = useRouteStore();
    const dataStore = useDataStore();
    const { initDynamicRoutes } = useRouteFn();
    const accessToken = userStore.accessToken;

    if (!to.meta.app) dataStore.clearAppInfo();
    else if (to.params.appId && dataStore.appInfo?.appId !== to.params.appId) {
      const res = await getOne(to.params.appId as string);
      if (res.status === "success") {
        const data = res.data;
        useDataStore().setAppInfo(data);
      } else message.error(res.message);
    }

    // 白名单
    const whiteList = serviceConfig.router.whiteList;

    // 判断是访问登陆页，有 Token 就在当前页面，没有 Token 重置路由并放行到登陆页
    if (to.path === LOGIN_URL) {
      if (accessToken) return next(from.fullPath);
      resetRouter();
      return next();
    }

    // 判断访问页面是否在路由白名单地址中，如果存在直接放行
    if (whiteList.includes("*")) {
      if (!routeStore.loadedRouteList.length) {
        await initDynamicRoutes(["*"]);
        return next({ ...to, replace: true });
      }

      return next();
    } else if (whiteList.includes("next") || whiteList.includes(to.path)) return next();

    // 判断是否有 Token，没有重定向到 login
    if (!accessToken) return next({ path: LOGIN_URL, replace: true });

    // 判断是否加载过路由，如果没有则加载路由
    if (!routeStore.loadedRouteList.length) {
      try {
        const userInfo = await userStore.getUserInfo();
        await initDynamicRoutes(userInfo.roles);
        return next({ ...to, replace: true });
      } catch (error) {
        userStore.clearPermission();
        router.replace(LOGIN_URL);
        return Promise.reject(error);
      }
    }
    next();
  });
};
