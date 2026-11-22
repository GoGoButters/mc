---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация Печи
  icon: minecraft:furnace
---

# Автоматизация Печи

Учтите, что поскольку здесь используется <ItemLink id="pattern_provider" />, эта схема предназначена для интеграции в вашу [автокрафт-установку](../ae2-mechanics/autocrafting.md).
Если вы хотите автоматизировать печь отдельно, используйте воронки, сундуки и тому подобное.

Автоматизация <ItemLink id="minecraft:furnace" /> немного сложнее, чем автоматизация более простых машин, например [зарядника](../example-setups/charger-automation.md).
Печи требуется ввод с двух отдельных сторон и извлечение из третьей. Предмет для переплавки должен вталкиваться в верхнюю грань,
топливо должно вталкиваться в боковую грань, а результат должен выталкиваться снизу.

Это можно сделать через <ItemLink id="pattern_provider" />
сверху, <ItemLink id="export_bus" /> сбоку для постоянной подачи топлива и <ItemLink id="import_bus" /> снизу
для импорта результатов в сеть. Однако это использует 3 [канала](../ae2-mechanics/channels.md).

Вот как это можно сделать всего с 1 каналом:

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/furnace_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) Поставщик шаблонов: Направленный вариант, с помощью гаечного ключа из истинного кварца, с соответствующими шаблонами обработки.

        ![Шаблон Железа](../assets/diagrams/furnace_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (2) Интерфейс: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="1.3 2 1">
        (3) Шина хранения №1: Отфильтрована на уголь.
        <ItemImage id="minecraft:coal" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 2 0" max="1 2.3 1">
        (4) Шина хранения №2: Отфильтрована на чёрный список угля, с помощью инвертирующей карты.
        <Row><ItemImage id="minecraft:coal" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* <ItemLink id="pattern_provider" /> (1) находится в конфигурации по умолчанию, с соответствующими <ItemLink id="processing_pattern" />.
    Он становится направленным, если применить к нему <ItemLink id="certus_quartz_wrench" />.

  ![Шаблон Железа](../assets/diagrams/furnace_pattern.png)

* <ItemLink id="interface" /> (2) находится в конфигурации по умолчанию.
* Первая <ItemLink id="storage_bus" /> (3) отфильтрована на уголь или любое топливо, которое вы хотите использовать.
* Вторая <ItemLink id="storage_bus" /> (4) отфильтрована с чёрным списком используемого топлива, с помощью <ItemLink id="inverter_card" />.

## Как это работает

1. <ItemLink id="pattern_provider" /> вталкивает ингредиенты в <ItemLink id="interface" />.
   (На самом деле, в качестве оптимизации, он вталкивает их напрямую через шины хранения, как если бы они были продолжением граней поставщика. Предметы на самом деле никогда не попадают в интерфейс.)
2. Интерфейс настроен ничего не хранить, поэтому он пытается втолкнуть ингредиенты в [хранилище сети](../ae2-mechanics/import-export-storage.md).
3. Единственные хранилища в зелёной подсети — это <ItemLink id="storage_bus" />. Шина, отфильтрованная на уголь, кладёт уголь в слот топлива печи через боковую грань.
    Шина, отфильтрованная на НЕ уголь, кладёт предметы для переплавки в верхний слот через верхнюю грань.
4. Печь делает своё печное дело
5. Воронка вытаскивает результат из нижней части печи и кладёт его в слоты возврата поставщика, возвращая его в основную сеть.