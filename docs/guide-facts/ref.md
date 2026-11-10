# Факты раздела «Справка» (ref/) — сессия C

## commands.md (перенос, контент без потерь)
- Перенос `start/commands.md → ref/commands.md`; изменения только навигация (parent: ref/index.md; ссылка «В начало книги» → ../index.md). Клавиши key.push_to_talk, key.voice_chat, key.recruits.command_screen_key/team_screen_key/map_screen_key, key.minecolonytax.open_tax_gui подтверждены в jar (см. ниже).

## Клавиши (ref/keys.md) — реальные id KeyMapping
- Voice Chat (voicechat-2.6.27.jar, ru_ru): key.push_to_talk, key.whisper, key.mute_microphone, key.disable_voice_chat, key.hide_icons, key.voice_chat, key.voice_chat_settings, key.voice_chat_group, key.voice_chat_toggle_recording.
- Recruits (recruits-1.15.2.jar): key.recruits.command_screen_key, key.recruits.team_screen_key, key.recruits.map_screen_key, key.recruits.r_key, key.recruits.u_key; категория key.category.*.
- War 'N Taxes (WarNTaxes-5.0.9.jar): key.minecolonytax.open_tax_gui, key.category.minecolonytax.
- Combat Roll: keybinds.combat_roll.roll (байткод Keybindings.class).
- Backpacked: key.backpacked.open_backpack, key.backpacked.open_management (lang jar).
- Corpse: key.corpse.death_history (lang jar).
- Jade: key.jade.config, key.jade.show_overlay, key.jade.toggle_liquid, key.jade.show_details, key.jade.show_details_alternative, key.jade.show_recipes, key.jade.show_uses, key.jade.narrate (Jade-15.10.6.jar).
- JEI (jei-19.70.0.469.jar): key.jei.showRecipe, key.jei.showUses, key.jei.toggleOverlay, key.jei.bookmark, key.jei.focusSearch, key.jei.nextPage, key.jei.previousPage, key.jei.toggleCheatMode.
- Cosmetic Armor: cos.key.opencosarmorinventory (lang jar cosmeticarmorreworked).

## Где какое руководство (ref/guides.md)
- AE2: `ae2:guide` «Руководство» — item.ae2.guide ru; получается зарядкой книги в заряднике (data/ae2/recipe/charger/guide.json). Лок. перевод лежит в kubejs/assets/ae2/ae2guide.
- Ad Astra: Patchouli-книга ad_astra:astrodux (Астродукс); рецепт data/ad_astra/recipe/astrodux.json (книга+железо+синий краситель) → patchouli:guide_book с компонентом ad_astra:astrodux; **предмета ad_astra:astrodux нет** — не использовать ItemLink; тег `<Recipe id="ad_astra:astrodux"/>`.
- PneumaticCraft: `pneumaticcraft:manual` (модель assets/pneumaticcraft/models/item/manual.json; имя книги «PNC:R Руководство»).
- Railcraft Reborn: `railcraft:guide_book` (data/railcraft/patchouli_books/guide_book/book.json; «Railcraft Reborn Book»; Patchouli-книга, активная в beta).
- War 'N Taxes: `minecolonytax:war_taxes_codex` (Кодекс War 'N Taxes; assets/minecolonytax/models/item/war_taxes_codex.json; minecolonytax.book.name ru). Способ получения не подтверждён из jar (дистрибуция через код мода).
- Advanced Peripherals: **`advancedperipherals:manual`** (Patchouli; data/advancedperipherals/patchouli_books/manual/book.json, model advancedperipherals:book). Способ получения не подтверждён из jar (рецепта manual в данных нет).
- Create: Ponder-сцены (W + ПКМ по механизму) — описание в docs/guide-inventory.md (строка Create).
- Mekanism: Ponder-сцены через mekanism_ponders + JEI-расчёт мультиблоков (jei_mekanism_multiblocks).
- CC:Tweaked: встроенная справка `help` в компьютере.
- JEI: каталог предметов и рецептов.

## JEI и рецепты (ref/jei.md)
- JEI: key.jei.showRecipe (R), key.jei.showUses (U), key.jei.bookmark, key.jei.toggleOverlay, key.jei.focusSearch, key.jei.nextPage/previousPage — lang jar.
- Just Enough Mekanism Multiblocks: ru_ru kubejs jei_mekanism_multiblocks (text.*.building.*: реактор, индукционная матрица, лазеры …) — расчёты мультиблоков в JEI.
- Polymorph: выбор результата крафта при конфликте рецептов; команда /polymorph conflicts (commands.polymorph.conflicts.* lang jar polymorph-1.2.0).

## Карты и HUD (ref/maps-hud.md) — по описи docs/guide-inventory.md
- Xaero's Minimap / World Map (xaerominimap/xaeroworldmap), BetterF3-11.0.3, Jade+JadeAddons, Dynamic Crosshair, Chat Heads, Not Enough Animations, Better Third Person, AmbientSounds v6.3.11, Sound Physics Remastered 1.5.2, Distant Horizons 3.3.3, Sodium Extra 0.9.5, Sodium Dynamic Lights.
- Клавиши Jade и «показ деталей при наведении» — см. keys.md.

## Инвентарь и интерфейс (ref/ui.md) — по описи docs/guide-inventory.md
- Inventory Profiles Next 2.2.5 (жесты/сортировка), Mouse Tweaks 2.26.1, YUNG's Menu Tweaks (modid bettermenu), FancyMenu, Drippy Loading Screen, Better Advanced Tooltips (F3+H).