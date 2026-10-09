# 🚀 G57 — Mali GPU Issue Hub

<p align="center">
  <strong>🛠️ Report smarter. Debug faster. Build better drivers.</strong>
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Backend" src="https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase&logoColor=white">
  <img alt="Version" src="https://img.shields.io/badge/Version-0.2.8-00B8D9">
  <img alt="Status" src="https://img.shields.io/badge/Focus-Mali%20GPU%20%7C%20Winlator-111827">
</p>

**G57** is an Android issue-reporting hub for the **Mali GPU, driver, Winlator, and Android gaming/emulation community**. It helps users send organized reports—with the technical details developers need to investigate crashes, rendering bugs, Vulkan errors, and performance problems.

> 🎯 **The mission:** turn “it crashes” into a useful report that helps developers reproduce, diagnose, and fix the problem.

## ✨ What can you report?

Create an issue with the details that matter:

- 🎮 Game name and version
- 📱 Device and Mali GPU information
- ⚙️ Graphics driver and emulator / compatibility layer
- 🧩 DXVK, Wine, Proton, VKD3D, and Box64 versions
- 🐛 Crash, black screen, broken textures, Vulkan errors, or FPS/performance issues
- 📝 Clear descriptions and steps to reproduce the problem

## 📎 Attach logs and evidence

Give developers more than just a description. Issues can include supporting files such as:

- 📸 Screenshots
- 🎥 Videos
- 📄 Log and text files
- 🗂️ JSON, ZIP, and other diagnostic attachments

Attachments stay associated with their issue, helping developers understand the report and investigate it more efficiently.

## 👥 One app, role-based access

### 🙋 Users
- ➕ Create issue reports
- 📋 View and manage your own issues
- 📎 Upload logs and evidence
- 🔔 Receive in-app notifications when an issue is marked as solved

### 🧑‍💻 Developers / Admins
- 🗃️ Review submitted issues
- 🔍 Inspect device details, environment information, logs, screenshots, and videos
- 🔄 Update issue status and add developer notes
- 🧰 Manage issues and review solved users

🔐 **Privacy matters:** users should only be able to access their own issues and attachments. Authorization must be enforced by the backend and database policies—not just by hiding screens in the app.

## 🔄 Issue workflow

`Open → Investigating → Fix in Progress → Testing → Fixed → Closed`

The workflow helps keep reports organized from the first submission through testing and resolution.

## 🧰 Built with

| Technology | Purpose |
| --- | --- |
| 🤖 Android + Kotlin | Native Android app |
| 🎨 Jetpack Compose | User interface |
| 🔑 Supabase Auth | Account authentication |
| 🐘 PostgreSQL | Structured data |
| ☁️ Supabase Storage | Issue attachments |
| 🛡️ PostgreSQL RLS | Database access policies |

## 🔐 Security notes

- The Android app must **never contain** the Supabase `service_role` key.
- Keep privileged credentials in Supabase server-side secrets.
- Protect issue records and private attachments with verified authorization policies.
- Review logs and configuration before publishing builds; never commit passwords, tokens, or private keys.

### 🗑️ Admin user deletion (optional backend setup)

To enable the admin delete-user feature, deploy the Edge Function included in this repository:

```bash
supabase functions deploy admin-delete-user
```

The function expects `SUPABASE_SERVICE_ROLE_KEY` to be configured as a **server-side Supabase secret**. It validates the caller's session and admin profile before performing privileged actions. Never copy this secret into the Android app or public repository.

## 📲 Get G57

➡️ **[Open the latest G57 releases](https://github.com/SOLO-XP/G57/releases/latest)**

Check the release notes and installation instructions before installing a build. Builds distributed as test artifacts may differ from signed production releases.

## 🤝 Help improve Mali gaming

Found a bug? Have useful logs? Testing a driver or compatibility layer?

A clear report can save developers hours. Include your device, driver, game, versions, exact symptoms, and steps to reproduce whenever possible. 🚀

---

<p align="center">
  <strong>💚 G57 — Better reports. Better debugging. Better Mali gaming.</strong>
</p>
