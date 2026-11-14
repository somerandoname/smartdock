# Smart Dock - Developer Documentation

## Overview
Smart Dock is a user-friendly desktop mode launcher for Android, offering a modern, highly customizable user interface. It supports multi-window, keyboard shortcuts, and is compatible with Android Nougat and above. The app is designed to be installed as a system app for full functionality, leveraging accessibility and notification services to provide a desktop-like experience.

## Directory Structure
```
app/
  src/
    main/
      java/
        cu/axel/smartdock/
          activities/      # Main and launcher activities
          adapters/        # RecyclerView adapters for app lists, dock, notifications
          db/              # Database helper and contract
          dialogs/         # Dialogs for UI configuration
          fragments/       # Preference fragments for settings
          models/          # Data models (App, DockApp, AppTask, Action)
          preferences/     # Custom preference widgets
          receivers/       # Broadcast receivers (battery, sound events)
          services/        # Core background services (DockService, NotificationService)
          utils/           # Utility classes (AppUtils, DeviceUtils, ColorUtils, etc.)
          widgets/         # Custom UI widgets
      res/                 # Resources (layouts, drawables, strings)
      AndroidManifest.xml  # App manifest
```

## Main Components

### 1. Application Class
- **App.kt**: Initializes global settings and sets a custom uncaught exception handler to launch `DebugActivity` on crashes.

### 2. Activities
- **MainActivity.kt**: The main settings and entry point for user configuration. Hosts the `PreferencesFragment` and manages permission dialogs.
- **LauncherActivity.kt**: The main launcher activity, registered as a HOME intent.
- **SecondaryLauncherActivity.kt**: Alternative launcher for secondary home intent.
- **DebugActivity.kt**: Displays crash reports for debugging.

### 3. Services
- **DockService.kt**: The core of Smart Dock. Runs as an AccessibilityService, manages the dock UI overlay, app launching, task management, and system controls. Handles user interactions, gestures, and updates the dock in real time.
- **NotificationService.kt**: Listens for system notifications, manages a custom notification panel, and updates notification badges in the dock.

### 4. Adapters
- **DockAppAdapter.kt**: Binds `DockApp` data to the dock UI, handles click/long-click events, and applies icon theming.
- **AppAdapter.kt, AppActionsAdapter.kt, AppShortcutAdapter.kt, AppTaskAdapter.kt, DisplaysAdapter.kt, NotificationAdapter.kt**: Bind various app/task/notification data to RecyclerViews in the UI.

### 5. Models
- **App.kt**: Represents an installed app (name, package, icon, component, user).
- **DockApp.kt**: Extends `App`, adds a list of running tasks for multi-window support.
- **AppTask.kt**: Represents a running app task (id, label, package, icon).
- **Action.kt**: Represents a UI action (icon, text).

### 6. Utilities
- **AppUtils.kt**: Functions for app management (installed apps, pinning, running tasks, etc.).
- **DeviceUtils.kt**: Device-level operations (root commands, settings, device admin, overlays, sound, etc.).
- **ColorUtils.kt**: Color theming, wallpaper color extraction, and dynamic color application.
- **Utils.kt**: General helpers (dp conversion, bitmaps, logging, window params, etc.).
- **IconPackUtils.kt**: Loads and applies third-party icon packs.
- **OnSwipeListener.kt**: Handles swipe gestures for navigation and UI actions.

### 7. Preferences
- **AppChooserPreference.kt, AppChooserMultiplePreference.kt**: Custom preferences for selecting one or multiple apps.
- **FileChooserPreference.kt**: File selection preference.
- **IconPackPreference.kt**: Icon pack selection dialog.
- **SliderPreference.kt**: Slider for numeric preferences.
- **PreferenceHeader.kt**: Custom header for preference screens.

### 8. Fragments
- **PreferencesFragment.kt**: Hosts the main settings UI, loading preferences from XML resources.
- **Other fragments**: Grouped by settings category (appearance, sounds, notifications, etc.).

### 9. Receivers
- **BatteryStatsReceiver.kt**: Updates battery icon and plays sound on charge events.
- **SoundEventsReceiver.kt**: Plays sounds for USB and power events.

### 10. Database
- **DBHelper.kt**: SQLite helper for storing app launch modes.
- **DatabaseContract.kt**: Defines the schema for the launch modes table.

### 11. Widgets
- **HoverInterceptorLayout.kt**: Custom layout to intercept hover events for the dock overlay.

### 12. Dialogs
- **DockLayoutDialog.kt**: Dialog for selecting dock layout and updating related preferences.

## App Flow & Interactions
1. **Startup**: `App.kt` initializes the app and sets up crash handling. The launcher activity is started (`LauncherActivity` or `MainActivity`).
2. **Permissions**: `MainActivity` checks and requests necessary permissions (overlay, storage, accessibility, etc.).
3. **Dock Overlay**: `DockService` is started as an accessibility service. It creates and manages the dock overlay, listens for user input, and updates the UI based on running tasks, pinned apps, and system state.
4. **App Management**: Uses `AppUtils` and `DeviceUtils` to list, pin, launch, and manage apps and tasks. `DockAppAdapter` and related adapters bind this data to the UI.
5. **Notifications**: `NotificationService` listens for system notifications and updates the dock's notification area and badges.
6. **Preferences**: User settings are managed via custom preferences and fragments, persisted using Android's `SharedPreferences`.
7. **Database**: App launch modes and other persistent data are stored using `DBHelper` and `DatabaseContract`.
8. **Receivers**: Broadcast receivers update the UI and play sounds for system events (battery, USB, etc.).

## File Relationships
- **DockService** is the central orchestrator, interacting with adapters, models, utils, and the database.
- **NotificationService** works alongside DockService to provide notification integration.
- **Activities** provide entry points and settings UI, launching services and dialogs as needed.
- **Adapters** connect models to the UI, using utility classes for theming and data management.
- **Preferences** and **fragments** provide a modular, extensible settings interface.

## Extending the App
- To add new features, create new models, adapters, or fragments as needed.
- Utility functions should go in the appropriate utils file.
- For new settings, add a preference in XML and implement a custom preference if needed.
- For new system integrations, consider using a service or receiver.

---
For further questions, refer to the code comments. 