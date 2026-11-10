# Факты раздела «Быт» (life/) — сессия C

## Рюкзаки (life/backpacks.md)
- `backpacked:backpack` (Рюкзак) — ru_ru backpacked-3.0.5.jar (item.backpacked.backpack).
- Слот Curios «Рюкзак» (curios.identifier.backpacked).
- `backpacked:unlock_token` — Unlock Token (en_us; в ru отсутствует) — расширяет рюкзак.
- Клавиши: `key.backpacked.open_backpack` (Открыть рюкзак), `key.backpacked.open_management` — lang jar.

## Путешествия (life/travel.md)
- Waystones: `waystones:waystone` (Путеводный обелиск) + mossy/sandy варианты — block.* ru_ru waystones-21.1.46.jar.
- Телепорт межмировой — +27 XP; в пределах измерения — scaled_add_xp_cost(distance, 0.01) (строка в WaystonesConfigData, вызegistered config); со свитка/плиты — 0 (multiply_xp_cost(0)).
- Предметы: warp_scroll (Свиток перемещения), return_scroll (Свиток возврата), warp_stone (Камень перемещения), bound_scroll (Свиток привязки), warp_dust (Пыль перемещения), attuned_shard (Настроенный осколок) — ru_ru.
- Другие обелиски: портовый обелиск (portstone), плита перемещения (warp_plate), «судьбоносные» sharestone цвета.
- Comforts: `comforts:hammock_red` (Красный гамак; 16 цветов), спальные мешки — блоки `comforts:sleeping_bag_*` (16 цветов), `comforts:rope_and_nail` (Верёвка и гвоздь) — ru_ru comforts-9.0.5.jar.

## Сундуки (life/chests.md)
- Iron Chests: `ironchest:iron_chest` (Железный сундук) и медный/серебряный/золотой/алмазный/хрустальный/обсидиановый (+ловушки) — block.* ru_ru ironchest-16.0.7.jar.
- Апгрейды-предметы: wood_to_iron, wood_to_copper, copper_to_iron, copper_to_silver, silver_to_gold, iron_to_gold, gold_to_diamond, diamond_to_crystal, diamond_to_obsidian — item.* ru_ru jar; рецепты data/ironchest/recipe/upgrades/*.

## Ванлла-удобства (life/vanilla-plus.md)
- Amendments: смешивание зелий и красителей в котлах (liquid_cauldron/dye_cauldron), двойной торт, подвесной горшок, потолочный флаг, настенный фонарь, крючок для инструмента, груда черепов — block.* ru_ru amendments-2.2.7.jar.
- Supplementaries: верёвка (block.supplementaries.rope), мешок (sack), баночка (jar), клетка (cage), подсвечник (candle_holder), бра (sconce), кострище (fire_pit), мехи (bellows), глобус (globe), бамбуковые шипы — ru_ru supplementaries-4.0.2.jar.
- Chimes: ветряные колокольчики — `chimes:amethyst_chimes` («Аметистовый колокольчик ветра»), bamboo_chimes, copper_chimes, iron_chimes, glass_bells — ru_ru kubejs/chimes. (id «chimes:wind_chime» из описи НЕ существует — заменён на конкретные.)
- Torch hit: удар факелом (нет предметов) — описано в описи docs/guide-inventory.md.
- FallingTree: конфиг репо config/fallingtree.json — breakMode INSTANTANEOUS, detectionMode WHOLE_TREE, treeBreaking true, leavesBreaking true, дерево падает целиком.
- Multi Mine: копание нескольких блоков с сохранением прогресса (клиентский твик; опись).
- Cosmetic Armor Reworked: косметический слой брони поверх боевой; клавиша-кнопка в инвентаре, ключ lang cos.key.opencosarmorinventory; команда /cos — ru_ru jar cosmeticarmorreworked.
- Corpse: при смерти появляется тело с вещами (entity.corpse.corpse «Труп»), история смертей по клавише `key.corpse.death_history` — ru_ru corpse-1.1.22.jar.

## Таро (life/tarot.md)
- 22 старших аркана: карты tarotcards:the_fool, tarotcards:death, tarotcards:the_chariot, tarotcards:the_lovers, the_moon, the_sun, justice, judgement, strength, temperance и др. — item.* ru_ru tarotcards-2.3.5.jar (например «Таро: Смерть»).
- Колода: `tarotcards:tarot_deck` («Таро колода», хранит карты; рецепт: 4 золотых самородка + 4 бумаги + сундук — data/tarotcards/recipe/tarot_deck.json).
- Слот Curios «tarot» (data/tarotcards/curios/slots/tarot).
- Карты: рецептов в jar нет (кроме колоды) — находятся в луте; достижение «deck_collector» — собрать все 22 (data/tarotcards/advancement/deck_collector.json, criterion count 22).