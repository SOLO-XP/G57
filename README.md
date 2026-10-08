# G57 — Mali GPU Issue Reporter

G57 is one Android app with two roles:

- **User:** create Issues, upload screenshots/videos/logs, view only their own Issues, delete their own Issue, and receive an in-app notification when a developer resolves an Issue.
- **Admin (developer):** from the same app, view all Issues, open all attachments, update status, write developer notes, delete Issues, and open the **Solved Users** list.

## User flow

`Login → Create Issue → Describe problem → Environment → Images / Video / Logs → Submit`

The app automatically records the Android version and device model when a real Issue is submitted.

## Admin flow

`Login → Admin Panel → All Issues → Issue details → Status / Developer Note / Attachments`

When the developer changes an Issue to **Fixed** or **Closed**, a database trigger creates a notification for that user. The user sees the notification when entering the app. The user also appears in **Solved Users**.

## Stack

- Android / Kotlin / Jetpack Compose
- Supabase Auth + Postgres + Storage
- Private `issue-files` bucket
- PostgreSQL Row Level Security (RLS)

## Real admin account

The requested admin credentials are:

- Username: `NOYSZ`
- Password: `ZOG57`

Do **not** hardcode the production password into the APK. Create the Auth user in Supabase as `noysz@g57.app` with password `ZOG57`, then run:

```sql
update public.profiles set role = 'admin' where username = 'noysz';
```

The `NOYSZ / ZOG57` login displayed in an unconfigured build is only an offline demo path and does not connect to Supabase.

## Supabase setup

1. Create a Supabase project.
2. Run `supabase_schema.sql` in the SQL Editor.
3. Create the `NOYSZ` Auth user and promote it with the SQL above.
4. Create normal users from inside G57.
5. Add the following to `local.properties`:

```properties
SUPABASE_URL=https://YOUR-PROJECT.supabase.co
SUPABASE_PUBLISHABLE_KEY=YOUR-PUBLISHABLE-KEY
```

Never ship a Supabase service-role key inside the Android app. Use the publishable/anon key and RLS for authorization.

## File handling

The current MVP accepts images, videos, text/log/JSON/ZIP files up to 150 MB per file. Attachments are stored beneath the Issue UUID in a private bucket. Opening an attachment creates a short-lived signed URL.

Supabase's current Kotlin Storage API supports file uploads, upload progress flows, resumable uploads, signed URLs, and file deletion; the next performance pass can replace the current ByteArray upload path with disk-backed resumable uploads for large videos.

## Build

Open the project in Android Studio and sync Gradle. The included GitHub Actions workflow can also build a debug APK in the cloud once this project is pushed to a GitHub repository.
