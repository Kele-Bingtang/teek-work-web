import { http } from "@/common/http";

export namespace Profile {
  export interface Info {
    id: number;
    nickname: string;
    phone: string;
    email: string;
    sex: number;
  }

  export interface Password {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
  }
}

const baseUri = "/system/user/profile";

export const editProfile = (data: Profile.Info) => {
  return http.put<httpNs.Response<boolean>>(baseUri, data);
};

export const updatePassword = (data: Profile.Password) => {
  return http.put<httpNs.Response<boolean>>(`${baseUri}/updatePassword`, data);
};
