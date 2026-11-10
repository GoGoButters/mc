# Опись модов сборки и стартовые точки (этап 1)

Источник: `mods/*.pw.toml` (имя/filename/side), jar-файлы в `/tmp/depcache` (modid из `neoforge.mods.toml`/`fabric.mod.json`, ID предметов из lang-файлов и рецептов).

Категории: **игровой контент** — в книгу; **интерфейс-удобство** — в «Справку»; **библиотека/оптимизация** — в книгу не идёт.

> ⚠ ID предметов, помеченные «✓», проверены по jar (lang/рецепт). Без метки — по документации мода, перепроверить при вёрстке (правило: битые ссылки недопустимы).

Всего модов: **165**. По категориям: игровой контент — **80**, интерфейс-удобство — **26**, библиотека/оптимизация — **59**.

| Мод | modid | Side | Категория | Раздел книги | Что даёт игроку | Своё руководство в игре | Стартовая точка |
|---|---|---|---|---|---|---|---|
| Ad Astra | ad_astra | both | игровой контент | Искатель приключений | Космос: ракеты, планеты, ресурсы (деш/острум/калорит). | Patchouli-книга «Астродукс»: <ItemLink id="ad_astra:astrodux"/> (✓ id по lang). Добывается в начале (крафт/лут). | Книга ad_astra:astrodux ✓; первый полёт — <ItemLink id="ad_astra:tier_1_rocket"/> ✓ (собирается на NASA-верстаке ad_astra:nasa_workbench ✓ + стартовая площадка ad_astra:launch_pad ✓). |
| Advanced Peripherals | advancedperipherals | both | игровой контент | Инженер | Расширения для КомпьютерКрафта: периферия (чат-блок, ME-мост, геосканер) для автоматизации через Lua. | Своя Patchouli-книга (id advancedperipherals:manual, перепроверить) ✓ есть раздел book «manual». | Первая периферия — компьютер CC:Tweaked (см. CC: Tweaked) + периферийные блоки Advanced Peripherals; ID ключевых блоков уточнить на этапе 2. |
| Applied Energistics 2 | ae2 | both | игровой контент | Инженер | Хранение и автоматизация: ME-сеть, кабели, автокрафт, процессоры. | Встроенное руководство GuideME: <ItemLink id="ae2:guide"/> ✓ (переведено на русский в kubejs/assets/ae2/ae2guide). | Книга ae2:guide ✓; первый механизм — зарядник <ItemLink id="ae2:charger"/> ✓ (рецепт: железо+медь), дальше inscriber ae2:inscriber ✓. |
| Almanac | almanac | server | библиотека/оптимизация | — | — | — | — |
| AmbientSounds | ambientsounds | client | интерфейс-удобство | Справка | Дополнительное звуковое окружение (музыка, эмбиент, настройки громкости). | — | — |
| Amendments | amendments | both | игровой контент | Быт | Ванлла+-твики: подвесные таблички, утопленные предметы, дым, множественные удочки. | — | — |
| Angry Mobs | angrymobs | both | библиотека/оптимизация | — | (пак-разработка: ИИ мобов) | — | — |
| Applied Mekanistics | appmek | both | игровой контент | Инженер | Мост Mekanism→AE2: ресурсы Mekanism (газы, жидкости, энергии) через ME-сеть. | — | Первый шаг — ME-сеть AE2 (см. Applied Energistics 2). |
| Architectury API | architectury | both | библиотека/оптимизация | — | — | — | — |
| Artifacts | artifacts | both | игровой контент | Искатель приключений | Артефакты с пассивными бонусами в слотах Curios (некрафтятся, только лут/сундуки). | — | Первый артефакт из лута — напр. <ItemLink id="artifacts:everlasting_beef"/> ✓; искать в сундуках структур. |
| Backpacked | backpacked | both | игровой контент | Быт | Рюкзаки с прогрессией и модулями-аугментами. | — | Рюкзак <ItemLink id="backpacked:backpack"/> ✓ (крафт). |
| BadOptimizations | badoptimizations | client | библиотека/оптимизация | — | — | — | — |
| Balm | balm | both | библиотека/оптимизация | — | — | — | — |
| Better Advanced Tooltips | betteradvancedtooltips | both | интерфейс-удобство | Справка | Доп. тултипы (теги/компоненты) в режиме F3+H. | — | — |
| Better Combat | bettercombat | both | игровой контент | Искатель приключений | Боёвка в стиле Minecraft Dungeons: удары, замахи, оффхенд. | — | Первый шаг — любое оружие (меняется поведение). Механики видны в JEI/настройках. |
| Better Third Person | betterthirdperson | client | интерфейс-удобство | Справка | Независимый поворот камеры от третьего лица. | — | — |
| BetterF3 | betterf3 | client | интерфейс-удобство | Справка | Улучшенный F3-экран (настраиваемый, информативный). | — | — |
| Biomes O' Plenty | biomesoplenty | both | игровой контент | Мир | 50+ новых биомов и блоков. | — | Старт — исследование мира; ID предметов напр. <ItemLink id="biomesoplenty:mudball"/> ✓ (уточнить для ключевых). |
| BlockUI | blockui | both | библиотека/оптимизация | — | — | — | — |
| Bountiful | bountiful | both | игровой контент | Искатель приключений | Доски объявлений с заданиями и наградами (в деревнях). | — | Доска <ItemLink id="bountiful:bountyboard"/> ✓ (используется уже на стр. trade.md). |
| Bugs Aplenty | bugs_aplenty | both | игровой контент | Мир | Жуки-мобы в биомах BoP (поиск, дроп, коллекция). | — | Искать в биомах BoP. |
| BuildCraft Community Edition | buildcraftlib | both | игровой контент | Инженер | Классика: трубы, карьеры, автоматизация; энергия (Minecraft Joules). | — | Первый инструмент — гаечный ключ <ItemLink id="buildcraftcore:wrench"/> ✓; первый механизм — карьер/двигатель (ID уточнить на этапе 2). |
| Concurrent Chunk Management Engine (NeoForge) | c2me | server | библиотека/оптимизация | — | — | — | — |
| MCA Capitals \| A Monarchy Mod for MCA Reborn | mcacapitals | both | игровой контент | Правитель | Монархия поверх MCA: капиталы, коронации, наследие, дипломатия королевств. | — | Механика коронации и капитала; конкретные предметы-айтемы не найдены в lang (уточнить на этапе 2). |
| CC: Tweaked | computercraft | both | игровой контент | Инженер | Программируемые компьютеры, черепахи, периферия; язык Lua. | Встроенная справка в компьютере (help), журнал ComputerCraft; документация в игре через `help`. | Компьютер <ItemLink id="computercraft:computer_advanced"/> ✓ (крафт). |
| Chat Heads | chat_heads | client | интерфейс-удобство | Справка | Головы игроков у сообщений в чате. | — | — |
| Chimes | chimes | both | игровой контент | Быт | Декоративные ветряные колокольчики со звуком. | — | <ItemLink id="chimes:wind_chime"/> (id по доке; проверить). |
| Cloth Config API | cloth_config | both | библиотека/оптимизация | — | — | — | — |
| Clumps | clumps | both | библиотека/оптимизация | — | (опт: слипание XP-шаров) | — | — |
| Cobweb | cobweb | both | библиотека/оптимизация | — | — | — | — |
| Colonist Thieves - Heists for MineColonies | colonist_thieves | both | игровой контент | Правитель | Профессия «Вор» для MinerColonies: укрытия, кражи, метки, ограбления поселений. | — | Построить Скрытую базу (hideout) колонией; ID по доке/рецепту (уточнить). |
| Colony Lifts – A Create + MineColonies Addon | colonylifts | both | игровой контент | Инженер | Лифты Create работают с колонией (пути горожан). | — | Механизмы Create (см. Create): лифт-контакт create:mechanical_elevator ? (проверить). |
| ColonyLink | colonylink | both | игровой контент | Инженер | Связка MineColonies×AE2: стройка колонии через автокрафт ME-сети. | — | Нужны ME-сеть AE2 и колония MineColonies (см. разделы). |
| Combat Roll | combat_roll | both | игровой контент | Искатель приключений | Рывок-кувырок в бою (перекат), характеристики на уклонение. | — | Клавиша рывка (см. Справку: клавиши); предметов нет. |
| Comforts | comforts | both | игровой контент | Быт | Спальные мешки и гамаки. | — | Спальный мешок <ItemLink id="comforts:sleeping_bag"/> (id уточнить; найден блок гамака comforts:hammock_red ✓). |
| Common Storage Lib | common_storage_lib | both | библиотека/оптимизация | — | — | — | — |
| Corpse x Cosmetic Armor Reworked Compat | cosmeticcorpsecompat | both | библиотека/оптимизация | — | — | — | — |
| Corpse | corpse | both | игровой контент | Быт | Могила с вещами при смерти. | — | Автоматически при смерти; <ItemLink id="corpse:corpse"/> (id уточнить). |
| Cosmetic Armor Reworked | cosmeticarmorreworked | both | игровой контент | Быт | Экипировка-косметика: второй слой брони на показ. | — | Слот косметики в инвентаре (Curios); предметы брони. |
| Crabber's Delight | crabbersdelight | both | игровой контент | Хозяйство | Морские блюда и краб-ловушки для Farmer's Delight. | — | Краболовка/ингредиенты — смотреть в JEI (id уточнить). |
| Create: Central Kitchen | create_central_kitchen | both | игровой контент | Инженер | Кухонная автоматика для Create (связка с Farmer's Delight и др.). | Ponder. | Кухонные механизмы Create (см. Create). |
| Create: Dragons Plus | create_dragons_plus | both | библиотека/оптимизация | — | (библиотека Create-аддонов) | — | — |
| Create | create | both | игровой контент | Инженер | Кинетическая механика: валы, шестерни, конвейеры, поезда. | Ponder-сцены на каждом механизме (удерживать W + ПКМ). | Гаечный ключ <ItemLink id="create:wrench"/> ✓; первый механизм — рукоятка (create:crank, id уточнить) к чему-нибудь. |
| Create Crafts & Additions | createaddition | both | игровой контент | Инженер | Мост Create↔электричество: генераторы, электромоторы, коннекторы. | Ponder. | Первый предмет — коннектор <ItemLink id="createaddition:connector"/> ✓ (крафт). |
| Create: Recycled | ? | both | игровой контент | Инженер | Переработка/утилизация ресурсов через Create. | Ponder. | Механизмы Create (см. Create). |
| CreativeCore | creativecore | both | библиотека/оптимизация | — | — | — | — |
| Creeper Overhaul | creeperoverhaul | both | игровой контент | Мир | Биомные варианты криперов (дружелюбные/опасные). | — | Искать в мире; старта-предмета нет. |
| ChoiceTheorem's Overhauled Village | ctov | both | игровой контент | Мир | Новые деревни/аванпосты под биомы. | — | Искать деревни /locate; старта-предмета нет. |
| Curios API | curios | both | библиотека/оптимизация | — | (API: слоты аксессуаров; используется артефактами) | — | — |
| Distant Horizons | distanthorizons | both | интерфейс-удобство | Справка | Рендер дальнего горизонта (LOD). | — | — |
| Domum Ornamentum | domum_ornamentum | both | игровой контент | Правитель | Стройматериалы-украшения для колоний (полы, крыши, рамы). | — | Крафт блоков из ресурсов; ID по доке (уточнить). |
| Drippy Loading Screen | drippyloadingscreen | client | интерфейс-удобство | Справка | Кастомизация экрана загрузки (FancyMenu-аддон). | — | — |
| Dungeons and Taverns | ? | both | игровой контент | Мир | Новые структуры: данжи и таверны. | — | Искать /locate structure; старта-предмета нет. |
| Dynamic FPS | dynamic_fps | client | библиотека/оптимизация | — | — | — | — |
| Dynamic Crosshair | dynamiccrosshair | client | интерфейс-удобство | Справка | Динамический прицел-курсор. | — | — |
| Entity Culling | entityculling | client | библиотека/оптимизация | — | — | — | — |
| FallingTree | fallingtree | both | игровой контент | Быт | Деревья валятся целиком. | — | Удобство без предметов. |
| FancyMenu | fancymenu | client | интерфейс-удобство | Справка | Меню и экраны загрузки настраиваются паками. | — | — |
| Farmer's Delight | farmersdelight | both | игровой контент | Хозяйство | Готовка и фермерство: разделочные доски, котлы, блюда, дикие культуры. | — | Котел <ItemLink id="farmersdelight:cooking_pot"/> ✓, разделочная доска farmersdelight:cutting_board ✓, плита farmersdelight:stove ✓. |
| FerriteCore | ferritecore | both | библиотека/оптимизация | — | — | — | — |
| Framework | framework | both | библиотека/оптимизация | — | — | — | — |
| FTB Library (NeoForge) | ftblibrary | both | библиотека/оптимизация | — | — | — | — |
| FTB Quests (NeoForge) | ftbquests | both | библиотека/оптимизация | — | (квесты — этап 5 плана, отдельно) | — | — |
| FTB Teams (NeoForge) | ftbteams | both | библиотека/оптимизация | — | — | — | — |
| Fzzy Config | fzzy_config | both | библиотека/оптимизация | — | — | — | — |
| Geckolib | geckolib | both | библиотека/оптимизация | — | — | — | — |
| GlitchCore | glitchcore | both | библиотека/оптимизация | — | — | — | — |
| GuideME | guideme | both | библиотека/оптимизация | — | (движок книги — то, на чём писана «Летопись») | — | — |
| ImmediatelyFast | immediatelyfast | client | библиотека/оптимизация | — | — | — | — |
| Immersive Aircraft | immersive_aircraft | both | игровой контент | Искатель приключений | Винтажные самолёты, дирижабли, техника для полётов. | — | Первый летательный аппарат — квадрокоптер <ItemLink id="immersive_aircraft:quadrocopter"/> ✓ (крафт). |
| Immersive Armors | immersive_armors | both | игровой контент | Искатель приключений | Наборы брони (магнетитовая, латная и др.). | — | Крафт брони; ID по доке (уточнить). |
| Inventory Profiles Next | inventoryprofilesnext | client | интерфейс-удобство | Справка | Управление инвентарём жестами/сортировкой. | — | — |
| Iron Chests | ironchest | both | игровой контент | Быт | Сундуки крупнее (железный→алмазный→кристальный). | — | Железный сундук <ItemLink id="ironchest:iron_chest"/> ✓ (апгрейд ванлла-сундука). |
| Jade Addons (Neo/Forge) | jadeaddons | both | интерфейс-удобство | Справка | Поддержка дополнительных модов в Jade. | — | — |
| Jade 🔍 | jade | both | интерфейс-удобство | Справка | Показывает, на что смотришь (блок/существо, HP, энергия). | — | — |
| Just Enough Items (JEI) | jei | both | интерфейс-удобство | Справка | Каталог предметов и рецептов (основа для книги и крафта). | — | — |
| Just Enough Mekanism Multiblocks | jei_mekanism_multiblocks | both | интерфейс-удобство | Справка | Расчёт мультиблоков Mekanism прямо в JEI. | — | — |
| Kambrik | kambrik | both | библиотека/оптимизация | — | — | — | — |
| Konkrete | konkrete | client | библиотека/оптимизация | — | — | — | — |
| Kotlin for Forge | ? | both | библиотека/оптимизация | — | — | — | — |
| Krypton Reno | krypton_fnp | both | библиотека/оптимизация | — | — | — | — |
| KubeJS | kubejs | both | библиотека/оптимизация | — | (код сборки; книга zhivoymir делается через него) | — | — |
| L_Ender's Cataclysm | cataclysm | both | игровой контент | Искатель приключений | Боссы Катаклизма, новые измерения-арены, экипировка из игнития. | — | Первый босс/игнитий — <ItemLink id="cataclysm:ignitium_ingot"/> ✓ (дроп босса). |
| libIPN | libipn | client | библиотека/оптимизация | — | — | — | — |
| Lionfish-API | lionfishapi | both | библиотека/оптимизация | — | — | — | — |
| Lithium | lithium | both | библиотека/оптимизация | — | — | — | — |
| Lithostitched | lithostitched | both | библиотека/оптимизация | — | — | — | — |
| Living Colonists for MineColonies | livingcolonists | both | игровой контент | Правитель | Колонисты пишут в чат, помнят разговоры, общаются. | — | Механика в чате; предметов нет. |
| Living Voice | livingvoice | server | игровой контент | Правитель | Голосовые разговоры с жителями MCA/колонистами через Simple Voice Chat (речь→распознавание→ответы). | — | Голосовой чат (см. Simple Voice Chat); предметов нет. |
| Let Me Despawn | letmedespawn | server | библиотека/оптимизация | — | — | — | — |
| Lootr | lootr | both | игровой контент | Искатель приключений | Лут в сундуках структур уникален для каждого игрока. | — | Сундуки структур (без предметов). |
| Man of Many Planes | man_of_many_planes | both | игровой контент | Искатель приключений | Дополнительные самолёты с поддержкой Create. | — | Самолёты (ID по доке: man_of_many_planes:…, уточнить). |
| Trade Post for MineColonies | mctradepost | both | игровой контент | Правитель | Экономическое исследование MineColonies: рынок, курорт, переработка, торговые посты. | — | Исследование в колонии (см. MineColonies); ID по доке). |
| MCA: Conversations | mcaconversations | both | игровой контент | Правитель | Глубокие диалоги MCA: личные темы, открывающиеся по уровню сердца. | — | Диалоги с жителями/супругом. |
| Mekanism Generators | mekanismgenerators | both | игровой контент | Инженер | Генерация энергии Mekanism: ветряки, солнечные, газовые, ядерные. | Ponder (Mekanism: Ponders). | Первый генератор — ветряк/солнечный (mekanismgenerators:solar_generator, уточнить) — смотреть Ponder. |
| Mekanism: Ponders | mekanism_ponders | both | интерфейс-удобство | Справка | Ponder-сцены для механизмов Mekanism (обучение в игре). | Ponder-сцены Mekanism. | — |
| Mekanism Tools | mekanismtools | both | игровой контент | Инженер | Инструменты и броня Mekanism (осмиевые → атомные). | Ponder (Mekanism: Ponders). | Инструменты из слитков (напр. mekanism:ingot_osmium ✓ → tools). |
| Mekanism | mekanism | both | игровой контент | Инженер | Техника: машины, газы, химия, энергия (joules), 5 ступеней сплавов. | Ponder (Mekanism: Ponders) + JEI. | Первый механизм — металлургический инфузер <ItemLink id="mekanism:metallurgic_infuser"/> ✓ (рецепт: печка+железо+осмий+редстоун). |
| Melody | melody | client | библиотека/оптимизация | — | — | — | — |
| MezzConfig | ? | both | библиотека/оптимизация | — | — | — | — |
| Compatibility addon for MineColonies | minecolonies_compatibility | both | библиотека/оптимизация | — | — | — | — |
| Tweaks addon for MineColonies | minecolonies_tweaks | both | библиотека/оптимизация | — | — | — | — |
| Minecolonies: War 'N Taxes  | minecolonytax | both | игровой контент | Правитель | Война, налоги, шпионаж, осады колоний; кодекс. | Своя Patchouli-книга: <ItemLink id="minecolonytax:war_taxes_codex"/> ✓ (book.json model). | Книга minecolonytax:war_taxes_codex; команды /wnt (см. Справку). |
| MineColonies | minecolonies | both | игровой контент | Правитель | Собственная колония: рабочие, постройки, исследования, технология. | — | Стартовый лагерь <ItemLink id="minecolonies:supplycampdeployer"/> ✓ (выдаётся при первом входе). |
| MCA Reborn | mca | both | игровой контент | Правитель | Жители-люди: семья, брак, дети, профессии, отношения на сервере. | — | Начать: поговорить с жителем; предметы подарков — напр. <ItemLink id="mca:engagement_ring"/> ✓ (позже). |
| ModernFix | modernfix | both | библиотека/оптимизация | — | — | — | — |
| Moonlight Lib | moonlight | both | библиотека/оптимизация | — | — | — | — |
| More Culling | moreculling | client | библиотека/оптимизация | — | — | — | — |
| Mouse Tweaks | mousetweaks | client | интерфейс-удобство | Справка | Улучшения мыши в инвентаре: перетаскивание, скролл. | — | — |
| Multi Mine | multimine | both | игровой контент | Быт | Копание нескольких блоков сразу (прогресс сохраняется). | — | Удобство без предметов. |
| Multi-Piston | multipiston | both | игровой контент | Инженер | Универсальный поршень: двигает несколько блоков, любой формы, с GUI. | — | Блок <ItemLink id="multipiston:multipiston"/> ✓ (крафт; id по lang? подтверждён lang-ключ block.multipiston.multipiston). |
| Nedologin | nedologin | both | библиотека/оптимизация | — | (авторизация на сервере) | — | — |
| Night Lights | nightlights | both | игровой контент | Искатель приключений | Дополнительные незеритовые предметы и блоки. | — | Крафт; ID по доке (уточнить). |
| Not Enough Animations | notenoughanimations | client | интерфейс-удобство | Справка | Анимации от третьего лица. | — | — |
| Create: Numismatics | numismatics | both | игровой контент | Инженер | Валюта в стиле Create: монеты, банкоматы, автоматы. | Ponder. | Монета <ItemLink id="numismatics:sun_dollar"/> (id уточнить). |
| Patchouli | patchouli | both | библиотека/оптимизация | — | (движок книг у модов: см. «Своё руководство» по модам) | — | — |
| Pathfinding Edition for MineColonies | colonypathingedition | both | библиотека/оптимизация | — | (переработанный pathfinding колонистов) | — | — |
| playerAnimator | playeranimator | both | библиотека/оптимизация | — | — | — | — |
| PneumaticCraft: Repressurized | pneumaticcraft | both | игровой контент | Инженер | Техника на сжатом воздухе: давление, трубы, программируемые дроны. | Своя Patchouli-книга: <ItemLink id="pneumaticcraft:manual"/> ✓ (book.json model). | Первый механизм — воздушный компрессор <ItemLink id="pneumaticcraft:air_compressor"/> ✓ (рецепт: укреп. кирпичи+печка+труба давления). |
| Polymorph | polymorph | both | интерфейс-удобство | Справка | Выбор результата крафта при конфликте рецептов. | — | — |
| Puzzles Lib | puzzleslib | both | библиотека/оптимизация | — | — | — | — |
| Railcraft Engines | railcraftengines | both | игровой контент | Инженер | Паровые двигатели для Railcraft (классика). | — | Паровой двигатель railcraftengines:steam_engine (id по доке; уточнить). |
| Railcraft Reborn | railcraft | both | игровой контент | Инженер | Железные дороги: рельсы, локомотивы, котлы, логика путей. | Своя Patchouli-книга: <ItemLink id="railcraft:guide_book"/> ✓ (book.json model). | Книга railcraft:guide_book; первый шаг — рельсы/локомотив (ID уточнить). |
| Regions Unexplored | regions_unexplored | both | игровой контент | Мир | Новые биомы Оверворлда и Незера. | — | Исследование мира; старта-предмета нет. |
| Resourceful Config | resourcefulconfig | both | библиотека/оптимизация | — | — | — | — |
| Resourceful Lib | resourcefullib | both | библиотека/оптимизация | — | — | — | — |
| Rhino | rhino | both | библиотека/оптимизация | — | — | — | — |
| Scalable Cat's Force | ? | both | библиотека/оптимизация | — | — | — | — |
| Serene Seasons | sereneseasons | both | игровой контент | Хозяйство | Сезоны: зима/весна/лето/осень, температура, рост культур. | — | Календарь сезонов (в книге — таблица); предметов нет. |
| Shield Expansion | shieldexp | both | игровой контент | Искатель приключений | Щиты с тирами и парированием. | — | Щиты из ресурсов (ID уточнить). |
| Simple Voice Chat | voicechat | both | интерфейс-удобство | Справка | Голосовой чат (близость, группы) — база для Living Voice. | — | — |
| Skylands UpPort | skylands_upport | both | игровой контент | Мир | Возвращённое измерение Skylands (небесные острова). | — | Портал в Skylands (ID уточнить на этапе 2). |
| Sodium Dynamic Lights | sodiumdynamiclights | client | интерфейс-удобство | Справка | Динамическое освещение от предметов в руках. | — | — |
| Sodium Extra | sodium_extra | client | интерфейс-удобство | Справка | Доп. настройки Sodium (анимация, погода, интерфейс). | — | — |
| Sodium | sodium | client | библиотека/оптимизация | — | — | — | — |
| Sound Physics Remastered | sound_physics_remastered | both | интерфейс-удобство | Справка | Реалистичный звук: эхо, поглощение стенами, реверберация. | — | — |
| spark | spark | both | библиотека/оптимизация | — | — | — | — |
| Spartan Weaponry Unofficial | spartan_weaponry_unofficial | both | игровой контент | Искатель приключений | Арсенал оружия: мечи, алебарды, луки, копья. | — | Крафт оружия из дерева→незерит (ID по доке: spartan_weaponry:*; уточнить). |
| Spelunkery | spelunkery | both | игровой контент | Искатель приключений | Подземный геймплей: новые руды, механизмы шахтёра, порталы. | — | Искать под землёй; магнетитовый/rесурсы (ID уточнить). |
| Structure Layout Optimizer | structure_layout_optimizer | both | библиотека/оптимизация | — | — | — | — |
| Structurize | structurize | both | игровой контент | Правитель | Копирование и вставка построек (для своих схем). | — | Инструмент строителя-схемщика (ID уточнить). |
| Supplementaries | supplementaries | both | игровой контент | Быт | Vanilla+ блоки: канаты, клей, знаки, сигнальные фонари. | — | Крафт; ID по доке (уточнить). |
| TarotCards: Remastered | tarotcards | both | игровой контент | Быт | Карты Таро со случайными пассивными бонусами. | — | Колода/карты из лута (напр. tarotcards:the_fool ✓; id самой колоды уточнить). |
| Tectonic | tectonic | both | игровой контент | Мир | Новый рельеф: большие горы, каньоны, пляжи. | — | Исследование мира; старта-предмета нет. |
| TerraBlender | terrablender | both | библиотека/оптимизация | — | — | — | — |
| The Graveyard (Fabric/Neoforge) | graveyard | both | игровой контент | Искатель приключений | Кладбища, склепы, боссы и новая нежить. | — | Искать кладбища по миру; старта-предмета нет (вариант: спрос в сундуках — уточнить). |
| Torch hit! | torch_hit | both | игровой контент | Быт | Удар факелом поджигает/наносит урон. | — | Удобство без предметов. |
| Villager Recruits | recruits | both | игровой контент | Правитель | Наёмные рекруты с рангами, фракции, командование (как в Bannerlord). | — | Экраны командования (клавиши — Справка); предмет-вербовки уточнить. |
| VillagersTradingPlus | villagertradingplus | both | библиотека/оптимизация | — | (datapack-движок торговли жителей — влияет на торговлю; разобраться в конфиге на этапе 2) | — | — |
| Waystones | waystones | both | игровой контент | Быт | Телепортация между вехами (waystone), возврат на точку смерти. | — | Веха <ItemLink id="waystones:waystone"/> ✓ (крафт/находка). |
| Xaero's Minimap | xaerominimap | both | интерфейс-удобство | Справка | Миникарта. | — | — |
| Xaero's World Map | xaeroworldmap | both | интерфейс-удобство | Справка | Полная карта мира (самопишущаяся). | — | — |
| YUNG's API | yungsapi | both | библиотека/оптимизация | — | — | — | — |
| YUNG's Better Desert Temples | betterdeserttemples | both | игровой контент | Мир | Пустынные храмы переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Better Dungeons | betterdungeons | both | игровой контент | Мир | Данжи переработаны (многоуровневые). | — | Искать /locate; старта-предмета нет. |
| YUNG's Better End Island | betterendisland | both | игровой контент | Мир | Остров Края и битва с драконом переработаны. | — | В Край; старта-предмета нет. |
| YUNG's Better Jungle Temples | betterjungletemples | both | игровой контент | Мир | Храмы джунглей переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Better Mineshafts | bettermineshafts | both | игровой контент | Мир | Заброшенные шахты переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Better Nether Fortresses | betterfortresses | both | игровой контент | Мир | Крепости Незера переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Better Ocean Monuments | betteroceanmonuments | both | игровой контент | Мир | Океанские монументы переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Better Strongholds | betterstrongholds | both | игровой контент | Мир | Крепости (stronghold) переработаны. | — | Искать /locate; старта-предмета нет. |
| YUNG's Menu Tweaks | yungsmenutweaks | client | интерфейс-удобство | Справка | Улучшения меню и списков (в т.ч. настроек). | — | — |
| Settlement Bridge | zmbridge | server | игровой контент | Правитель | Мост поселений: связь деревень MCA и колоний MineColonies (переселение, охрана). | — | Команды/механика трейта поселений; предметов нет (уточнить). |
| Zhivoy Mir Client Tweaks | zmclient | client | интерфейс-удобство | Справка | Клиентские правки озвучки (дальность голоса жителей). | — | — |
| Living World God | zmgod | server | игровой контент | Бог | ИИ-бог: слышит молитвы (вслух/чат/алтарь), вмешивается знамениями. | — | Алтарь из магнетита (см. существ. стр. god.md); предметов нет. |

## Примечания
- Стартовые точки «—» (чистые механики/мир) — в книге даём ориентир, а не ItemLink.
- Worldgen-моды (YUNG, BoP, RU, Tectonic, Skylands, CTV, D&T) предлагается свести в раздел «Мир» (см. guide-outline.md), хотя в плане его нет.
- ID, требующие проверки на этапе 2: chimes:wind_chime, numismatics:*, comforts:sleeping_bag, corpse:* , spartan_weaponry:*, immersive_armors, nightlights, railcraftengines:steam_engine, man_of_many_planes:*, multipiston(блок), skylands_upport:portal, tarotcards:колода, MineColonies аддоны (colonist_thieves, mctradepost, domum_ornamentum, structurize).