# demo-news

News app: categories -> paginated news list -> article details.

## Stack

Kotlin, Coroutines/Flow, Jetpack Compose, Hilt, Retrofit + Moshi,
Navigation Compose, manual pagination, Mokkery for tests.

## Architecture

Three layers: `presentation -> domain <- data`.

- **domain** - pure Kotlin: models, reposirtory interfaces, use cases,
  `Outcome<AppError, T>` for typed errors.
- **data** - Retrofit, DTOs with the `{ "code": 0, "list": [...] }` envelope,
  mappers, repository implementations. Maps `HttpException`/`IOException`
  to `AppError`, rethrows `CancellationException`.
- **presentation** - Compose screens + Flow-first ViewModels. Manual pagination
  via `Paginator<T>` + `PagingState<K, I>`.

Key decisions:
- Errors are typed values (`Outcome`), not exceptions - no `try-catch` in ViewModels.
- Pagination is a suspend reducer (`Paginator.loadNext`), no `Job?.cancel()`.
- HTML content via `HtmlText` (AndroidView + `HtmlCompat`) with clickable links.

## How to run

1. Add the API base URL to `local.properties` (gitignored):
   ```properties
   DEMO_NEWS_DEV_BASE_URL=https://your-test-api/
   DEMO_NEWS_PROD_BASE_URL=https://your-prod-api/
   ```
2. Testing
    ```bash
    ./gradlew :app:testDevDebugUnitTest
   ```
   40+ unit tests covering use cases, repositories (exception mapping
   CancellationException rethrow, null payload), ViewModels (pagination states,
   retry), and mappers.

   Not unit-tested by design: Compose screens (UI tests), DTOs (Moshi codegen),
   Hilt modules (generated code).

## Screenshots

| Categories | News list | Details                                      |
|---|---|----------------------------------------------|
| ![categories](docs/screenshots/categories.png) | ![list](docs/screenshots/newslist.png) | ![details](docs/screenshots/newsdetails.png) |