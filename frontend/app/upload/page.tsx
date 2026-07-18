"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

import { uploadVideo } from "@/lib/api";

type UploadState = "idle" | "uploading" | "error";

export default function UploadVideoPage() {
  const router = useRouter();
  const [file, setFile] = useState<File | null>(null);
  const [state, setState] = useState<UploadState>("idle");
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!file) {
      return;
    }

    setState("uploading");
    setError(null);

    try {
      await uploadVideo(file);
      router.push("/processing");
    } catch {
      setState("error");
      setError("Upload failed. Please try again.");
    }
  }

  return (
    <div className="mx-auto max-w-md py-16 px-6">
      <h1 className="text-2xl font-semibold">Upload Video</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <input
          type="file"
          accept="video/*"
          onChange={(event) => setFile(event.target.files?.[0] ?? null)}
          disabled={state === "uploading"}
        />

        <button
          type="submit"
          disabled={!file || state === "uploading"}
          className="rounded-full bg-foreground px-5 py-2 text-background disabled:opacity-50"
        >
          {state === "uploading" ? "Uploading…" : "Upload"}
        </button>

        {error && <p className="text-sm text-red-600">{error}</p>}
      </form>
    </div>
  );
}
