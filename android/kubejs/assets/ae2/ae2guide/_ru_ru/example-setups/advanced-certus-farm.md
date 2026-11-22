---
navigation:
  parent: example-setups/example-setups-index.md
  title: Продвинутая ферма истинного кварца
  icon: certus_quartz_crystal
  position: 120
---

# Продвинутая ферма истинного кварца

В основном это просто [полуавтоматическая ферма истинного кварца](semiauto-certus-farm.md), за исключением того, что она полностью интегрирована в вашу
МЭ-систему.

Вместо большого запаса цветущих блоков и ручного их обновления время от времени
эта установка использует [Автоматизацию зарядника](charger-automation.md) и [Автоматизацию бросания в воду](throw-in-water-automation.md),
чтобы делать это автоматически.

См. [Рост истинного кварца](../ae2-mechanics/certus-growth.md) для ориентировочных скоростей.

**ЭТО СЛОЖНАЯ ПОСТРОЙКА, ГДЕ ВЕЩИ СПРЯТАНЫ ЗА ДРУГИМИ ВЕЩАМИ, КРУТИТЕ КАМЕРУ, ЧТОБЫ РАССМОТРЕТЬ ЕЁ СО ВСЕХ СТОРОН**

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/advanced_certus_farm.snbt" />

  <BoxAnnotation color="#ddaaaa" min="3.7 2 1" max="4 3 2">
        (1) Плоскость аннигиляции №1: нет интерфейса для настройки, но можно наложить чары «Удача».
  </BoxAnnotation>

  <BoxAnnotation color="#ddaaaa" min="2 2 1.7" max="3 3 2">
        (2) Шина хранения №1: фильтруется по кристаллу истинного кварца.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 2.5 1.5" color="#ff0000">
    Подсеть ломания гроздей
  </DiamondAnnotation>

  <BoxAnnotation color="#aaddaa" min="3.7 1 1" max="4 2 2">
        (3) Плоскость аннигиляции №2: нет интерфейса для настройки, но зачарована «Шёлковым касанием».
  </BoxAnnotation>

  <BoxAnnotation color="#aaddaa" min="2 1 1.7" max="3 2 2">
        (4) Шина хранения №2: фильтруется по блоку истинного кварца.
        <BlockImage id="quartz_block" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 1.5 1.5" color="#00ff00">
    Подсеть ломания блока истинного кварца
  </DiamondAnnotation>

  <BoxAnnotation color="#ffddaa" min="4 0.7 1" max="5 1 2">
        (5) Плоскость формирования: в конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#ffddaa" min="2 0.7 2" max="3 1 3">
        (6) Шина импорта: фильтруется по надтреснутому цветущему блоку истинного кварца.
        <BlockImage id="flawed_budding_quartz" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 0.5 1.5" color="#ddcc00">
    Подсеть размещения цветущих блоков
  </DiamondAnnotation>

  <BoxAnnotation color="#aaaadd" min="1.7 2 2" max="2 3 3">
        (7) Шина хранения №3: фильтруется по кристаллу истинного кварца. Имеет более высокий приоритет, чем ваше основное хранилище.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#aaaadd" min="2 1 2" max="3 2 3">
        (8) Интерфейс: настроен на поддержание в себе 1 цветущего блока истинного кварца с изъяном, имеет карту крафта.
        <Row><BlockImage id="flawed_budding_quartz" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="1.5 0.5 0" color="#00ff00">
        К основной сети, автоматизации зарядника и автоматизации бросания в воду
        <Row>
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/charger_automation.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/throw_in_water.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
        </Row>
    </DiamondAnnotation>

  <IsometricCamera yaw="165" pitch="5" />
</GameScene>

## Конфигурации

### Ломание гроздей:

* Первая <ItemLink id="annihilation_plane" /> (1) не имеет интерфейса и не может быть настроена, но может быть зачарована на «Удачу».
* Первая <ItemLink id="storage_bus" /> (2) фильтруется по <ItemLink id="certus_quartz_crystal" />.

### Ломание блока истинного кварца:

* Вторая <ItemLink id="annihilation_plane" /> (3) не имеет интерфейса и не может быть настроена, но должна быть зачарована на «Шёлковое касание».
* Вторая <ItemLink id="storage_bus" /> (4) фильтруется по <ItemLink id="quartz_block" />.

### Размещение цветущих блоков:

* <ItemLink id="formation_plane" /> (5) находится в конфигурации по умолчанию.
* <ItemLink id="import_bus" /> (6) фильтруется по <ItemLink id="flawed_budding_quartz" />.

### В основной сети:

* Третья <ItemLink id="storage_bus" /> (7) фильтруется по <ItemLink id="certus_quartz_crystal" /> и имеет
  [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), заданный выше, чем у вашего основного хранилища.
* <ItemLink id="interface" /> (8) настроен на поддержание в себе 1 цветущего блока истинного кварца с изъяном и имеет <ItemLink id="crafting_card" />.

## Как это работает

### Ломание гроздей:

Подсеть ломания гроздей работает очень похоже на подсеть в [простой ферме истинного кварца](simple-certus-farm.md).

1. <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="quartz_cluster" />,
   потому что единственное хранилище в подсети — это <ItemLink id="storage_bus" />, фильтрованная по <ItemLink id="certus_quartz_crystal" />.
2. <ItemLink id="storage_bus" /> хранит кристаллы истинного кварца в бочке.

### Ломание блока истинного кварца

Подсеть ломания блока истинного кварца служит для ломания истощённого цветущего блока, как только он превращается в обычный <ItemLink id="quartz_block" />.
Она работает аналогично подсети ломания гроздей.

1. <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="quartz_block" />,
   потому что единственное хранилище в подсети — это <ItemLink id="storage_bus" />, фильтрованная по <ItemLink id="quartz_block" />.
   Плоскости нужно «шёлковое касание», чтобы цветущий блок не деградировал при ломании, и таким образом плоскость не сломает его преждевременно.
2. <ItemLink id="storage_bus" /> хранит блок истинного кварца в <ItemLink id="interface" />, позволяя
   [автоматизации бросания в воду](throw-in-water-automation.md) использовать его для создания нового <ItemLink id="flawed_budding_quartz" />.

### Размещение цветущих блоков

Подсеть размещения цветущих блоков служит для размещения нового <ItemLink id="flawed_budding_quartz" />, когда подсеть ломания ломает старый истощённый.

1. <ItemLink id="import_bus" /> импортирует цветущий блок из <ItemLink id="interface" /> в [сетевое хранилище](../ae2-mechanics/import-export-storage.md).
2. Единственное хранилище в подсети — это <ItemLink id="formation_plane" />, которая размещает цветущий блок.

### В основной сети

* <ItemLink id="storage_bus" /> даёт основной сети (а также [автоматизации зарядника](charger-automation.md)) доступ ко всем кристаллам истинного кварца в бочке. Она задана с
  высоким [приоритетом](../ae2-mechanics/import-export-storage.md#storage-priority), чтобы кристаллы истинного кварца предпочтительно
  возвращались в бочку, а не попадали в ваше основное хранилище.
* <ItemLink id="interface" /> даёт подсети размещения цветущих блоков доступ к <ItemLink id="flawed_budding_quartz" /> и
    даёт подсети ломания блока истинного кварца способ вернуть истощённые блоки в основную сеть.
    <ItemLink id="crafting_card" /> позволяет интерфейсу запрашивать новые цветущие блоки из [автокрафта](../ae2-mechanics/autocrafting.md) основной сети.