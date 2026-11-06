---
navigation:
  parent: example-setups/example-setups-index.md
  title: Опустошитель вёдер
  icon: minecraft:bucket
---

# Опустошитель вёдер

См. также [Наполнитель вёдер](bucket-filler.md).

Учтите, что, поскольку здесь используется <ItemLink id="pattern_provider" />, эта установка предназначена для интеграции в вашу [систему автокрафта](../ae2-mechanics/autocrafting.md).

Иногда жизнь неудобна, и вам нужна сама жидкость, но вы можете сделать жидкость только в ведре. Иногда машина может сделать это за вас
(например, жидкостный трансфузор из Thermal Expansion), но у вас не всегда может быть мод, который сделает это удобно. К счастью,
в ванильном Minecraft есть чуть менее удобный способ — <ItemLink id="minecraft:dispenser" />.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/bucket_emptier.snbt" />

<BoxAnnotation color="#dddddd" min="2 1 0" max="3 2 1">
        (1) Поставщик шаблонов: настроен на блокировку крафта «по редстоун-сигналу», блокирующий режим включён, с соответствующими производственными шаблонами.

        <Row>
        ![Шаблон опустошения](../assets/diagrams/water_empty_pattern_small.png)
        ![Шаблон опустошения](../assets/diagrams/lava_empty_pattern_small.png)
        </Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2.1 2 0.1" max="2.9 2.2 0.9">
        (2) Интерфейс: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.1 2 1.1" max="3.9 2.2 1.9">
        (3) Шина хранения №1: в конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="4.05 1.05 0.8" max="4.95 1.95 1">
        (4) Плоскость аннигиляции: не имеет интерфейса для настройки.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.2 1.2 0.8" max="3.8 1.8 1">
        (5) Шина импорта: фильтруется по вёдрам.
        <ItemImage id="minecraft:bucket" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1.1 0.1" max="3.2 1.9 0.9">
        (6) Шина хранения №2: в конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="0 1.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="225" pitch="45" />
</GameScene>

## Конфигурации

* <ItemLink id="pattern_provider" /> (1) настроен на блокировку крафта «по редстоун-сигналу», блокирующий режим включён,
  с соответствующими <ItemLink id="processing_pattern" />ами.
  
    ![Шаблон зарядника](../assets/diagrams/water_empty_pattern.png)
    ![Шаблон зарядника](../assets/diagrams/lava_empty_pattern.png)

* <ItemLink id="interface" /> (2) находится в конфигурации по умолчанию.
* Первая <ItemLink id="storage_bus" /> (3) находится в конфигурации по умолчанию.
* <ItemLink id="annihilation_plane" /> (4) не имеет интерфейса и не может быть настроена.
* <ItemLink id="import_bus" /> (5) фильтруется по вёдрам.
  <ItemImage id="minecraft:bucket" scale="2" />
* Вторая <ItemLink id="storage_bus" /> (6) находится в конфигурации по умолчанию.

## Как это работает

1. <ItemLink id="pattern_provider" /> выталкивает ингредиенты в <ItemLink id="interface" />.
   (На самом деле, как оптимизация, он выталкивает их напрямую через шину хранения, как если бы она была продолжением граней поставщика. Предметы фактически никогда не попадают в интерфейс.)
2. С помощью механизмов, описанных в [трубопроводных подсетях](pipe-subnet.md#providing-to-multiple-places),
   ведро оказывается в <ItemLink id="minecraft:dispenser" />.
3. <ItemLink id="minecraft:comparator" /> обнаруживает ведро в раздатчике и, таким образом, одновременно питает раздатчик и блокирует
   <ItemLink id="pattern_provider" />.
4. Раздатчик выливает жидкость из ведра; теперь в нём пустое ведро.
5. <ItemLink id="import_bus" /> вытягивает пустое ведро из раздатчика и хранит его через
   <ItemLink id="storage_bus" /> в поставщике шаблонов, возвращая его в основную сеть.
6. Компаратор видит, что раздатчик пуст, и разблокирует поставщика.