# Квесты FTB Quests — проект (2026-11-10)

Сборка: NeoForge 1.21.1, FTB Quests **2101.1.38** (jar: `/tmp/depcache/ftb-quests-neoforge-2101.1.38.jar`), FTB Teams 2101.1.12, FTB Library 2101.1.39, GuideMe 21.1.22.
Квесты = маршрут «к чему стремиться»; справочник `zhivoymir:start` (GuideMe) = энциклопедия. Главы повторяют «Карту пути».
Каждый факт/ID проверен по jar (`/tmp/depcache`) или `docs/guide-facts*`; ничего нового не утверждаем.

---

## Техника (FTB Quests 2101.1.38)

### Файлы и формат
- Каталог квестов: `config/ftbquests/quests/` (подтверждено в `BaseQuestFile`: строки `quests`, `data.snbt`, `chapters`, `chapter_groups.snbt`, `reward_tables`, `lang`).
  - `data.snbt` — корень файла квестов: `id`, `groups` (id групп), дефолты (`default_quest_shape`, `default_consume_items`, `default_reward_team` …).
  - `chapters/<id>.snbt` — по одному файлу на главу; поле `group` привязывает главу к группе, `filename` — имя файла.
  - `chapter_groups.snbt` — список групп глав (`title`, порядок).
  - `reward_tables/<id>.snbt` — таблицы наград (опционально; для нас не обязательны — награды привязаны к квестам напрямую).
  - `lang/<locale>.snbt` — таблицы переводов текстов квестов (см. ниже).
- Рядом с каталогом — серверный конфиг `config/ftbquests/server-config.snbt` (генерируется модом, в репозиторий класть не обязательно).

### ID объектов
- Авто-ID — `long`, пишется как `%016X` (16 hex-символов, верхний регистр; подтверждено байткодом `QuestObjectBase`).
- `id` в SNBT — **только 16-hex** (`BaseQuestFile.readID`: строка → `Long.parseLong(.., 16)`, иначе выдаётся новый случайный ID и зависимости рвутся). Читаемые имена (`first_camp`) живут только в генераторе `tools/quests-gen.py`: ID = sha1(имя) → 16 hex без знакового бита, детерминированно (перегенерация не ломает прогресс).

### Типы задач (TaskTypes, из байткода `TaskTypes.class` + lang `ftbquests.task.*`)
`item`, `fluid`, `custom`, `xp`, `dimension`, `stat`, `kill`, `location`, `checkmark`, `advancement`, `observation`, `biome`, `structure`, `gamestage`, `interaction` (+ условные `forge_energy`, `botania_mana`, `ic2_energy`, `npc_dialog`, `npc_faction` и т. п.).
**Используем в проекте:** `item`, `advancement`, `kill`, `dimension`, `biome`, `structure`, `checkmark`, `interaction`.

Поля (по байткоду):
- `item` (ItemTask): `item` (id или тег), `count`, `consume_items`, `only_from_crafting`, `match_components`.
- `kill` (KillTask): `entity` (entity id) или `entityTypeTag`.
- `advancement` (AdvancementTask): `advancement` (id), `criterion` (опц.).
- `dimension` (DimensionTask): `dim`.
- `biome` (BiomeTask): `biome`.
- `structure` (StructureTask): `structure` (id из `data/<mod>/worldgen/structure/`).
- `interaction` (InteractionTask): `type` ∈ {BLOCK, BLOCK_TAG, BLOCK_STATE, ENTITY_TYPE, ENTITY_TYPE_TAG}, `to_interact` (id). Срабатывает на ПКМ.
- `checkmark` — ручная отметка (только где иначе никак: молитва, постройка, найм, налог).

### Типы наград (RewardTypes, из байткода + lang `ftbquests.reward.*`)
`item`, `choice`, `all_table`, `random`, `loot`, `command`, `custom`, `xp`, `xp_levels`, `advancement`, `toast`, `gamestage`, `currency`.
**Используем в проекте:** `item` (материалы/расходники), `xp_levels` (опыт уровнями; «уровни» понятнее игроку, чем точки `xp`). Награды-команды не нужны.

