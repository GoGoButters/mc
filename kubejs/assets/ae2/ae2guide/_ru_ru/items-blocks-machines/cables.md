---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Кабели
  icon: fluix_glass_cable
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:white_glass_cable
- ae2:orange_glass_cable
- ae2:magenta_glass_cable
- ae2:light_blue_glass_cable
- ae2:yellow_glass_cable
- ae2:lime_glass_cable
- ae2:pink_glass_cable
- ae2:gray_glass_cable
- ae2:light_gray_glass_cable
- ae2:cyan_glass_cable
- ae2:purple_glass_cable
- ae2:blue_glass_cable
- ae2:brown_glass_cable
- ae2:green_glass_cable
- ae2:red_glass_cable
- ae2:black_glass_cable
- ae2:fluix_glass_cable
- ae2:white_covered_cable
- ae2:orange_covered_cable
- ae2:magenta_covered_cable
- ae2:light_blue_covered_cable
- ae2:yellow_covered_cable
- ae2:lime_covered_cable
- ae2:pink_covered_cable
- ae2:gray_covered_cable
- ae2:light_gray_covered_cable
- ae2:cyan_covered_cable
- ae2:purple_covered_cable
- ae2:blue_covered_cable
- ae2:brown_covered_cable
- ae2:green_covered_cable
- ae2:red_covered_cable
- ae2:black_covered_cable
- ae2:fluix_covered_cable
- ae2:white_covered_dense_cable
- ae2:orange_covered_dense_cable
- ae2:magenta_covered_dense_cable
- ae2:light_blue_covered_dense_cable
- ae2:yellow_covered_dense_cable
- ae2:lime_covered_dense_cable
- ae2:pink_covered_dense_cable
- ae2:gray_covered_dense_cable
- ae2:light_gray_covered_dense_cable
- ae2:cyan_covered_dense_cable
- ae2:purple_covered_dense_cable
- ae2:blue_covered_dense_cable
- ae2:brown_covered_dense_cable
- ae2:green_covered_dense_cable
- ae2:red_covered_dense_cable
- ae2:black_covered_dense_cable
- ae2:fluix_covered_dense_cable
- ae2:white_smart_cable
- ae2:orange_smart_cable
- ae2:magenta_smart_cable
- ae2:light_blue_smart_cable
- ae2:yellow_smart_cable
- ae2:lime_smart_cable
- ae2:pink_smart_cable
- ae2:gray_smart_cable
- ae2:light_gray_smart_cable
- ae2:cyan_smart_cable
- ae2:purple_smart_cable
- ae2:blue_smart_cable
- ae2:brown_smart_cable
- ae2:green_smart_cable
- ae2:red_smart_cable
- ae2:black_smart_cable
- ae2:fluix_smart_cable
- ae2:white_smart_dense_cable
- ae2:orange_smart_dense_cable
- ae2:magenta_smart_dense_cable
- ae2:light_blue_smart_dense_cable
- ae2:yellow_smart_dense_cable
- ae2:lime_smart_dense_cable
- ae2:pink_smart_dense_cable
- ae2:gray_smart_dense_cable
- ae2:light_gray_smart_dense_cable
- ae2:cyan_smart_dense_cable
- ae2:purple_smart_dense_cable
- ae2:blue_smart_dense_cable
- ae2:brown_smart_dense_cable
- ae2:green_smart_dense_cable
- ae2:red_smart_dense_cable
- ae2:black_smart_dense_cable
- ae2:fluix_smart_dense_cable
---

# Кабели

<GameScene zoom="3" background="transparent">
  <ImportStructure src="../assets/assemblies/cables.snbt" />
  <IsometricCamera yaw="180" pitch="30" />
</GameScene>

Хотя МЭ-сети также создаются соседними МЭ-совместимыми машинами, кабели являются основным способом
расширения МЭ-сети на большие площади.

Кабели разного цвета можно использовать, чтобы гарантировать, что соседние кабели не соединяются друг с другом,
что позволяет распределять [каналы](../ae2-mechanics/channels.md) более эффективно. Они также влияют на цвет подключённых к ним терминалов,
так что вам не обязательно, чтобы все ваши терминалы были фиолетовыми. Флюикс-кабели соединяются со всеми остальными цветами.

