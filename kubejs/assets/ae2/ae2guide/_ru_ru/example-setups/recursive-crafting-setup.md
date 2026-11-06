---
navigation:
  parent: example-setups/example-setups-index.md
  title: Рекурсивный Крафт
  icon: minecraft:netherite_upgrade_smithing_template
---

# Рекурсивная Крафт-Схема

Как указано в [автокрафте](../ae2-mechanics/autocrafting.md), алгоритм планирования автокрафта не может обработать рецепты, где
основной выход является одним из входов. Например, он не может обработать клонирование <ItemLink id="minecraft:netherite_upgrade_smithing_template" />.

Одно из решений — использовать способность <ItemLink id="level_emitter" /> притворяться [шаблоном](../items-blocks-machines/patterns.md).

Это затем будет использовано для включения небольшой схемы, которая постоянно выполняет крафт. В этом случае мы рассмотрим схему
для клонирования <ItemLink id="minecraft:netherite_upgrade_smithing_template" />.

<RecipeFor id="minecraft:netherite_upgrade_smithing_template" />

***

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/recursive_recipe_setup.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) Интерфейс: настроен хранить требуемые дополнительные ингредиенты: алмаз и нзерак.
        <Row><ItemImage id="minecraft:diamond" scale="2" /> <ItemImage id="minecraft:netherrack" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 1 0.3" max="2.7 1.3 0.7">
        (2) Уровневый эмиттер: Настроен с «нетертитовым шаблоном доспехов», установлен на «Излучать редстоун для крафта предмета».
        <Row><ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
        (3) Шина импорта №1: Отфильтрована на предметы, которые хранит интерфейс. Имеет редстоун-карту. Режим редстоуна установлен на
        «Активен при сигнале».
        <Row>
        <ItemImage id="minecraft:diamond" scale="2" />
        <ItemImage id="minecraft:netherrack" scale="2" />
        <ItemImage id="redstone_card" scale="2" />
        </Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="4 1.3 2">
        (4) Шина хранения №1: Установлена на более высокий приоритет, чем другая шина хранения. ОЧЕНЬ ВАЖНО.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 0 1" max="4 1 2">
        (5) Молекулярный сборщик: В нём шаблон для дублирования шаблона доспехов.

        ![Шаблон](../assets/diagrams/smithing_template_pattern_small.png)

        Также в него при первой сборке вручную уже вставлен один шаблон доспехов.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        (6) Шина импорта №2: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="2 1 1.3">
        (7) Шина хранения №2: Отфильтрована на «нетертитовый шаблон доспехов». Установлена на более низкий приоритет, чем другая шина хранения.
        <ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

## Конфигурации

* <ItemLink id="interface" /> (1) настроен хранить требуемые дополнительные ингредиенты: алмаз и нзерак.
* <ItemLink id="level_emitter" /> (2) настроен с «нетертитовым шаблоном доспехов» и установлен на «Излучать редстоун для крафта предмета».
* Первая <ItemLink id="import_bus" /> (3) отфильтрована на предметы, которые хранит интерфейс. В ней есть редстоун-карта. Режим редстоуна установлен на «Активен при сигнале».
* Первая <ItemLink id="storage_bus" /> (4) установлена на *более высокий* [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), чем вторая шина хранения.
* <ItemLink id="molecular_assembler" /> (5) имеет шаблон для дублирования шаблона доспехов и один шаблон доспехов, уже вставленный вручную.

  ![Шаблон](../assets/diagrams/smithing_template_pattern.png)

* Вторая <ItemLink id="import_bus" /> (6) находится в конфигурации по умолчанию.
* Вторая <ItemLink id="storage_bus" /> (7) отфильтрована на «нетертитовый шаблон доспехов». Она имеет *более низкий* [приоритет](../ae2-mechanics/import-export-storage.md#storage-priority), чем первая шина хранения.

## Как это работает

1. <ItemLink id="level_emitter" /> притворяется [шаблоном](../items-blocks-machines/patterns.md) благодаря вставленной
   <ItemLink id="crafting_card" /> и настройке «Излучать редстоун для крафта предмета». Таким образом, «нетертитовый шаблон доспехов» появляется в
   [терминалах](../items-blocks-machines/terminals.md) как допустимый предмет для [автокрафта](../ae2-mechanics/autocrafting.md).
2. Получив запрос на крафт этого предмета, будь то от игрока или от самой системы, уровневый эмиттер включается.
3. Первая <ItemLink id="import_bus" /> активируется уровневым эмиттером и забирает ингредиенты, хранящиеся в <ItemLink id="interface" />.
4. Единственная <ItemLink id="storage_bus" /> в сети, способная хранить эти ингредиенты, — та, что на сборщике.
5. <ItemLink id="molecular_assembler" /> получает ингредиенты (уже имея внутри 1 шаблон доспехов) и выполняет крафт, производя 2 шаблона доспехов.
6. Вторая <ItemLink id="import_bus" /> извлекает 1 шаблон доспехов.
7. Первая шина хранения имеет более высокий приоритет, поэтому этот шаблон доспехов возвращается в сборщик.
8. Вторая <ItemLink id="import_bus" /> извлекает 1 шаблон доспехов.
9. Сборщик не может получить ещё один шаблон доспехов, поэтому второй шаблон доспехов уходит в шину хранения с более низким приоритетом, вставляя его в интерфейс.
10. <ItemLink id="interface" />, не настроенный хранить шаблоны доспехов, вставляет его в сеть.