### Язык (lang/*.snbt) — обязателен
- Формат файла version 13: `title`/`description` в самих квестах — устаревшая форма (при чтении переносится в переводы, `addInitialTranslation`). Тексты хранятся в `quests/lang/<locale>.snbt`, плоская таблица ключей `<тип>.<HEX>.<поле>`: `quest.<HEX>.title`, `quest.<HEX>.quest_desc` (список строк), `chapter.<HEX>.title`, `file.<HEX>.title` (`TranslationManager.makeKey`, `TranslationKey`).
- Запасная локаль — `en_us` (`DEFAULT_FALLBACK_LOCALE`), поэтому русский текст лежит **и в `ru_ru`, и в `en_us`** — у клиента с другим языком квесты не пустые.

### Ссылки на справочник (кликабельность) — проверено
1. **Нативная кнопка «Открыть в справочнике»**: у квеста есть поле `guide_page` (`Quest.readData`), в панели квеста появляется кнопка-книга (`ViewQuestPanel$OpenInGuideButton` → `handleClick("guide", guidePage)`). GuideMe слушает custom-click `guide:*`. Значение — id книги + страница, напр. `zhivoymir:start:path/index`. **Точный формат проверить на клиенте на шаге 2** (см. спорные вопросы).
2. **Кликабельные команды в описании**: описания читаются `TextUtils.parseRawText` — сначала JSON-компонент (`Component.Serializer.fromJson`, поддерживает `clickEvent` `run_command`/`open_url`), иначе FTB Library текст. Можно вставить `{"text":"Справочник","clickEvent":{"action":"run_command","value":"/guide"}}` или команду открытия страницы.
3. **GuideMe-команды открытия страницы** (из `GuideCommand`/`GuideClientCommand`):
   - `/guideme open <цель> <guide> <page>` — серверная;
   - `/guidemec guide <guide> open [page]` — клиентская, без прав.
   id книги — `zhivoymir:start`.

### Где лежат и как раздаются (packwiz)
- **Сервер**: `config/ftbquests/quests/**` в серверной папке — источник истины; прогресс команд хранится в данных мира. Клиенты получают квесты по сети (файлы на клиенте не нужны).
- **Репо сборки** `/root/work/minecraft-pack`: кладём `config/ftbquests/quests/**` и регистрируем в `index.toml` (как уже сделано для `config/warntax/`, `config/graveyard/` и др. — конфиги включены в главный pack).
- **Side**: в `index.toml` у файлов нет поля side (оно есть только у `.pw.toml` модов); остальные конфиги (`config/warntax`, `config/graveyard`…) идут обеим сторонам — квесты тоже. Безвредно: клиент файлы не читает, получает квесты с сервера.
- **Не деплоить** до окончания предгенерации (п.4 плана); на сервер .239 сейчас не ходим.

### Командная игра
FTB Teams в комплекте — прогресс идёт на команду по умолчанию; `team_reward` для наград. Тексты пишем в единственном числе от лица «ты» (команда общая).

### Требования к фактам (повтор правил плана)
- Книги Patchouli (`pneumaticcraft:manual`, `railcraft:guide_book`, `advancedperipherals:manual`, `ad_astra:astrodux`, `minecolonytax:war_taxes_codex`) — **не предметы** (`patchouli:guide_book` с компонентом), в item-задачи НЕ ставить.
- Предмет проверяем по реестру (рецепт `result` / lang `item.*`, `block.*`), а не по `models/item`.
- Артефакты и карты Таро не крафтятся — только лут; в задачах — «получить из лута», а не «скрафтить».
- Достижения — только существующие ID из `data/*/advancement`.
- Убийства жителей не поощряем (бог карает): Испорченный чемпион Graveyard — необязательный квест с предупреждением.

---

## Итог: 82 квеста в 7 главах, изумрудов 15

| Глава | Квестов | Изумруды в наградах |
|---|---|---|
| 1. Первые шаги (входная) | 10 | 3 |
| 2. Правитель | 14 | 7 |
| 3. Инженер | 19 | 0 |
| 4. Искатель | 18 | 3 |
| 5. Хозяин | 9 | 0 |
| 6. Быт | 7 | 1 |
| 7. Бог | 5 | 1 |
| **Итого** | **82** | **15** |

Основное в наградах — **опыт уровнями** и материалы следующей ступени. Изумруды — редко и по мелочи; **15 всего** — много меньше «пары часов работы на досках заданий» (аудит `docs/economy-audit.md`: Bountiful 1–4 за задание, обновление 300 с → даже ~10 заданий дают больше). Экономику не ломаем.

---

