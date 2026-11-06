---
navigation:
  parent: example-setups/example-setups-index.md
  title: Полуавтоматическая Ферма Кварца
  icon: certus_quartz_crystal
  position: 115
---

# Полуавтоматическая Ферма Кварца

К сожалению, [простая ферма кварца](simple-certus-farm.md) требует <ItemLink id="flawless_budding_quartz" /> для полностью автоматической работы.
Для этого нужны либо [пространственное I/O](../ae2-mechanics/spatial-io.md), либо строительство фермы на [метеорите](../ae2-mechanics/meteorites.md).

Однако AE2 может ставить и ломать блоки, так что, возможно,
можно сделать ферму, которая *заменяет цветущий кварц за вас*. (Вам придётся периодически вставлять немного
<ItemLink id="flawed_budding_quartz" /> во входной бочонок и извлекать <ItemLink id="quartz_block" /> из бочонка с истраченным
цветущим кварцем)

Чтобы сделать это полностью автоматически, см. [Продвинутую ферму кварца](advanced-certus-farm.md).

Эта ферма немного сложнее, чем [простая ферма кварца](simple-certus-farm.md), потому что на самом деле это
3 отдельные схемы, собранные вместе.

См. [Рост Кварца](../ae2-mechanics/certus-growth.md) для ориентировочных скоростей.

**ЭТО СЛОЖНАЯ КОНСТРУКЦИЯ С ВЕЩАМИ, СПРЯТАННЫМИ ЗА ДРУГИМИ ВЕЩАМИ, ПАНОРАМИРУЙТЕ, ЧТОБЫ ОСМОТРЕТЬ ЕЁ СО ВСЕХ УГЛОВ**

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/semiauto_certus_farm.snbt" />

  <BoxAnnotation color="#ddaaaa" min="3.7 2 1" max="4 3 2">
        (1) Плоскость аннигиляции №1: Нет GUI для настройки, но можно зачаровать на фортуну.
  </BoxAnnotation>

  <BoxAnnotation color="#ddaaaa" min="2 2 1" max="2.3 3 2">
        (2) Шина хранения №1: Отфильтрована на кристалл истинного кварца.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 2.5 1.5" color="#ff0000">
    Подсеть ломателя скоплений
  </DiamondAnnotation>

  <BoxAnnotation color="#aaddaa" min="3.7 1 1" max="4 2 2">
        (3) Плоскость аннигиляции №2: Нет GUI для настройки, но зачарована на шёлковое касание.
  </BoxAnnotation>

  <BoxAnnotation color="#aaddaa" min="2 1 1" max="2.3 2 2">
        (4) Шина хранения №2: Отфильтрована на блок истинного кварца.
        <BlockImage id="quartz_block" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 1.5 1.5" color="#00ff00">
    Подсеть ломателя блоков кварца
  </DiamondAnnotation>

  <BoxAnnotation color="#ffddaa" min="4 0.7 1" max="5 1 2">
        (5) Плоскость формирования: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#ffddaa" min="2 0 1" max="2.3 1 2">
        (6) Шина импорта: В конфигурации по умолчанию.
  </BoxAnnotation>

  <DiamondAnnotation pos="3 0.5 1.5" color="#ddcc00">
    Подсеть установщика цветущих блоков
  </DiamondAnnotation>

  <BoxAnnotation color="#aaaadd" min="0.7 2 1" max="1 3 2">
        (7) Шина хранения №3: Отфильтрована на кристалл истинного кварца. Имеет приоритет выше, чем ваше основное хранилище.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

    <DiamondAnnotation pos="1.5 0.5 1.5" color="#00ff00">
        Вставьте вручную испорченный цветущий кварц.
        <BlockImage id="flawed_budding_quartz" scale="2" />
    </DiamondAnnotation>

    <DiamondAnnotation pos="1.5 1.5 1.5" color="#00ff00">
        Извлеките вручную блок истинного кварца.
        <BlockImage id="quartz_block" scale="2" />
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="165" pitch="5" />
</GameScene>

## Конфигурации

### Ломатель скоплений:

* Первая <ItemLink id="annihilation_plane" /> (1) не имеет GUI и не может быть настроена, но может быть зачарована на фортуну.
* Первая <ItemLink id="storage_bus" /> (2) отфильтрована на <ItemLink id="certus_quartz_crystal" />.

### Ломатель блоков кварца:

* Вторая <ItemLink id="annihilation_plane" /> (3) не имеет GUI и не может быть настроена, но должна быть зачарована на шёлковое касание.
* Вторая <ItemLink id="storage_bus" /> (4) отфильтрована на <ItemLink id="quartz_block" />.

### Установщик цветущих блоков:

* <ItemLink id="formation_plane" /> (5) находится в конфигурации по умолчанию.
* <ItemLink id="import_bus" /> (6) находится в конфигурации по умолчанию.

### На основной сети:

* Третья <ItemLink id="storage_bus" /> (7) отфильтрована на <ItemLink id="certus_quartz_crystal" />, и её
  [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority) установлен выше, чем у вашего основного хранилища.

## Как это работает

### Ломатель скоплений:

Подсеть ломателя скоплений работает очень похоже на подсеть в [простой ферме кварца](simple-certus-farm.md).

1. <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="quartz_cluster" />,
   потому что единственное хранилище в подсети — <ItemLink id="storage_bus" />, отфильтрованная на <ItemLink id="certus_quartz_crystal" />.
2. <ItemLink id="storage_bus" /> сохраняет кристаллы истинного кварца в бочонке.

### Ломатель блоков кварца:

Подсеть ломателя блоков кварца служит для ломания истощённого цветущего блока, когда он превращается в обычный <ItemLink id="quartz_block" />.
Она работает так же, как ломатель скоплений.

1. <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="quartz_block" />,
   потому что единственное хранилище в подсети — <ItemLink id="storage_bus" />, отфильтрованная на <ItemLink id="quartz_block" />.
   Плоскость должна иметь шёлковое касание, чтобы цветущий блок не деградировал при ломании, и таким образом плоскость не сломает его преждевременно.
2. <ItemLink id="storage_bus" /> сохраняет блок истинного кварца в бочонке с истраченным
   цветущим кварцем; вам придётся вручную бросить его в воду с <ItemLink id="charged_certus_quartz_crystal" />, чтобы обновить его.

### Установщик цветущих блоков:

Подсеть установщика цветущих блоков служит для установки нового <ItemLink id="flawed_budding_quartz" />, когда подсеть-ломатель ломает старый истощённый.

1. <ItemLink id="import_bus" /> импортирует цветущий блок из входного бочонка.
2. Единственное хранилище в подсети — <ItemLink id="formation_plane" />, который ставит цветущий блок.

### На основной сети:

* <ItemLink id="storage_bus" /> даёт основной сети (а также [Автоматизации Зарядника](charger-automation.md)) доступ ко всем кристаллам истинного кварца в бочонке. Она установлена на
  высокий [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), чтобы кристаллы истинного кварца преимущественно
  возвращались в бочонок, а не в ваше основное хранилище.