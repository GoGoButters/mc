---
navigation:
  parent: example-setups/example-setups-index.md
  title: Специализированное Локальное Хранилище
  icon: drive
---

# Специализированное Локальное Хранилище

Используя одно из [особых поведений интерфейса](../items-blocks-machines/interface.md#special-interactions),
[подсеть](../ae2-mechanics/subnetworks.md) может показывать содержимое своего хранилища основной сети, не имея при этом возможности
видеть хранилище основной сети, и занимая всего 1 [канал](../ae2-mechanics/channels.md).

Это полезно для локального хранения на какой-нибудь ферме, чтобы предметы не переполняли ваше основное хранилище.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/local_storage.snbt" />

<BoxAnnotation color="#dddddd" min="4 0 0" max="5 2 1">
        (1) Какой-то способ импорта предметов (в данном случае интерфейс)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 0 0" max="4 1 1">
        (2) Накопитель: В нём несколько ячеек. Ячейки должны быть отфильтрованы на то, что выводит ферма.
        Ячейки могут иметь карты равного распределения и карты уничтожения избытка.
        <Row><ItemImage id="item_storage_cell_4k" scale="2" /> <ItemImage id="equal_distribution_card" scale="2" /> <ItemImage id="void_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1 0" max="4 2 0.3">
        (3) Крафт-терминал: Он может видеть содержимое накопителя в подсети, но не содержимое хранилища вашей основной сети.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
        (4) Интерфейс №2: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 0" max="2 1 1">
        (5) Шина хранения: Имеет приоритет выше, чем основное хранилище, может быть отфильтрована на то, что выводит ферма.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 2 0.3">
        Крафт-терминал: Он может видеть и содержимое хранилища основной сети, *и* подсети.
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первый <ItemLink id="interface" /> (1) просто принимает предметы с вашей фермы и вталкивает их в подсеть.
* <ItemLink id="drive" /> (2) имеет несколько [ячеек](../items-blocks-machines/storage_cells.md). Ячейки должны быть
  [разделены на секции](../items-blocks-machines/cell_workbench.md) под то, что выводит ферма.
  Ячейки могут иметь <ItemLink id="equal_distribution_card" /> и <ItemLink id="void_card" />.
* Второй <ItemLink id="interface" /> (4) находится в конфигурации по умолчанию.
* <ItemLink id="storage_bus" /> имеет [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), установленный
  выше, чем у основного хранилища. Она может быть отфильтрована на то, что выводит ферма.

## Как это работает

* Интерфейс <ItemLink id="interface" /> в подсети показывает шине хранения <ItemLink id="storage_bus" /> на основной сети, что находится в
накопителе <ItemLink id="drive" />. Это означает, что шина хранения может напрямую забирать предметы из ячеек накопителя и вталкивать их в него.
* Шина хранения установлена на высокий [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), чтобы предметы преимущественно
  возвращались в подсеть, а не в ваше основное хранилище.
* Важно: если ячейки в подсети заполняются, предметы не переполнят основную сеть. Если ферма такого типа, что
ломается при переполнении, для удаления лишних предметов можно использовать <ItemLink id="void_card" />. 
* Если ферма выводит несколько предметов, <ItemLink id="equal_distribution_card" /> могут помешать одному предмету заполнить все ячейки
и не дать сохраниться другим.