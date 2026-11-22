---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация Процессоров
  icon: logic_processor
---

# Автоматизация Производства Процессоров

Есть много способов автоматизировать [процессоры](../items-blocks-machines/processors.md), и это один из них.

Эта общая компоновка может быть выполнена с любым типом логистической трубы, кондуита, воздуховода или как ещё мод это называет, 
если вы можете её отфильтровать.

![Диаграмма Потока Процесса](../assets/diagrams/processor_flow_diagram.png)

Далее подробно описано, как это сделать только с помощью AE2, используя [«трубные» подсети](pipe-subnet.md).

Учтите, что поскольку здесь используется <ItemLink id="pattern_provider" />, эта схема предназначена для интеграции в вашу [автокрафт-установку](../ae2-mechanics/autocrafting.md).
Если вы хотите автоматизировать процессоры отдельно, замените поставщика шаблонов другим бочонком и кладите ингредиенты прямо в верхний бочонок.

Это, кстати, совместимо с прошлыми
версиями AE2, потому что даже если <ItemLink id="inscriber" /> направленные, трубные подсети всё равно вставляют в
правильные грани и извлекают из них.

## Урок По Кодированию Шаблонов

Часто [шаблон](../items-blocks-machines/patterns.md), который вам нужно закодировать, **НЕ БУДЕТ СОВПАДАТЬ С ТЕМ, ЧТО ВЫ ВИДИТЕ В JEI**, или с тем, что выводит JEI при нажатии кнопки +.
В этом случае JEI выдаст 2 отдельных шаблона: один для напечатанных компонентов и один для финальной сборки, и шаблон напечатанных
компонентов будет включать [пресс](../items-blocks-machines/presses.md). Это не то, что нам нужно, потому что схема будет делать не это. Нам нужен 1 шаблон, который
принимает сырьё на входе и выдаёт готовый процессор, и поскольку пресс уже находится в высекателе, мы не должны включать его в шаблон.

---

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/processor_automation.snbt" />

  <BoxAnnotation color="#dddddd" min="5 1 0" max="6 2 1" thickness=".05">
        (1) Поставщик шаблонов: В конфигурации по умолчанию, с соответствующими шаблонами обработки.

        <Row>
            ![Логический Шаблон](../assets/diagrams/logic_pattern_small.png)
            ![Шаблон Вычислений](../assets/diagrams/calculation_pattern_small.png)
            ![Инженерный Шаблон](../assets/diagrams/engineering_pattern_small.png)
        </Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4.7 2 0" max="5 3 1" thickness=".05">
        (2) Шина хранения №1: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 1 0" max="4.3 2 1" thickness=".05">
        (3) Шина экспорта №1: Отфильтрована на кремний, имеет 2 ускорительные карты
        <Row><ItemImage id="silicon" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 4 0" max="4.3 3 1" thickness=".05">
        (4) Шина экспорта №2: Отфильтрована на золотой слиток, имеет 2 ускорительные карты
        <Row><ItemImage id="minecraft:gold_ingot" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 5 0" max="4.3 4 1" thickness=".05">
        (5) Шина экспорта №3: Отфильтрована на кристалл истинного кварца, имеет 2 ускорительные карты
        <Row><ItemImage id="certus_quartz_crystal" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 6 0" max="4.3 5 1" thickness=".05">
        (6) Шина экспорта №4: Отфильтрована на алмаз, имеет 2 ускорительные карты
        <Row><ItemImage id="minecraft:diamond" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 3 0" max="2 2 1" thickness=".05">
        (7) Шина экспорта №5: Отфильтрована на редстоун-пыль, имеет 2 ускорительные карты
        <Row><ItemImage id="minecraft:redstone" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 1 0" max="3 2 1" thickness=".05">
        (8) Высекатель №1: В конфигурации по умолчанию. Имеет кремниевый пресс и 4 ускорительные карты
        <Row><ItemImage id="silicon_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 3 0" max="3 4 1" thickness=".05">
        (9) Высекатель №2: В конфигурации по умолчанию. Имеет логический пресс и 4 ускорительные карты
        <Row><ItemImage id="logic_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 4 0" max="3 5 1" thickness=".05">
        (10) Высекатель №3: В конфигурации по умолчанию. Имеет пресс вычислений и 4 ускорительные карты
        <Row><ItemImage id="calculation_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 5 0" max="3 6 1" thickness=".05">
        (11) Высекатель №4: В конфигурации по умолчанию. Имеет инженерный пресс и 4 ускорительные карты
        <Row><ItemImage id="engineering_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 2 0" max="1 3 1" thickness=".05">
        (12) Высекатель №5: В конфигурации по умолчанию. Имеет 4 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 2 0" max="3 1 1" thickness=".05">
        (13) Шина импорта №1: В конфигурации по умолчанию, имеет 2 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 4 0" max="3 3 1" thickness=".05">
        (14) Шина импорта №2: В конфигурации по умолчанию, имеет 2 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 5 0" max="3 4 1" thickness=".05">
        (15) Шина импорта №3: В конфигурации по умолчанию, имеет 2 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 6 0" max="3 5 1" thickness=".05">
        (16) Шина импорта №4: В конфигурации по умолчанию, имеет 2 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 3 0" max="1 3.3 1" thickness=".05">
        (17) Шина хранения №2: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 1.7 0" max="1 2 1" thickness=".05">
        (18) Шина хранения №3: В конфигурации по умолчанию.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 2 0" max="0.7 3 1" thickness=".05">
        (19) Шина импорта №5: В конфигурации по умолчанию, имеет 2 ускорительные карты
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="5 0.7 0" max="6 1 1" thickness=".05">
        (20) Шина хранения №4: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.3 2.7 0.3" max="3.7 3 0.7" thickness=".05">
        Кварцевое волокно питает все 3 высекателя, потому что высекатели действуют как кабели и, следовательно, передают энергию
  </BoxAnnotation>

