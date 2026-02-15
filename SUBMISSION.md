# Duck Hunt - Messay Coding Challenge Submission

## Project Overview

I've implemented a hands-free Duck Hunt game (inspired from the classic Duck Hunt game of course) using the Messay Development Kit (MDK) for eye and face tracking. The game is entirely controlled through facial movements and eye gestures - no traditional input required!

## Key Features Implemented

### Core Features 
- **Face Movement Control**: Reticle follows face position using `MdkTarget.FaceMovement`
- **Eye-Based Shooting**: Quick eye close/blink triggers shots using `MdkTarget.EyeCloseHold` (50ms)
- **MVI Architecture**: Clean separation of concerns with Intent-State-Effect pattern
- **Compose Multiplatform**: Shared UI and logic, with platform-specific logic for `GifPlayer`, `Audio SoundPool`, `Permission Handling`, `Toast Manager` and `Back Press Handler`.
- **Feedback**: Visual, haptic and sound effects upon various game events such as `Duck Hit`, `Gunshot`, `Duck Miss`, etc.

### Extra Features 
- **Rich Visuals**: Nostalgic landscape background, ducks, gun barrel and crosshair
- **Duck Behavior**: One duck at a time with random flight patterns and speeds
- **Hit Detection**: Distance-based collision between reticle and duck hitbox
- **Progressive Difficulty**: Ducks get harder to hit with each hit
- **Multiple Levels**: 5 levels with increasing baseline difficulty
- **Statistics Tracking**: Score, accuracy, hits, misses

### Things to note
**The iOS environment is set up and the app runs on the *Simulator*, but it crashes when accessing camera features after granting Camera Permission. The crash appears to originate from the MDK SDK, likely due to the simulator’s lack of camera support. I don’t currently have a physical iOS device to test this on real hardware.**
****So I cannot guarantee if it runs properly on iOS but I am hopeful that it does.****

**Thank you for reviewing my submission!** 

I thoroughly enjoyed building this project and exploring the possibilities of eye-tracking controls in games. The Messay MDK is a powerful tool, and I'm excited to see what else can be built with it!





