---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Точка беспроводного доступа
  icon: wireless_access_point
  position: 210
categories:
- devices
item_ids:
- ae2:wireless_booster
- ae2:wireless_access_point
---

# Точка беспроводного доступа

<BlockImage id="wireless_access_point" p:state="has_channel" scale="8" />

Обеспечивает беспроводной доступ через <ItemLink id="wireless_terminal" />.
Дальность и расход энергии определяются количеством установленных <ItemLink id="wireless_booster" />.

В сети может быть любое количество точек беспроводного доступа с любым количеством
установленных в каждой из них усилителей <ItemLink id="wireless_booster" />, что позволяет оптимизировать расход энергии
и дальность, изменяя вашу установку.

Требует [канал](../ae2-mechanics/channels.md).

Также используется для привязки [беспроводных терминалов](wireless_terminals.md)

# Усилитель беспроводной связи

<ItemImage id="wireless_booster" scale="2" />

Используется для увеличения дальности точки беспроводного доступа.

## Рецепты

<RecipeFor id="wireless_access_point" />

<RecipeFor id="wireless_booster" />