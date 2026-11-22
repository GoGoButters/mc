---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: МЭ-порт ввода-вывода
  icon: io_port
  position: 210
categories:
- devices
item_ids:
- ae2:io_port
---

# МЭ-порт ввода-вывода

<BlockImage id="io_port" p:powered="true" scale="8" />

Порт ввода-вывода позволяет быстро заполнять или опустошать [ячейки хранения](../items-blocks-machines/storage_cells.md) в
[сетевое хранилище](../ae2-mechanics/import-export-storage.md) или из него.

Его можно повернуть с помощью <ItemLink id="certus_quartz_wrench" />.

## Настройки

*   Порт ввода-вывода можно настроить на перемещение ячейки в слоты вывода, когда ячейка пуста, полна или когда работа завершена.
*   Если вставлена <ItemLink id="redstone_card" />, появятся опции для различных условий редстоуна
*   В центре интерфейса есть стрелка, задающая направление передачи предметов: из ячейки в [сетевое хранилище](../ae2-mechanics/import-export-storage.md)
    или из хранилища в ячейку.

## Улучшения

Порт ввода-вывода поддерживает следующие [улучшения](upgrade_cards.md):

*   <ItemLink id="speed_card" /> увеличивает объём перемещаемого за операцию
*   <ItemLink id="redstone_card" /> добавляет управление редстоуном: активен при высоком сигнале, низком сигнале или один раз за импульс

## Рецепт

<RecipeFor id="io_port" />