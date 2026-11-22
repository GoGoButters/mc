---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматический Фортунящий Руды
  icon: minecraft:raw_iron
---

# Автоматизация Добычи Руды с Фортуной

Плоскость аннигиляции <ItemLink id="annihilation_plane" /> можно зачаровать любым зачарованием кирки, включая фортуну, поэтому очевидный вариант применения — наложить
фортуну на несколько, и с помощью <ItemLink id="formation_plane" /> и <ItemLink id="annihilation_plane" /> быстро ставить и
ломать руды.

Учтите, что поскольку <ItemLink id="import_bus" /> «разгоняются до полной скорости», схема будет работать медленно, а затем достигнет полной скорости через несколько секунд.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 0 2" max="3 1 3">
        (1) Шина импорта: В ней несколько ускорительных карт.
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 2" max="2 1 2.3">
        (2) Плоскости формирования: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 0.7" max="2 1 1">
        (3) Плоскости аннигиляции: Нет GUI для настройки, но зачарованы фортуной.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 0" max="3 1 1">
        (4) Шина хранения: В конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="3.5 0.5 2.5" color="#00ff00">
        Вход
    </DiamondAnnotation>

<DiamondAnnotation pos="3.5 0.5 0.5" color="#00ff00">
        Выход
    </DiamondAnnotation>

<DiamondAnnotation pos="4 0.5 1.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

*   <ItemLink id="import_bus" /> (1) имеет несколько <ItemLink id="speed_card" />. Чем больше плоскостей формирования
    в массиве, тем больше карт требуется, так как они заставляют шину импорта забирать больше предметов одновременно.
*   <ItemLink id="formation_plane" /> (2) находятся в конфигурациях по умолчанию.
*   <ItemLink id="annihilation_plane" /> (3) не имеют GUI и не могут быть настроены, но зачарованы фортуной.
*   <ItemLink id="storage_bus" /> (4) находится в конфигурации по умолчанию.

## Как это работает

1.  <ItemLink id="import_bus" /> в зелёной подсети импортирует блоки из первого бочонка в [хранилище сети](../ae2-mechanics/import-export-storage.md)
2.  Единственное хранилище в зелёной подсети — <ItemLink id="formation_plane" />, который ставит блоки.
3.  <ItemLink id="annihilation_plane" /> в оранжевой подсети ломает блоки, применяя к ним фортуну.
4.  <ItemLink id="storage_bus" /> в оранжевой подсети сохраняет результаты добычи во втором бочонке.