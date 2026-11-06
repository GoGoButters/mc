---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Переключаемая шина
  icon: toggle_bus
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:toggle_bus
- ae2:inverted_toggle_bus
---

# Переключаемая шина

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/assemblies/toggle_bus.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Шина, которая работает аналогично <ItemLink id="fluix_glass_cable" /> или другим кабелям, но
позволяет переключать своё состояние соединения с помощью редстоуна. Это позволяет отрезать
часть [МЭ-сети](../ae2-mechanics/me-network-connections.md).

Когда подаётся сигнал редстоуна, часть включает соединение, <ItemLink id="inverted_toggle_bus" /> обеспечивает обратное
поведение, отключая соединение вместо этого.

Обратите внимание, что переключение этих шин может привести к перезагрузке сети и пересчёту подключённых устройств.

Они являются [подчастями кабеля](../ae2-mechanics/cable-subparts.md).

## Рецепты

<RecipeFor id="toggle_bus" />

<RecipeFor id="inverted_toggle_bus" />