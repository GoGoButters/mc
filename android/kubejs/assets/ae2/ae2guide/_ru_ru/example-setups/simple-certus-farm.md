---
navigation:
  parent: example-setups/example-setups-index.md
  title: Простая Ферма Кварца
  icon: certus_quartz_crystal
  position: 110
---

# Простая Ферма Кварца

Как упоминалось в [Росте Кварца](../ae2-mechanics/certus-growth.md), автоматизация сбора <ItemLink id="certus_quartz_crystal" />
включает <ItemLink id="annihilation_plane" /> и <ItemLink id="storage_bus" />. 
<ItemLink id="growth_accelerator" /> используются для массового ускорения роста почек истинного кварца, а затем плоскости
ломают полностью выросшие <ItemLink id="quartz_cluster" />. Они фильтруются благодаря подозрительно удачливому свойству: незрелые
почки кварца выпадают <ItemLink id="certus_quartz_dust" /> вместо того, чтобы ничего не выпадать.

Эта ферма работает полностью автоматически с <ItemLink id="flawless_budding_quartz" />, но с испорченным, сколотым и повреждённым
цветущим кварцем вам придётся заменять цветущий блок вручную. Или, как описано в [Полуавтоматической ферме кварца](semiauto-certus-farm.md)
и [Продвинутой ферме кварца](advanced-certus-farm.md), автоматически.

См. [Рост Кварца](../ae2-mechanics/certus-growth.md) для ориентировочных скоростей.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/simple_certus_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="3.7 1 1" max="4 2 2">
        (1) Плоскость аннигиляции: Нет GUI для настройки, но можно зачаровать на фортуну.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="3.3 2 2">
        (2) Шина хранения №1: Отфильтрована на кристалл истинного кварца.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 .7" max="2 2 1">
        (3) Шина хранения №2: Отфильтрована на кристалл истинного кварца. Имеет приоритет выше, чем основное хранилище.
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первая <ItemLink id="annihilation_plane" /> (1) не имеет GUI и не может быть настроена, но может быть зачарована на фортуну.
* Первая <ItemLink id="storage_bus" /> (2) отфильтрована на <ItemLink id="certus_quartz_crystal" />.
* Вторая <ItemLink id="storage_bus" /> (3) отфильтрована на <ItemLink id="certus_quartz_crystal" />, и её
  [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority) установлен выше, чем у основного хранилища.

## Как это работает

1. <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="quartz_cluster" />,
   потому что единственное хранилище в подсети — <ItemLink id="storage_bus" />, отфильтрованная на <ItemLink id="certus_quartz_crystal" />.
4. Первая <ItemLink id="storage_bus" /> сохраняет кристаллы истинного кварца в бочонке.
5. Вторая <ItemLink id="storage_bus" /> даёт основной сети доступ ко всем кристаллам истинного кварца в бочонке. Она установлена на
   высокий [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), чтобы кристаллы истинного кварца преимущественно
   возвращались в бочонок, а не в ваше основное хранилище.