[🇯🇵 日本語 (Japanese) ](./README_ja.md)

# Messay Coding Challenge

Welcome to the Messay Coding Challenge! This repository is a skeleton project built with Kotlin Multiplatform (KMP).
Your mission is to use this project as a base to implement a demo screen using our proprietary eye-tracking library, the "Messay Development Kit (MDK)."

## 🎯 Challenge Objective

Please implement a "demo screen for a new user experience controlled by eye gaze."
The Messay Development Kit is already defined in `libs.versions.toml`. Please use this library's API to create a screen that meets the following requirements.
We will provide a username and token to enable the Messay Development Kit; please add them to `local.properties` as follows:

local.properties
```
maven.messay.username=your-username
maven.messay.password=your-token
```

### Requirements

1. Messay SDK Integration:
   Properly initialize the SDK within `composeApp` and ensure that gaze data (coordinates, detection events, etc.) can be retrieved.

2. Interactive UI:
   Implement a screen in App.kt using `MdkTarget.EyeCloseHold` for an action triggered by closing and then opening your eyes, and `MdkTarget.FaceMovement` for pointer control.

3. Platforms:
   Android: Required. Must run on an emulator or a physical device.
   iOS: Optional (if you have the environment). It is desirable to leverage KMP features to share logic.

### Tech Stack

- Language: Kotlin

- UI Framework: Compose Multiplatform

- Architecture: No specific requirement, but a structure that prioritizes readability and maintainability (e.g., MVVM, MVI) is recommended.

## 🛠 About Messay Development Kit (MDK)
This is our proprietary library that detects eye open/close states, face angles, and iris positions from images captured by the front camera, converting them into data formats suitable for user interaction.

Specific usage instructions are summarized in this [document](https://honey-sassafras-fe0.notion.site/MDK-Implementation-Guide-30e6ae08ad1f47799402bf6c83e172f2).

## 📂 Project Structure
This project follows the standard Kotlin Multiplatform structure.

`/composeApp`: Contains the shared code for Compose Multiplatform and platform-specific implementations.

`commonMain`: Describes logic and UI (Compose) common to Android/iOS. Basically, write your code here.

`androidMain`: Android-specific implementations (e.g., if handling Camera permissions is necessary).

`iosMain`: iOS-specific implementations.

`/iosApp`: Entry point for the iOS application (Xcode project).

## 🚀 Build and Run
Run Android Application
Use the Run configuration in Android Studio or execute the following from the terminal.

macOS/Linux:
```
./gradlew :composeApp:assembleDebug
```

Windows:
```
.\gradlew.bat :composeApp:assembleDebug
```

Run iOS Application (Optional)
Open the `/iosApp` directory in Xcode to run it, or run it from Android Studio (with the KMP plugin installed).

## ✅ Review Points
The submitted code will be reviewed based on the following criteria:

- Functionality: Does the eye tracking work according to the requirements?

- Architecture: Do you understand the characteristics of KMP (code sharing) and is it implemented with an appropriate architecture?

- UI/UX: Are you providing appropriate feedback (visual effects, etc.) to the user for the unique input method of eye gaze?

- Code Quality: Naming conventions, readability, and error handling.

## 📦 Submission
1. Fork this repository or download it as a ZIP file.

2. After completing the challenge, please share the URL of your repository (GitHub, etc.) or the ZIP file.

3. Create a `SUBMISSION.md` (optional) to describe your key selling points or any implementation ingenuity.

Good luck! We are looking forward to your code! 🚀