---
navigation:
  title: Небо и космос
  parent: adv/index.md
  position: 4
---

# Небо и космос

## Самолёты (Immersive Aircraft)

Летательные аппараты собираются из частей: <ItemLink id="immersive_aircraft:engine" />, <ItemLink id="immersive_aircraft:sail" />, <ItemLink id="immersive_aircraft:propeller" />, <ItemLink id="immersive_aircraft:hull" /> — полный состав рецепта показывает JEI.

Все аппараты летают **только на керосине** PneumaticCraft. Уголь, дрова и прочее печное топливо в слот топлива не лягут. Керосин получают двумя путями:

- **Нефтепереработка.** <ItemLink id="pneumaticcraft:refinery" /> — очистительный завод — с тремя и более выходами <ItemLink id="pneumaticcraft:refinery_output" />: из 10 мБ нефти выходит 3 мБ керосина. С двумя выходами керосина нет.
- **Из дизеля.** <ItemLink id="pneumaticcraft:thermopneumatic_processing_plant" /> — термопневматический завод — делает из 100 мБ дизеля 80 мБ керосина.

Набери керосин в ведро или бак (например, <ItemLink id="pneumaticcraft:small_tank" />) и положи в слот топлива аппарата: он заберёт столько, сколько влезет. Неполные ёмкости тоже годятся. Про нефть и давление — [eng/pneumaticcraft →](../eng/pneumaticcraft.md).

Поэтому <ItemLink id="immersive_aircraft:quadrocopter" /> (квадрокоптер) — **не стартовая машина**: в небо ты поднимешься, когда наладишь нефтепереработку. Машины на выбор: квадрокоптер, <ItemLink id="immersive_aircraft:biplane" />, <ItemLink id="immersive_aircraft:airship" /> (дирижабль) и <ItemLink id="immersive_aircraft:gyrodyne" /> (винтокрыл — вертолёт с двигателем на керосине). Войди в аппарат, как в седло, и управляй клавишами движения.

Аддон Man of Many Planes добавляет <ItemLink id="man_of_many_planes:scarlet_biplane" /> (алый биплан) и <ItemLink id="man_of_many_planes:economy_plane" /> — экономичный самолёт.

## Космос (Ad Astra)

Путь в космос начинается с книги «Астродукс» (руководство Ad Astra на русском):

<Recipe id="ad_astra:astrodux" fallbackText="Книга + железо + синий краситель." />

Затем, по порядку:

1. **Верстак НАСА** <ItemLink id="ad_astra:nasa_workbench" /> — на нём собираются ракеты.
2. **Стартовая площадка** <ItemLink id="ad_astra:launch_pad" /> — ракета ставится на неё.
3. Ракета 1 уровня <ItemLink id="ad_astra:tier_1_rocket" /> — первый полёт: на Луну.
4. Скафандр и кислород: <ItemLink id="ad_astra:space_suit" /> и остальные части скафандра (рецепты в JEI); кислород на базах даёт <ItemLink id="ad_astra:oxygen_distributor" />.

Дальше — планеты: Марс, Меркурий, Венера и ледяной Гласио. Для них нужны ракеты 2–4 уровней, а для ракет — новые материалы: деш, острум, калорит. Какая ракета куда летит и из чего собирается — смотри в «Астродуксе» и в JEI.