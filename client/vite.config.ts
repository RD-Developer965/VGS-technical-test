/// <reference types="vitest" />

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  test: {
    globals: true,          // puedes usar describe/test/expect sin importar nada
    environment: 'jsdom',   // simula un navegador para React
    setupFiles: './src/setupTests.ts', // archivo con configuraciones comunes
  },
})
