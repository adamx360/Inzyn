# Dokumentacja Techniczna

## Informacje ogólne
Aplikacja jest oparta na platformie Android i korzysta z Firebase jako backendu. Zawiera funkcje do zarządzania planami treningowymi, ćwiczeniami i seriami. Obejmuje integrację z Firebase Authentication i Realtime Database, a także funkcjonalności offline.

---

## Struktura projektu

### Główne komponenty:
- **Model** - reprezentacje danych (np. `Exercise`, `Plan`, `Set`, `User`).
- **Widoki (Fragmenty)** - interfejs użytkownika (np. `AddExerciseFragment`, `ListFragment`).
- **ViewModel** - logika biznesowa i pośredniczenie między danymi a widokami (np. `AddExerciseViewModel`, `PlanViewModel`).
- **Repozytoria** - obsługa zapytań do Firebase (np. `ExerciseRepository`, `PlanRepository`, `SetRepository`).
- **Navigation** - klasy do obsługi nawigacji między fragmentami.

---

## Klasy i ich funkcjonalności

### Modele:
#### `Exercise`
- **Pola:**
  - `id: String` - unikalne ID ćwiczenia.
  - `name: String` - nazwa ćwiczenia.
  - `description: String` - opis ćwiczenia.

#### `Plan`
- **Pola:**
  - `id: String` - unikalne ID planu.
  - `name: String` - nazwa planu.
  - `exercisesIDs: List<String>` - lista ID ćwiczeń w planie.

#### `Set`
- **Pola:**
  - `id: String` - unikalne ID serii.
  - `exerciseID: String` - ID ćwiczenia powiązanego z serią.
  - `weight: Double` - ciężar.
  - `reps: Int` - liczba powtórzeń.
  - `date: String` - data wykonania serii.
  - `description: String?` - opis serii.

---

### Fragmenty:
#### `AddExerciseFragment`
- Formularz do dodawania lub edycji ćwiczenia.
- **Metody:**
  - `saveFormData()` - zapisuje dane ćwiczenia.

#### `ListFragment`
- Lista ćwiczeń.
- **Metody:**
  - `showExerciseStatisticsDialog()` - wyświetla statystyki ćwiczenia.
  - `showDaySelectionDialog()` - umożliwia przypisanie ćwiczenia do planu.

#### `CalendarFragment`
- Wyświetla listę serii na dany dzień.

#### `AddPlanFragment`
- Formularz do dodawania lub edycji planu.

---

### ViewModel:
#### `AddExerciseViewModel`
- Obsługuje dodawanie i edytowanie ćwiczeń.
- **Metody:**
  - `init(id: String?)` - inicjalizuje dane dla edycji.
  - `onSave()` - zapisuje lub aktualizuje ćwiczenie.

#### `AddPlanViewModel`
- Obsługuje dodawanie i edytowanie planów.
- **Metody:**
  - `init(planId: String?)` - inicjalizuje plan.
  - `onSave()` - zapisuje lub aktualizuje plan.

#### `CalendarViewModel`
- Zarządza seriami wyświetlanymi w kalendarzu.

---

### Repozytoria:
#### `ExerciseRepository`
- **Metody:**
  - `add(userId: String, exercise: Exercise)` - dodaje nowe ćwiczenie.
  - `removeById(userId: String, exerciseId: String)` - usuwa ćwiczenie.

#### `PlanRepository`
- **Metody:**
  - `add(userId: String, plan: Plan)` - dodaje nowy plan.
  - `getPlanById(userId: String, planId: String): Plan?` - pobiera plan po ID.

#### `SetRepository`
- **Metody:**
  - `add(userId: String, set: Set)` - dodaje nową serię.
  - `getSetList(userId: String, database: DatabaseReference): List<Set>` - pobiera listę serii.

---

## Firebase
- **Authentication:** Firebase Authentication zarządza użytkownikami aplikacji.
- **Realtime Database:** Przechowuje dane o ćwiczeniach, planach i seriach.

---

## Dodatkowe funkcjonalności
- **Obsługa offline:** Firebase Realtime Database umożliwia zapis danych lokalnie w trybie offline.
- **Zarządzanie językiem i motywem:** Możliwość zmiany języka oraz przełączania między trybem jasnym i ciemnym.
- **Statystyki treningowe:** Obliczanie łącznej liczby serii, objętości treningowej i ulubionego ćwiczenia.

---

## Wymagania środowiskowe
- Android Studio Arctic Fox (lub nowszy).
- Minimum SDK: 21.
- Firebase Realtime Database i Authentication.

---