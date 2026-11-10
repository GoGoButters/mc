# Факты для раздела «Инженер» (eng/), сессия B

Проверено 2026-11-10 по jar-файлам в `/tmp/depcache` (lang ru_ru/en_us, рецепты data/*/recipe) и kubejs-переводам сборки. Сборка: NeoForge 1.21.1, рецепты изменённые kubejs нет — только тег `kubejs/data/c/tags/fluid/crude_oil.json`.

## Общие связи
- **Нефть**: тег `c:crude_oil` = `pneumaticcraft:oil` (дефолт jar `data/c/tags/fluid/crude_oil.json`) + `ad_astra:oil` (наш kubejs-тег). PNC и Ad Astra делят нефть.
- Порядок: Create (кинетика) → Mekanism (машины/химия) → AE2 (ME-сеть). Мосты: Applied Mekanistics (газы/энергия в ME), ColonyLink (стройка колонии через ME-автокрафт), Create: Recycled (дробление компонентов Create обратно в ресурсы).

## Create 6.0.10
- `create:wrench` = «Гаечный ключ» (jar ru). Рецепт (data/create/recipe/crafting/kinetics/wrench.json): 2 золотые пластины (c:plates/gold), шестерня create:cogwheel, деревянный стержень (c:rods/wooden) — форма "GG/GP/ S".
- `create:hand_crank` = «Рукоятка» (jar ru, block.create.hand_crank). ПКМ — крутить против часовой, Sneak+ПКМ — по часовой (ponder.hand_crank).
- `create:cogwheel` = «Шестерня», `create:large_cogwheel` = «Большая шестерня», `create:powered_shaft` = «Силовой вал», `create:mechanical_bearing` = «Механический вращатель», `create:mechanical_press` = «Механический пресс», `create:crushing_wheel` = «Дробильное колесо», `create:track` = «Железнодорожный путь», `create:steam_engine` = «Паровой двигатель».
- Ponder-сцены: зажать W и нажать ПКМ по механизму (стандарт Create, пондер-заголовки в jar ru/en).
- Мультипоршень `multipiston:multipistonblock` = «Мультипоршень» (наш kubejs ru; en block.multipiston.multipistonblock).

## Create Crafts & Additions 1.7.2
- `createaddition:connector` = «Низковольтный соединитель» (jar ru).
- `createaddition:electric_motor` = «Электромотор» (jar ru); `createaddition:alternator` = «Генератор переменного тока» (jar ru).
- `createaddition:copper_spool` = «Медная катушка», `createaddition:copper_wire` = «Медный провод» (jar ru).
- Ponder-перевод в нашем kubejs (assets/createaddition/lang/ru_ru.json): источник → разъём в режиме «Вытягивание», потребитель → «Вталкивание»; провода — ПКМ медной катушкой по двум разъёмам; пустая катушка — снять провод.

## Numismatics 1.1.0
- Монеты — 6 номиналов (тег `data/numismatics/tags/item/coins.json`): `numismatics:spur` (Шпора), `bevel` (Фаска), `sprocket` (Звёздочка), `cog` (Шестерёнка), `crown` (Коронка), `sun` (Солнышко) — ru-имена из jar ru.
- `numismatics:bank_terminal` = «Банковский терминал» (jar ru). Рецепт: create:precision_mechanism + create:industrial_iron_block + create:electron_tube (jar data/numismatics/recipe/crafting/bank_terminal.json).
- `numismatics:blaze_banker` = «Всполох-банкир» (jar ru). «Инструкция банкира» `numismatics:banking_guide`; применяется к горелке всполоха (ponder из нашего kubejs).
- Торговые автоматы: `numismatics:vendor` = «Торговый автомат», `numismatics:andesite_depositor` = «Андезитовый валидатор» (jar ru). Цена в валюте мода (¤).
- Банковские карты: card/id_card/authorized_card 16 цветов (крафты в jar).

## Mekanism 10.7.19
- `mekanism:metallurgic_infuser` = «Металлургический наполнитель» (jar ru). Рецепт (jar data/mekanism/recipe/metallurgic_infuser.json): печка + железо (c:ingots/iron) + осмий (c:ingots/osmium) + редстоун (c:dusts/redstone), шаблон "I#I/ROR/I#I".
- `mekanism:enrichment_chamber` = «Камера обогащения» (jar ru). Рецепт (jar): стальной корпус `mekanism:steel_casing` («Стальной корпус») + базовые сплавы (mekanism:alloys/basic) + базовые схемы (c:circuits/basic) + железо.
- `mekanism:ingot_osmium` = «Осмиевый слиток», `mekanism:ingot_steel` = «Стальной слиток», `mekanism:ingot_refined_obsidian` = «Очищенный слиток обсидиана» (jar ru).
- `mekanism:osmium_compressor` = «Осмиевый компрессор», `mekanism:chemical_infuser` = «Химический наполнитель» (jar ru).
- `mekanism:basic_energy_cube` = «Базовый энергетический куб», `mekanism:energy_tablet` = «Энергетический планшет» (jar ru).
- Генераторы (MekanismGenerators, jar ru): `mekanismgenerators:solar_generator` = «Солнечная панель», `wind_generator` = «Ветрогенератор», `heat_generator` = «Теплогенератор», `gas_burning_generator` = «Газовый генератор».
- Инструменты (MekanismTools, jar ru): `mekanismtools:osmium_pickaxe` и др. осмиевые (item.mekanismtools.osmium_*).
- Mekanism: Ponders (`mekanism_ponders`): Ponder-сцены для мультиблоков (создание индукционной матрицы, промышленной турбины, термоядерного реактора и т.д.; список в assets/mekanism_ponders/lang.en_us header'ы, перевод в нашем kubejs/ru).

## AE2 19.2.18
- `ae2:charger` = «Зарядник» (jar ru). Рецепт (jar data/ae2/recipe/network/blocks/crystal_processing_charger.json): железо + медь (c:ingots), шаблон "aba/a  /aba".
- `ae2:inscriber` = «Высекатель» (jar ru). Рецепт (jar data/ae2/recipe/network/blocks/inscribers.json): железо + поршень + медь.
- `ae2:guide` = «Руководство» (jar ru). Получение: из обычной книги в заряднике (jar data/ae2/recipe/charger/guide.json — тип ae2:charger, ingredient minecraft:book).
- Клавиша: `key.ae2.guide` = «Открыть руководство по предметам» (jar ru). Категория key.ae2.category.
- Термины как в офиц. переводе: «МЭ» — `block.ae2.drive` = «МЭ накопитель», `block.ae2.chest` = «МЭ сундук», `item.ae2.matter_ball` нет нужды; `ae2:item_storage_cell_1k` = «1К МЭ предметная ячейка хранения», `ae2:energy_acceptor` = «Приёмщик энергии», `ae2:certus_quartz_crystal` = «Кристалл истинного кварца» (jar ru).
- Метеориты: кристаллы истинного кварца растут днём из настоящего кварца блоков; метеориты ищутся по compass — просто упомянуть (в jar есть charger/meteorite_compass.json).

## Applied Mekanistics 1.6.3 (appmek)
- `appmek:chemical_cell_housing` = «Корпус химической ячейки ME», `appmek:chemical_storage_cell_1k` = «Химическая ячейка памяти ME 1К» (…4k/16k/64k/256k), `appmek:portable_chemical_cell_1k` = «Переносная химическая ячейка 1К», `appmek:chemical_p2p_tunnel` = «Химический P2P тоннель» (jar ru).
- Назначение: газы Mekanism (и жидкости/энергия) в ME-сети через химические интерфейсы.

## ColonyLink 1.7.0
- `colonylink:colony_link_wand` = «Буфер обмена ColonyLink» (jar ru). Рецепт (jar data/colonylink/recipe/colony_link_wand.json): эндер-жемчуг + ae2:fluix_crystal + minecolonies:clipboard + ae2:logic_processor.
- `colonylink:colonylink_package` = «Посылка ColonyLink», `colonylink:warehouse_link_terminal` = «Терминал связи склада», `colonylink:colony_link_redirector` = «Перенаправитель ColonyLink» (jar ru).
- Назначение: стройка колонии MineColonies через автокрафт ME-сети.

## Create: Recycled 1.0.0
- Только рецепты (data/createrecycled/recipe/crushing/*.json): дробильное колесо возвращает компоненты Create (шлюзы, валы, трубы, механизмы и т.д.) в ресурсы. Своих предметов нет.

## PneumaticCraft: Repressurized 8.2.24
- `pneumaticcraft:air_compressor` = «Воздушный компрессор» (jar ru). Рецепт (jar data/pneumaticcraft/recipe/air_compressor.json): укреплённые кирпичи (tag pneumaticcraft:reinforced_stone_bricks) + печка + `pneumaticcraft:pressure_tube` (Напорная труба), шаблон "III/I T/IFI".
- `pneumaticcraft:manual_compressor` = «Ручной компрессор» (jar ru; рецепт jar).
- Книга: `pneumaticcraft:manual` (Patchouli, модель pneumaticcraft:manual; крафт — сжатое железо + книга, jar data/patchouli/recipe/guide_book.json).
- Дроны: `pneumaticcraft:drone` = «Дрон», `pneumaticcraft:harvesting_drone` = «Дрон-фермер», `pneumaticcraft:programming_drone` нет, `pneumaticcraft:collector_drone` = «Дрон-собиратель», `pneumaticcraft:logistics_drone` = «Дрон-логистик», `pneumaticcraft:guard_drone` = «Дрон-защитник» (jar ru entity-и item).
- Барокамера: `pneumaticcraft:pressure_chamber_wall/valve/interface/glass` («Барокамерная…») (jar ru).
- Нефть: тег c:crude_oil = pneumaticcraft:oil (+ наша ad_astra:oil).

## Railcraft Reborn 1.2.16
- Книга: Patchouli `railcraft:guide_book` (assets/railcraft/patchouli_books/guide_book; книжечка-крафт 2 типа: logbook — writable_book+gold+red_wool+planks, и patchouli_book_crafting).
- `railcraft:steam_locomotive` = «Паровоз» (jar ru).
- Пути: базовый `railcraft:strap_iron_track` (Strap Iron Track), улучшенные iron/reinforced/high_speed (jar en; ру-перевод в нашем kubejs/ru имеет только часть, для strap_iron ориентир en+описание).
- Вагонетки: `railcraft:cargo_minecart` = «Грузовая вагонетка», `railcraft:bed_minecart` = «Вагонетка с кроватью» (наш kubejs ru).
- Клавиша: `key.railcraft.bed_minecart.sleep` = «Спать в вагонетке с кроватью» (наш kubejs ru).

## Railcraft Engines 1.0.0
- `railcraftengines:hobbyist_steam_engine` = «Любительский паровой двигатель», `commercial_steam_engine` = «Коммерческий пар. двигатель», `industrial_steam_engine` = «Промышленный пар. двигатель» (наш kubejs ru). «Использует воду и твёрдое печное топливо», «Производит до %s FE/тик», «Генерирует пар внутри себя», «Требуется сигнал редстоуна» (tooltip'ы нашего kubejs ru).

## BuildCraft CE 8.0.27
- `buildcraftcore:wrench` = гаечный ключ (модель assets/buildcraftcore/models/item/wrench.json). Рецепт (jar data/buildcraftcore/recipe/wrench.json): каменная шестерня (buildcraftcore:gears/gear_stone) + железо, шаблон "I I/ G / I ".
- `buildcraftbuilders:quarry` = карьер. Рецепт (jar data/buildcraftbuilders/recipe/quarry.json): железные/золотые/алмазные шестерни + редстоун + алмазная кирка, шаблон "iri/gig/dpd".
- Русский интерфейс частично (assets/buildcraft/lang ru в kubejs — строки интерфейса, предметы en).

## CC:Tweaked 1.121.0
- `computercraft:computer_advanced` = «Продвинутый компьютер», `computercraft:turtle_advanced` = «Продвинутая черепашка» (jar ru).
- Встроенная справка: `help` в терминале компьютера.

## Advanced Peripherals 0.8.2a
- Книга: Patchouli `advancedperipherals:manual` (assets/advancedperipherals/patchouli_books/manual; en_us).
- Периферия (en_us — ru не найдено): `advancedperipherals:chat_box` = «Чат-коробка» (jar ru), `advancedperipherals:me_bridge` = «ME-мост» (jar ru), `advancedperipherals:geo_scanner` = «Гео-сканер» (jar ru), `block_reader`, `inventory_manager`, `energy_detector`, `player_detector`, `environment_detector`, `nbt_storage`, `rs_bridge`, `computer_tool`, `memory_card`.

## Связи для страниц
- create-electric: alternator(источник)–connector(вытягивание)–wire–connector(вталкивание)–electric_motor; numismatics — валюта на Create-механике (монеты, банковский терминал, автоматы).
- mek-ae2: appmek газы ↔ ME; colonylink стройка через ME; createrecycled — переработка Create в ресурсы.
- «Карта пути» (сессия D) — ступени: печка→инфузер→энергокамера (MeK) / гаечный ключ+рукоятка (Create) / зарядник→высекатель→процессоры (AE2) / компрессор (PNC) / рельсы и паровоз (Railcraft) / компьютеры (CC) — все ID в этом файле.
