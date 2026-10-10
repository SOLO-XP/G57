# 🚀 GMailGPU — Mali GPU Community Hub

<p align="center">
  <strong>🛠️ Better reports. Smarter debugging. Stronger Mali gaming.</strong>
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Backend" src="https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase&logoColor=white">
  <img alt="GPU Families" src="https://img.shields.io/badge/Mali-G57%20%7C%20G52%20%7C%20G615%20%7C%20G720-111827">
  <img alt="Version" src="https://img.shields.io/badge/Version-0.2%20Beta-00B8D9">
</p>

**GMailGPU 0.2 Beta** is an Android community hub for reporting Mali GPU driver issues and finding relevant open-source driver projects for **G57, G52, G615, and G720**. It helps users organize crash reports, Vulkan errors, rendering glitches, black screens, and performance problems with useful diagnostic evidence.

## ✨ Version 0.2 Beta highlights

- 💬 Issue chat inside each report, including message history and manual refresh.
- 🧑‍💻 User/developer communication based on account permissions.
- 🎨 UI improvements for a cleaner, more consistent experience.
- 🏷️ Renamed to GMailGPU to make clear this is the same project with broader Mali GPU coverage.
- 🎮 Report issues related to Mali-G57, G52, G615, and G720.
- 🔐 Supabase Row Level Security policies for issue chat.

> GPU categories are available for issue reporting. This does not guarantee that every listed GPU has a working custom driver.

## 📘 How to use GMailGPU (step by step)

New here? Follow this guide from account creation to sending your first issue.

### 1. Create a user account

1. Open GMailGPU on your Android phone.
2. On the **Choose access** screen, select **USER**. Do not select **ADMIN** unless you have administrator credentials.
3. Enter a unique username that you will remember.
4. Select **CREATE ACCOUNT**.
5. Create a password with **at least 8 characters**. Keep your username and password safe so you can sign in again later.
6. Tap **CREATE ACCOUNT** and wait for the app to finish. If an error appears, read it and check your connection or whether the username is already taken.

### 2. Sign in next time

1. Select **USER**.
2. Enter the same username and password you used when registering.
3. Select **SIGN IN**.

Use **CREATE ACCOUNT** only when registering for the first time. If you already have an account, use **SIGN IN**.

### 3. Open a new issue (bug report)

1. From your user home screen, tap **CREATE ISSUE**.
2. Enter a short, clear **Title**. Example: `Black textures in a game with PanVK`.
3. Enter the **Game** name.
4. In **Description**, explain exactly what happens and how to reproduce it. Include any error message and what you expected to happen.
5. Choose the relevant **Problem Type** and fill in any other details you know, such as driver, emulator, game version, DXVK, Wine, VKD3D, or Box64. If you do not know a version, do not guess.
6. **Required evidence:** attach at least one diagnostic log file OR one video showing the problem. Screenshots alone do not meet this requirement. The app will block submission if neither a log nor a video is attached.
7. Write the issue title and description in **English only**, including the exact error and steps to reproduce the problem.
8. Tap the issue submission button and wait for the confirmation that your issue was submitted.

**Tips for a useful report:** mention your phone model, Android version, Mali GPU, game name, driver/emulator versions, FPS or symptoms, and steps that reproduce the problem. Hide passwords, access tokens, and other private information from logs or screenshots.

### 4. Open and track your issue

1. Your submitted reports appear on the user home screen under **Your issues**.
2. Tap an issue to open its details.
3. Check the issue status and the attachments already uploaded.

### 5. Send messages to the developer

1. Open one of your existing issues.
2. Scroll to **CHAT WITH DEVELOPER**.
3. Type your message in **Write a message**.
4. Tap **SEND MESSAGE**.
5. To check for a reply, tap **REFRESH CHAT**. Chat currently uses manual refresh, not live updates.

Keep the conversation inside the same issue so the developer can see the report and its history together.

### 6. Add files to an existing issue (no new report needed)

If you forgot a screenshot or need to send another log:

1. Open the existing issue from **Your issues**.
2. Find **ADD MORE FILES**.
3. Choose **Images**, **Video**, or **Logs**.
4. Review the selected files and remove any you do not want to send.
5. Tap **UPLOAD FILES TO THIS ISSUE**.

The new files are attached to that existing issue; you do not need to create another issue. Uploading is subject to account permissions and storage policies.

### 7. Admin / developer access

The **ADMIN** option is for authorized developers only. Select it and sign in with the administrator username and password provided to you by the project owner. User registration does not create an administrator account. Admins can review reports, update issue status, and reply to users in the issue chat.

### Troubleshooting

- **Username already taken:** choose a different username.
- **Password too short:** use at least 8 characters when creating a user account.
- **Can't sign in:** check the username and password and make sure you selected **USER** and **SIGN IN**.
- **Chat is empty:** open the correct issue and tap **REFRESH CHAT**. The Supabase chat migration must be applied for chat to work.
- **Can't upload files:** check your internet connection, file selection, and account permissions.
- **Cloud/database error:** the app needs a correctly configured Supabase backend; a local/demo screen is not the same as a successful cloud submission.

## 🎮 Supported GPU project categories

These links lead to open-source driver repositories maintained by their respective project owners. They are community resources that may help users and developers investigate issues; they do not guarantee support or a fix.

### 🟣 Mali-G57
- [Noysz / panvk-g99-jm](https://github.com/Noysz/panvk-g99-jm)
- [mexicanbr0auth / mesa-panvk-g57](https://github.com/mexicanbr0auth/mesa-panvk-g57)
- [FristOneRR / FristOneRR-Panvk-Driver](https://github.com/FristOneRR/FristOneRR-Panvk-Driver)

### 🔵 Mali-G52
- [LukeValen / panvk-mali-g52](https://github.com/LukeValen/panvk-mali-g52)

### 🟢 Mali-G615
- [GunaCharanTeja / panvk-kbase-android](https://github.com/GunaCharanTeja/panvk-kbase-android)

### 🟠 Mali-G720
- [wonderkast02 / panvk-g720-kbase-csf](https://github.com/wonderkast02/panvk-g720-kbase-csf)

## ✨ Issue reporting

Include the details that help reproduce a problem:

- 📱 Device model, Android version, SoC, and GPU
- 🎮 Game name and version
- ⚙️ Driver, emulator, Wine, DXVK, VKD3D, and Box64 versions
- 🐛 Exact symptoms: crash, black screen, broken textures, Vulkan error, or low FPS
- 📎 Screenshots, videos, logs, and steps to reproduce

## 👥 Account roles

- 🙋 **Users:** submit issues, attach evidence, and track their own reports.
- 🧑‍💻 **Developers/Admins:** review reports, inspect diagnostic files, update statuses, and add troubleshooting notes.

## 🔄 Issue workflow

`Open → Investigating → Fix in Progress → Testing → Fixed → Closed`

## 🧰 Technology

- 🤖 Android + Kotlin
- 🎨 Jetpack Compose
- 🔑 Supabase Auth
- 🐘 PostgreSQL and Row Level Security
- ☁️ Supabase Storage for issue attachments

## 📲 Download

➡️ [View GMailGPU builds and releases](https://github.com/SOLO-XP/GMailGPU/actions/workflows/build-apk.yml)

This project is in **early beta**. Test builds may be debug-signed and are not production releases. Back up important data and review the release notes before installing.

## 🤝 Community driver resources

The linked projects are maintained independently by their respective developers. Please open technical questions or bug reports in the appropriate repository and include clear logs and reproduction steps.

---

<p align="center"><strong>💚 GMailGPU — G57 • G52 • G615 • G720</strong></p>
