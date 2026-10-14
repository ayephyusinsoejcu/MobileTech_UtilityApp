# MobileTech Utility App – StudyFlow

**StudyFlow: A Smart Study Timer and Productivity Companion**

StudyFlow is an Android utility application developed for **CP3406 / CP5307 Assessment 1: Utility App** at James Cook University Singapore.

The application is designed to help students improve concentration, manage study sessions, and maintain a balanced study routine. By combining a customizable focus timer, break management, session tracking, academic calendar, and motivational quotes, StudyFlow provides essential productivity tools within a simple and intuitive interface.

Built using **Kotlin, Jetpack Compose, and Material Design 3**, the application demonstrates modern Android development practices, including lifecycle-aware state management, MVVM architecture, and REST API integration using Retrofit.

The primary objective is to deliver a practical, user-friendly utility that provides relevant information at a glance while minimizing unnecessary user interactions.

---

## Getting Started

### Prerequisites
- Android Studio
- Android SDK
- Kotlin and Gradle
- Android Emulator or physical Android device
- Internet connection for motivational quote retrieval

### How to Run
1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronize and install the required dependencies.
4. Select an Android emulator or connect a physical Android device.
5. Click **Run** to build and launch the application.
6. Navigate between the application's screens to explore its functionality.

---

## Core Features

### 1. Focus and Break Timer
- Provides dedicated Focus and Break modes to support structured study sessions.
- Uses a default 25-minute focus session and a 5-minute break.
- Allows users to start, pause, resume, and reset the countdown.
- Displays remaining time through a clear countdown interface.
- Includes a circular progress indicator for immediate visual feedback.
- Uses `SystemClock.elapsedRealtime()` to reduce countdown inaccuracies caused by scheduling delays.

### 2. Study Session Tracking
- Automatically records completed focus sessions.
- Displays the total number of completed sessions.
- Updates session information dynamically using StateFlow.
- Encourages consistent study habits through visible progress feedback.

### 3. Academic Calendar
- Provides a dedicated calendar interface for viewing academic information.
- Organizes calendar content using structured event data.
- Uses visual event categories to distinguish different academic activities.
- Helps students view relevant academic dates alongside their productivity tools.

### 4. Daily Motivation
- Retrieves motivational quotes from the DummyJSON REST API.
- Displays motivational messages and their authors.
- Allows users to refresh the displayed quote.
- Provides loading and error feedback during network requests.
- Includes default motivational content when an external quote cannot be retrieved.

### 5. Customizable Settings
- Allows users to adjust focus and break durations.
- Provides a dark mode option for different viewing preferences.
- Applies relevant duration changes to the timer.
- Maintains settings through the ViewModel during the active application lifecycle.

### 6. Intuitive User Interface
- Implements Material Design 3 components.
- Provides navigation between the application's main sections.
- Uses Cards, Buttons, and progress indicators to establish a clear visual hierarchy.
- Supports light and dark appearance.
- Prioritizes essential information and straightforward interactions.

---

## Composables

### UtilityApp()
- Acts as the primary application interface.
- Uses Scaffold to organize application content and navigation.
- Coordinates the main screen layout.
- Observes application state through lifecycle-aware state collection.
- Applies the selected Material Design theme.

### UtilityScreen()
- Displays the primary study timer interface.
- Provides Focus and Break session controls.
- Shows remaining time and countdown progress.
- Includes Start, Pause, and Reset functionality.
- Displays completed study sessions and motivational content.

### CalendarScreen()
- Displays the academic calendar interface.
- Presents academic events and related information.
- Uses structured calendar data to organize the displayed content.

### SettingsScreen()
- Provides configuration options for focus and break durations.
- Includes the dark mode preference.
- Sends user-selected settings to the TimerViewModel.
- Keeps the settings interface consistent with the main application design.

### DailyMotivationCard()
- Displays motivational quotes obtained through the API.
- Presents author information.
- Handles loading and error states.
- Allows users to request another motivational quote.

### DurationOption()
- Provides selectable timer duration options.
- Highlights the selected duration.
- Supports reusable and consistent settings controls.

---

## Application Architecture

StudyFlow follows an **MVVM (Model-View-ViewModel) architecture**, supported by a Repository pattern to separate interface presentation, application state, business logic, and network operations.

### TimerViewModel
- Manages countdown execution and timer state.
- Controls Focus and Break session modes.
- Handles timer operations and duration settings.
- Tracks completed focus sessions.
- Maintains dark mode preferences.
- Exposes immutable UI state through StateFlow.

### QuoteViewModel
- Manages motivational quote retrieval.
- Coordinates requests through QuoteRepository.
- Maintains loading, success, and error states.
- Exposes quote information to the Compose interface.

### QuoteRepository
- Separates external data retrieval from presentation logic.
- Communicates with QuoteApiService.
- Provides a dedicated data access layer for motivational quotes.

### QuoteApiService
- Defines the external REST API endpoint.
- Uses Retrofit to perform HTTP requests.
- Uses Gson Converter to deserialize JSON responses into Kotlin data objects.

### State Management
- Uses `MutableStateFlow` for internal state updates.
- Exposes state through `StateFlow`.
- Uses `collectAsStateWithLifecycle()` to observe state in Compose.
- Uses Kotlin Coroutines for asynchronous operations.

This architecture improves code organization, supports separation of concerns, and makes the application easier to maintain and extend.

---

## API Integration

