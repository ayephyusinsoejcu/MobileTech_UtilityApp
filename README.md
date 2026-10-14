# MobileTech Utility App – StudyFlow

**StudyFlow | A Focused Study Timer and Productivity Utility**

StudyFlow is an Android utility application developed for **CP3406 / CP5307 Assessment 1: Utility App** at James Cook University Singapore.

The application is designed to support students in managing their study sessions through a simple, intuitive, and distraction-free interface. It combines a customizable countdown timer, focused study and break sessions, progress tracking, and motivational quotes to encourage effective time management and consistent study habits.

Developed using **Kotlin, Jetpack Compose, and Material Design 3**, StudyFlow demonstrates modern Android application development practices, including reactive state management, lifecycle-aware ViewModels, separation of concerns, and external API integration through Retrofit.

---

## 1. Project Overview

### Purpose

The primary purpose of StudyFlow is to provide students with a practical time-management utility that presents essential study information at a glance.

Students frequently experience difficulties maintaining concentration, managing study duration, and taking appropriate breaks. StudyFlow addresses these challenges by providing a structured approach to study sessions inspired by the Pomodoro Technique.

The application emphasizes three fundamental design objectives:

- **Simplicity:** Provide an uncluttered interface that prioritizes essential timer functionality.
- **Usability:** Enable users to manage study sessions through minimal and intuitive interactions.
- **Personalization:** Allow users to adjust study durations, break intervals, and visual appearance according to their preferences.

### Target Users

StudyFlow is primarily designed for university students and independent learners who require a lightweight productivity tool to organize individual study sessions.

### Core Objectives

1. Develop a functional Android utility application using Kotlin and modern Android APIs.
2. Implement a responsive and visually consistent user interface using Jetpack Compose and Material Design 3.
3. Apply the Model-View-ViewModel (MVVM) architectural approach to separate UI components from application logic.
4. Integrate an external REST API to provide motivational content.
5. Deliver an intuitive user experience through clear visual feedback and accessible controls.

---

## 2. Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Kotlin
- Gradle
- Android emulator or physical Android device
- Internet connection for retrieving motivational quotes

**Minimum supported Android version:** API 26 (Android 8.0)

### How to Run

1. Clone or download this GitHub repository.
2. Open Android Studio.
3. Select **Open** and navigate to the project directory.
4. Allow Gradle to synchronize the project dependencies.
5. Select an Android emulator or connect a physical Android device.
6. Click **Run** to build and launch StudyFlow.

### Clone Repository

```bash
git clone https://github.com/<ayephyusinsoejcu>/MobileTech_UtilityApp.git
```

---

## 3. Core Application Features

### 3.1 Focus and Break Timer

The central functionality of StudyFlow is its customizable countdown timer, which supports structured study and break sessions.

**Implemented functionality:**

- Two selectable session modes: Focus and Break.
- Default focus duration of 25 minutes.
- Default break duration of 5 minutes.
- Start, pause, and reset controls.
- Real-time countdown display in minutes and seconds.
- Circular progress indicator displaying the remaining session proportion.
- Dynamic session status messages.

The timer uses `SystemClock.elapsedRealtime()` to calculate the remaining duration against a monotonic time reference. This approach reduces timing drift that could occur when relying exclusively on repeated delay intervals.

Countdown updates are coordinated through Kotlin Coroutines within the ViewModel.

### 3.2 Session Progress Tracking

StudyFlow includes a lightweight progress-tracking feature to help users monitor their completed study sessions.

**Implemented functionality:**

- Displays the number of completed focus sessions.
- Automatically increments the session counter when a focus session finishes.
- Updates the displayed count through reactive state management.

This provides immediate feedback while preserving the application's primary focus on essential, at-a-glance information.

### 3.3 Daily Motivation

StudyFlow incorporates a motivational quote feature to support a positive study experience.

**Implemented functionality:**

- Retrieves motivational quotes from an external REST API.
- Displays the retrieved quote and its author.
- Provides a refresh action to request another quote.
- Displays loading feedback during data retrieval.
- Handles unsuccessful API requests through an error state.
- Retains default motivational content as fallback information.

**API provider:** DummyJSON

**Base URL:**

```text
https://dummyjson.com/
```

**Endpoint:**

```text
GET /quotes/random
```

Networking is implemented through Retrofit, with Gson Converter responsible for JSON deserialization.

### 3.4 Customizable Settings

The Settings screen allows users to adjust the application according to their individual study preferences.

**Implemented functionality:**

- Configure focus-session duration.
- Configure break-session duration.
- Enable or disable dark mode.
- Apply updated settings to the relevant timer session.
- Navigate between Timer and Settings without complex navigation components.

Timer settings are maintained in the shared `TimerViewModel` during the application's active lifecycle.

Persistent storage using DataStore or SharedPreferences is not currently implemented.

### 3.5 Material Design 3 Interface

StudyFlow applies Material Design 3 principles to establish a consistent and user-friendly interface.

**Key interface elements:**

