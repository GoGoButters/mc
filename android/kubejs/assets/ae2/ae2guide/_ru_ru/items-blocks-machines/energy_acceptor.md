---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Приёмник энергии
  icon: energy_acceptor
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:energy_acceptor
---

# Приёмник энергии

<Row gap="20">
<BlockImage id="energy_acceptor" scale="8" /> 

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/cable_energy_acceptor.snbt" />
</GameScene>
</Row>

Приёмник энергии преобразует распространённые формы энергии из других техномодов во внутреннюю форму энергии AE2 — [энергию](../ae2-mechanics/energy.md),
AE. Хотя <ItemLink id="controller" /> тоже может это делать, грани контроллера ценны, поэтому часто лучше использовать приёмник
энергии.

Коэффициенты преобразования Forge Energy и TechReborn Energy:

*   2 FE = 1 AE (Forge)
*   1 E  = 2 AE (Fabric)

Скорость преобразования полностью зависит от того, сколько AE может хранить ваша сеть по причинам, описанным
[на этой странице](../ae2-mechanics/energy.md).

## Варианты

Приёмники энергии бывают двух вариантов: обычный и плоский/[субчасть](../ae2-mechanics/cable-subparts.md). Это позволяет делать некоторые конструкции более компактными.

Приёмники энергии можно переключать между обычным и плоским в сетке крафта.

## Рецепт

<RecipeFor id="energy_acceptor" />

<RecipeFor id="cable_energy_acceptor" />