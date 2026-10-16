# Project Froyo — APBn Squad Message Portal
### Native Android App Edition · Connected Messaging

<p align="center">
  <strong>A dedicated Android client for the APBn Squad messaging ecosystem.</strong>
  <br>
  <em>Classic MSN Messenger aesthetics. Windows XP-inspired interface. Android integration.</em>
</p>

---

## 01. Project Identity

| Property | Specification |
|---|---|
| Project | Project Froyo — APBn Squad Message Portal |
| Developer & Author | Fahad Al-Belal |
| Software Initiative | !Froyo¡ |
| Application ID | `com.froyo.apbnsquad.portal` |
| Version | `21.12.2008` |
| Platform | Android |
| Primary Language | Kotlin |
| Native UI Framework | Jetpack Compose |
| Backend Platform | Supabase |
| Interface Inspiration | MSN Messenger / Windows XP |
| Copyright | © 2026 Fahad Al-Belal / APBn Squad |

**Developer Portfolio & Project Archive:**  
https://fahadnway.qd.je

**Original Portal Service:**  
https://apbnsquad.qd.je

---

## 02. Project Overview

**Project Froyo: APBn Squad Message Portal** is an Android application developed by Fahad Al-Belal as part of the broader !Froyo¡ software and emulation development ecosystem.

The application provides a dedicated Android entry point to the APBn Squad messaging platform, combining a nostalgic desktop-inspired interface with mobile-oriented application functionality.

Its visual identity draws inspiration from the interaction patterns of MSN Messenger and the graphical environment of Microsoft Windows XP, preserving the character of early-2000s desktop communication software.

The project bridges a web-based messaging service and Android system capabilities, with an emphasis on interface continuity, convenient access, connectivity handling, and native operating-system integration.

The Android application is intended to evolve independently from the original website while maintaining compatibility with the established portal and its backend services.

### Engineering Objectives

- Provide a dedicated Android application experience.
- Preserve the visual identity of the original messaging portal.
- Reproduce the retro desktop interface using native Android UI components.
- Maintain compatibility with existing backend services and user accounts.
- Integrate appropriate Android system capabilities.
- Support reliable application behavior during network interruptions.
- Keep presentation, application logic, and backend operations maintainable.
- Establish a foundation for future !Froyo¡ ecosystem integrations.

---

## 03. Application Architecture

Project Froyo combines Android application infrastructure with the APBn Squad portal's existing service layer.

The intended architecture separates interface rendering, application state, backend communication, and operating-system integration.

```text
┌──────────────────────────────────────────────┐
│            PROJECT FROYO / ANDROID            │
├──────────────────────────────────────────────┤
│                                              │
│  ┌────────────────────────────────────────┐  │
│  │         Presentation Subsystem          │  │
│  │                                        │  │
│  │  Native Android UI                     │  │
│  │  MSN / Windows XP Design Language      │  │
│  │  Messaging Screens                     │  │
│  │  Navigation and UI State               │  │
│  └───────────────────┬────────────────────┘  │
│                      │                       │
│  ┌───────────────────▼────────────────────┐  │
│  │          Application Subsystem          │  │
│  │                                        │  │
│  │  User Interaction Processing           │  │
│  │  Session and State Management           │  │
│  │  Network State Coordination             │  │
│  │  Notification Coordination              │  │
│  └───────────────────┬────────────────────┘  │
│                      │                       │
│  ┌───────────────────▼────────────────────┐  │
│  │            Data Subsystem               │  │
│  │                                        │  │
│  │  Authentication                        │  │
│  │  Message Operations                    │  │
│  │  Remote Data Access                    │  │
│  │  Realtime Event Processing             │  │
│  └───────────────────┬────────────────────┘  │
│                      │                       │
├──────────────────────┼───────────────────────┤
│            REMOTE SERVICE LAYER              │
├──────────────────────┼───────────────────────┤
│                      ▼                       │
│  ┌────────────────────────────────────────┐  │
│  │             Supabase                   │  │
│  │                                        │  │
│  │  Authentication                        │  │
│  │  PostgreSQL                            │  │
│  │  Realtime Subscriptions                │  │
│  │  Row Level Security                    │  │
│  │  Edge Functions, where applicable      │  │
│  └────────────────────────────────────────┘  │
│                                              │
└──────────────────────────────────────────────┘
```

*This diagram describes the intended logical separation of responsibilities. The exact implementation depends on the components present in the current source code.*

---

## 04. Technology Stack

### Kotlin