- Material Design 3 Scaffold.
- Bottom navigation bar.
- Elevated and rounded Cards.
- CircularProgressIndicator.
- Material Buttons and OutlinedButtons.
- Consistent typography and spacing.
- Custom light and dark color schemes.
- Scrollable layouts.

The interface prioritizes readability and visual hierarchy, ensuring that the remaining study time and essential controls are readily identifiable.

---

## 4. Application Architecture

StudyFlow follows a **Model-View-ViewModel (MVVM)-inspired architecture** to promote maintainability, separation of concerns, and clear data flow.

### Architecture Overview

```text
              MainActivity
                   |
               UtilityApp
                   |
          -------------------
          |                 |
     UtilityScreen     SettingsScreen
          |                 |
          -------------------
                   |
             TimerViewModel
                   |
              TimerUiState


            UtilityScreen
                   |
             QuoteViewModel
                   |
             QuoteRepository
                   |
             QuoteApiService
                   |
                Retrofit
                   |
          DummyJSON REST API
```

### 4.1 Presentation Layer

The presentation layer is implemented using Jetpack Compose.

The Composable functions are responsible for rendering application information, responding to user interactions, and observing state changes.

### 4.2 ViewModel Layer

The ViewModel layer manages application state and coordinates the business logic independently of the UI components.

**TimerViewModel**

Responsible for:

- Countdown timer execution.
- Session mode selection.
- Focus and break duration configuration.
- Timer start, pause, and reset operations.
- Completed-session tracking.
- Dark mode preferences.

**QuoteViewModel**

Responsible for:

- Requesting motivational quotes.
- Managing API loading states.
- Processing successful responses.
- Handling request failures.
- Exposing quote information to the user interface.

### 4.3 Repository Layer

`QuoteRepository` provides an abstraction between the ViewModel and external data source.

This separation enables networking responsibilities to remain independent of the presentation layer and supports future improvements to testability and maintainability.

### 4.4 Network Layer

`QuoteApiService` defines the REST API endpoint using Retrofit annotations.

The network layer retrieves JSON responses from DummyJSON and converts them into Kotlin data objects using Gson Converter.

### 4.5 Dependency Management

StudyFlow uses constructor-based dependency provision for `QuoteViewModel`, supported by a custom `ViewModelProvider.Factory`.

The Retrofit service and repository instances are created through lazy initialization.

A dedicated dependency injection framework such as Hilt is not currently implemented.

---

## 5. Composable Functions

### UtilityApp()

The primary application Composable manages the overall screen structure and navigation.

**Responsibilities:**

- Initializes the shared `TimerViewModel`.
- Observes application state using `collectAsStateWithLifecycle()`.
- Provides bottom navigation through Scaffold.
- Switches between Timer and Settings.
- Applies the selected Material Design theme.
- Connects user interactions to ViewModel functions.

### UtilityScreen()

Implements the main study timer interface.

**Responsibilities:**

- Displays Focus and Break session controls.
- Presents remaining countdown time.
- Displays timer progress visually.
- Provides Start, Pause, and Reset controls.
- Displays completed focus-session information.
- Integrates motivational quotes.

### SettingsScreen()

Implements the application configuration interface.

**Responsibilities:**

- Displays focus duration options.
- Displays break duration options.
- Provides dark mode configuration.
- Communicates user selections to `TimerViewModel`.
- Maintains consistency with Material Design 3.

### DailyMotivationCard()

Provides a dedicated interface component for displaying motivational content.

**Responsibilities:**

- Displays quotes and author information.
- Presents API loading feedback.
- Displays errors when a request fails.
- Allows users to request a new quote.

---

## 6. State Management and Lifecycle

StudyFlow uses `StateFlow` and lifecycle-aware state observation to support reactive UI updates.

### StateFlow

The ViewModels expose immutable UI state using `StateFlow`, while internal changes are managed through `MutableStateFlow`.

This provides a centralized source of truth for timer and quote information.

### Lifecycle-Aware Observation

The Compose interface observes ViewModel state using:

```kotlin
collectAsStateWithLifecycle()
```

This ensures state collection is coordinated with the Android lifecycle.

### Coroutines

Kotlin Coroutines support asynchronous operations such as:

- Countdown execution.
- Periodic timer state updates.
- External API requests.

Timer execution uses `viewModelScope` so countdown work is associated with the ViewModel lifecycle.

---

## 7. Key Concepts Covered

| Week | Technical Concept | Implementation |
|------|-------------------|----------------|
| 1 | Kotlin and Android Studio | MainActivity, Kotlin classes, enumerations, Gradle configuration |
| 2 | Jetpack Compose Layouts | UtilityApp(), UtilityScreen(), SettingsScreen(), Column, Row, Box |
| 3 | Material Design 3 | Scaffold, NavigationBar, Cards, Buttons, Typography, Light/Dark Themes |
| 4 | App Architecture | TimerViewModel, QuoteViewModel, StateFlow, Repository Pattern, ViewModel Factory |
| 5 | Web APIs using Retrofit | QuoteApiService, QuoteRepository, Retrofit, Gson Converter |

