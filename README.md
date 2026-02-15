# 🌞 Outdoor Mode Quick Settings Tile

Root-based Quick Settings tile for Samsung OneUI 5 / Android 13+\
Enables Samsung's hidden **display_outdoor_mode** (High Brightness Mode)
manually.

------------------------------------------------------------------------

## 📱 What It Does

This app adds a Quick Settings tile that allows you to manually toggle:

    display_outdoor_mode

When enabled, it forces **maximum panel brightness (HBM)** --- useful in
direct sunlight.

------------------------------------------------------------------------

## ⚙ How It Works

The tile executes:

Enable:

    settings put system display_outdoor_mode 1

Disable:

    settings put system display_outdoor_mode 0

State is verified using:

    settings get system display_outdoor_mode

All commands run via **root (Magisk required)**.

------------------------------------------------------------------------

## ✅ Features (v2.5 Stable)

-   Real-time root state detection
-   15-minute automatic safety timeout
-   Haptic feedback
-   Toast confirmation
-   Long-press → Display settings
-   Samsung OneUI lifecycle safe handling
-   No LSPosed
-   No CSC modification
-   No framework patches

------------------------------------------------------------------------

## 📌 Requirements

-   Samsung Galaxy device
-   OneUI 5 / Android 13+
-   Root (Magisk)
-   Superuser permission granted

------------------------------------------------------------------------

## 🔧 Installation

1.  Install APK
2.  Grant Magisk root permission
3.  Add tile in Quick Settings panel
4.  Tap to enable / disable

------------------------------------------------------------------------

## ⚠ Important: Why Updates May Not Install

This project is built using **GitHub Actions CI**.

Each GitHub runner generates a new debug signing key unless a permanent
keystore is configured.

Because of that:

Installing a newer build may fail with: "App not installed as package
conflicts with an existing package"

### Solution:

Uninstall the previous version before installing the new one.

For production distribution, a permanent signing key should be used.

------------------------------------------------------------------------

## 🏗 Build Environment

-   GitHub Actions (Ubuntu runner)
-   JDK 17 (Temurin)
-   Gradle 8.4
-   Compile SDK 34
-   Target SDK 34
-   Min SDK 29

Build command:

    gradle assembleDebug

------------------------------------------------------------------------

## 📜 Changelog

### v2.5

-   Replaced exact alarms with standard AlarmManager.set()
-   Fixed Android 13 exact alarm crash
-   Improved null safety for root reads
-   Fully stable after long device idle

### v2.4

-   Added VIBRATE permission
-   Fixed cold-start crash
-   Improved lifecycle stability

### v2.3

-   Reworked root state detection
-   Improved timeout reliability

### v2.2

-   Internal optimizations (experimental)

### v2.1

-   Added 15-minute auto timeout
-   Added haptic feedback
-   Added long-press Display settings

### v2.0

-   Initial stable Quick Settings implementation

------------------------------------------------------------------------

## ⚠ Disclaimer

Use at your own risk.

Outdoor mode forces maximum brightness and may increase:

-   Heat
-   Battery drain
-   AMOLED wear

------------------------------------------------------------------------

**Current Version:** 2.5\
Generated: 2026-02-15 UTC
