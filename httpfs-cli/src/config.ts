export const AppConfig = {
  ApiEndpoint: import.meta.env.VITE_APP_API_URL,
  MaximumUploadSize: import.meta.env.VITE_APP_API_UPLOAD_MAX_SIZE,
} as const;
