#### **README: Exercise 4b (The Shared Bridge)**

# 🌉 Exercise 4b: Shared UI & State

**Module:** Shared UI with Compose Multiplatform

**Estimated Time:** 25 Minutes

### **🎓 Objective**

In this exercise, you will implement a **Shared ViewModel** and a **Shared UI Screen**. Instead of writing separate code for Android and iOS, you will write the logic and the UI once in `commonMain`. Both platforms will immediately display the data fetched from your Gist.

### **✅ Task 1: The Shared ViewModel**

We need a state holder that can survive configuration changes and manage background tasks.

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`.
2. **Action**: Define a `MutableStateFlow` to hold the list of `Deck` objects.
3. **Action**: In the `init` block (or a `loadDecks` function), launch a Coroutine.
4. **Logic**: Call `apiService.fetchAllDecks()` and update the state flow.
* *Hint: Remember to use `Dispatchers.Main` for UI-related state updates.*



### **✅ Task 2: The Compose Multiplatform UI**

Now let's render the data using Jetpack Compose, which now runs on iOS too!

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/App.kt`.
2. **Action**: Inside the `App()` composable, initialize your `MainViewModel`.
* *Note: You can use Koin's `koinInject()` if configured, or manually pass the service.*


3. **Action**: Collect the stream:
```kotlin
val decks by viewModel.decks.collectAsState()

```


4. **Action**: Replace the placeholder Text with a `LazyColumn`.
5. **Design**: For each deck, create a simple `Row` showing the **Deck Title** and the **Card Count** (e.g., "Geography (50 cards)").

### **✅ Task 3: Verify Both Platforms**

1. **Run on Android**: Launch the `composeApp` configuration on an Android Emulator.
2. **Run on iOS**: Launch the `iosApp` configuration on an iPhone Simulator.
3. **Goal**: Both devices should show the exact same list of 10 decks.