## Глава 1. Первые шаги (входная, 10 квестов)

Открыта сразу. Финальный квест `first_path` — ворота для всех остальных глав.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| first_camp | Стартовый лагерь | item `minecolonies:supplycampdeployer` | — | Лагерь выдаётся при первом входе вместе с книгой. Не ставь его вплотную к деревне: ближе 20 блоков к её границе появится предупреждение. | Твоя колония | хлеб ×8 | 0 |
| first_villager | Знакомство | interaction ENTITY_TYPE_TAG `minecraft:villager` (тег включает жителей MCA) | first_camp | Нажми ПКМ по жителю деревни и поговори с ним. У каждого есть имя, характер и сердца — его отношение к тебе. | Деревня и жители | 5 ур. XP | 0 |
| first_emerald | Первый изумруд | item `minecraft:emerald` | first_villager | Продай жителю что-нибудь или возьми задание с доски. Изумруды — валюта деревни. | Экономика и торговля | 2 изумруда | 2 |
| first_board | Доска заданий | interaction BLOCK `bountiful:bountyboard` | first_villager | В деревнях стоят доски объявлений — за поручения платят изумрудами. | Экономика и торговля | 1 изумруд | 1 |
| first_scepter | Инструмент строителя | item `structurize:sceptergold` | first_camp | В чертеже стартового лагеря лежит инструмент строителя — им ставят постройки колонии. | Твоя колония | 5 ур. XP | 0 |
| first_townhall | Первая ратуша | checkmark | first_scepter | Поставь ратушу инструментом строителя: с неё начинаются рабочие, исследования и постройки. | Твоя колония | дубовые доски ×32 | 0 |
| first_bread | Хлеб насущный | item `minecraft:bread` | first_villager | Собери пшеницу, испеки хлеб. Сытый путник сильнее голодного. | Хозяйство | 5 ур. XP | 0 |
| first_sword | Защитник | item `minecraft:iron_sword` | first_villager | Ночь опасна. Выкуй железный меч — без защиты из деревни лучше не выходить. | Бой, оружие и броня | 5 ур. XP | 0 |
| first_hunt | Охота на чудовищ | kill `minecraft:zombie` | first_sword | Сними первого зомби у стен деревни. | Бой, оружие и броня | 5 ур. XP | 0 |
| first_path | Путь выбран | checkmark | first_townhall, first_hunt | Открой в книге «Карту пути» и выбери направление: инженер, правитель, искатель или хозяин. | Карта пути | 10 ур. XP | 0 |

---

## Глава 2. Правитель (14 квестов)

