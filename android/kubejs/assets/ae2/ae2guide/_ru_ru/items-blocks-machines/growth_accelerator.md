---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Ускоритель роста
  icon: growth_accelerator
  position: 310
categories:
- machines
item_ids:
- ae2:growth_accelerator
---

# Ускоритель роста

<BlockImage id="growth_accelerator" p:powered="true" scale="8"/>

Ускоритель роста значительно ускоряет [рост](../ae2-mechanics/certus-growth.md) истинного кварца или аметиста, будучи размещённым рядом с почкующимся блоком.

Любопытно, что он *также* может ускорять рост различных растений.

Он делает это, применяя «случайные тики» к соседним блокам в дополнение к случайным тикам, которые происходят естественно.
В теории это означает, что 1 ускоритель должен заставлять вещи расти примерно в 90 раз быстрее нормы, и эффект складывается.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/growth_accelerator.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Питание может подаваться сверху или снизу — либо через кабели AE2 ([cables](cables.md)), либо через кабели питания других модов. Он может
принимать как энергию AE2 (AE), так и энергию Forge (FE).

Чтобы питать его вручную, разместите <ItemLink id="crank" /> сверху или снизу и щёлкайте по нему правой кнопкой мыши.

Верх и низ можно опознать по розовым флюиксовым завиткам на них.

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/accelerator_connections.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Рецепт

<RecipeFor id="growth_accelerator" />