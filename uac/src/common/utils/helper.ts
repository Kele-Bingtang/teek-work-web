/**
 * 截取字符串首字符（中文）或者截取两个单词首字母（英文）
 */
export const captureText = (text: string | undefined) => {
  if (!text) return "";
  const isChinese = /^[\u4e00-\u9fa5]+$/.test(text.charAt(0));

  // 如果是中文，截取第一个字符
  if (isChinese) return text.charAt(0);
  // 如果是英文，截取前两个单词的首字母转大写
  const words = text.split(/\s+/).filter(word => word.length > 0);
  if (words.length >= 2) {
    return words
      .slice(0, 2)
      .map(word => word.charAt(0).toUpperCase())
      .join("");
  }
  // 如果只有一个单词，只截取第一个字母转大写
  if (words.length === 1) return words[0].charAt(0).toUpperCase();

  return "";
};
