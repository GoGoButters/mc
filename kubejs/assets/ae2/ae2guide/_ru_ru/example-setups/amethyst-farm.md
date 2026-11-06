---
navigation:
  parent: example-setups/example-setups-index.md
  title: Ферма аметиста
  icon: minecraft:amethyst_shard
---

# Ферма аметиста

Хотя <ItemLink id="growth_accelerator" /> работает и с аметистом, обычные методы фильтрации [почек истинного кварца](../items-blocks-machines/budding_certus.md)
с помощью <ItemLink id="annihilation_plane" /> не работают с аметистовыми почками. В отличие от незрелых почек истинного кварца, которые выпадают
<ItemLink id="certus_quartz_dust" />, незрелые аметистовые почки не выпадают ничем, поэтому плоскость аннигиляции всегда будет ломать их,
потому что сеть всегда может хранить «ничего».

Обходной путь — зачаровать плоскость аннигиляции на «Шёлковое касание». Тогда незрелые аметистовые почки *будут* выпадать чем-то
(различными стадиями физических блоков почек), и, таким образом, их можно будет фильтровать.

Затем <ItemLink id="minecraft:amethyst_cluster" /> должен быть снова размещён <ItemLink id="formation_plane" />, чтобы потом его
снова сломала <ItemLink id="annihilation_plane" /> без «шёлкового касания», чтобы получить <ItemLink id="minecraft:amethyst_shard" />ы.

Учтите, что из-за направленности грозди напротив плоскости формирования должна находиться сплошная грань блока.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/amethyst_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 1 1" max="3 2 2">
        (1) Плоскость аннигиляции №1: нет интерфейса для настройки, но зачарована «Шёлковым касанием».
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 1 1" max="2.3 2 2">
        (2) Плоскость формирования: фильтруется по грозди аметиста.
        <ItemImage id="minecraft:amethyst_cluster" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 0.7 1" max="2 1 2">
        (3) Плоскость аннигиляции №2: нет интерфейса для настройки, но может быть зачарована на «Удачу».
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="1.3 1 2">
        (4) Шина хранения №1: фильтруется по осколку аметиста.
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 .7" max="1 1 1">
        (5) Шина хранения №2: фильтруется по осколку аметиста. Имеет более высокий приоритет, чем ваше основное хранилище.
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* Первая <ItemLink id="annihilation_plane" /> (1) не имеет интерфейса и не может быть настроена, но должна быть зачарована на «Шёлковое касание».
* <ItemLink id="formation_plane" /> (2) фильтруется по <ItemLink id="minecraft:amethyst_cluster" />.
* Вторая <ItemLink id="annihilation_plane" /> (3) не имеет интерфейса и не может быть настроена, но может быть зачарована на «Удачу».
* Первая <ItemLink id="storage_bus" /> (4) фильтруется по <ItemLink id="minecraft:amethyst_shard" />.
* Вторая <ItemLink id="storage_bus" /> (5) фильтруется по <ItemLink id="minecraft:amethyst_shard" /> и имеет
  [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), заданный выше, чем у вашего основного хранилища.

## Как это работает

1. Первая <ItemLink id="annihilation_plane" /> пытается сломать то, что перед ней, но может сломать только <ItemLink id="minecraft:amethyst_cluster" />,
    потому что единственное хранилище в подсети — это <ItemLink id="formation_plane" />, фильтрованная по грозди аметиста. Это работает только потому,
что плоскость зачарована на «Шёлковое касание»; иначе она могла бы ломать и незрелые почки, поскольку те не выпадают ничем.
2. <ItemLink id="formation_plane" /> размещает гроздь на блоке напротив себя.
3. Вторая <ItemLink id="annihilation_plane" /> ломает гроздь, производя <ItemLink id="minecraft:amethyst_shard" />.
4. Первая <ItemLink id="storage_bus" /> хранит осколки в бочке. Технически её не нужно фильтровать, потому что единственное,
с чем должна сталкиваться вторая плоскость аннигиляции, — это полностью выросшие грозди.
5. Вторая <ItemLink id="storage_bus" /> даёт основной сети доступ ко всем осколкам аметиста в бочке. Она задана с
высоким [приоритетом](../ae2-mechanics/import-export-storage.md#storage-priority), чтобы осколки аметиста предпочтительно
возвращались в бочку, а не попадали в ваше основное хранилище.