StudyFlow integrates an external REST API to enhance its utility beyond local timer functionality.

**API Provider:** DummyJSON

**API Endpoint:** `https://dummyjson.com/quotes/random`

**HTTP Method:** GET

**Implementation:**
- Retrofit handles communication with the external API.
- Gson Converter transforms JSON responses into Kotlin objects.
- QuoteRepository manages the data request.
- QuoteViewModel coordinates API results and interface state.
- The UI handles loading and unsuccessful requests.

The quote feature is independent of the countdown functionality, allowing the main study timer to remain usable without network connectivity.

---

## Key Concepts Covered

| Week | Concept | Implementation |
|------|---------|----------------|
| 1 | Kotlin and Android Studio | MainActivity.kt, Kotlin classes, Gradle |
| 2 | Jetpack Compose Layouts | UtilityApp(), UtilityScreen(), CalendarScreen(), SettingsScreen() |
| 3 | Material Design 3 | Scaffold, Cards, Navigation, Buttons, Typography, Light/Dark Theme |
| 4 | App Architecture | TimerViewModel, QuoteViewModel, StateFlow, Repository Pattern, ViewModel Factory |
| 5 | Web APIs using Retrofit | QuoteApiService, QuoteRepository, Retrofit, Gson Converter |

---

## Technologies Used

| Technology | Purpose |
|------------|---------|
| Kotlin | Core application development |
| Android Studio | Development and debugging environment |
| Jetpack Compose | Declarative user interface |
| Material Design 3 | UI design and components |
| Android ViewModel | Lifecycle-aware state management |
| StateFlow | Reactive state updates |
| Kotlin Coroutines | Asynchronous timer and networking operations |
| Retrofit | REST API communication |
| Gson Converter | JSON deserialization |
| AndroidX Lifecycle | Lifecycle-aware UI state observation |
| Gradle | Build and dependency management |
| GitHub | Version control and project documentation |

---

## Project Structure

```text
MobileTech_UtilityApp/
│
├── app/
│   └── src/main/
│       ├── java/au/edu/jcu/assessment/utilityapp/
│       │   │
│       │   ├── MainActivity.kt
│       │   │
│       │   ├── network/
│       │   │   └── QuoteApiService.kt
│       │   │
│       │   ├── repository/
│       │   │   └── QuoteRepository.kt
│       │   │
│       │   ├── viewmodel/
│       │   │   ├── TimerViewModel.kt
│       │   │   └── QuoteViewModel.kt
│       │   │
│       │   └── ui/theme/
│       │       ├── Color.kt
│       │       ├── Theme.kt
│       │       └── Type.kt
│       │
│       └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

The project uses dedicated packages for network communication, repository logic, ViewModels, and theme configuration. The primary Composable screens are currently implemented within MainActivity.kt.

---

## Design and Implementation Decisions

### User-Centred Design
StudyFlow prioritizes essential study information through a clear countdown display, accessible controls, and limited interaction requirements. This approach supports the utility-app principle of providing useful information at a glance.

### Accurate Timer Management
The countdown uses `SystemClock.elapsedRealtime()` to calculate the remaining duration relative to a monotonic clock. This provides more reliable timing than simply reducing a counter after every coroutine delay.

### Separation of Concerns
Application logic is managed through ViewModels, while networking responsibilities are separated into Repository and API service components. This reduces unnecessary dependencies between UI and data operations.

### Reactive UI Updates
StateFlow allows changes in timer status, session counts, and settings to be reflected automatically in the Compose interface.

### Resilient API Integration
The motivational quote feature includes loading and error handling. Network failures do not prevent users from accessing the application's core timer functionality.

---

## Current Limitations

- Timer preferences and session counts are not stored persistently.
- The countdown does not currently provide background-service functionality.
- Study session history and analytics are not implemented.
- Automatic Focus-to-Break transitions are not included.
- Motivational quotes require an internet connection for fresh content.
- Dedicated automated tests for timer behavior and API handling have not yet been implemented.

These limitations define the current application scope and provide clear opportunities for further development.

---

## Suggested Future Enhancements

- Implement DataStore to persist user preferences.
- Add Room Database for study history.
- Introduce daily and weekly study analytics.
- Provide notifications when study sessions finish.
- Implement automatic Focus and Break transitions.
- Add customizable session targets.
- Improve automated testing for ViewModels and UI components.
- Extend calendar functionality with user-created study reminders.

---

## Version Control

GitHub is used to manage the application source code and document incremental development.

Development is organized around the implementation of core functionality, user interface improvements, state management, API integration, and settings configuration.

Meaningful commits provide a record of project evolution and support maintainability and traceability.

---

## Academic Information

**Institution:** James Cook University Singapore

**Subject:** CP3406 / CP5307 – Mobile Computing

**Assessment:** Assessment 1 – Utility App

**Application Name:** StudyFlow

**Development Language:** Kotlin

**UI Framework:** Jetpack Compose and Material Design 3

**Development Environment:** Android Studio

---

## 📚 License

StudyFlow is developed for educational purposes as part of **CP3406 / CP5307 Assessment 1: Utility App** at James Cook University Singapore.

The project demonstrates the application of modern Android development principles, including Jetpack Compose, Material Design 3, ViewModel-based architecture, reactive state management, and REST API integration.

Third-party libraries and services remain subject to their respective licences and terms of use.3. Apply the Model-View-ViewModel (MVVM) architectural approach to separate UI components from application logic.
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
