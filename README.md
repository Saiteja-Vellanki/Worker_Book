# Workers Book

Offline-first Android app to track daily-wage, contract, part-time, and permanent
(monthly salary) farm workers — entries, reports, and salary payments. No login,
no server: everything is stored locally on-device with Room (SQLite).

Companion app to Farmer Book, same build approach (Kotlin, built via GitHub Actions,
no local Android Studio required).

## Stack
- Kotlin + Jetpack Compose + Material 3
- Room (local SQLite database)
- Navigation Compose
- Multi-language: English, Telugu (`values-te`), Hindi (`values-hi`) — Android
  auto-picks the right one from the phone's system language. Add more by copying
  a `values-XX/strings.xml` folder (XX = language code, e.g. `kn` for Kannada,
  `ta` for Tamil, `mr` for Marathi).

## What's implemented
- Dashboard: today's summary by worker type + total labour cost
- Entries: add/list/filter daily entries (Male / Female / Contract / Part-Time),
  with type-specific fields and auto-calculated totals
- Reports: date-range summary + detailed entry list
- Permanent Workers: add worker, record monthly salary payments, payment history

## What's stubbed / to extend before Play Store launch
- Edit/delete UI for existing entries and permanent workers (DAO methods already exist)
- Multi-farm switcher (dashboard currently assumes a single farm; add a `Farm` entity
  + farmId foreign keys on `entries`/`permanent_workers` if you need multiple farms)
- CSV/PDF export of reports
- App icon (a simple placeholder vector is included — swap in your real logo)
- Play Store listing assets (screenshots, feature graphic, privacy policy — see below)

## Build locally
Requires JDK 17. No Android Studio needed if you don't want it — but it makes
editing/previewing Compose UI much easier.

```
./gradlew assembleDebug
```
(If you don't have a `gradlew` wrapper committed, run `gradle wrapper --gradle-version 8.7`
once with a local Gradle install, or just let the GitHub Actions workflow build it —
see below.)

## Build via GitHub Actions (like Farmer Book)
`.github/workflows/build-apk.yml` is already set up:
1. Push this repo to GitHub.
2. Go to the **Actions** tab → run "Build APK" (or just push to `main`).
3. Download `workers-book-debug` (installable APK for testing) or
   `workers-book-release-unsigned` from the workflow run's **Artifacts**.

The release APK is **unsigned** — for a real Play Store upload you need a signed
**AAB** (Android App Bundle), not an APK. Two options:
1. Simplest: use **Play App Signing**. Generate an upload keystore locally
   (`keytool -genkeypair -v -keystore upload-keystore.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000`),
   store the keystore + passwords as GitHub Secrets, add a signing step to the
   workflow, and run `./gradlew bundleRelease` instead of `assembleRelease`.
2. Ask me and I'll add the signing config + secrets-based signing step to the
   workflow — same pattern as before, just needs your keystore secrets set up
   in the repo.

## Kotlin on Play Store
Yes — Kotlin is fully accepted and is Google's recommended language for Android
apps; there's no restriction around it for Play Store publishing.

## Play Store checklist (since you mentioned nominees/business use)
- Privacy policy URL (required even for a no-login, fully offline app if you
  collect any data — this app collects none, but Play Console still asks for
  the policy field)
- Data safety form: since there's no login, no analytics, no network calls, and
  no data leaves the device, you can declare "No data collected"
- App content rating questionnaire
- At least 2 phone screenshots, a 512x512 icon, a 1024x500 feature graphic
- A support email
