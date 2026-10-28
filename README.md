# YieldGrain

## Repository inspection

I inspected the current repository at `fayazsiyalh-dr/pssf-market` before changing anything.

Findings:
- The project currently uses Vite + React (from `package.json` with `vite`, `react`, `react-dom`, and `@vitejs/plugin-react`).
- The app is a single-page web UI for a neural marketing tool, not an Android application.
- It contains a Gemini API key lookup from `import.meta.env.VITE_GEMINI_API_KEY` in `main.jsx`, which is not suitable for a production server-side secret flow and is not compliant with the requirement to avoid storing API keys in source.
- There is no backend, no secure auth layer, no persistent database, no role-based access control, and no automated test suite.
- There are no Android project files, no Gradle build, and no offline-first data strategy for mobile.

This means the current repository is not a viable starting point for the requested mobile procurement platform. Rather than replacing the project outright, I am preserving the existing repo and creating a staged implementation under a new backend and Android app structure.

## Staged implementation plan

### Stage 1 — Baseline security and domain rules
- Establish the server with Spring Boot and PostgreSQL.
- Define procurement, storage, resale, and transport fee logic using Pakistani rupees and standard unit conversions.
- Add environment-based config for DB, JWT, and Twilio OTP provider credentials.
- Add unit tests for fee calculation and formatting.

### Stage 2 — Authentication and access control
- Implement phone-number OTP flow through Twilio Verify.
- Generate signed JWTs with customer/admin roles.
- Enforce RBAC on the server using Spring Security `@PreAuthorize`.
- Keep OTPs and secrets out of source code.

### Stage 3 — Procurement and inventory workflow
- Add rate management, procurement requests, warehouse receipts, and storage calculations.
- Track inventory ownership by customer and warehouse.
- Record fees as separate ledger entries rather than hiding transport/handling/fumigation charges.

### Stage 4 — Resale and reporting
- Add resale requests with brokerage computation at 0.5% of actual sale value.
- Generate admin reports and customer transaction history.
- Expose transaction endpoints and secure reporting APIs.

### Stage 5 — Android client
- Build a Compose-based Android app named YieldGrain.
- Include login, dashboard, rates, inventory, procurement, and resale flows.
- Add offline-friendly state handling and validation for Pakistan mobile numbers and local currency formatting.
- Design with loading, empty, success, and error states.

### Stage 6 — QA and setup
- Add automated tests for pricing rules and formatter behavior.
- Provide setup instructions for backend database, Twilio credentials, Android SDK, and local development.
- Document known constraints and gaps if the local environment lacks Android SDK support.

## Current status

This repository now contains:
- A Spring Boot backend for YieldGrain under `backend/`
- An Android Compose app skeleton under `android/YieldGrainApp/`
- Test coverage for pricing rules and formatting
- Setup and environment guidance

Note: I have not claimed runtime success for features that were not executed in this environment. The code is structured for local verification, but Android compilation and full backend runtime tests require a machine with Java/Gradle and Android SDK configured.

## Local setup

### Backend
1. Copy `backend/.env.example` to `backend/.env` and fill real values.
2. Start PostgreSQL and create a database named `yieldgrain`.
3. Run:
   - `cd backend`
   - `./gradlew test`
   - `./gradlew bootRun`

### Android app
1. Install Android Studio with SDK 34+.
2. Open `android/YieldGrainApp` in Android Studio.
3. Update the backend base URL in `app/src/main/java/com/yieldgrain/data/ApiConfig.kt` or your environment config.
4. Run the app on a device or emulator.

## Security notes

- Never commit `.env` files or real OTP/Twilio/JWT secrets.
- Store all secrets in environment variables or a secrets manager.
- Use PostgreSQL in production and only H2 for local tests.
- Server-side authorization is enforced in the backend, not only in the UI.
