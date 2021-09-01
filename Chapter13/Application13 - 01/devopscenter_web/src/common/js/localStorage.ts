
/**
 * 设置localStorage
 * @param name string 名
 * @param value string 值
 */
export function setLocalStorage(name: string, value: string): void {
  if (!value || !name) return;
  localStorage.setItem(name , JSON.stringify(value));
}

/**
 * 删除localStorage
 * @param name string 名
 */
export function delLocalStorage(name: string): void {
  localStorage.removeItem(name);
}

/**
 * 获取localStorage
 * @param name string 名
 */
export function getLocalStorage(name: string): string | null {
  const data: string | null = localStorage.getItem(name);
  if (!data) {
    return null;
  }
  return JSON.parse(data);
}
