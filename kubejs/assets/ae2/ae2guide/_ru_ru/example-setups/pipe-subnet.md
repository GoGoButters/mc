---
navigation:
  parent: example-setups/example-setups-index.md
  title: Подсеть «Труба» для Предметов/Жидкостей
  icon: storage_bus
---

# Подсеть «Труба» для Предметов/Жидкостей

Простой способ имитации трубы для предметов и/или жидкостей с помощью [устройств](../ae2-mechanics/devices.md) AE2, полезный, ну, для всего, для чего вы бы использовали трубу для предметов или жидкостей.
В том числе для возврата результата крафта в <ItemLink id="pattern_provider" />.

Обычно есть два разных способа это сделать:

## Шина импорта -> Шина хранения

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        (1) Шина импорта: Может быть отфильтрована.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        (2) Шина хранения: Может быть отфильтрована. Она (и другие шины хранения, которые вы хотите сделать пунктом назначения)
        должна быть единственным хранилищем в сети.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
        Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
        Пункт назначения
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Шина импорта <ItemLink id="import_bus" /> (1) в инвентаре-источнике импортирует предметы или жидкость и пытается сохранить их в [хранилище сети](../ae2-mechanics/import-export-storage.md).
Поскольку единственное хранилище в сети — <ItemLink id="storage_bus" /> (2) (поэтому это подсеть, а не ваша основная сеть), предметы или жидкость
помещаются в инвентарь назначения, таким образом переносятся. Энергия подаётся через <ItemLink id="quartz_fiber" />.
И шина импорта, и шина хранения могут быть отфильтрованы, но схема будет переносить всё, к чему сможет получить доступ, если фильтры не применены.
Эта схема также работает с несколькими шинами импорта и несколькими шинами хранения.

## Шина хранения -> Шина экспорта

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        (1) Шина хранения: Может быть отфильтрована. Она (и другие шины хранения, которые вы хотите сделать источником)
        должна быть единственным хранилищем в сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        (2) Шина экспорта: Должна быть отфильтрована.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
        Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
        Пункт назначения
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Шина экспорта <ItemLink id="export_bus" /> в инвентаре назначения пытается вытащить предметы из своего фильтра из [хранилища сети](../ae2-mechanics/import-export-storage.md).
Поскольку единственное хранилище в сети — <ItemLink id="storage_bus" /> (поэтому это подсеть, а не ваша основная сеть), предметы или жидкость
вытягиваются из инвентаря-источника, таким образом переносятся. Энергия подаётся через <ItemLink id="quartz_fiber" />.
Поскольку шины экспорта должны быть отфильтрованы для работы, эта схема работает только если вы отфильтруете шину экспорта.
Эта схема также работает с несколькими шинами хранения и несколькими шинами экспорта.

## Схема, Которая Не Работает (Шина импорта -> Шина экспорта)

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_export_pipe.snbt" />

<BoxAnnotation color="#dd3333" min="3.7 0 0" max="4 1 1">
        Шина импорта: Поскольку в сети нет хранилища, ей некуда импортировать.
  </BoxAnnotation>

<BoxAnnotation color="#dd3333" min="1 0 0" max="1.3 1 1">
        (2) Шина экспорта: Поскольку в сети нет хранилища, ей нечего экспортировать.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#ff0000">
        Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#ff0000">
        Пункт назначения
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Схема только с шиной импорта и экспорта не заработает. Шина импорта будет пытаться вытягивать из инвентаря-источника
и сохранять предметы или жидкость в хранилище сети. Шина экспорта будет пытаться вытягивать из хранилища сети и класть
предметы или жидкость в инвентарь назначения. Однако, поскольку эта сеть **не имеет хранилища**, шина импорта не может импортировать,
а шина экспорта не может экспортировать, так что ничего не происходит.

## Ввод И Вывод Через Одну Грань

Предположим, у вас есть машина, которая может принимать ввод и у которой можно вытягивать вывод через 1 грань. (Например, <ItemLink id="charger" />)
Вы можете одновременно вталкивать ингредиенты и вытягивать результат, объединив два метода трубных подсетей:

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="4 1 1" max="5 1.3 2">
        (1) Шина импорта: Может быть отфильтрована.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
        (2) Шина хранения: Может быть отфильтрована. Она (и другие шины хранения, через которые вы хотите вталкивать и вытягивать предметы)
        должна быть единственным хранилищем в сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
        (3) Вещь, В Которую Вы Хотите Вталкивать И Из Которой Вытягивать: В данном случае Зарядник.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 1" max="1 1.3 2">
        (4) Шина экспорта: Должна быть отфильтрована.
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 1.5" color="#00ff00">
        Источник
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 1.5" color="#00ff00">
        Пункт назначения
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Интерфейсы

Оказывается, есть [устройства](../ae2-mechanics/devices.md), кроме шин импорта и шин экспорта, которые вталкивают предметы в
[хранилище сети](../ae2-mechanics/import-export-storage.md) и вытягивают их из него!
Релевантен здесь <ItemLink id="interface" />. Если вставлен предмет, который интерфейс не настроен хранить, интерфейс
втолкнёт его в хранилище сети, что можно использовать так же, как трубу «шина импорта -> шина хранения». Если настроить интерфейс
хранить какой-то предмет, он будет вытягивать его из хранилища сети, аналогично трубе «шина хранения -> шина экспорта». Интерфейсы можно настроить
хранить одни предметы и не хранить другие, что позволяет удалённо вталкивать и вытягивать через шины хранения, если вам по какой-то причине это нужно.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/interface_pipes.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        Интерфейс
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        Шина хранения
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.7 0 2" max="4 1 3">
        Шина хранения
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 2" max="1 1.3 3">
        Интерфейс
  </BoxAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Один-ко-многим и Многие-к-одному (и многие-ко-многим)

Конечно, вам не обязательно использовать только одну <ItemLink id="import_bus" />, или <ItemLink id="export_bus" />, или <ItemLink id="storage_bus" />

<GameScene zoom="3" background="transparent">
<ImportStructure src="../assets/assemblies/many_to_many_pipe.snbt" />

<IsometricCamera yaw="185" pitch="30" />
</GameScene>

## Подача Во Многие Места

Из всего этого мы можем вывести способ отправки ингредиентов с одной грани <ItemLink id="pattern_provider" /> во многие разные
места, например в массив машин или в несколько разных граней одной машины.

Мы не хотим трубу «импорт -> хранение» или «хранение -> экспорт», потому что <ItemLink id="pattern_provider" /> на самом деле
не содержит ингредиенты. Вместо этого поставщики *вталкивают* ингредиенты в соседние инвентари, поэтому нам нужен какой-то 
соседний инвентарь, который также может импортировать предметы.

Это звучит как... <ItemLink id="interface" />!
Убедитесь, что поставщик находится в направленном или плоском режиме субчасти, и/или интерфейс находится в плоском режиме субчасти, чтобы они не образовывали сетевое
соединение.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        Интерфейс (должен быть плоским, не полноразмерным блоком)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 4">
        Шины хранения
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 4">
        Места, куда вы хотите подавать по шаблонам (несколько машин или несколько граней одной машины)
  </BoxAnnotation>

<IsometricCamera yaw="185" pitch="30" />
</GameScene>