Вход — `first_path`. Порядок: люди и семья → колония → рекруты → экономика → казна → война → корона.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| ruler_tree | Семейное древо | item `mca:family_tree` | first_path | Древо показывает родню всей деревни: кто кому родич и сосед. | Люди и семьи | 10 ур. XP | 0 |
| ruler_match | Кольцо свахи | item `mca:matchmakers_ring` | ruler_tree | Кольцо свахи (из железа) не для тебя — оно женит пару жителей между собой. Колец нужно два, по одному на каждого; уже женатые откажутся. | Люди и семьи | 5 ур. XP | 0 |
| ruler_engage | Кольцо для помолвки | item `mca:engagement_ring` (достижения помолвки в MCA нет) | ruler_match | Помолвочное кольцо — золотой слиток и алмаз. С ним можно обручиться с тем, к кому у тебя достаточно сердец. | Люди и семьи | 10 ур. XP | 0 |
| ruler_marry | Свадьба | advancement `mca:marriage` | ruler_engage | Обручальное кольцо позволяет вступить в брак при достаточном числе сердец. Квест засчитается, когда сыграешь свадьбу. | Люди и семьи | 15 ур. XP | 0 |
| ruler_worker | Первый рабочий | checkmark | first_townhall, first_path | Найми первого рабочего и дострой его постройку — колония оживает. | Твоя колония | доски ×16 | 0 |
| ruler_invite | Переселение | checkmark (`zmbridge:invite` не предмет — это `minecraft:paper` с компонентами) | ruler_worker | Приглашение в колонию — бумага, изумруд и перо. ПКМ приглашением по жителю деревни, который тебе доверяет, — и он переедет в твою колонию. | Деревня и колония вместе | 15 ур. XP | 0 |
| ruler_recruit | Рекрут | checkmark | ruler_worker | Нажми ПКМ по рекруту и выбери «Нанять» — простой рекрут стоит 8 изумрудов. Жалованье — 1 изумруд каждые 20 минут с бойца. | Рекруты и армия | 2 изумруда | 2 |
| ruler_squad | Отряд | checkmark | ruler_recruit | Три рекрута под рукой — уже отряд. Плату они берут из своего инвентаря или из сундука обеспечения. | Рекруты и армия | 3 изумруда | 3 |
| ruler_bank | Банковский терминал | item `numismatics:bank_terminal` | first_path | Банковский терминал Numismatics — счета, карты и обмен в одном блоке. Монеты — валюта между игроками; жители берут изумруды. | Электричество и валюта Create | 2 изумруда | 2 |
| ruler_codex | Казна колонии | checkmark | first_path | War 'N Taxes: постройки приносят налог в казну, политики меняют правила. Прочитай раздел о казне. | Казна и налоги | 10 ур. XP | 0 |
| ruler_tax | Первая казна | checkmark | ruler_codex | Собери налог командой `/wnt claimtax` — казна колонии пополнилась. | Казна и налоги | 15 ур. XP | 0 |
| ruler_voice | Голос жителей | checkmark | first_path | Поговори с жителем вслух (Push-to-Talk) — жители и колонисты слышат и отвечают. | Голос жителей | 10 ур. XP | 0 |
| ruler_war | Война | checkmark *(необязательный)* | ruler_tax | Проведи набег или отрази осаду. Слава и трофеи — или разорение. | Война, воры и оборона | 25 ур. XP | 0 |
| ruler_crown | Корона | advancement `mca:monarch` *(необязательный)* | ruler_marry | С репутацией растёт ранг в деревне: Крестьянин → … → Мэр → Монарх; для высших рангов деревня должна быть хорошо отстроена. | Деревня и жители | 50 ур. XP | 0 |

---

## Глава 3. Инженер (19 квестов)

Вход — `first_path`. Порядок: Create → Mekanism → AE2 → PNC → Railcraft → компьютеры → мосты.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| eng_wrench | Гаечный ключ | item `create:wrench` | first_path | Ключ собирает и разбирает механизмы Create — без него машин не починить. | Create: кинетика | 5 ур. XP | 0 |
| eng_crank | Рукоятка | item `create:hand_crank` | eng_wrench | Рукоятка даёт первое вращение: ПКМ крутит в одну сторону, Shift+ПКМ — в другую. | Create: кинетика | 5 ур. XP | 0 |
| eng_cog | Шестерни | item `create:cogwheel` | eng_crank | Шестерни передают вращение дальше, от механизма к механизму. | Create: кинетика | 5 ур. XP | 0 |
| eng_shaft | Вал | item `create:shaft` | eng_cog | Вал передаёт вращение по прямой — соедини им механизмы. | Create: кинетика | 5 ур. XP | 0 |
| eng_osmium | Осмий | item `mekanism:ingot_osmium` | eng_cog | Осмий — основа машин Mekanism: добывай руду и переплавляй в слитки. | Mekanism: машины и химия | 10 ур. XP | 0 |
| eng_infuser | Металлургический наполнитель | item `mekanism:metallurgic_infuser` | eng_osmium | Печка, железо, осмий и редстоун — ключ к механике Mekanism. | Mekanism: машины и химия | 10 ур. XP | 0 |
| eng_enrich | Камера обогащения | item `mekanism:enrichment_chamber` | eng_infuser | Стальной корпус + базовые сплавы: удвоение руды и первые цепочки. | Mekanism: машины и химия | 10 ур. XP | 0 |
| eng_energy | Энергокуб | item `mekanism:basic_energy_cube` *(необязательный)* | eng_enrich | Накопи энергию — машинам нужно питание. | Mekanism: машины и химия | 10 ур. XP | 0 |
| eng_charger | Зарядник | item `ae2:charger` | eng_enrich | Железо + медь: зарядник заряжает кристаллы истинного кварца. | AE2: ME-сеть и автокрафт | 10 ур. XP | 0 |
| eng_inscriber | Высекатель | item `ae2:inscriber` | eng_charger | Высекай процессоры — мозг будущей ME-сети. | AE2: ME-сеть и автокрафт | 10 ур. XP | 0 |
| eng_drive | МЭ-накопитель | item `ae2:drive` | eng_inscriber | Первый накопитель МЭ: хранение и учёт каждой вещи. | AE2: ME-сеть и автокрафт | 15 ур. XP | 0 |
| eng_cell | Ячейка 1К | item `ae2:item_storage_cell_1k` | eng_drive | Ячейка хранения вмещает и упорядочивает твои ресурсы. | AE2: ME-сеть и автокрафт | 10 ур. XP | 0 |
| eng_pnc | Воздушный компрессор | item `pneumaticcraft:air_compressor` | eng_enrich | Укреплённые кирпичи + печка + напорная труба нагнетают давление. | Пневматика: сжатый воздух | 15 ур. XP | 0 |
| eng_drone | Дрон | item `pneumaticcraft:drone` *(необязательный)* | eng_pnc | Запрограммированный дрон — летающий помощник на заданиях. | Пневматика: сжатый воздух | 15 ур. XP | 0 |
| eng_track | Рельсы | item `railcraft:strap_iron_track` | first_path | Первые рельсы — путь для вагонеток и паровоза. | Железные дороги | 10 ур. XP | 0 |
| eng_train | Паровоз | item `railcraft:steam_locomotive` *(необязательный)* | eng_track | Паровоз возит грузы по рельсам — твоя железная дорога. | Железные дороги | 25 ур. XP | 0 |
| eng_computer | Компьютер | item `computercraft:computer_advanced` | first_path | Программируемый компьютер на Lua: от простых скриптов до управления машинами. | Компьютеры | 15 ур. XP | 0 |
| eng_colonylink | Колония через сеть | item `colonylink:colony_link_wand` *(необязательный)* | eng_drive | Жезл ColonyLink связывает стройку колонии с автокрафтом ME-сети. | Мосты: Mekanism×AE2 и колония | 25 ур. XP | 0 |
| eng_gas_cell | Газ в сети | item `appmek:chemical_storage_cell_1k` *(необязательный)* | eng_drive | Химическая ячейка переносит газы Mekanism в ME-сеть. | Мосты: Mekanism×AE2 и колония | 20 ур. XP | 0 |

