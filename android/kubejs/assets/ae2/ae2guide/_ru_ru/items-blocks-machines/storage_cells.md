---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Ячейки хранения
  icon: item_storage_cell_1k
  position: 410
categories:
- tools
item_ids:
- ae2:item_cell_housing
- ae2:fluid_cell_housing
- ae2:cell_component_1k
- ae2:cell_component_4k
- ae2:cell_component_16k
- ae2:cell_component_64k
- ae2:cell_component_256k
- ae2:item_storage_cell_1k
- ae2:item_storage_cell_4k
- ae2:item_storage_cell_16k
- ae2:item_storage_cell_64k
- ae2:item_storage_cell_256k
- ae2:fluid_storage_cell_1k
- ae2:fluid_storage_cell_4k
- ae2:fluid_storage_cell_16k
- ae2:fluid_storage_cell_64k
- ae2:fluid_storage_cell_256k
---

# Ячейки хранения

<Column>
  <Row>
    <ItemImage id="item_storage_cell_1k" scale="4" />

    <ItemImage id="item_storage_cell_4k" scale="4" />

    <ItemImage id="item_storage_cell_16k" scale="4" />

    <ItemImage id="item_storage_cell_64k" scale="4" />

    <ItemImage id="item_storage_cell_256k" scale="4" />
  </Row>

  <Row>
    <ItemImage id="fluid_storage_cell_1k" scale="4" />

    <ItemImage id="fluid_storage_cell_4k" scale="4" />

    <ItemImage id="fluid_storage_cell_16k" scale="4" />

    <ItemImage id="fluid_storage_cell_64k" scale="4" />

    <ItemImage id="fluid_storage_cell_256k" scale="4" />
  </Row>
</Column>

Ячейки хранения — один из основных способов хранения в Applied Energistics. Они помещаются в <ItemLink id="drive" />s
или <ItemLink id="chest" />s.

См. [Байты и типы](../ae2-mechanics/bytes-and-types.md) за объяснением их ёмкости в байтах и типах.

Компоненты хранения можно извлечь из корпуса, если ячейка пуста, нажав шифт и правую кнопку мыши с ячейкой в руке.

<Row>
    <Recipe id="upgrade/item_storage_cell_1k_to_4k" />

    Ячейки хранения можно улучшать до более высоких тиров, комбинируя их с компонентами хранения более высокого тира в верстаке. Их содержимое сохранится, а компонент более низкого тира будет возвращён.
</Row>

## Ёмкость хранения при различном количестве типов

[Начальная стоимость типов](../ae2-mechanics/bytes-and-types.md) устроена так, что ячейка, содержащая 1 тип, может вместить в 2 раза больше, чем ячейка со всеми 63 используемыми типами.

| Ячейка                                    | Полная ёмкость ячейки с 1 типом в использовании | Полная ёмкость ячейки с 63 типами в использовании |
| ---------------------------------------- | ----------------------------------------: | ------------------------------------------: |
| <ItemLink id="item_storage_cell_1k" />   |                                     8,128 |                                       4,160 |
| <ItemLink id="item_storage_cell_4k" />   |                                    32,512 |                                      16,640 |
| <ItemLink id="item_storage_cell_16k" />  |                                   130,048 |                                      66,560 |
| <ItemLink id="item_storage_cell_64k" />  |                                   520,192 |                                     266,240 |
| <ItemLink id="item_storage_cell_256k" /> |                                 2,080,768 |                                   1,064,960 |


## Разделение

Ячейки можно фильтровать, чтобы принимать только определённые предметы, подобно тому, как фильтруются <ItemLink id="storage_bus" />ses. Это делается в <ItemLink id="cell_workbench" />.

Предметы можно перетаскивать в слоты из JEI/REI, даже если у вас нет ни одного такого предмета.

## Улучшения

Ячейки хранения поддерживают следующие [улучшения](upgrade_cards.md), вставляемые через <ItemLink id="cell_workbench" />:

*   <ItemLink id="fuzzy_card" /> (недоступна на жидкостных ячейках) позволяет разбивать ячейку по уровню повреждения и/или игнорировать NBT предметов
*   <ItemLink id="inverter_card" /> переключает фильтр с белого списка на чёрный
*   <ItemLink id="equal_distribution_card" /> выделяет одинаковое количество байтового пространства ячейки на каждый тип, чтобы один тип не мог заполнить всю ячейку
*   <ItemLink id="void_card" /> уничтожает вставленные предметы, если ячейка полна (или если заполнено выделенное пространство определённого типа в случае карты равного распределения), полезно для предотвращения засорения ферм. Будьте осторожны, обязательно настройте её разделение!
*   Переносные ячейки могут принимать <ItemLink id="energy_card" />, чтобы увеличить ёмкость своей батареи

