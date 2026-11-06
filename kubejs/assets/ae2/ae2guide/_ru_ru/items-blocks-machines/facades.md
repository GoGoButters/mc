---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Фасады
  icon: facade
  icon_components:
    "ae2:facade_item": "minecraft:stone"
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:facade
---

# Фасады

Фасады можно использовать, чтобы ваша база выглядела более аккуратной. Они могут скрывать кабели обоих размеров и изготавливаются из многих
видов блоков.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/facades_1.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Они могут покрывать все стороны кабеля, но позволяют [субчастям](../ae2-mechanics/cable-subparts.md) и соединениям кабеля
выступать наружу.

<GameScene zoom="6"  interactive={true}>
  <ImportStructure src="../assets/assemblies/facades_2.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Проявите смекалку, чтобы улучшить эстетику базы или создать блоки с разными текстурами на каждой стороне.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/facades_3.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Скрытие фасадов

Фасады будут скрыты, пока вы держите <a href="network_tool.md">сетевой инструмент</a> в любой руке.

Вы можете взаимодействовать с блоками за скрытыми фасадами, не снимая фасады.

## Рецепт

Поместите блок, текстуру которого хотите получить, в центр 4 <ItemLink id="cable_anchor" />.

![Рецепт фасада](../assets/diagrams/facade_recipe.png)