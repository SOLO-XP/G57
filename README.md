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