---

## Глава 4. Искатель (18 квестов)

Вход — `first_path`. Порядок: разведка → структуры и лут → артефакты → боссы → небо и космос.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| adv_waystone | Веха | item `waystones:waystone` | first_path | Путеводный обелиск — точка быстрой телепортации и возврата из походов. | Путешествия | 5 ур. XP | 0 |
| adv_waystone_use | Дальняя дорога | checkmark | adv_waystone | Телепортируйся через веху — мир стал ближе. | Путешествия | 5 ур. XP | 0 |
| adv_biome | Новые земли | biome `biomesoplenty:highland` (вишнёвой рощи в BOP 21.1 нет; тега «все биомы BOP» нет; нагорье — самый частый биом примарного региона BOP) | first_path | Biomes O' Plenty перекроили местность. Найди нагорье — один из частых новых биомов. | Биомы и рельеф | 10 ур. XP | 0 |
| adv_dungeon | В данж | structure `betterdungeons:small_dungeon` | first_path | YUNG перестроил данжи — спустись в малый данж. | Структуры: что и где искать | 15 ур. XP | 0 |
| adv_loot | Лут | checkmark | adv_dungeon | Открой сундук структуры (Lootr): лут в них для каждого игрока свой — грабь смело. | Структуры: что и где искать | 2 изумруда | 2 |
| adv_archaeologist | Археолог | advancement `artifacts:amateur_archaeologist` | adv_dungeon | Найди первый артефакт в луте — они не крафтятся, только сокровища. | Артефакты | 10 ур. XP | 0 |
| adv_beef | Вечная еда | item `artifacts:everlasting_beef` | adv_archaeologist | Бесконечная еда из лута — не тратится, а кулдаун короткий. | Артефакты | 10 ур. XP | 0 |
| adv_cloud | Двойной прыжок | item `artifacts:cloud_in_a_bottle` *(необязательный)* | adv_archaeologist | Облако в бутылке даёт второй прыжок в воздухе. | Артефакты | 10 ур. XP | 0 |
| adv_mimic | Победитель мимика | advancement `artifacts:chest_slayer` *(необязательный)* | adv_archaeologist | Сундук оказался мимиком? Убей его. | Артефакты | 15 ур. XP | 0 |
| adv_dark_iron | Тёмное железо | item `graveyard:dark_iron_ingot` | first_path | Слиток тёмного железа крафтят из порчи и железного слитка (рецепт `graveyard:dark_iron_ingot`), а ещё находят в сундуках склепов и кладбищ (loot crypt/great_crypt/large_graveyard). | Боссы и нежить | 15 ур. XP | 0 |
| adv_lich | Испорченный чемпион | advancement `graveyard:graveyard/summon_lich` *(необязательный)* | adv_dark_iron | ⚠ Осторожно: кровь для призыва берётся с жителей, а бог видит каждое убийство. Делай это, только если готов к такой цене. | Боссы и нежить | 25 ур. XP | 0 |
| adv_ignis | Игнис | kill `cataclysm:ignis` | adv_dark_iron | Победи Игниса — повелителя пламени, одного из боссов Cataclysm, что охраняют редкие структуры. | Боссы и нежить | 50 ур. XP | 0 |
| adv_guardian | Эндер-хранитель | kill `cataclysm:ender_guardian` *(необязательный)* | adv_ignis | Эндер-хранитель — страж Края. | Боссы и нежить | 50 ур. XP | 0 |
| adv_leviathan | Левиафан | kill `cataclysm:the_leviathan` *(необязательный)* | adv_ignis | Левиафан — морской босс, вне воды неуязвим. | Боссы и нежить | 50 ур. XP | 0 |
| adv_quadro | Квадрокоптер | item `immersive_aircraft:quadrocopter` | first_path | Первый летательный аппарат — с него начинается небо. | Небо и космос | 1 изумруд | 1 |
| adv_suit | Скафандр | item `ad_astra:space_suit` | adv_quadro | В вакууме не выжить — собери скафандр. | Небо и космос | 15 ур. XP | 0 |
| adv_rocket | Ракета | item `ad_astra:tier_1_rocket` | adv_suit | Собери ракету 1-го уровня на верстаке НАСА и поставь на стартовую площадку. | Небо и космос | 20 ур. XP | 0 |
| adv_moon | Луна | dimension `ad_astra:moon` | adv_rocket | Первый шаг на Луне — маленький шаг для странника. | Небо и космос | 25 ур. XP | 0 |

