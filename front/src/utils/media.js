// normalize media URLs returned from backend
export function normalizeMedia(raw) {
  if (!raw) return "";
  if (typeof raw !== "string") return "";
  if (raw.startsWith("http")) return raw;
  if (raw.startsWith("/api/")) return raw;
  if (raw.startsWith("/uploads/")) return `/api${raw}`;
  return raw;
}