Kotlin serves as the primary language for Android-side application logic, asynchronous operations, lifecycle integration, and typed application models.

### Jetpack Compose

Jetpack Compose provides the native declarative UI framework for the Android implementation.

It supports reusable interface components, state-driven rendering, adaptive layouts, and a custom visual system inspired by classic desktop software.

### Android SDK

Android platform APIs provide access to application lifecycle management, notifications, connectivity information, system resources, and other supported operating-system features.

### Gradle

Gradle manages project configuration, dependency resolution, build variants, compilation, and APK packaging.

### Supabase

Supabase supplies the existing portal's backend infrastructure.

Depending on the implemented integration, this may include:

- User authentication and session management.
- PostgreSQL-backed messaging data.
- Realtime database event subscriptions.
- Server-side operations through Edge Functions.
- Access control through Row Level Security.
- Persistent application data shared with the web client.

The native client is intended to use the existing service infrastructure rather than unnecessarily duplicating the portal's backend.

---

## 05. Native Android Implementation

The Android edition is designed to provide a dedicated application experience instead of simply exposing the portal through a mobile browser.

Where implemented, native Android components manage application navigation, interaction, state, and system-level integration.

### Native Interface Characteristics

- Kotlin-based application logic.
- Jetpack Compose interface components.
- Native input and keyboard handling.
- Android lifecycle-aware screen management.
- Platform-compatible navigation.
- Native notification integration.
- Adaptive layouts for mobile displays.

The original web portal remains available independently.

**Website:** https://apbnsquad.qd.je/

The precise implementation of each screen should be verified against the current Android source code.

---

## 06. Retro Interface Engineering

### MSN Messenger × Windows XP

The visual system combines the recognizable communication-oriented design of MSN Messenger with the desktop conventions of Windows XP.

The goal is to retain the original portal's visual character rather than replace it with a generic contemporary messaging interface.

### Design Principles

**Desktop-Inspired Composition**

Panels, window-like regions, title bars, borders, and controls reproduce the visual language of classic desktop applications where appropriate.

**Consistent Visual Identity**

Typography, spacing, colors, and component styling should remain consistent with the original portal.

**Mobile Adaptation**

Desktop-inspired layouts must adapt to narrower screens, touch interaction, and Android system insets without losing their recognizable appearance.

**Component Reusability**

Reusable UI components help maintain consistent styling and simplify future modifications.

### Interface Direction

The design prioritizes:

- Retro desktop aesthetics.
- Familiar messaging interactions.
- Clearly distinguishable interface controls.
- Consistent visual hierarchy.
- Mobile usability without unnecessary redesign.

---

## 07. Messaging and Realtime Data Flow

The messaging subsystem integrates with the existing APBn Squad messaging service.

Where supported by the Android implementation, messaging operations follow a general client-server interaction model.

```text
User Interaction
       │
       ▼
Message Composition
       │
       ▼
Android Client Validation
       │
       ▼
Authenticated Backend Request
       │
       ▼
Backend Authorization
       │
       ▼
Database Persistence
       │
       ▼
Realtime Event Distribution
       │
       ▼
Subscribed Client Updates
       │
       ▼
Interface State Refresh
```

### Messaging Responsibilities

| Subsystem | Responsibility |
|---|---|
| Message Composer | Captures message input |
| Data Access Layer | Coordinates remote data operations |
| Authentication Layer | Supplies the current authenticated session |
| Realtime Handler | Processes applicable subscribed events |
| UI State Management | Reflects changes in the interface |
| Error Handling | Represents failed operations and connectivity issues |

Actual behavior depends on the backend schema, authorization policies, realtime configuration, and Android implementation.

### Consistency Considerations

A robust implementation should account for duplicate realtime events, temporary disconnections, failed submissions, session expiration, and updates received while the application is inactive.

---

## 08. Native Notification Pipeline

Android notifications provide a system-level mechanism for communicating relevant messaging events to the user.

The original project documentation describes a notification pipeline intended to produce native alerts for incoming messages.

Where implemented, the pipeline may coordinate backend events, application state, notification construction, and Android's notification service.

### Notification Engineering Considerations

- Notification channel configuration.
- Appropriate notification icons.
- Permission handling on supported Android versions.
- Duplicate-notification prevention.
- Session and message-context validation.
- Notification lifecycle and dismissal behavior.
- Privacy-conscious notification content.
- Background-execution constraints.

Exactly-once notification delivery cannot be assumed merely from receiving realtime events. Reliable deduplication requires suitable event identifiers and notification-state handling.

