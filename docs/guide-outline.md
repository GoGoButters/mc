# Справочник «Живой мир» — проект оглавления (этап 1, черновик на утверждение)

Сопутствующий файл: `docs/guide-inventory.md` — опись 165 модов (80 контент / 26 интерфейс-удобство / 59 библиотеки-оптимизации), modid, руководства, стартовые точки.

Книга: GuideME `zhivoymir:start` (kubejs/assets/zhivoymir/guides/zhivoymir/start/**). Разделы — подпапки, у каждой свой index-страница с intro «в голосе летописи» и `<SubPages />`. Нынешние 15 страниц распределены по разделам (переезжают без потери контента).

**Итого: 59 страниц** (40–60 по плану). Из них переносится 15 существующих, новых — 44.

---

## 0. С чего начать (start/, 2 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (сущ.) | Летопись странника | Точка входа, «Твой путь», `<SubPages />` | — |
| `first-steps.md` (сущ.) | Первые шаги | Первые 1–2 ч: деревня → стартовый лагерь → бог, `/guide` | minecolonies, mca, zmgod |

## 1. Карта пути (path/, 5 стр.)

Обзор-страница + четыре «пути». Ступени — только то, что подтверждается рецептами/фактами (источники в docs/guide-facts.md).

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` | Карта пути | Матрица: 4 пути × 3 стадии (ранняя/средняя/поздняя), перекрёстки между путями | все |
| `engineer.md` | Путь инженера | **Ранняя**: печка → металлургический инфузер **mekanism:metallurgic_infuser** (рецепт из jar: печка+железо+осмий+редстоун ✓) → энергокамера mekanism:enrichment_chamber (на стальном каркасе) → Create (гаечный ключ create:wrench ✓, рукоятка) → **Средняя**: AE2 — зарядник **ae2:charger** (рецепт: железо+медь ✓) → inscriber ae2:inscriber ✓ → ME-сеть → **Поздняя**: PNC (компрессор pneumaticcraft:air_compressor ✓: укреп. кирпичи+печка+труба) → Railcraft → CC:Tweaked → интеграции (applied-mekanistics, colonylink) | create, mekanism, ae2, pneumaticcraft, railcraft, cc-tweaked, buildcraft, applied-mekanistics, colonylink, numismatics |
| `ruler.md` | Путь правителя | **Ранняя**: деревня MCA → стартовый лагерь minecolonies:supplycampdeployer ✓ → ратуша → **Средняя**: рабочие и исследования → экономика (рынок mc-trade-post) → **Поздняя**: налоги и войны (WNT, кодекс minecolonytax:war_taxes_codex ✓) → монархия (mcacapitals) | mca, minecolonies, zmbridge, wnt, mcacapitals, mctradepost, livingvoice |
| `adventurer.md` | Путь искателя | **Ранняя**: разведка, вехи waystones:waystone ✓ → **Средняя**: структуры YUNG `/locate`, данжи, лут (Lootr) → **Поздняя**: боссы (graveyard, cataclysm) → **Небо**: самолёты immersive_aircraft:quadrocopter ✓ → ракета ad_astra:tier_1_rocket ✓ | waystones, yung*, dungeons-and-taverns, lootr, graveyard, cataclysm, immersive-aircraft, man-of-many-planes, ad-astra, artifacts |
| `farmer.md` | Путь хозяина | **Ранняя**: ферма+котёл farmersdelight:cooking_pot ✓ (разделочная доска ✓, плита ✓) → **Средняя**: сезоны (календарь serene-seasons) → **Поздняя**: кухонная автоматика Create (create-central-kitchen) | farmers-delight, crabbers-delight, serene-seasons, create, create-central-kitchen |

## 2. Правитель (ruler/, 14 стр.)

Ядро «Живого мира»: люди, колонии, экономика, войны.

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Путь правителя | Интро «в голосе летописи», схема: деревня⇄колония⇄королевства, `<SubPages />` | все |
| `mca.md` (нов.) | Люди и семьи (MCA Reborn) | Жители как люди: имена, семья, брак (mca:engagement_ring ✓), дети; диалоги MCA Conversations | minecraft-comes-alive-reborn, mca-conversations |
| `village.md` (сущ.) | Деревня и жители | Устройство деревни, роли, как с ней взаимодействовать | mca |
| `colony.md` (сущ.) | Твоя колония | Стартовый лагерь, ратуша, рабочие, исследования | minecolonies |
| `colony-build.md` (нов.) | Строительство и украшения | Схемы построек (structurize), декоративные блоки (domum-ornamentum), лифты (colony-lifts) | structurize, domum-ornamentum, colonylifts |
| `colony-and-village.md` (сущ.) | Деревня и колония вместе | Мост поселений: переселение, охрана деревни, связь двух механик | zmbridge |
| `neighbors.md` (сущ.) | Соседи | Взаимодействие с соседними поселениями и колониями | zmbridge, minecolonies |
| `recruits.md` (сущ.) | Рекруты и армия | Наём рекрутов, ранги, командование, фракции, карта территорий | villager-recruits |
| `foreign-villages.md` (сущ.) | Чужие деревни и короли | Другие деревни мира, королевские дома; → монархия (Capitals) | mca, mcacapitals |
| `economy.md` (сущ., вмещает trade.md) | Экономика и торговля | Изумруды, рынки, торговые посты (mc-trade-post), курорт и переработка; старые страницы economy+trade сливаются | minecolonies, mctradepost, villagerstradingplus (упомянуть) |
| `treasury.md` (сущ.) | Казна и налоги | Налоги WNT: политики, казна, `/wnt claimtax` | minecolonies-war-n-taxes |
| `war.md` (сущ.) | Война, воры и оборона | Набеги, осады, вымогательства; воры-колонисты, укрытия | minecolonies-war-n-taxes, colonist-thieves |
| `peace.md` (сущ.) | Мир и союзы | Мирные договоры, вассалы, дань, дипломатия | minecolonies-war-n-taxes |
| `voice.md` (нов.) | Голос жителей | Как разговаривать голосом: жители MCA и колонисты слышат/отвечают (livingvoice + living-colonists), настройки | simple-voice-chat, livingvoice, livingcolonists, zmclient |

## 3. Инженер (eng/, 10 стр.)

Техника. Политика раздела: **обзор + первый шаг + куда смотреть подробнее** (Ponder/JEI/встроенные руководства), без пересказа.

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Инженер: с чего начать | Интро «в голосе летописи», порядок Create→Mekanism→AE2 (ссыл. на path/engineer), как учиться: Ponder, JEI, встроенные книги; `<SubPages />` | create, mekanism, ae2 |
| `create.md` (нов.) | Create: кинетика | Что даёт (валы/шестерни/конвейеры), первый шаг — гаечный ключ create:wrench ✓ + рукоятка, обучение Ponder; мультиблоки шестеренок; поршни multi-piston | create, multipiston |
| `create-electric.md` (нов.) | Электричество и валюта Create | createaddition (коннектор ✓, электромотор), numismatics (монеты/банкомат) | createaddition, numismatics |
| `mekanism.md` (нов.) | Mekanism: машины и химия | Начало (металлургический инфузер ✓→энергокамера), ступени сплавов, газы; генераторы и инструменты блоками; Ponder-обучение | mekanism, mekanism-generators, mekanism-tools, mekanism-ponders |
| `ae2.md` (нов.) | AE2: ME-сеть и автокрафт | Ранний путь: зарядник ✓→inscriber✓→процессоры, метеориты; где подробнее — **встроенное русское руководство ae2:guide ✓** | ae2 |
| `mek-ae2.md` (нов.) | Мосты: Mekanism×AE2 и колония | applied-mekanistics (газы/энергия в ME), colonylink (стройка колонии через автокрафт), create-recycled | applied-mekanistics, colonylink, createrecycled |
| `pneumaticcraft.md` (нов.) | Пневматика: сжатый воздух | Давление, компрессор ✓, трубы, дроны; руководство: **книга pneumaticcraft:manual ✓** | pneumaticcraft |
| `railcraft.md` (нов.) | Железные дороги | Рельсы, локомотивы, котлы, логика; руководство: **railcraft:guide_book ✓**; паровые двигатели (аддон) | railcraft-reborn, railcraft-engines |
| `buildcraft.md` (нов.) | BuildCraft: трубы и карьеры | Классика автоматизации; первый шаг — гаечный ключ buildcraftcore:wrench ✓ | buildcraft-community-edition |
| `computers.md` (нов.) | Компьютеры: CraftTweaked и периферия | CC:Tweaked (компьютер ✓, черепахи, Lua), Advanced Peripherals (мост в ME, чат-блок и др.); руководство: **advancedperipherals:manual (уточнить id)** + справка внутри CC `help` | cc-tweaked, advancedperipherals |

## 4. Искатель приключений (adv/, 5 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Искатель: с чего начать | Интро «в голосе летописи», экипировка→структуры→боссы, `<SubPages />` | все |
| `combat.md` (нов.) | Бой, оружие и броня | Боёвка better-combat, рывок combat-roll, щиты shield-expansion, арсенал spartan-weaponry, броня immersive-armors | better-combat, combat-roll, shield-expansion, spartan-weaponry-unofficial, immersive-armors, nightlights |
| `artifacts.md` (нов.) | Артефакты и шкура богов | Артефакты на слотах Curios (из лута): первый — artifacts:everlasting_beef ✓; список механик | artifacts, curios |
| `bosses.md` (нов.) | Боссы и нежить | Кладбища graveyard, катакомбы cataclysm (игнитий ✓), биомные криперы | graveyard, cataclysm, creeper-overhaul |
| `sky.md` (нов.) | Небо и космос | Самолёты (immersive_aircraft:quadrocopter ✓, man-of-many-planes), космос: ракета ad_astra:tier_1_rocket ✓ → планеты | immersive-aircraft, man-of-many-planes, ad-astra |

## 5. Мир (world/, 4 стр.) — *предлагаемый новый раздел*

Обоснование: 15+ модов генерации/структур (YUNG×7, BoP, RU, Tectonic, Skylands, CTV, D&T) не умещаются в «Искатель» без раздувания. Искатель тогда — «чем это воевать/крафтить», Мир — «где это искать».

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Мир: генерация и биомы | Интро «в голосе летописи»; списки модов генерации; `<SubPages />` | tectonic, biomes-o-plenty, regions-unexplored, creeper-overhaul |
| `biomes.md` (нов.) | Биомы и рельеф | BoP (биомы), Regions Unexplored, Tectonic (горы/каньоны), жуки в биомах | biomes-o-plenty, regions-unexplored, tectonic, bugs-aplenty |
| `skylands.md` (нов.) | Скайленды | Возвращённое измерение небесных островов; как попасть (портал — ID уточнить на этапе 2) | skylands-upport |
| `structures.md` (нов.) | Структуры: что и где искать | Сводно по YUNG (храмы/данжи/шахты/крепости) + D&T + CTV-деревни; `/locate`; лут в сундуках уникален (Lootr) | yungs-api, yungs-better-*, dungeons-and-taverns, ct-overhaul-village, lootr |

## 6. Хозяйство (farm/, 5 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Хозяйство и кухня | Интро «в голосе летописи», пища↔сытость↔автоматика, `<SubPages />` | farmers-delight |
| `cooking.md` (нов.) | Готовка (Farmer's Delight) | Котёл ✓, разделочная доска ✓, плита ✓, рецепты через JEI | farmers-delight |
| `crabber.md` (нов.) | Море: Краб-ловец | Морские блюда, ловушки, добыча крабов | crabbers-delight |
| `seasons.md` (нов.) | Сезоны | Календарь годового цикла, влияние на рост и погоду (таблица) | serene-seasons |
| `kitchen-auto.md` (нов.) | Кухонная автоматика | Как подключить Create к готовке (create-central-kitchen), пример конвейера | create, create-central-kitchen, createaddition |

## 7. Быт (life/, 6 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Быт: удобства жизни | Интро «в голосе летописи», карта раздела, `<SubPages />` | — |
| `backpacks.md` (нов.) | Рюкзаки | backpacked:backpack ✓: модули-аугменты, прогрессия | backpacked |
| `travel.md` (нов.) | Путешествия | Вехи waystones:waystone ✓ (телепорт, точка смерти), спальные мешки comforts, гамаки | waystones, comforts |
| `chests.md` (нов.) | Сундуки и хранилища | ironchest:iron_chest ✓ и апгрейды | iron-chests |
| `vanilla-plus.md` (нов.) | Ванлла-удобства | amendments, supplementaries, chimes, torch-hit, falling-tree, multi-mine, cosmetic-armor (слот показа), могила corpse | amendments, supplementaries, chimes, torch-hit, fallingtree, multi-mine, cosmetic-armor-reworked, corpse |
| `tarot.md` (нов.) | Карты Таро | 22 аркана с пассивными бонусами; как получить (лут), как работает | tarotcards-remastered |

## 8. Бог (god/, 1 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `god.md` (сущ.) | Бог и вера | Обновить: алтарь из магнетита, молитвы (вслух/чат/алтарь), знамения и вмешательства | zmgod |

## 9. Справка (ref/, 7 стр.)

| Файл | Заголовок | Содержание | Моды |
|---|---|---|---|
| `index.md` (нов.) | Справка | Оглавление раздела, куда смотреть, `<SubPages />` | — |
| `commands.md` (сущ.) | Голос, клавиши и команды | Переносится как есть (контент не теряется): push-to-talk, клавиши модов, таблица команд | simple-voice-chat, villager-recruits, minecolonies-war-n-taxes |
| `keys.md` (нов.) | Клавиши по модам | Полный список `<KeyBind>` ключевых действий (дополнение к commands.md) | все |
| `guides.md` (нов.) | Где какое руководство | Patchouli-книги (ad_astra:astrodux ✓, pneumaticcraft:manual ✓, railcraft:guide_book ✓, minecolonytax:war_taxes_codex ✓, advancedperipherals:manual), AE2-руководство ae2:guide ✓, Ponder-сцены (Create/Mekanism/AP), справка CC, JEI | patchouli, ae2, ad-astra, pneumaticcraft, railcraft, wnt, advancedperipherals, guideme, jei |
| `jei.md` (нов.) | JEI и рецепты | Основы JEI (R рецепт, U использование), расчёт мультиблоков Mekanism (jei_mekanism_multiblocks), Polymorph при конфликтах | jei, jei-mekanism-multiblocks, polymorph |
| `maps-hud.md` (нов.) | Карты и HUD | Xaero (мини+мир), BetterF3, Jade и аддоны, динамический прицел, головы в чате, анимации, звук (Ambient/Sound Physics), DistantHorizons | xaeros-minimap, xaeros-world-map, betterf3, jade, jadeaddons, dynamiccrosshair, chat-heads, not-enough-animations, better-third-person, ambientsounds, sound-physics-remastered, distanthorizons, sodium-extra, sodium-dynamic-lights |
| `ui.md` (нов.) | Инвентарь и интерфейс | IPN (жесты/сортировка), mouse-tweaks, yungs-menu-tweaks, fancymenu/drippy-loading, better-advanced-tooltips (F3+H) | inventory-profiles-next, mousetweaks, yungsmenutweaks, fancymenu, drippyloadingscreen, betteradvancedtooltips |

---

## Техника GuideMe (проверено по guideme-21.1.22.jar + нынешние страницы + AE2-руководство)

- **Модель книги**: предмет `guideme:guide[guideme:guide_id="zhivoymir:start"]`; регистрация — `kubejs/assets/zhivoymir/guideme_guides/start.json` (название/tooltip, `default_language: ru_ru`); выдача — `kubejs/server_scripts/guide.js` (первый вход + `/guide`).
- **Frontmatter**: `navigation: { title, parent, position, icon }` — существующие страницы строят дерево через `parent` (index.md) и `<SubPages />` в index. Подпапки поддерживаются (AE2-руководство использует `items-blocks-machines/…`; там же `categories`, `item_ids`).
- **Теги** (TagCompiler в jar): `<ItemLink id="ns:id" />` и `<ItemImage id="ns:id" scale />` — любой зарегистрированный предмет/блок, включая предметы **других модов** (уже работают: `minecolonies:supplycampdeployer`, `bountiful:bountyboard`); `<Recipe id="ns:id" fallbackText="…" />`; `<KeyBind id="key.mod.bind" />`; `<CommandLink>…</CommandLink>`; также есть компиляторы: BlockImage, Color, PlayerName, Box/Div/Flow/A, Scene (интерактивные 3D-сцены, как в AE2-руководстве), CategoryIndex/ItemIndex (для каталогов).
- **Навигация в нынешних страницах**: линейное дерево `index.md` → 14 страниц с `parent: index.md` и `position`. Для разделов: каждый раздел = подпапка с `index.md` (заголовок раздела, intro «в голосе летописи», `<SubPages />`), страницы раздела — `parent: <раздел>/index.md`.
- **Ссылки на предметы других модов — да**, правила: ID из jar-языков/рецептов (см. guide-inventory.md, «✓» = проверено). Битые ссылки запрещены.
- **Ограничения/проверить на этапе 2**: устаревший перевод AE2-руководства лежит в kubejs (апдейт?); линковка предметов с NBT (например, `guideme:guide` с компонентом) в `<ItemLink>`; поведение `parent` при переносе файлов в подпапки (тест на тест-сервере после окончания предгенерации).

---

## Спорные вопросы к пользователю

1. **Раздел «Мир»** — добавляет 4 страницы (world/). Вариант Б: раскидать YUNG/BoP/RU/Tectonic/Skylands по «Искателю» (будет ~11 стр. в ADV вместо 5, раздел станет «миром и приключениями»).
2. **Слияние страниц**: economy+trade → одна «Экономика и торговля»; neighbors оставить отдельной. Не возражения?
3. **Голос жителей** — отдельная страница в «Правителе» (voice.md) + команды/клавиши остаются в Справке. Ок?
4. **Карта пути** — 4 направления (инженер/правитель/искатель/хозяин). 5-е «Быт»/«Бог» не нужны как пути? (Бог — единая линейная вера, Быт — повсюду.)
5. **FTB Quests** в книге не упоминаются (этап 5 отдельно) — подтвердить.
6. **Невыясненные ID** (пометить на этапе 2, прежде чем вставлять в `<ItemLink>`): chimes:wind_chime, numismatics:sun_dollar, comforts:sleeping_bag, corpse:corpse, spartan_weaponry:*, immersive_armors:*, nightlights:*, railcraftengines:steam_engine, man_of_many_planes:*, multipiston:multipiston, skylands_upport:портал, tarotcards (колода), advancedperipherals:manual, colonist_thieves (hideout), mctradepost (постройка), domum_ornamentum/structurize (инструмент), villager-recruits (предмет-вербовка).
7. **Ad Astra** остаётся в «Искателе» (по плану), хотя ракетостроение техническое — готовы к этому? (Вариант: упомянуть в eng/ кратко.)
8. **VillagersTradingPlus** (datapack-движок торговли) — служебный, но меняет торговлю жителей: проверить конфиг сборки на этапе 2 и дать ли строку в «Экономике».
9. **Стартовые теги на стр. first-steps** уже ссылаются на `minecolonies:supplycampdeployer` — совпадает с jar ✓.
10. **Язык книги**: сейчас `default_language: ru_ru`; страницы AE2-руководства переведены вручную — синхронизировать ли с апстрим-обновлениями (это уже существующее решение сборки).
