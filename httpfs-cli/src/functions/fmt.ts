export function formatSize(size: number): string {
  let s = size || 0;
  let u = "";
  for (u of ["B", "KB", "MB", "GB"]) {
    if (s < 1024) {
      break;
    }
    s = Math.round(s / 1024);
  }
  return `${s} ${u}`;
}

export function formatDateTime(str: string): string {
  if (!str) {
    return "";
  }
  const date = new Date(str);
  const yyyy = date.getFullYear();
  const MM = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mm = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}/${MM}/${dd} ${hh}:${mm}:${ss}`;
}
