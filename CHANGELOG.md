# G57 0.2.8

- Bumped Android version to 0.2.8 (versionCode 9).
- Separated the owner password-management page from the public download homepage.
- Kept owner-only authorization checks in the Supabase Edge Function.
- Fixed missing ViewModel actions that prevented the Android build from compiling.
- Updated the APK workflow's version verification and artifact name.

# G57 0.2.0

- Added real Android pickers for images, videos and logs.
- Added user Issue details and Issue deletion.
- Added Admin Issue deletion.
- Added Admin status controls and developer notes.
- Added automatic in-app resolution notifications when status changes to Fixed/Closed.
- Added Admin "Solved Users" list.
- Added device model + Android version capture for new Issues.
- Fixed user/admin RLS so only Admin can update Issue status/notes.
- Added private Storage delete/read policies.
- Added GitHub Actions cloud build workflow for a debug APK.
