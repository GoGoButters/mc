# Живой мир — AI Colony Pack

Сборка Minecraft **1.21.1 / NeoForge 21.1.265**, центр — MineColonies, ИИ-жители (Living Colonists, MCA Reborn), войны и торговля колоний.

Формат — [packwiz](https://packwiz.infra.link/). Моды не хранятся в репозитории: здесь только ссылки на Modrinth/CurseForge.

## Установка клиента

**Проще всего — установщик (Windows):** [`dist/ZhivoyMir-Setup.exe`](dist/ZhivoyMir-Setup.exe) — спрашивает ник, сам создаёт сборку в Prism/MultiMC (или ставит моды в вашу папку игры) и включает автообновление.
Портативно: [`dist/ZhivoyMir-Portable.zip`](dist/ZhivoyMir-Portable.zip). Подробно — [`docs/CLIENT.md`](docs/CLIENT.md).

Вручную:

1. Minecraft **1.21.1** + **NeoForge 21.1.265** в вашем лаунчере.
2. Положите [`packwiz-installer-bootstrap.jar`](https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar) в папку игры (`.minecraft` сборки).
3. Команда перед запуском (pre-launch command):

   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://gogobutters.github.io/mc/pack.toml
   ```

4. Память: 6–8 ГБ.

При каждом запуске моды сверяются со сборкой и обновляются автоматически.

## Сервер

`globe.r2d2.dedyn.io`
