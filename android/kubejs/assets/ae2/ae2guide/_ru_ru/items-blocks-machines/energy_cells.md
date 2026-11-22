---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Энергоячейки
  icon: energy_cell
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:energy_cell
- ae2:dense_energy_cell
- ae2:creative_energy_cell
---

# Энергоячейки

<Row gap="20">
  <BlockImage id="energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="dense_energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="creative_energy_cell" scale="8" />
</Row>

Энергоячейки дают сети больше [энергии](../ae2-mechanics/energy.md) для хранения. Некоторый запас энергии помогает сглаживать
пики потребления при вставке или извлечении больших объёмов предметов, а больший запас энергии
позволяет сети работать, пока энергия не вырабатывается (например, ночью с солнечными панелями), или справляться с огромным мгновенным
потреблением энергии [пространственным хранилищем](../ae2-mechanics/spatial-io.md).

## Полосы заполнения

<Row>
<BlockImage id="energy_cell" scale="4" p:fullness="0" />
<BlockImage id="energy_cell" scale="4" p:fullness="1" />
<BlockImage id="energy_cell" scale="4" p:fullness="2" />
<BlockImage id="energy_cell" scale="4" p:fullness="3" />
<BlockImage id="energy_cell" scale="4" p:fullness="4" />
</Row>

Полосы на боку ячейки соответствуют тому, сколько в ней энергии.

*   0 — заряд ниже 25%
*   1 — заряд от 25% до 50%
*   2 — заряд от 50% до 75%
*   3 — заряд от 75% до 99%
*   4 — заряд выше 99%

## Типы ячеек

*   <ItemLink id="energy_cell" /> может хранить 200 тыс. AE, и одной должно быть достаточно для большинства случаев — она легко справляется
     со скачками питания при обычном использовании сети.
*   <ItemLink id="dense_energy_cell" /> может хранить 1,6 млн AE и предназначена для случаев, когда вы хотите питать сеть от накопленной энергии или
     справляться с огромным мгновенным потреблением энергии крупными [пространственными хранилищами](../ae2-mechanics/spatial-io.md).
*   <ItemLink id="creative_energy_cell" /> — творческий предмет для тестирования, дающий БЕСКОНЕЧНУЮ МОЩНОСТЬ или что-то в этом роде.

## Рецепты

<Row>
  <RecipeFor id="energy_cell" />

  <RecipeFor id="dense_energy_cell" />
</Row>