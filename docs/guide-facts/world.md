# Факты раздела «Мир» (world/) — сессия C

## Биомы и рельеф (world/biomes.md)
- Biomes O' Plenty: 65 ключей биомов в `assets/biomesoplenty/lang/ru_ru.json` (bamboo_grove, bayou, bog, cherry_blossom_grove, clover_patch, cold_desert и др.).
- Конфиг репо `config/biomesoplenty/generation.toml`: веса регионов BoP в Оверворлде (primary 8, secondary 6, rare 2) и Незере (10/2).
- Regions Unexplored: 82 ключа биомов в `assets/regions_unexplored/lang/ru_ru.json` (jar regions-unexplored-0.6.3, ru_ru).
- Tectonic: переопределяет датапаком `data/minecraft/worldgen/` (density_function/noise_router, карверы cave/canyon) + собственные карверы `data/clifftree/worldgen/configured_carver/inferno_cave`, `ocean_canyon` — tectonic-3.0.28.jar. → меняет континентальность (горы, каньоны, глубокие пещеры).
- Bugs Aplenty: жуки-мобы (жуки, стрекозы, светлячки-варианты) — entity.bugs_aplenty.* (en_us, ru нет; именование от русского клиента не переведено).
- Creeper Overhaul — 16 биомных криперов (см. adv.md).

## Скайленды (world/skylands.md)
- modid **skylands_upport** (Skylands UpPort, jar skylands_upport-0.0.1).
- Предмет-активатор: `skylands_upport:skylands` (ru: «Перо Небесных земель»).
- Рецепт: data/skylands_upport/recipe/skyfeatherrecipe.json — 8 призмарина (minecraft:prismarine) + перо (minecraft:feather).
- **Портал**: рамка из **призмариновых кирпичей** (minecraft:prismarine_bricks) — байткод SkylandsPortalShape.lambda$static$0 сравнивает блок с Blocks.PRISMARINE_BRICKS; активация: ПКМ пером по пустому блоку внутри/перед рамкой → portalSpawn (SkylandsItem.useOn), перо получает урон (hurtAndBreak 1). Портал-блок skylands_upport:skylands_portal (blockstate axis=x/z).
- Измерение data/skylands_upport/dimension/skylands.json («Небесные земли»); достижения welcometo_skylands (войти) и wiki (справка об истории Скайлендов).
- Ру-перевод в kubejs/assets/skylands_upport/lang/ru_ru.json.

## Структуры (world/structures.md)
- YUNG (id структур для /locate по файлам data/<mod>/worldgen/structure/):
  - betterdungeons: small_dungeon, skeleton_dungeon, zombie_dungeon, spider_dungeon, small_nether_dungeon;
  - betterdeserttemples: desert_temple; betterjungletemples: jungle_temple;
  - bettermineshafts: mineshaft_* (acacia, desert, dripstone, ice, jungle, lush, mesa, mushroom …);
  - betterfortresses: fortress; betteroceanmonuments: ocean_monument; betterstrongholds: stronghold.
  - Better End Island: меняет остров Края данными (data), id структуры нет.
- Dungeons & Taverns v4.4.4: ~140 структур, namespace **nova_structures** — tavern_* (таверны по биомам), ruin_town, firewatch_tower_*, nether_keep, end_castle/lighthouse/ship, conduit_ruin, illager_camp/hideout/manor и др.; + правка minecraft:village_taiga.
- ChoiceTheorem's Overhauled Village (ctov): деревни и аванпосты разбойников по биомам, id ctov:pillager_outpost_* (badlands/beach/desert/jungle/plains/savanna/snowy/swamp/taiga…), ctov:village_*.
- Lootr: блоки-сундуки структур (block.lootr.lootr_chest/barrel/shulker/trapped_chest, Decorated Loot Pot) — lang lootr-1.11.38.127.jar; механика: сундук структур хранит лут отдельно для каждого игрока (API LootrChestBlockEntity с Player-методами; механика мода). Старт: любой сундук структуры.