---

## 09. Offline Interface and Connectivity Recovery

The original project documentation describes an offline desktop-inspired interface containing interactive windows, draggable dialogs, a taskbar, and developer-profile elements.

Where retained in the current implementation, these components provide an offline-accessible interface separate from live backend-dependent functionality.

### Offline Behavior

Offline interface availability does not automatically imply that messaging data is synchronized or that messages can be delivered without a connection.

The application should distinguish between:

- Locally available interface elements.
- Previously retrieved data, if cached.
- Pending network operations, if supported.
- Live backend-dependent functionality.
- Failed requests requiring retry.

### Connectivity Recovery

The original WebView implementation documented an `Android.retryConnection()` JavaScript bridge for connection recovery.

That bridge belongs to a WebView-based integration and should not be represented as part of the native implementation unless it is intentionally retained.

A fully native implementation should use appropriate Android networking and lifecycle mechanisms instead of depending on a JavaScript bridge.

---

## 10. Authentication and Session Management

Authentication connects the Android client to the established APBn Squad account system.

The Android implementation must use the authentication flow supported by the existing backend and preserve compatibility with existing accounts.

### Responsibilities

- Authentication request handling.
- Session persistence and restoration.
- Expired-session handling.
- Authenticated navigation.
- Logout and session invalidation.
- Authorization failure handling.
- Secure handling of authentication credentials and tokens.

The interface must not be treated as the final authorization boundary.

Sensitive operations must be independently validated by trusted backend services.

---

## 11. Backend Security and Data Integrity

The Android application communicates with remote services using appropriately secured network connections.

The backend remains responsible for enforcing data access permissions and validating privileged operations.

### Security Requirements

- Never embed Supabase service-role keys in the APK.
- Keep private signing keys outside public repositories.
- Preserve existing Row Level Security policies.
- Validate privileged operations on the server.
- Avoid trusting client-supplied privilege claims.
- Apply suitable authentication and authorization checks.
- Handle authentication tokens securely.
- Avoid unnecessarily exposing personal or sensitive data.
- Keep backend configuration separate from privileged credentials.

Android application packages can be inspected and reverse-engineered. Client-side code must therefore not be considered a secure storage location for privileged secrets.

---

## 12. Application Lifecycle and Network Resilience

Unlike a continuously running desktop process, Android applications operate within a lifecycle and resource-management environment controlled by the operating system.

The application should account for:

- Activity lifecycle transitions.
- Process recreation.
- Screen configuration changes.
- Temporary connectivity loss.
- Network reconnection.
- Realtime subscription cleanup.
- Session restoration.
- Cancellation of obsolete asynchronous work.
- Keyboard visibility and input focus.
- Loading, empty, success, and error states.

Realtime listeners and network operations should be managed according to the lifecycle and behavior of the actual SDKs in use.

---

## 13. Detailed Feature Set

This section records the feature set for the native Android edition. Feature names describe the intended application design; confirm each item against the source code before treating it as implemented in a release.

### 13.1 Authentic Retro Windows XP and MSN Messenger UI

- **Luna Blue Window Frames (`XpWindow`):** Windows XP-inspired title bars, beveled borders, gradient headers, window branding, and minimize, maximize, and close controls.
- **XP Taskbar and Start Menu (`XpTaskbar`):** A classic green Start button, an `about_us.exe` system dialog, active-window indicators, and a retro system tray showing application status.
- **MSN Messenger Aesthetic:** A two-panel layout, sunken text fields, beveled `XpButton` controls, retro contact lists, and a squad-rules reminder box.
- **XP Desktop Wallpaper:** Bliss-inspired desktop imagery behind floating windows and dialogs.

### 13.2 Squad Authentication and Account System

- **Squad username format:** Validate account handles using the `handle@id` convention.
- **Supabase authentication:** Sign in and sign up through the existing Supabase backend, with credential validation and username-availability checks.
- **Persistent sessions (`SessionManager`):** Store session tokens and user preferences using secure Android storage appropriate to the selected authentication SDK. Keep user IDs and profile data available across app restarts as required.
- **Ban enforcement:** Detect banned accounts, end or invalidate their active session as supported by the backend, and display the administrative ban reason.

### 13.3 Realtime Messaging and Chat Experience

