export interface DescriptionItem {
  value: string;
  label: string;
  span?: number;
}

export interface DescriptionProps {
  title?: string;
  avatarBgColor?: string;
  data?: DescriptionItem[];
}
