export const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export type VideoStatus = "UPLOADED" | "PROCESSING" | "READY" | "FAILED";

export interface Video {
  id: number;
  filename: string;
  title: string | null;
  timestamp: string;
  fileSize: number;
  duration: number | null;
  status: VideoStatus;
}

export async function uploadVideo(file: File): Promise<Video> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_BASE}/videos`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error(`Upload failed with status ${response.status}`);
  }

  return response.json();
}

export async function listVideos(): Promise<Video[]> {
  const response = await fetch(`${API_BASE}/videos`, { cache: "no-store" });

  if (!response.ok) {
    throw new Error(`Failed to load videos: ${response.status}`);
  }

  return response.json();
}
