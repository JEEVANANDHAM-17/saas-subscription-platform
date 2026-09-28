This is a app for managing you subscriptions

to start this in terminal by using debugger port use bellow code
.\gradlew.bat bootRun --debug-jvm

if by using specific port
.\gradlew.bat bootRun --debug-jvm -PdebugPort=[port number]

## Frontend

React + TypeScript + Vite app in `frontend/` (login, signup and plans for now).

```
cd frontend
npm install
npm run dev
```

Open http://localhost:5180. The dev server forwards `/api/*` to the backend on http://localhost:8080,
so start the backend first. If the backend runs on another port:

```
$env:API_TARGET="http://localhost:8081"; npm run dev
```

- `npm test` runs the tests (Vitest, with the API mocked by MSW)
- `npm run build` type-checks and builds to `frontend/dist`

Every API call except `/login` and `/signup` needs the `Authorization: Bearer <token>` header from `/login`.
The token lasts 1 hour (`app.jwt.access-token-ttl`); the app sends you back to the login page when it expires.