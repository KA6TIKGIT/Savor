# Savor

Savor is a food explorer for Android. Search millions of products from the [Open Food Facts](https://world.openfoodfacts.org) database, see photos and Nutri-Score ratings, save favorites, and write personal reviews.

Built with Kotlin and Jetpack Compose.

## Features

- **Search** food products by name (for example "pizza" or "chocolate")
- **Product details**: photo, brand, Nutri-Score grade, categories, ingredients
- **Favorites**: save products and view them offline
- **Personal reviews**: rate each product from 1 to 5 stars and write notes, stored on the device
- **Settings**: dark theme and sort order (default, name, Nutri-Score)
- **Remembers your last search** between launches
- **Open in browser** to view the product on Open Food Facts
- **Share by email** to send a product to a friend

## Tech stack

| Requirement | Implementation |
|---|---|
| Retrofit | Calls the Open Food Facts search API (`SearchApi`) and product API (`OpenFoodFactsApi`), with Gson and an OkHttp logging interceptor |
| Glide | Loads product images through the Glide Compose integration (`ProductImage`) |
| Room | Local database with a `favorites` table and a `reviews` table |
| SharedPreferences | `SettingsStore` keeps the theme, sort order and last search |
| Resources | Strings, colors and dimensions are defined in `res/values` |
| Implicit intents | `ACTION_VIEW` opens the browser, `ACTION_SENDTO` opens an email app |

## Architecture

```
ui/            Compose screens, ViewModel, intent helper
data/
  remote/      Retrofit client, API interfaces, response models
  local/       Room database, DAOs, entities, SharedPreferences store
  repository/  SavorRepository joins remote and local data
```

The `SavorViewModel` exposes state as `StateFlow` and the Compose screens collect it. The repository retries searches up to two times when the server returns a 503 or the network drops.

## Setup

1. Clone the repository and open it in Android Studio.
2. Let Gradle sync. The project uses AGP 9 with built-in Kotlin, KSP and Room 2.8.0.
3. Run the app on an emulator or device with internet access.

`gradle.properties` contains `android.disallowKotlinSourceSets=false`. KSP needs it to register Room's generated code under AGP 9's built-in Kotlin.

## API notes

- Text search uses Search-a-licious at `https://search.openfoodfacts.org`.
- Open Food Facts rate-limits requests (about 10 searches per minute per IP) and answers with HTTP 503 when a limit is exceeded. If a search fails, wait a minute and try again.
- Some products have no photo or no Nutri-Score. The app shows a placeholder or hides the badge.

## Data attribution

Product data comes from [Open Food Facts](https://world.openfoodfacts.org) and is available under the [Open Database License](https://opendatacommons.org/licenses/odbl/1-0/). Product images are licensed under CC BY-SA.

## Project structure notes

- Package: `com.example.savor`
- Min screens: Explore, Favorites, Detail
- Navigation: Navigation Compose with a bottom bar