## Окрашивание

Переносные предметные и жидкостные ячейки можно окрашивать, аналогично кожаной броне, создавая их вместе с красителями.

# Корпуса

Ячейки можно сделать из компонента хранения и корпуса, либо по рецепту корпуса вокруг компонента хранения:

<Row>
  <Recipe id="network/cells/item_storage_cell_1k" />

  <Recipe id="network/cells/item_storage_cell_1k_storage" />
</Row>

Сами корпуса создаются так:

<Row>
  <RecipeFor id="item_cell_housing" />

  <RecipeFor id="fluid_cell_housing" />
</Row>

# Компоненты хранения

Компоненты хранения — ядро всех ячеек AE2, определяющее их ёмкость. Каждый тир увеличивает ёмкость
в 4 раза и стоит 3 компонента предыдущего тира.

<Column>
  <Row>
    <RecipeFor id="cell_component_1k" />

    <RecipeFor id="cell_component_4k" />

    <RecipeFor id="cell_component_16k" />
  </Row>

  <Row>
    <RecipeFor id="cell_component_64k" />

    <RecipeFor id="cell_component_256k" />
  </Row>
</Column>

# Предметные ячейки хранения

Предметные ячейки хранения могут вмещать до 63 различных типов предметов и доступны во всех стандартных ёмкостях.

<Column>
  <Row>
    <Recipe id="network/cells/item_storage_cell_1k_storage" />

    <Recipe id="network/cells/item_storage_cell_4k_storage" />

    <Recipe id="network/cells/item_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/item_storage_cell_64k_storage" />

    <Recipe id="network/cells/item_storage_cell_256k_storage" />
  </Row>
</Column>

## Переносная предметная ячейка

Они действуют как крошечный <ItemLink id="chest" /> в вашем кармане, или как своего рода рюкзак. Их можно заряжать в <ItemLink id="charger" />

В отличие от стандартных ячеек хранения, они фактически *уменьшают* ёмкость типов по мере роста байтовой ёмкости и имеют половину
полной байтовой ёмкости.

В дополнение к картам улучшений, которые могут принимать все ячейки, они также принимают <ItemLink id="energy_card" />s для улучшения своих внутренних батарей.

<Column>
  <Row>
    <RecipeFor id="portable_item_cell_1k" />

    <RecipeFor id="portable_item_cell_4k" />

    <RecipeFor id="portable_item_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_item_cell_64k" />

    <RecipeFor id="portable_item_cell_256k" />
  </Row>
</Column>

# Жидкостные ячейки хранения

Жидкостные ячейки хранения могут вмещать до 5 различных типов жидкостей и доступны во всех стандартных ёмкостях.

<Column>
  <Row>
    <Recipe id="network/cells/fluid_storage_cell_1k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_4k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/fluid_storage_cell_64k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_256k_storage" />
  </Row>
</Column>

## Переносная жидкостная ячейка

Они действуют как крошечный <ItemLink id="chest" /> в вашем кармане, или как своего рода рюкзак. Их можно заряжать в <ItemLink id="charger" />

В отличие от стандартных ячеек хранения, они фактически *уменьшают* ёмкость типов по мере роста байтовой ёмкости и имеют половину
полной байтовой ёмкости.

В дополнение к картам улучшений, которые могут принимать все ячейки, они также принимают <ItemLink id="energy_card" />s для улучшения своих внутренних батарей.

<Column>
  <Row>
    <RecipeFor id="portable_fluid_cell_1k" />

    <RecipeFor id="portable_fluid_cell_4k" />

    <RecipeFor id="portable_fluid_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_fluid_cell_64k" />

    <RecipeFor id="portable_fluid_cell_256k" />
  </Row>
</Column>

# Творческая ячейка хранения

<Row>
  <ItemImage id="creative_storage_cell" scale="2" />
</Row>

Творческие ячейки **не обеспечивают бесконечное хранение**. Вместо этого они действуют как бесконечные источники и стоки любого
предмета или жидкости, к которым вы их [разделили](cell_workbench.md).