- **Group chat stream:** Display the squad's message feed and smoothly follow the latest messages when appropriate.
- **Replies and threading:** Allow a user to select a message and show a reply banner quoting the original author and message.
- **Message deletion and moderation:** Let users delete their own messages and authorized squad administrators moderate messages from other members. Deleted-message behavior should follow the portal's backend rules.
- **Timestamps and role badges:** Display formatted timestamps, display names, `@usernames`, and clear `ADMIN` role badges.

### 13.4 Realtime Search and Highlighting

- **Expandable keyword filter:** Provide an XP-styled search toolbar for message content and sender usernames.
- **Live match counter:** Show the number of matching messages and, where useful, the total message count.
- **Search highlighting:** Highlight matching terms in message text and author names, using bold styling or another accessible visual distinction.

### 13.5 Rich Media and Attachments

- **Android photo and media picker:** Use Android's system Photo Picker (`PickVisualMedia`) for supported images, GIFs, and videos without requesting broad media-library access unnecessarily.
- **Cloud media hosting (ImgBB):** Upload selected media in the background and keep raw hosted URLs out of the visible message composer where possible.
- **Inline previews:** Render supported images in message bubbles with rounded corners and loading/error states.
- **Fullscreen preview (`ImageViewerDialog`):** Open a selected media thumbnail in an XP-styled preview dialog with clear dismissal controls.

**Security note:** Keep upload credentials and secret API keys out of the APK. Use a safe upload design and follow the hosting provider's current API requirements.

### 13.6 Realtime Typing Indicators

- **Supabase Realtime (`RealtimeTypingManager`):** Use a Realtime broadcast topic such as `realtime:chat_typing` if configured in the backend.
- **Animated typing dots:** Indicate which squad members are typing.
- **Expiring presence:** Automatically clear stale typing indicators after approximately 3.5 seconds and maintain the relevant WebSocket connection/heartbeat behavior.

Typing indicators are ephemeral UI state; they should not be treated as proof that a message has been sent.

### 13.7 Presence and Status Management

- **Status modes:** Online (green), DND/Busy (red), and Idle/Away (orange).
- **Status selector (`StatusMenuDialog`):** Allow status changes through a toolbar status pill and profile sidebar.
- **Shared status state:** If status is synchronized with other clients, persist and authorize it through the backend rather than relying only on local UI state.

### 13.8 User Profiles and Popovers

- **Clickable avatars and usernames:** Open a profile popover when a member's avatar or username is selected.
- **Profile details:** Show the avatar in an MSN-inspired frame, display name, handle, role badge, bio, and join date when available.
- **Profile editing (`EditProfileDialog`):** Support changes to display name, avatar URL, and personal bio, subject to backend validation and access rules.

### 13.9 Squad Administration and Moderation

- **Admin-only access:** Show administrative tools only to accounts whose role is verified by the trusted backend.
- **Member management:** List registered members, roles, and account statuses.
- **Ban and unban controls:** Allow authorized administrators to apply or remove bans with an administrative reason.
- **Server-side authorization:** Hiding admin controls in the interface is not sufficient security. Every administrative operation must also be authorized by Supabase policies or a trusted server-side function.

### 13.10 Native Notifications and Background Monitoring

- **Background component (`PortalBackgroundService`):** Where permitted and justified, use an Android foreground service for ongoing work that meets Android's foreground-service requirements. A service should not be assumed to run indefinitely.
- **Keep-alive scheduling:** Use Android scheduling APIs such as WorkManager or alarms only where appropriate; exact behavior is subject to Android version, battery optimization, and background-execution restrictions.
- **Boot receiver (`BootReceiver`):** If supported by the app and allowed by Android, handle `RECEIVE_BOOT_COMPLETED` to reschedule permitted work after device restart.
- **Notification channels (`NotificationHelper`):** Create Android notification channels and display sender names, message snippets, and the app's notification icon.
- **Settings (`SettingsDialog`):** Provide a notification enable/disable preference and a test-notification action.

Background delivery must respect Android platform restrictions, user notification settings, battery usage, and applicable foreground-service policies. Realtime WebSockets may be suspended when the process is stopped; push notifications are generally more reliable for notifications when the app is not running.

### 13.11 Connectivity Recovery and Offline Mode

- **Network monitor (`NetworkMonitor`):** Observe connectivity changes using supported Android connectivity APIs.
- **Offline fallback (`offline.html`):** Preserve the legacy standalone offline interface where it remains part of the project, or implement an equivalent native offline screen in the native edition.
- **Recovery behavior:** Explain connection state, avoid repeated aggressive retries, and provide a user-triggered retry when connectivity returns.