---

## Глава 5. Хозяин (9 квестов)

Вход — `first_path`. Порядок: кухня → море → сезоны → автоматика.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| farm_pot | Кухонный котёл | item `farmersdelight:cooking_pot` | first_path | Котёл греется снизу (плита или огонь): ингредиенты + жидкая основа — порционные блюда. | Готовка | 5 ур. XP | 0 |
| farm_stove | Плита | item `farmersdelight:stove` | farm_pot | Плита — источник жара для котла. | Готовка | 5 ур. XP | 0 |
| farm_board | Разделочная доска | item `farmersdelight:cutting_board` | farm_pot | На доске ножом режут и разделывают — ингредиенты к котлу. | Готовка | 5 ур. XP | 0 |
| farm_soup | Куриный суп | item `farmersdelight:chicken_soup` | farm_stove, farm_board | Первое блюдо из котла: сытнее и полезнее простой еды. | Готовка | хлеб ×4 | 0 |
| farm_trap | Крабовая ловушка | item `crabbersdelight:crab_trap` | first_path | Ловушка у воды приносит крабов, креветок и моллюсков. | Море: Краб-ловец | 5 ур. XP | 0 |
| farm_crab | Жареный краб | item `crabbersdelight:cooked_crab` | farm_trap | Морские яства к столу — добыча ловушки на огне. | Море: Краб-ловец | 10 ур. XP | 0 |
| farm_calendar | Календарь | item `sereneseasons:calendar` | farm_soup | 8 бумаг + часы: календарь показывает сезон и отсчёт до следующего. | Сезоны | 10 ур. XP | 0 |
| farm_season | Сезонный урожай | checkmark | farm_calendar | Культуры плодоносят в свой сезон; вне сезона — теплица. | Сезоны | 15 ур. XP | 0 |
| farm_auto | Кухонная автоматика | checkmark *(необязательный)* | farm_soup | Механическая рука и конвейер кормят котёл без твоих рук. | Кухонная автоматика | 25 ур. XP | 0 |

---

## Глава 6. Быт (7 квестов)

