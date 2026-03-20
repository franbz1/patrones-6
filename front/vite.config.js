import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/health': 'http://localhost:4567',
      '/game': 'http://localhost:4567',
      '/towers': 'http://localhost:4567',
      '/wave': 'http://localhost:4567',
    },
  },
  test: {
    environment: 'happy-dom',
    setupFiles: './src/test/setupTests.js',
    globals: true,
  },
})
