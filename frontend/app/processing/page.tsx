"use client";

import { useEffect, useState } from "react";

import { listVideos, type Video } from "@/lib/api";

function formatSize(bytes: number): string {
  const mb = bytes / (1024 * 1024);
  return `${mb.toFixed(1)} MB`;
}

function formatDuration(seconds: number | null): string {
  if (seconds === null) {
    return "—";
  }
  const minutes = Math.floor(seconds / 60);
  const remaining = seconds % 60;
  return `${minutes}:${remaining.toString().padStart(2, "0")}`;
}

export default function ProcessingPage() {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function refresh() {
    setError(null);
    try {
      const data = await listVideos();
      setVideos(data.sort((a, b) => b.id - a.id));
    } catch {
      setError("Could not load the processing queue.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- standard fetch-on-mount
    refresh();
  }, []);

  return (
    <div className="mx-auto max-w-2xl py-16 px-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Processing Queue</h1>
        <button
          onClick={refresh}
          className="rounded-full border border-black/[.08] px-4 py-1.5 text-sm dark:border-white/[.145]"
        >
          Refresh
        </button>
      </div>

      {loading && <p className="mt-6 text-sm text-zinc-500">Loading…</p>}
      {error && <p className="mt-6 text-sm text-red-600">{error}</p>}

      {!loading && !error && videos.length === 0 && (
        <p className="mt-6 text-sm text-zinc-500">No videos uploaded yet.</p>
      )}

      {videos.length > 0 && (
        <table className="mt-6 w-full text-left text-sm">
          <thead>
            <tr className="border-b border-black/[.08] dark:border-white/[.145]">
              <th className="py-2 font-medium">Filename</th>
              <th className="py-2 font-medium">Uploaded</th>
              <th className="py-2 font-medium">Duration</th>
              <th className="py-2 font-medium">Size</th>
              <th className="py-2 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {videos.map((video) => (
              <tr
                key={video.id}
                className="border-b border-black/[.05] dark:border-white/[.08]"
              >
                <td className="py-2">{video.filename}</td>
                <td className="py-2">
                  {new Date(video.timestamp).toLocaleString()}
                </td>
                <td className="py-2">{formatDuration(video.duration)}</td>
                <td className="py-2">{formatSize(video.fileSize)}</td>
                <td className="py-2">{video.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
