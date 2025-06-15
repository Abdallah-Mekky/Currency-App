# 💱 Currency_App

Please ⭐️ this repository if you find it useful and share it with others!

## 📄 Description

- First screen (convert currencies from different bases)
1. From/To dropdown lists showing all available currencies
2. The input field for amount (numbers only), by default always 1, and
another input field to show the converted value.
3. When user change the amount, converted value changed
4. Button to swap the values in FROM and TO
  
- Second Screen (The history of currency)
1. the historical data for my FROM/TO selections in last 4 days (day by day)
2. Historical data for last 4 days viewed in a list

### The Challenge

Build an Android application that allows users to:

1. View real-time currency exchange rates.
2. Convert between currencies with a clean and responsive UI.
3. Save currency calculations and view history.
4. Fetch latest data from a remote API using Retrofit.
5. Use local caching with Room database.
6. Follow modern Android architecture principles.

### Key Features

- 📊 Live currency rates.
- 🔄 Currency conversion.
- 💾 Offline support with local caching.
- 🎨 Responsive layout with SSP & SDP.
- 📤 Clean architecture with MVVM pattern.
- ☕ Dependency injection with Hilt.

---

## 📷 Screenshots

<div>
  <img src="https://github.com/user-attachments/assets/4f8ba9b4-f098-4970-922c-82eb56967e14" width="250">
  <img src="https://github.com/user-attachments/assets/1535d239-459a-4051-af5e-421096a27ce2" width="250">
</div>

---

## 🧰 Tech Stack

### 🔧 Core Language & Frameworks
- [Kotlin](https://kotlinlang.org/)
- [Android Jetpack](https://developer.android.com/jetpack)

### 🧱 Architecture & Patterns
- [MVVM](https://developer.android.com/topic/architecture)
- [Clean Architecture](https://developer.android.com/topic/architecture)

### 📚 Libraries & Tools

| Category              | Libraries                                                                 |
|-----------------------|---------------------------------------------------------------------------|
| UI Components         | `Material`, `ConstraintLayout`, `RecyclerView`, `Navigation Component`    |
| Dependency Injection  | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) |
| Database              | [Room](https://developer.android.com/jetpack/androidx/releases/room)      |
| Networking            | [Retrofit](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/) |
| JSON Serialization    | [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)  |
| State Management      | `ViewModel`, `SharedFlow`, `StateFlow`                                      |
| Asynchronous Tasks    | [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)        |
| Data Storage          | [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) |
| Responsive Design     | [SSP](https://github.com/intuit/ssp), [SDP](https://github.com/intuit/sdp) |

---

## 🧮 API

- [Fixer.io](https://fixer.io) - Free foreign exchange rates and currency conversion API.

### 🔗 Endpoints

- `/latest` — Get the latest exchange rates.

---

## 🧭 Code Architecture

<p align="center">
  <img src="https://user-images.githubusercontent.com/86564639/166422026-4a5f4f9b-44b6-44c7-b4c6-852be532b41f.png" width="600">
</p>

<p align="center">
  <img src="https://github.com/android10/Sample-Data/blob/master/Android-CleanArchitecture/clean_architecture.png?raw=true" width="600">
</p>

---

## 📂 Folder Structure

```bash
├── data
│   ├── model
│   ├── repository
│   └── source
├── domain
│   ├── model
│   ├── usecase
│   └── repository
├── presentation
│   ├── ui
│   ├── utils
│   └── adapter
├── di
├── core