<DiamondAnnotation pos="7 1.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="185" pitch="5" />
</GameScene>

## Конфигурации

* <ItemLink id="pattern_provider" /> (1) находится в конфигурации по умолчанию, с соответствующими <ItemLink id="processing_pattern" />.
  Обратите внимание, что шаблоны идут напрямую от сырья к готовому процессору и **НЕ** включают [пресс](../items-blocks-machines/presses.md).

  ![Логический Шаблон](../assets/diagrams/logic_pattern.png)
  ![Шаблон Вычислений](../assets/diagrams/calculation_pattern.png)
  ![Инженерный Шаблон](../assets/diagrams/engineering_pattern.png)

* <ItemLink id="storage_bus" /> (2, 17, 18, 20) находятся в конфигурациях по умолчанию.
* <ItemLink id="export_bus" /> (3-7) отфильтрованы на соответствующий ингредиент. В них 2 <ItemLink id="speed_card" />.
    <Row>
      <ItemImage id="silicon" scale="2" />
      <ItemImage id="minecraft:gold_ingot" scale="2" />
      <ItemImage id="certus_quartz_crystal" scale="2" />
      <ItemImage id="minecraft:diamond" scale="2" />
      <ItemImage id="minecraft:redstone" scale="2" />
    </Row>
* <ItemLink id="import_bus" /> (13-16, 19) находятся в конфигурациях по умолчанию. В них 2 <ItemLink id="speed_card" />.
* <ItemLink id="inscriber" /> находятся в конфигурациях по умолчанию. В них соответствующий [пресс](../items-blocks-machines/presses.md),
   и 4 <ItemLink id="speed_card" />.
   <Row>
     <ItemImage id="silicon_press" scale="2" />
     <ItemImage id="logic_processor_press" scale="2" />
     <ItemImage id="calculation_processor_press" scale="2" />
     <ItemImage id="engineering_processor_press" scale="2" />
   </Row>

## Как это работает

1. <ItemLink id="pattern_provider" /> вталкивает ингредиенты в бочонок.
2. Первая [трубная подсеть](pipe-subnet.md) (оранжевая) вытягивает кремний, редстоун-пыль и ингредиент соответствующего процессора
   (золотой слиток, кристалл истинного кварца или алмаз) из бочонка и кладёт их в соответствующий <ItemLink id="inscriber" />.
3. Первые четыре <ItemLink id="inscriber" /> изготавливают <ItemLink id="printed_silicon" /> и <ItemLink id="printed_logic_processor" />,
   <ItemLink id="printed_calculation_processor" /> или <ItemLink id="printed_engineering_processor" />.
4. Вторая и третья [трубные подсети](pipe-subnet.md) (зелёные) вынимают напечатанные платы из первых четырёх <ItemLink id="inscriber" />
    и кладут их в пятый, финально-сборочный <ItemLink id="inscriber" />.
5. Пятый <ItemLink id="inscriber" /> собирает [процессор](../items-blocks-machines/processors.md).
6. Четвёртая [трубная подсеть](pipe-subnet.md) (фиолетовая) кладёт процессор в поставщика шаблонов, возвращая его в основную сеть.