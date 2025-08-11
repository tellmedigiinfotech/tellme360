# TellMe360 - VR Video Player

A Kotlin Multiplatform VR video player application built with Compose Multiplatform, targeting Android and iOS platforms.

## Features

- **Home Screen**: Browse featured videos, categories, and series
- **VR Videos**: Dedicated section for VR content with filtering options
- **Series**: Browse and watch video series with episode management
- **Account**: User profile, settings, and account management
- **Modern UI**: Clean, Material Design 3 interface
- **Cross-Platform**: Works on both Android and iOS

## Project Structure

* [/composeApp](./composeApp/src) contains the shared Compose Multiplatform code:
  - [commonMain](./composeApp/src/commonMain/kotlin) - Code shared across all platforms
  - [androidMain](./composeApp/src/androidMain/kotlin) - Android-specific code
  - [iosMain](./composeApp/src/iosMain/kotlin) - iOS-specific code

* [/iosApp](./iosApp/iosApp) contains the iOS application entry point

## Screens

- **Home**: Featured content, categories, recent videos, and series
- **VR Videos**: VR-specific content with category filtering
- **Series**: Browse video series with search and filtering
- **Series Detail**: Detailed view of a series with episodes
- **Account**: User profile, settings, and account options

## Architecture

The app follows a clean, beginner-friendly architecture:
- **Data Models**: Simple data classes for videos, series, and categories
- **Navigation**: Bottom navigation with nested navigation for series details
- **UI Components**: Reusable Compose components
- **Theme**: Material Design 3 theming with light/dark support

## Getting Started

1. Clone the repository
2. Open in Android Studio or IntelliJ IDEA
3. Run on Android or iOS simulator/device

## Dependencies

- Compose Multiplatform
- Navigation Compose
- Material Design 3
- Material Icons Extended

## Development Status

This is a UI-only implementation with mock data. The app provides a complete user interface for a VR video player with:
- Clean, modern design
- Responsive layout
- Navigation between screens
- Mock content for demonstration

Future enhancements could include:
- Real video playback functionality
- Backend integration
- User authentication
- Content management
- VR headset integration

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/).# tellme360
# tellme360
# tellme360
# Tellme360
# Tellme360
# Tellme360
# tellme360
