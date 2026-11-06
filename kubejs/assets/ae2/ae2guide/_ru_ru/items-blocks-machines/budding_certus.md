---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Почкопорождающий Кварц
  icon: flawless_budding_quartz
  position: 010
categories:
- misc ingredients blocks
item_ids:
- ae2:flawless_budding_quartz
- ae2:flawed_budding_quartz
- ae2:chipped_budding_quartz
- ae2:damaged_budding_quartz
- ae2:small_quartz_bud
- ae2:medium_quartz_bud
- ae2:large_quartz_bud
- ae2:quartz_cluster
---

# Почкопорождающий Кварц

(также см. [Рост Кварца](../ae2-mechanics/certus-growth.md))

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_blocks.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Почки истинного кварца будут прорастать из цветущих блоков кварца, подобно аметисту. Они встречаются в [метеоритах](../ae2-mechanics/meteorites.md).
Есть 4 яруса цветущих блоков кварца: Безупречный, Испорченный, Сколотый и Повреждённый. Их проще всего различить
с помощью мода вроде HWYLA, Jade, The One Probe и т.п. (или экрана F3)

С испорченным, сколотым и повреждённым цветущим кварцем каждый раз, когда почка вырастает ещё на одну стадию, цветущий блок имеет шанс
деградировать на один ярус, в итоге превращаясь в обычный <ItemLink id="quartz_block" />.

Безупречный цветущий кварц не деградирует от роста почек и действует как бесконечный источник.

Если сломать обычной киркой, цветущие блоки кварца деградируют на 1 ярус. Если сломать киркой,
зачарованной на шёлковое касание, они не деградируют, если только не были безупречными. **Это значит, что безупречные цветущие блоки кварца нельзя
подобрать и переместить киркой**. Вместо этого для
вырезания и вставки безупречных цветущих блоков можно использовать [Пространственное Хранилище](../ae2-mechanics/spatial-io.md).

## Рецепты

Испорченный, сколотый и повреждённый цветущий кварц можно скрафтить, бросив предыдущий ярус цветущего блока (или <ItemLink id="quartz_block" />)
в воду с одним или несколькими <ItemLink id="charged_certus_quartz_crystal" />.

Безупречный цветущий кварц нельзя скрафтить, он встречается только в мире.

<Row>
  <RecipeFor id="damaged_budding_quartz" />

  <RecipeFor id="chipped_budding_quartz" />

  <RecipeFor id="flawed_budding_quartz" />
</Row>