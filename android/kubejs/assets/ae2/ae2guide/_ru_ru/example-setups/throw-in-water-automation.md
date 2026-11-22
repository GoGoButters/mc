---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация Бросания в Воду
  icon: fluix_crystal
---

# Автоматизация Рецептов Бросания в Воду

Учтите, что поскольку здесь используется <ItemLink id="pattern_provider" />, эта схема предназначена для интеграции в вашу [автокрафт-установку](../ae2-mechanics/autocrafting.md).

Некоторые рецепты требуют бросания предметов в воду (хотя аналогичную схему можно использовать для бросания предметов куда угодно).
Это можно автоматизировать с помощью <ItemLink id="formation_plane" />, <ItemLink id="annihilation_plane" /> и вспомогательной
инфраструктуры (по сути, это 2 модифицированные [трубные подсети](pipe-subnet.md)).

Эта схема предназначена для использования в сочетании с [автоматизацией зарядника](charger-automation.md) для получения <ItemLink id="charged_certus_quartz_crystal" />.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/throw_in_water.snbt" />

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
        (1) Поставщик шаблонов: В конфигурации по умолчанию, с соответствующими шаблонами обработки.

        ![Шаблон Флюикса](../assets/diagrams/fluix_pattern_small.png) ![Шаблон Испорченного Почкопорождающего](../assets/diagrams/flawed_budding_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 1" max="2 1 2">
        (2) Интерфейс: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 .7 1" max="2 1 2">
        (3) Плоскость формирования: Установлена выбрасывать входные предметы как предметы.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 2 1" max="2 2.3 2">
        (4) Плоскость аннигиляции: Нет GUI для настройки.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
        (5) Шина хранения: Отфильтрована на выходы шаблонов
        <Row><ItemImage id="fluix_crystal" scale="2" /><BlockImage id="flawless_budding_quartz" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="3.9 0.5 1.5" color="#00ff00">
        К основной сети и автоматизации зарядника
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/charger_automation.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
    </DiamondAnnotation>

  <IsometricCamera yaw="180" pitch="0" />
</GameScene>

## Конфигурации и Шаблоны

* <ItemLink id="pattern_provider" /> (1) находится в конфигурации по умолчанию, с соответствующими <ItemLink id="processing_pattern" />.
  * Для <ItemLink id="fluix_crystal" /> стандартный рецепт из JEI/REI работает нормально:

    ![Шаблон Флюикса](../assets/diagrams/fluix_pattern.png)

  * Для <ItemLink id="flawed_budding_quartz" />, вероятно, лучше всего делать его напрямую из <ItemLink id="quartz_block" />,
    что позволяет избежать проблем, когда вход одного рецепта является выходом другого, из-за чего шина хранения не может фильтровать:

    ![Шаблон Испорченного Почкопорождающего](../assets/diagrams/flawed_budding_pattern.png)

* <ItemLink id="interface" /> (2) находится в конфигурации по умолчанию.
* <ItemLink id="formation_plane" /> (3) установлена выбрасывать входные предметы как предметы.
* <ItemLink id="annihilation_plane" /> (4) не имеет GUI и не может быть настроена.
* <ItemLink id="storage_bus" /> (5) отфильтрована на выходы шаблонов.

## Как это работает

1.  <ItemLink id="pattern_provider" /> вталкивает ингредиенты в <ItemLink id="interface" /> сбоку от себя, в зелёной подсети
2.  Интерфейс (будучи по умолчанию настроенным ничего не хранить) пытается втолкнуть своё содержимое в [хранилище сети](../ae2-mechanics/import-export-storage.md)
3.  Единственное хранилище в зелёной подсети — <ItemLink id="formation_plane" />, которая выбрасывает получаемые предметы в воду
4.  <ItemLink id="annihilation_plane" /> в оранжевой подсети пытается подобрать только что выброшенные предметы, но не может, потому что
    <ItemLink id="storage_bus" /> сверху поставщика шаблонов (единственное хранилище в оранжевой подсети) отфильтрована на приём только результатов возможных крафтов
5.  Предметы проходят своё преобразование в мире.
6.  Плоскость аннигиляции теперь может подобрать предметы перед ней, поскольку шине хранения разрешено их хранить.
7.  Шина хранения сохраняет полученные предметы в поставщике шаблонов, возвращая их в сеть.