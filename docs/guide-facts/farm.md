# Факты для раздела «Хозяйство» (farm/), сессия B

Проверено 2026-11-10 по jar-файлам в `/tmp/depcache` (lang ru_ru/en_us, рецепты data/*/recipe) и kubejs-переводам сборки.

## Farmer's Delight 1.3.4
- `farmersdelight:cooking_pot` = «Кухонный котёл» (jar ru). Рецепт — в JEI (данные jar data/farmersdelight/recipe/cooking_pot.json есть в каталоге).
- `farmersdelight:cutting_board` = «Разделочная доска» (jar ru).
- `farmersdelight:stove` = «Кухонная плита» (jar ru).
- Готовка: котёл греется снизу (плита/огонь), ингредиенты + жидкая основа, порционные блюда в мисках; разделочная доска — нарезка/разделка ножом. Рецепты — через JEI.

## Crabber's Delight 1.3.1
- `crabbersdelight:crab_trap` = «Крабовая ловушка» (jar ru block.crabbersdelight.crab_trap). Тематика в JEI: crabbersdelight.jei.crab_trap_loot = «Крабовая ловушка».
- Добыча: `crabbersdelight:crab` = «Краб», `crabbersdelight:cooked_crab` = «Жареный краб», `crabbersdelight:clawster` = «Клешнехват», `crabbersdelight:shrimp` = «Креветка», `crabbersdelight:clam` = «Моллюск», щупальца спрута/светящегося (jar ru).
- Хранение (jar ru): `block.crabbersdelight.crab_barrel` = «Бочка с крабами», `clam_barrel`, `shrimp_barrel`, `cod_barrel`, `salmon_barrel` и др.
- Стройматериал: пальма — `block.crabbersdelight.palm_log` = «Пальмовое бревно» и производные (jar ru).
- Инструменты: `block.crabbersdelight.tackle_box` = «Ящик со снастями», `worm_bin` = «Червячник» (jar ru).

## Serene Seasons 10.1.0.9
- `sereneseasons:calendar` = «Календарь» (jar ru item.sereneseasons.calendar). Рецепт (jar data/sereneseasons/recipe/calendar.json): 8×paper + minecraft:clock, шаблон "PPP/PCP/PPP".
- `sereneseasons:season_sensor` = датчик сезона (jar data recipe season_sensor.json).
- Цикл: 4 сезона с суб-сезонами (StartingSubSeason 0–12), снег/лёд зимой (weather_settings.generate_snow_ice в SeasonsConfig), частота осадков меняется по сезону (change_weather_frequency).
- Растения: теги сезонности в jar data/sereneseasons/tags/item: spring_crops / summer_crops / autumn_crops / winter_crops / year_round_crops; совместимые культуры не плодоносят вне сезона (механика мода: пашня «бесплодная» вне сезона, greenhouse_glass даёт теплицу).

## Create: Central Kitchen 2.6.2b
- Связка Create ↔ кухня (Farmer's Delight и другие моды готовки). Своих предметов нет (управление рецептами кухонных модов через Create-механику).
- Ponder-сцены (переведены в jar ru, assets/create_central_kitchen/lang/ru_ru.json):
  - cooking_pot — «Автоматизацией с Create для кухонного котла»: шлюз извлекает готовую еду.
  - cutting_board — «Автоматизацией с Create в разделывании»: механическая рука кладёт сырые ингредиенты на доску, автономный активатор с ножом режет, механическая пила тоже режет.
  - chiller — «Автоматизацией с Create для охладителя»: механическая рука кладёт лёд/снег в охладитель.
- Входит в раздел Инженер (eng/index), но связан с кухней: страница kitchen-auto в farm/.

## Связи
- Кухонная автоматика: конвейеры и механические руки Create обслуживают котёл и разделочную доску; источник вращения — от Create (энергия/валы); от созданий для сумм — см. eng/create.md.
- Путь хозяина (сессия D): ферма+котёл farmersdelight:cooking_pot → сезоны (календарь sereneseasons:calendar) → кухонная автоматика Create (create_central_kitchen).