Вход — `first_path`. Короткая глава: удобства жизни.

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| life_backpack | Рюкзак | item `backpacked:backpack` | first_path | Рюкзак в слоте Curios — больше места в походе. | Рюкзаки | 5 ур. XP | 0 |
| life_chest | Железный сундук | item `ironchest:iron_chest` | first_path | Сундук побольше — и не один. | Сундуки и хранилища | 5 ур. XP | 0 |
| life_upgrade | Улучшение сундука | item `ironchest:wood_to_iron_chest_upgrade` | life_chest | Апгрейд превращает деревянный сундук в железный — без перестановки. | Сундуки и хранилища | 5 ур. XP | 0 |
| life_hammock | Гамак | item `comforts:hammock_red` | first_path | Гамак и спальный мешок — спи и переживай ночь где угодно. | Путешествия | 5 ур. XP | 0 |
| life_deck | Колода Таро | item `tarotcards:tarot_deck` | first_path | Колода хранит карты; сами карты — только лут, не крафтятся. | Карты Таро | 10 ур. XP | 0 |
| life_card | Первая карта | advancement `tarotcards:progression/root` (критерий: любой предмет тега `tarotcards:tarot_cards`; item-задачу по тегу FTB Quests без FTB Filter System не умеет) | life_deck | Найди в луте любую карту Таро и вложи в колоду — пассивный бонус. | Карты Таро | 1 изумруд | 1 |
| life_collector | Коллекционер | advancement `tarotcards:deck_collector` *(необязательный)* | life_card | Собери все 22 старших аркана. | Карты Таро | 30 ур. XP | 0 |

---

## Глава 7. Бог (5 квестов)

Вход — `first_path`. Короткая глава: вера и алтарь. Молитва — только checkmark (срабатывает по голосу/чату, авто-задачи нет).

| ID | Название | Задача (тип + ID) | Зависит от | Описание (черновик) | Справочник | Награда | Изумр. |
|---|---|---|---|---|---|---|---|
| god_altar | Алтарь | item `minecraft:lodestone` | first_path | Алтарь — блок магнетита; у него бог чаще отвечает на молитвы, и на нём приносят дары. | Бог и вера | 5 ур. XP | 0 |
| god_pray | Молитва | checkmark | god_altar | Начни молитву с обращения («Боже», «Господи»…) — в чате или голосом. У алтаря (не дальше 6 блоков) бог отвечает охотнее. | Бог и вера | 10 ур. XP | 0 |
| god_offer | Дар | checkmark | god_pray | Принеси дар на алтарь: ПКМ предметом по алтарю. Хлеб и пшеница +1 веры, изумруд +3, алмаз +5. | Бог и вера | 1 изумруд | 1 |
| god_sign | Знамение | checkmark *(необязательный)* | god_offer | Верным бог посылает знамения — гром, колокольный звон, звёздный свет, молнию (она не ранит). | Бог и вера | 15 ур. XP | 0 |
| god_bless | Благословение | checkmark *(необязательный)* | god_sign | Ещё верным бог посылает благословения — регенерацию, скорость, силу в работе, удачу, защиту, сытость, ночное зрение и другие. | Бог и вера | 20 ур. XP | 0 |

---

## Итог по изумрудам

Подсчёт по квестам выше (колонка «Изумр.»): 3 + 7 + 0 + 3 + 0 + 1 + 1 = **15 изумрудов** суммарно за все 82 квеста. Разбросаны по 10 квестам (почти все — ранняя/средняя игра). Для сравнения, аудит `docs/economy-audit.md`: доска Bountiful платит 1–4 изумруда за задание и обновляется каждые 300 с — пара часов работы на досках даёт порядка 20–40 изумрудов. **Наши 15 — заметно ниже**; основная валюта наград — опыт уровнями (5–50) и материалы следующей ступени.

---

## Спорные вопросы к пользователю