---

## 8. Technologies and Libraries

| Technology | Purpose |
|------------|---------|
| Kotlin | Application development |
| Android Studio | Development and debugging |
| Jetpack Compose | Declarative user interface |
| Material Design 3 | UI design components |
| Android ViewModel | Lifecycle-aware state management |
| StateFlow | Reactive state updates |
| Kotlin Coroutines | Asynchronous operations |
| Retrofit 2.11.0 | REST API communication |
| Gson Converter 2.11.0 | JSON deserialization |
| AndroidX Lifecycle | Lifecycle-aware state observation |
| Gradle | Build and dependency management |
| GitHub | Source control and development history |

---

## 9. Project Structure

```text
MobileTech_UtilityApp/
|
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── au/edu/jcu/assessment/utilityapp/
│   │       │       |
│   │       │       ├── MainActivity.kt
│   │       │       |
│   │       │       ├── network/
│   │       │       │   └── QuoteApiService.kt
│   │       │       |
│   │       │       ├── repository/
│   │       │       │   └── QuoteRepository.kt
│   │       │       |
│   │       │       ├── viewmodel/
│   │       │       │   ├── TimerViewModel.kt
│   │       │       │   └── QuoteViewModel.kt
│   │       │       |
│   │       │       └── ui/
│   │       │           └── theme/
│   │       │               ├── Color.kt
│   │       │               ├── Theme.kt
│   │       │               └── Type.kt
│   │       |
│   │       └── AndroidManifest.xml
│   |
│   └── build.gradle.kts
|
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

The project separates networking, repository logic, state management, and theme configuration into dedicated files.

This organization improves code readability and supports future development.

---

## 10. Design Decisions and Technical Considerations

### At-a-Glance User Experience

StudyFlow emphasizes immediate access to information through a prominent timer display, clearly distinguished session modes, and minimal interaction requirements.

This design supports the core purpose of utility applications by reducing unnecessary navigation and visual complexity.

### Separation of Concerns

Business logic is managed within ViewModels rather than directly within Composable functions.

Networking operations are handled through a dedicated Repository and Retrofit service.

This approach reduces coupling between interface components and data operations.

### Timer Accuracy

The timer calculates its remaining duration using `SystemClock.elapsedRealtime()` rather than simply subtracting one second after each delay.

This approach helps reduce cumulative countdown drift caused by coroutine scheduling delays.

### Reactive Interface

Application state changes are automatically reflected in the user interface through StateFlow and Compose recomposition.

This avoids the need for manual interface refresh operations.

### Error Handling

The quote feature handles network failures by updating its error state and allowing users to retry.

The timer remains independent of the quote service, preserving the application's primary utility when the external service is unavailable.

---

## 11. Current Limitations

Although StudyFlow implements its primary utility functions, several limitations remain:

- Study preferences are not persisted after the application process ends.
- Completed study sessions are tracked only in application memory.
- Timer execution is managed through a ViewModel rather than a background service.
- The application does not currently provide completion notifications.
- Automatic transitions between Focus and Break modes are not implemented.
- External quote retrieval requires an internet connection.
- A dedicated automated test suite for timer and API behavior has not yet been implemented.

These limitations identify opportunities for further improvements while maintaining a clearly defined scope for the current assessment.

---

## 12. Suggested Future Enhancements

Potential enhancements include:

- **Persistent preferences:** Integrate Jetpack DataStore to save user-selected settings.
- **Session history:** Store completed sessions using Room Database.
- **Study analytics:** Introduce daily and weekly productivity summaries.
- **Notifications:** Notify users when study or break sessions finish.
- **Automatic transitions:** Support configurable Focus and Break cycles.
- **Background timing:** Investigate appropriate Android lifecycle and background execution mechanisms.
- **Automated testing:** Add ViewModel unit tests and Compose UI tests.

These improvements would extend StudyFlow while preserving its core objective of providing an accessible, focused productivity utility.

---

## 13. Version Control and Development

GitHub is used to maintain the project's source code and document its development.

The repository provides access to the application source, Gradle configuration, dependencies, and project documentation.

Development commits should demonstrate incremental implementation of functionality, including UI development, timer logic, settings, architecture, networking, and refinements.

Clear and descriptive commit messages support traceability and communicate the evolution of the application.

---

## 14. Academic Context

**Institution:** James Cook University Singapore

**Subject:** CP3406 / CP5307 – Mobile Computing

**Assessment:** Assessment 1 – Utility App

**Application:** StudyFlow – Study Timer Utility

**Development Language:** Kotlin

**Development Environment:** Android Studio

**UI Framework:** Jetpack Compose with Material Design 3

---

## 📚 License

StudyFlow was developed for educational purposes as part of **CP3406 / CP5307 Assessment 1: Utility App** at James Cook University Singapore.

The project demonstrates the practical application of Android development principles, user interface design, reactive state management, MVVM-inspired architecture, and REST API integration.

Any third-party libraries, frameworks, and external services remain subject to their respective licences and terms of use.
