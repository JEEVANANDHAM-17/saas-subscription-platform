import { setupServer } from 'msw/node'

// Each test registers the backend responses it needs with server.use(...).
export const server = setupServer()
