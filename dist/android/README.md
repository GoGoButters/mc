# Живой мир — Android (Zalith Launcher 2)

`ZhivoyMir-Android.mrpack` — мобильная версия клиента для импорта в Zalith Launcher 2
(«Импорт модпака» → файл .mrpack). Архив в git не хранится — собирается командой ниже.
Устарел: основной путь — APK с синхронизацией packwiz-пака `android/` (ниже); `android-check.py`
проверяет mrpack только с флагом `--mrpack`.

## Пересборка

```bash
python3 tools/android-export.py   # → dist/android/ZhivoyMir-Android.mrpack, сам прогоняет depcheck
python3 tools/android-check.py --mrpack   # валидность modrinth.index.json и сверка списка модов
```

jar берутся из кэша depcheck (`/tmp/depcache`); недостающие CurseForge-jar — `../tools/cf-cache-sync.sh`.

## Пак для лаунчера

Android-лаунчер при каждом запуске синхронизируется с packwiz-паком
**https://gogobutters.github.io/mc/android/pack.toml** (как ПК-клиент с `/mc/pack.toml`).
Это папка `android/` в корне репо: копии `*.pw.toml` клиентского набора без `EXCLUDE`, config/kubejs/servers.dat
без `EXCLUDE_FILES`, мобильные `options.txt` и `config/voicechat/voicechat-client.properties`
(`preserve = true` — синхронизация не затирает настройки игрока). Основной пак её не видит (`.packwizignore`).

**Синхронизацию делает `../tools/deploy-test.sh`** сам: после `packwiz refresh`, если вне `android/` всё
закоммичено, он запускает команды ниже и коммитит `android/` («Android-пак: синхронизация с основным»).
Вручную (без деплоя):

```bash
python3 tools/android-export.py --pack-only   # пересоздаёт android/ (без mrpack и загрузок jar)
python3 tools/android-check.py                # моды android/ = клиентский набор, хэши индекса сходятся
git add android && git commit -m "android: синхронизация пака" -- android
```

Публикация — тот же `git push` в main (Pages отдаёт корень ветки), например через `../tools/deploy-test.sh`.
Проверка: `java -jar packwiz-installer-bootstrap.jar -g -s client https://gogobutters.github.io/mc/android/pack.toml`
в пустой папке.

## Что внутри

- Моды side=client|both из packwiz-сборки. С Modrinth — ссылками в `modrinth.index.json`;
  CurseForge-моды и наши jar (zmclient, zmtweaks) — целиком в `overrides/mods`.
- `config/`, `kubejs/` (в т.ч. русификация), `servers.dat` — как у десктопа.
- `options.txt` — мобильный: дальность 8, симуляция 6, графика «быстрая», облака выкл,
  частицы «меньше», 60 FPS, GUI 3; клавиши как на десктопе.
- `config/voicechat/voicechat-client.properties`: `use_natives=false`, `denoiser=false`
  (нативных opus/rnnoise под Android нет), активация голосом.

## Кнопки

`ZhivoyMir-controls.json` — раскладка экранных кнопок Zalith Launcher 2 (2.8.x) под планшет 2944×1840:
стандартная раскладка Zalith + слой «Живой мир». Генерируется `tools/android-export.py`
(`--controls` — только её), клавиши берутся из `options.txt` сборки; проверка — `android-check.py`.

| Кнопка | Клавиша | Где |
|---|---|---|
| Жители | Caps Lock, удерживаемая: говорите с жителями, пока зажата | справа, над ЛКМ/ПКМ |
| Голос | V — меню Simple Voice Chat | верхний ряд |
| Карта | M — полноэкранная карта JourneyMap | верхний ряд |
| Рюкзак / Рюкзак: упр. | B / Shift+B | верхний ряд |
| Чат | T (бывшая «T» стандартной раскладки) | слева под Esc |

Кнопки «Квесты» нет: у FTB Quests нет клавиши открытия — книга квестов открывается из инвентаря.
Кнопка GUI (правый верхний угол) прячет и показывает все слои, включая наш.

1. Скопируйте `ZhivoyMir-controls.json` на планшет (в «Загрузки»).
2. Zalith → Настройки → «Раскладки» → «Импорт раскладки» → выберите файл.
3. Выберите её в настройках версии «Живой мир» → «Раскладка управления» (или сделайте раскладкой по умолчанию).
4. Подвинуть кнопки — редактор раскладки Zalith; при пересборке файла правки затрутся, лучше сохранить копию.

## Перенос пароля

Nedologin сам придумывает пароль при первом входе и хранит его открытым текстом в файле `.nl_password`
в папке игры. Пароль не привязан к компьютеру и серверу — достаточно, чтобы на планшете лежал тот же файл.

1. На Windows найдите файл (зависит от варианта установки из `ZhivoyMir-Setup`):
   - «Всё в одном»: `%LOCALAPPDATA%\ZhivoyMir\instances\ZhivoyMir\.minecraft\.nl_password`
   - Prism: `%APPDATA%\PrismLauncher\instances\ZhivoyMir\.minecraft\.nl_password`
   - PolyMC: `%APPDATA%\PolyMC\instances\ZhivoyMir\.minecraft\.nl_password`
2. Передайте его на планшет (Telegram, почта, USB) — файл целиком, имя `.nl_password` (с точкой, без `.txt`).
3. Хотя бы раз запустите сборку на планшете, закройте игру.
4. На планшете: приложение «Файлы» → боковое меню → «Zalith Launcher 2» → `android_data/files/.minecraft/versions/<имя версии>/`
   (полный путь: `/storage/emulated/0/Android/data/com.movtery.zalithlauncher.v2/files/.minecraft/versions/<имя версии>/`,
   `<имя версии>` — имя, заданное при импорте; у версии должна быть включена изоляция, при импорте модпака она по умолчанию).
   Положите туда `.nl_password` с заменой.
5. Запустите игру и зайдите на сервер под тем же ником.

Если файл положить не получается: откройте `.nl_password` на Windows Блокнотом, перешлите текст себе на планшет,
в игре на титульном экране нажмите кнопку «P» рядом с «Сетевой игрой» (если её видно в меню сборки) и вставьте пароль.
До переноса планшет пишет в `.nl_password` свой случайный пароль и сервер его не пускает — это не вредит, файл просто заменяется.
`/nl change_password <новый>` меняет пароль на сервере и в файле только того устройства, где введена команда —
после неё файл придётся переносить заново.

## Исключено

| Мод | Почему |
|---|---|
| Distant Horizons (+ `config/DistantHorizons.toml`, его русификация) | тяжёлый LOD-рендер, на мобильном GPU (MobileGlues/GL ES) не работает или сильно тормозит |

Шейдеров/Iris в сборке нет. Зависимых от DH модов нет (depcheck проходит).
Кандидат на исключение после тестов: Sound Physics Remastered.
Список исключений — `EXCLUDE` / `EXCLUDE_FILES` в `tools/android-export.py`.