The legacy web version may use `offline.html` and a JavaScript bridge such as `Android.retryConnection()`. Those web-specific components do not automatically exist in a fully native Compose application and must be recreated natively if required.

---

## 14. Project Structure

The original repository structure should remain authoritative.

A possible organization for a modular native implementation is shown below.

```text
ProjectFroyo/
│
├── app/
│   ├── build.gradle.kts
│   │
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           │
│           ├── java/
│           │   └── com/froyo/apbnsquad/portal/
│           │       ├── MainActivity.kt
│           │       │
│           │       ├── ui/
│           │       │   ├── components/
│           │       │   ├── screens/
│           │       │   ├── navigation/
│           │       │   └── theme/
│           │       │
│           │       ├── data/
│           │       │   ├── models/
│           │       │   ├── repositories/
│           │       │   └── remote/
│           │       │
│           │       ├── domain/
│           │       │   └── usecases/
│           │       │
│           │       └── core/
│           │           ├── network/
│           │           └── utilities/
│           │
│           └── res/
│               ├── drawable/
│               ├── mipmap/
│               └── values/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

*Illustrative structure only. Directory names and architectural layers should be aligned with the actual repository.*

---

## 15. Build Environment

### Requirements

- Android Studio compatible with the project's build configuration.
- A compatible Java Development Kit.
- Required Android SDK components.
- Access to the necessary backend configuration.
- A physical Android device or compatible emulator.

### Build from Source

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Allow Gradle synchronization to complete.
4. Configure the required backend settings.
5. Resolve any missing SDK or dependency requirements.
6. Build the debug variant.
7. Install and test the application.
8. Build a release variant after validation.

For projects containing a compatible Gradle wrapper:

```bash
./gradlew assembleDebug
```

The default debug APK output location is commonly:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Actual build tasks and output paths depend on the project's Gradle configuration.

---

## 16. Testing and Verification

The following areas should be tested before a production release.

### Functional Verification

- Application launch and navigation.
- Login and registration.
- Session restoration and logout.
- Message submission and retrieval.
- Realtime event processing.
- Replies and mentions, where implemented.
- Message deletion, where implemented.
- Administrative actions, where implemented.
- Notification behavior.
- Network failure and recovery.

### Compatibility Verification

- Existing APBn Squad accounts.
- Existing backend records.
- Supported Android versions.
- Different screen sizes and densities.
- Keyboard visibility and text input.
- Network interruptions.
- Expired sessions.
- Backend authorization failures.

### Release Verification

- Correct application identifier.
- Correct version metadata.
- Valid release signing configuration.
- No privileged credentials in the APK.
- No unintended development configuration.
- Successful installation and launch.
- Verified behavior for advertised features.

Feature completion should be documented only after the corresponding behavior has been tested.

---

## 17. Development Roadmap

Potential engineering priorities include:

- [ ] Verify native interface parity with the original website.
- [ ] Validate authentication compatibility.
- [ ] Verify realtime messaging behavior.
- [ ] Confirm notification deduplication.
- [ ] Validate offline interface availability.
- [ ] Improve lifecycle-aware network recovery.
- [ ] Verify authorization and backend security.
- [ ] Test across supported Android devices.
- [ ] Complete release configuration.
- [ ] Publish release documentation and screenshots.

This roadmap should be updated to reflect the current implementation rather than treated as a list of completed features.

---

## 18. About the Developer

**Fahad Al-Belal** is the creator and developer behind Project Froyo and its associated software experiments.

The !Froyo¡ project archive documents a broader development history involving software projects, Android experimentation, emulation, and operating-system concepts.

Project Froyo: APBn Squad Message Portal extends that development direction by bringing the APBn messaging experience to a dedicated Android application.

### Developer Portfolio

https://fahadnway.qd.je

Explore the portfolio for additional projects, software releases, and development archives.

---

## 19. Copyright and Licensing

Copyright © 2026 Fahad Al-Belal / APBn Squad. All rights reserved.

No additional open-source license is declared by this README.

Refer to any existing repository license and applicable third-party dependency licenses before redistribution or modification.

---

## 20. Official Service

**APBn Squad Message Portal — Web Edition**

https://apbnsquad.qd.je

The web portal and Android client provide separate interfaces to the APBn Squad messaging platform.

---

<p align="center">
  <strong>PROJECT FROYO</strong>
  <br>
  APBn Squad Message Portal
  <br><br>
  Native Android · Messaging Infrastructure
  <br>
  <em>Developed by Fahad Al-Belal</em>
</p>
