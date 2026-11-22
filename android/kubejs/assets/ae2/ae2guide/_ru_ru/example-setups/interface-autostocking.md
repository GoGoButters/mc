---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автопополнение через Интерфейс
  icon: interface
---

# Автопополнение через Интерфейс

Кто-то может спросить: «Как поддерживать определённое количество разных предметов в запасе, создавая ещё по мере необходимости?»

Одно из решений — использование <ItemLink id="interface" /> и <ItemLink id="crafting_card" /> для автоматического запроса новых предметов
из [автокрафта](../ae2-mechanics/autocrafting.md) вашей сети. Эта схема лучше подходит для поддержания небольшого количества большого
разнообразия предметов.

Эта демонстрационная схема обрезана, чтобы не быть слишком широкой; скорее всего, оптимально использовать 4 <ItemLink id="interface" /> и 4 <ItemLink id="storage_bus" />,
чтобы задействовать все 8 [каналов](../ae2-mechanics/channels.md) в обычном [кабеле](../items-blocks-machines/cables.md).

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_autostocking.snbt" />

<BoxAnnotation color="#dddddd" min="0 0 0" max="2 1 1">
        (1) Интерфейсы: Настроены хранить желаемые предметы в себе. В них есть карты крафта.
        <ItemImage id="crafting_card" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="2 1.3 1">
        (2) Шины хранения: «Режим ввода/вывода» установлен на «Только извлечение».
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* <ItemLink id="interface" /> (1) настроены хранить желаемые предметы в себе: нужно кликнуть желаемый предмет в их
   верхние слоты или перетащить его в верхние слоты из JEI, затем кликнуть по значку гаечного ключа над слотами, чтобы задать количество. В них есть <ItemLink id="crafting_card" />.
* <ItemLink id="storage_bus" /> (2) настроены так, что «Режим ввода/вывода» установлен на «Только извлечение».

## Как это работает

1. Если <ItemLink id="interface" /> не может получить достаточное количество настроенного предмета из [хранилища сети](../ae2-mechanics/import-export-storage.md),
   (и в нём есть <ItemLink id="crafting_card" />), он запросит у [автокрафта](../ae2-mechanics/autocrafting.md) сети создание большего количества этого предмета.
2. <ItemLink id="storage_bus" /> позволяют сети получать доступ к содержимому интерфейсов.