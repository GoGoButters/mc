# Факты раздела «Искатель приключений» (adv/) — сессия C

Каждый факт подтверждён: jar в `/tmp/depcache`, конфиги репо, lang-файлы (ru_ru из kubejs/assets или jar). Без источника — не писать.

## Бой (adv/combat.md)
- Better Combat: клиентская боёвка, настройки (удержание атаки, атака вместо копания рядом с врагами, удар сквозь траву, добыча оружием) — lang `text.autoconfig.bettercombat.*` (bettercombat-2.4.0.jar, en_us).
- Двуручное оружие — lang `item.held.two_handed` (bettercombat.jar).
- Combat Roll: клавиша, id KeyMapping **keybinds.combat_roll.roll** — подтверждено байткодом `net/combat_roll/client/Keybindings.class` (`ldc "keybinds.combat_roll.roll"` + `new KeyMapping`); lang `keybinds.combat_roll.roll`; атрибуты count/distance/recharge и зачарования acrobat/longfooted/multi_roll — lang en_us combat_roll-2.0.6.jar.
- Shield Expansion: щиты wooden/golden/iron/diamond/netherite/paragon/griefer — `item.shieldexp.*` (ru_ru shieldexp-1.4.3.jar); парирование: классы shieldexp: getParryWindow/parryCooldown (строка байткода).
- Spartan Weaponry: namespace **spartan_weaponry_unofficial**; части: simple_handle/handle/simple_pole/pole, greaseball, explosive_charge — ru_ru jar; оружие по материалам: напр. `iron_dagger`, `iron_spear`, `iron_greatsword`, `iron_halberd`, `iron_throwing_knife` и др. — lang en_us spartan_weaponry_unofficial-1.2.3.jar.
- Immersive Armors: наборы bone/wither/warrior/heavy/robe/slime/divine/prismarine — `item.immersive_armors.*` (ru_ru immersive_armors-1.7.6.jar).
- Night Lights: декоративные светящиеся блоки-фигурки (frog/mushroom/octopus/…) во всех 16 цветах — `block.nightlights.*` (en_us nightlights-1.3.0.jar).

## Артефакты (adv/artifacts.md)
- Артефакты не крафтятся; лут из сундуков структур; слоты Curios. Находятся через достижение «Археолог-любитель» (artifacts.ru: advancements.amateur_archaeologist).
- `artifacts:everlasting_beef` — «бесконечная» еда (не тратится; кулдаун/дроп с коров — config.items.everlasting_beef.* en_us artifacts-13.2.5.jar).
- `artifacts:cloud_in_a_bottle` — двойной прыжок (config.items.cloud_in_a_bottle.*).
- `artifacts:panic_necklace`, `artifacts:obsidian_skull`, `artifacts:running_shoes`, `artifacts:flame_pendant` — существуют по lang en_us.
- Подражатель (mimic): `artifacts:mimic_spawn_egg`; достижение «Победитель сундука» (убейте подражателя) — ru lang.
- В сборке артефакты отображаются на английском (в kubejs/ru jar нет item-переводов).

## Боссы и нежить (adv/bosses.md)
- Graveyard: конфиг репо `config/graveyard/graveyard.json5`:
  - «захваченные деревни» (overrun_village, overrun_desert/plains/savanna/snowy/taiga_village) — enabled: false, chance: 0.
  - Структуры включены: алтарь (chance 0.7), склеп (0.7, Y −40…−10), большое/среднее кладбище (0.7), малое кладбище и могилы (1.0), дом с привидениями (0.7), руины (0.7), тюрьма лича (Y 210).
  - Мобы: гуль (burns-in-sun, вес 10), выходец (вес 25, горит днём), костяной крипер (вес 25, не разрушает блоки, радиус 3.5), кошмар (вес 10), призрак/аколит/испорченные — без природного спавна (weight 0); жнец душ выключен (enabled false).
  - Орды нежити включены, только ночью (chancePerCheck 0.2).
  - Хроники: страницы в луте сундуков (chronicle.pagesInLoot true).
  - Призыв Испорченного чемпиона по конфигу: needsStaffFragments true; кровь с жителей (bloodEntities villager); 2 фазы боя (casting 400 HP / hunt 200), музыкa и заклинания.
- Graveyard предметы/мобы (ru_ru jar): `graveyard:dark_iron_ingot` (Слиток тёмного железа), `graveyard:bone_dagger`, `graveyard:vial_of_blood` (Пузырёк с кровью, 8 уровней), гробы, саркофаг; босс — «Испорченный чемпион» (entity.graveyard.lich); фрагменты костяного посоха (lower/middle/upper_bone_staff) раскладываются рядом с алтарём; посохи белый/чёрный/красный/циановый (item.graveyard.*_bone_staff).
- Cataclysm: `cataclysm:ignitium_ingot` (Игнитовый слиток) + броня игнития (шлем-клеймо, поножи-«огненные рефлексы», ботинки-ходьба по лаве, нагрудник-совместим с элитрами), `cataclysm:ignitium_upgrade_smithing_template` (апгрейд незерита) — ru_ru kubejs/cataclysm.
- Cataclysm боссы (ru_ru jar): Игнис, Незеритовое чудище, Эндер-голем, Левиафан (в воде, вне воды неуязвим), Древние Останки.
- Creeper Overhaul: 16 биомных криперов (тропический, бамбуковый, пустынный, натёчный, снежный и др.) — ru_ru CreeperOverhaul-4.0.6.jar.

## Небо и космос (adv/sky.md)
- Immersive Aircraft: `immersive_aircraft:quadrocopter` (Квадрокоптер), biplane, airship, gyrodyne… (модель item + ru_ru jar); детали: корпус, двигатель, пропеллер, парус, бойлер — item.immersive_aircraft.*.
- Man of Many Planes: `man_of_many_planes:scarlet_biplane` (Алый биплан), `man_of_many_planes:economy_plane` — ru_ru jar.
- Ad Astra (adastra-1.21.1-1.16.26-neoforge.jar):
  - Книга-путеводитель — Patchouli-книга **ad_astra:astrodux** (не предмет!), рецепт data/ad_astra/recipe/astrodux.json: книга + 4 железа + 4 синих красителя → patchouli:guide_book (компонент ad_astra:astrodux). Тег `<Recipe id="ad_astra:astrodux"/>` валиден.
  - `ad_astra:nasa_workbench` (Верстак НАСА), `ad_astra:launch_pad` (Стартовая площадка, редстоун-запуск) — block.* ru.
  - Ракеты 1–4 уровней: `ad_astra:tier_1_rocket` … (models/item/tier_*_rocket.json; entity.* ru); детали: двигатели, баки, носовой конус, плавники — рецепты в JEI.
  - Планеты/орбиты: луна, марс, меркурий, венера, гласио (+орбиты) — planet.ad_astra.* ru.
  - Скафандр: `ad_astra:space_suit` (Скафандр), шлем/поножи/ботинки, незеритовый скафандр, реактивный костюм — item.* ru; кислород: oxygen_distributor (Распылитель кислорода), oxygen_loader.