1. **Формат `guide_page`** (кнопка «Открыть в справочнике»). Механизм подтверждён в байткоде (`guide_page` → handleClick("guide", …) → GuideMe слушает custom-click `guide:*`), но точный синтаксис значения (напр. `zhivoymir:start:path/index` vs `zhivoymir:path/index`) проверить только на клиенте на шаге 2. Если не заработает — в описаниях даём кликабельный JSON с `/guide`, а ссылку на страницу — текстом.
2. **Испорченный чемпион Graveyard** — оставляю необязательным квестом с предупреждением о цене призыва (кровь жителей). Не делать совсем или оставить как есть?
3. **Молитва и «человеческие» вехи** — постройка ратуши, найм рекрута, налог, голос, знамение сделаны `checkmark` (ручная отметка): авто-задач нет. Приемлемо?
4. **Изумруды: 15 всего** — достаточно скромно? Могу поднять до ~30 (всё равно меньше «пары часов досок») или оставить.
5. **Главы** — 7 глав (Первые шаги + Правитель/Инженер/Искатель/Хозяин по путям + короткие Быт/Бог). Быт и Бог — 7 и 5 квестов; можно свернуть их в «Первые шаги»/«Правитель», если главы кажутся лишними.
6. **Алтарь бога** — факты из кода zmgod: тег алтарей = `minecraft:lodestone` (магнетит), а в старой книге god.md упоминался «магнетит». Ставлю quest на `minecraft:lodestone` по коду; страницу книги при этом правим на «магнит» — ок?

---

## Реализация (шаг 2, 2026-11-10)

Файлы: `config/ftbquests/quests/{data.snbt, chapter_groups.snbt (пустой список групп — главы в группе по умолчанию), chapters/{first,ruler,engineer,adventurer,farmer,life,god}.snbt, lang/{ru_ru,en_us}.snbt}`. Генерирует `tools/quests-gen.py` (данные квестов — там; править тексты/награды там и перегенерировать), проверяет `tools/quests-check.py` (+ индекс реестра `tools/qreg.py`).

- Ссылка на справочник — текстом в конце описания: «Справочник: <Раздел> → <Страница> (книга — команда /guide)»; названия берутся из frontmatter страниц. `guide_page` не используется.
- Все item-задачи `consume_items: false`; награды `xp_levels` и `item`; `default_reward_team: false`.
- Раскладка: x — глубина зависимостей в главе (шаг 2), каждая ветка — своя строка (шаг 1.5); наложений нет (проверяется).

Правки архитектора и отличия от черновика:
1. `eng_shaft` → `create:shaft` «Вал».
2. `eng_crank` — «ПКМ в одну сторону, Shift+ПКМ — в другую»; порядок eng_cog → eng_osmium → eng_infuser → eng_enrich.
3. `adv_golem` → `adv_guardian` kill `cataclysm:ender_guardian`; `adv_dark_iron` — по рецепту и луту jar; `adv_suit` «скафандр»; `adv_mimic` «мимик». `adv_ignis` — без «игнитового слитка с него» (в справочнике не связано), «повелитель пламени».
4. `ruler_match` «Кольцо свахи» — сводит пару жителей, колец нужно два (диалоги `interaction.matchmaker.fail.needtwo`, книга MCA). `ruler_engage` — item «Кольцо для помолвки» (достижения помолвки нет); `ruler_marry` — advancement `mca:marriage`. `ruler_recruit` — ПКМ по рекруту → «Нанять» (8 изумрудов). `ruler_coin` удалён (у `numismatics:spur` нет рецепта), `ruler_bank` ← first_path. `ruler_crown` — advancement `mca:monarch` (ранг «Монарх», есть в jar и справочнике) вместо ручной отметки. `ruler_invite` — checkmark: `zmbridge:invite` не зарегистрированный предмет, а бумага с компонентами.
5. `god_altar` — текст архитектора; `god_offer` — checkmark «ПКМ предметом по алтарю»; `god_sign`/`god_bless` — дословно по god.md «Что делает бог».
6. `life_card` — advancement `tarotcards:progression/root` (критерий — любой предмет тега `tarotcards:tarot_cards`): точнее checkmark, т. к. item-задачи по тегу без FTB Filter System нет. `farm_warehouse` удалён. `first_hunt` — без «колонисты ценят».
7. Проверено по jar: `cherry_blossom_grove` нет → `biomesoplenty:highland` (тега на все биомы BOP нет). Есть: `artifacts:amateur_archaeologist`, `artifacts:chest_slayer`, `graveyard:graveyard/summon_lich`, `tarotcards:deck_collector`, `cataclysm:ignis|ender_guardian|the_leviathan`, `betterdungeons:small_dungeon`, `ad_astra:moon`. `first_villager` — interaction по тегу `minecraft:villager` (в нём жители MCA).
8. Изумрудов 15; чемпион — необязательный с предупреждением; ручные отметки — молитва, дар, найм, налог и т. п.; 7 глав.