Стоит отметить, **ЧТО КАНАЛЫ НЕ ИМЕЮТ НИЧЕГО ОБЩЕГО С ЦВЕТОМ КАБЕЛЯ**

## Важное Примечание

**Если вы новичок в AE2 и не знакомы с каналами, используйте умный кабель и плотный умный кабель везде, где можете.
Он покажет, как каналы маршрутизируются через вашу сеть, делая их поведение более понятным.**

## Ещё одно Примечание

**Это не трубы для предметов, жидкостей, энергии и т.п.** У них нет внутреннего инвентаря, поставщики шаблонов и машины не «вталкивают»
в них ничего, всё, что они делают, — соединяют [устройства](../ae2-mechanics/devices.md) AE2 в сеть.

## Стеклянный Кабель

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/fluix_glass_cable.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

<ItemLink id="fluix_glass_cable" /> — самый простой в изготовлении кабель, передаёт энергию
и до 8 [каналов](../ae2-mechanics/channels.md). Он доступен в 17 разных цветах; по умолчанию
цвет флюикс, и его можно покрасить в любой цвет с помощью любого из 16 красителей.

Чтобы скрафтить цветные кабели, обложите краситель любого типа 8 кабелями одного и того же
типа (цвет кабелей не важен, но они должны быть одного типа:
стеклянные, умные и т.п.). Вы также можете красить кабели любой совместимой с Forge кистью для краски
в мире.

Вы можете скрафтить любой цветной кабель с ведром воды, чтобы смыть краситель.

Вы можете покрыть кабель шерстью, чтобы создать <ItemLink id="fluix_covered_cable" />, и скрафтить <ItemLink id="fluix_smart_cable" />, чтобы лучше понять, что происходит с
вашими [каналами](../ae2-mechanics/channels.md).

<RecipeFor id="fluix_glass_cable" />

<RecipeFor id="blue_glass_cable" />

## Покрытый Кабель

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_covered_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Покрытый кабель не даёт никаких игровых преимуществ по сравнению со своим аналогом <ItemLink id="fluix_glass_cable" />. Однако его можно использовать
как альтернативный эстетический выбор, если вам нравится покрытый вид.

Его можно красить тем же способом, что и <ItemLink id="fluix_glass_cable" />. Четыре <ItemLink id="fluix_covered_cable" /> можно скрафтить с
редстоуном и светокамнем, чтобы получить <ItemLink id="fluix_covered_dense_cable" />.

<Recipe id="network/cables/covered_fluix" />

<RecipeFor id="blue_covered_cable" />

## Плотный Кабель

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_covered_dense_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Кабель более высокой ёмкости, может нести 32 канала, в отличие от стандартного кабеля, который может нести только 8,
однако он не поддерживает шины, так что вы должны сначала перейти с плотного на
меньший кабель (например, <ItemLink id="fluix_glass_cable" /> или <ItemLink id="fluix_smart_cable" />), прежде чем использовать шины или
панели.

Плотные кабели немного переопределяют поведение «кратчайшего пути» каналов: каналы будут следовать кратчайшим путём до
плотного кабеля, а затем кратчайшим путём через этот плотный кабель до контроллера.

<Recipe id="network/cables/dense_covered_fluix" />

<RecipeFor id="blue_covered_dense_cable" />

## Умный Кабель

<Row>
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_smart_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_smart_dense_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>
</Row>

Несмотря на некоторое внешнее сходство с <ItemLink id="fluix_covered_cable" />, они
выполняют диагностическую функцию, визуализируя использование каналов на кабелях.
Каналы отображаются как светящиеся цветные линии, идущие вдоль чёрной полосы на
кабелях, что даёт вам понимание того, как ваши каналы используются в
вашей сети. Для обычных умных кабелей первые четыре канала отображаются как линии цвета кабеля,
следующие четыре — как белые линии. Для плотного умного кабеля каждая полоса представляет 4 канала.

В сетях с <ItemLink id="controller" /> линии на кабелях показывают точный путь, по которому идут каналы.

Умные кабели в специальных (ad-hoc) сетях вместо этого показывают количество используемых каналов по всей сети, а не количество каналов, проходящих через этот конкретный кабель.

Их также можно красить тем же способом, что и <ItemLink id="fluix_glass_cable" />.

<Recipe id="network/cables/smart_fluix" />

<Recipe id="network/cables/dense_smart_fluix" />

<RecipeFor id="blue_smart_cable" />