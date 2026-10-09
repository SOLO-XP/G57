# G57

**G57** is an Android application for reporting, organizing, and tracking problems related to **Mali GPU drivers and Android PC/emulator gaming environments**.

The goal is simple: make it easy for users to send a complete, useful bug report so developers can reproduce and investigate problems faster.

## What users can report

Users can create an Issue and provide:

- Game and game version
- Mali GPU / device information
- Mali driver version
- Emulator or compatibility layer
- DXVK version
- Wine version
- Proton version
- VKD3D / Box64 information
- Problem type such as crash, black screen, graphics problems, Vulkan errors, or performance issues
- A clear description and reproduction steps

## Attachments

An Issue can include supporting files such as:

- Screenshots
- Videos
- Log files
- Text, JSON, ZIP, and other diagnostic files

Each Issue keeps its attachments grouped together so developers can understand exactly which files belong to which report.

## User and developer access

G57 uses one Android application with role-based access.

**Users**
- Create Issues
- View and manage their own Issues
- Upload diagnostic files
- Receive notifications when their Issue is resolved

**Developers / Admins**
- View all submitted Issues
- Review descriptions, device/environment information, logs, images, and videos
- Update Issue status
- Add developer notes
- Manage Issues
- View solved users

Users cannot access other users' private Issues or attachments.

## Issue lifecycle

Issues can move through statuses such as:

`Open → Investigating → Fix in Progress → Testing → Fixed → Closed`

When a developer marks an Issue as solved, the affected user receives an in-app notification when they open G57.

## Technology

- Android
- Kotlin
- Jetpack Compose
- Supabase Auth
- PostgreSQL
- Supabase Storage
- PostgreSQL Row Level Security (RLS)

## Cloud storage

Issue attachments are stored in a private cloud bucket and associated with their Issue. Access is controlled by the application's authorization rules.

## Project goal

G57 is designed for the Mali GPU / emulator community, with a focus on making bug reports more complete, organized, and useful for driver and emulator developers.

---

**G57 — Mali GPU Issue Reporter**


## Username accounts and reinstall recovery (v0.2.7)

User accounts now use a username plus password so a user can sign in again after reinstalling the app. New accounts use the internal email form `<username>@g57.app`; users do not need to supply a real email address. In the Supabase Dashboard, disable email confirmation for this username/password flow, because these internal addresses cannot receive confirmation emails.

Older username-only anonymous accounts cannot be securely recovered after app data is removed: Supabase anonymous users cannot prove identity once their local session is gone. For a one-time migration, sign in as admin, open **User Management**, delete the old legacy profile (which frees the username), then register that username again with a password. Future reinstalls should use **Sign In** with that password.

### Secure admin user deletion

The Android app never contains a Supabase service-role key. To enable the admin delete button, deploy the Edge Function in `supabase/functions/admin-delete-user/index.ts`:

```bash
supabase functions deploy admin-delete-user
```

The function uses the project secret `SUPABASE_SERVICE_ROLE_KEY`, validates the caller's Supabase session and admin profile, removes files associated with the user's issues, then deletes the Auth user. Never put the service-role key in Android app configuration.
