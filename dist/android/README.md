# Живой мир — Android (Zalith Launcher 2)

`ZhivoyMir-Android.mrpack` — мобильная версия клиента для импорта в Zalith Launcher 2
(«Импорт модпака» → файл .mrpack). Архив в git не хранится — собирается командой ниже.

## Пересборка

```bash
python3 tools/android-export.py   # → dist/android/ZhivoyMir-Android.mrpack, сам прогоняет depcheck
python3 tools/android-check.py    # валидность modrinth.index.json и сверка списка модов
```

jar берутся из кэша depcheck (`/tmp/depcache`); недостающие CurseForge-jar — `../tools/cf-cache-sync.sh`.

## Что внутри

- Моды side=client|both из packwiz-сборки. С Modrinth — ссылками в `modrinth.index.json`;
  CurseForge-моды и наши jar (zmclient, zmtweaks) — целиком в `overrides/mods`.
- `config/`, `kubejs/` (в т.ч. русификация), `servers.dat` — как у десктопа.
- `options.txt` — мобильный: дальность 8, симуляция 6, графика «быстрая», облака выкл,
  частицы «меньше», 60 FPS, GUI 3; клавиши как на десктопе.
- `config/voicechat/voicechat-client.properties`: `use_natives=false`, `denoiser=false`
  (нативных opus/rnnoise под Android нет), активация голосом.

## Исключено

| Мод | Почему |
|---|---|
| Distant Horizons (+ `config/DistantHorizons.toml`, его русификация) | тяжёлый LOD-рендер, на мобильном GPU (MobileGlues/GL ES) не работает или сильно тормозит |

Шейдеров/Iris в сборке нет. Зависимых от DH модов нет (depcheck проходит).
Кандидат на исключение после тестов: Sound Physics Remastered.
Список исключений — `EXCLUDE` / `EXCLUDE_FILES` в `tools/android-export.py`.
