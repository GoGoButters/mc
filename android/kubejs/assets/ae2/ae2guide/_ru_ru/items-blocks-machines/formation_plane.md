---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Плоскость формирования
  icon: formation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:formation_plane
---

# Плоскость формирования

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/formation_plane.snbt" />
</GameScene>

Плоскость формирования размещает блоки и выкидывает предметы. Она работает аналогично <ItemLink id="storage_bus" /> только на вставку,
размещая/выкидывая предметы, когда их «хранят» в ней [устройства](../ae2-mechanics/devices.md), вставляющие в [сетевое хранилище](../ae2-mechanics/import-export-storage.md),
например <ItemLink id="import_bus" />ses и <ItemLink id="interface" />s.

<GameScene zoom="8" interactive={true}>
  <ImportStructure src="../assets/assemblies/formation_plane_demonstration.snbt" />
  <IsometricCamera yaw="255" pitch="30" />
</GameScene>

Обратите внимание: они похожи на «шина импорта -> шина хранения» и «интерфейс -> шина хранения» в [трубных подсетях](../example-setups/pipe-subnet.md).

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это [устройство](../ae2-mechanics/devices.md) использует механики, применяемые шинами хранения в таких вещах, как [трубные подсети](../example-setups/pipe-subnet.md),
и может заменять шины хранения в этих конструкциях, если вы хотите бросать предметы/размещать блоки вместо транспортировки предметов.

Они являются [субчастями кабеля](../ae2-mechanics/cable-subparts.md).

**НЕ ЗАБУДЬТЕ ВКЛЮЧИТЬ ФАКОВЫХ ИГРОКОВ (FAKE PLAYERS) В ЗАХВАТЕ ЧАНКОВ**

## Фильтрация

По умолчанию плоскость размещает/выбрасывает всё. Предметы, вставленные в её слоты фильтра, действуют как белый список, разрешая
размещение только этих конкретных предметов.

Предметы и жидкости можно перетаскивать в слоты из JEI/REI, даже если у вас на самом деле нет ни одного такого предмета.

Щёлкните правой кнопкой мыши с контейнером для жидкости (например, ведром или резервуаром), чтобы задать эту жидкость как фильтр вместо предмета ведра или резервуара.

## Приоритет

Приоритеты можно задать, щёлкнув по гаечному ключу в правом верхнем углу интерфейса.
Предметы, поступающие в сеть, первым делом будут направляться в хранилище с наивысшим приоритетом.

## Настройки

*   Плоскость можно настроить на размещение блоков в мире или выбрасывание предметов

## Улучшения

Плоскость формирования поддерживает следующие [улучшения](upgrade_cards.md):

*   <ItemLink id="capacity_card" /> увеличивает количество слотов фильтра
*   <ItemLink id="fuzzy_card" /> позволяет плоскости фильтровать по уровню повреждения и/или игнорировать NBT предмета
*   <ItemLink id="inverter_card" /> переключает фильтр с белого списка на чёрный

## Рецепт

<RecipeFor id="formation_plane" />