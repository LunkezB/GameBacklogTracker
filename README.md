# 🎮 GameBacklogTracker

Android-приложение для отслеживания игрового бэклога: помогает вести список игр в трёх статусах — **Backlog** (хочу пройти), **Playing** (играю сейчас) и **Completed** (пройдено), а также искать новые игры в онлайн-каталоге и добавлять их к себе.

## Возможности

- 📋 Три вкладки со списками игр по статусу: Backlog / Playing / Completed
- 🔍 Каталог игр — поиск и просмотр игр из внешнего API
- ℹ️ Экран деталей игры с описанием, жанром, разработчиком и датой выхода
- 💾 Офлайн-first хранение: данные кэшируются локально в Room и доступны без сети
- ⭐ Присвоение и изменение статуса игре одним нажатием

## Стек технологий

- **Kotlin**, **Jetpack Compose** (Material 3) — UI
- **Navigation Compose** — навигация между экранами
- **Room** — локальная база данных (SQLite)
- **Retrofit + OkHttp + Gson** — работа с сетевым API
- **Coil** — загрузка изображений
- **Kotlin Coroutines** — асинхронность
- Архитектура: **MVVM** + repository pattern (offline-first)

## Структура проекта

```
app/src/main/java/com/example/gamebacklogtracker/
├── data/
│   ├── local/         # Room: база данных, DAO, сущности
│   ├── remote/         # Retrofit API, DTO
│   └── repository/     # Репозитории (offline-first)
├── domain/
│   ├── model/          # Доменные модели (Game, GameStatus и т.д.)
│   └── mapper/          # Мапперы между слоями
└── ui/
    ├── navigation/       # Навиграф и Scaffold приложения
    ├── screens/          # Composable-экраны (Catalog, GameList, GameDetails)
    ├── theme/             # Тема Compose
    └── viewmodel/         # ViewModel'и экранов
```

## Требования

- Android Studio (Koala или новее)
- JDK 17
- Android SDK: `compileSdk 36`, `minSdk 24`, `targetSdk 36`

## Установка и запуск

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/<your-username>/GameBacklogTracker.git
   ```
2. Откройте проект в Android Studio (`File → Open`).
3. Дождитесь синхронизации Gradle.
4. Запустите конфигурацию `app` на эмуляторе или устройстве (Android 7.0+).

Либо через терминал:

```bash
./gradlew assembleDebug
```

APK-файл появится в `app/build/outputs/apk/debug/`.

## Лицензия

Учебный/пет-проект. Лицензия не определена — используйте на своё усмотрение.
