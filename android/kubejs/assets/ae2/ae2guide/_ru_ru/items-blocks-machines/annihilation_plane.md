---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Плоскость Аннигиляции
  icon: annihilation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:annihilation_plane
---

# Плоскость Аннигиляции

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/annihilation_plane.snbt" />
</GameScene>

Плоскость аннигиляции ломает блоки и подбирает предметы. Она работает аналогично <ItemLink id="import_bus" />, вталкивая вещи
в [хранилище сети](../ae2-mechanics/import-export-storage.md). Чтобы предметы были подобраны, они должны столкнуться с
гранью плоскости, она не подбирает по области.

Плоскости аннигиляции можно зачаровать любым зачарованием кирки, так что да, можно наложить безумные уровни фортуны на несколько и
[автоматизировать обработку руды](../example-setups/ore-fortuner.md), если ваша сборка это позволяет. Кроме того, шёлковое касание делает то,
что вы ожидаете, эффективность снижает затраты энергии на ломание блока, а неразрушимость даёт шанс не тратить энергию вообще.

Они являются [кабельными субчастями](../ae2-mechanics/cable-subparts.md).

**НЕ ЗАБУДЬТЕ ВКЛЮЧИТЬ ФЕЙКОВЫХ ИГРОКОВ В ЗАЯВКЕ НА ТЕРРИТОРИЮ**

## Фильтрация

Плоскость аннигиляции сломает блок или подберёт предмет только в том случае, если сможет сохранить полученные дропы/предметы
в своей сети. Это значит, что чтобы отфильтровать её, *вы должны ограничить, что можно хранить в её сети*, скорее всего, поместив
её в [подсеть](../ae2-mechanics/subnetworks.md). <ItemLink id="storage_bus" /> или [ячейка](../items-blocks-machines/storage_cells.md)
может быть [разделена на секции](cell_workbench.md) для достижения этой цели.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/annihilation_filtering.snbt" />

  <DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
        Отфильтрована на то, что выпадает из того, что вы хотите ломать.
  </DiamondAnnotation>

  <DiamondAnnotation pos=".5 0.5 2.5" color="#00ff00">
        Разделена на секции под то, что выпадает из того, что вы хотите ломать.
  </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

И снова, она фильтрует *по выпадающим предметам*, так что, например, если вы хотите отфильтровать ломание <ItemLink id="minecraft:amethyst_cluster" />,
вам нужна плоскость, зачарованная на шёлковое касание, иначе каждая предыдущая стадия роста ничего не выпадает, и плоскость сломает их в любом
случае, поскольку сеть всегда может хранить «ничего».

## Рецепт

<RecipeFor id="annihilation_plane" />