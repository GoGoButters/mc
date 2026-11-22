---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Молекулярный сборщик
  icon: molecular_assembler
  position: 310
categories:
- machines
item_ids:
- ae2:molecular_assembler
---

# Молекулярный сборщик

<BlockImage id="molecular_assembler" scale="8" />

Молекулярный сборщик принимает вводимые в него предметы и выполняет операцию, заданную соседним <ItemLink id="pattern_provider" />,
или вставленным <ItemLink id="crafting_pattern" />, <ItemLink id="smithing_table_pattern" /> или <ItemLink id="stonecutting_pattern" />,
после чего выталкивает результат в соседние инвентари.

У этого сборщика есть шаблон крафта, задающий рецепт 1 дубовое бревно = 4 дубовые доски. Когда дубовые брёвна подаются в верхнюю воронку,
сборщик крафтит и выплёвывает дубовые доски в нижнюю воронку.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/standalone_assembler.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Основное применение молекулярного сборщика

Однако их основное применение — рядом с <ItemLink id="pattern_provider" />. В этом случае поставщики шаблонов ведут себя особым образом
и отправляют информацию о соответствующем шаблоне вместе с ингредиентами соседним сборщикам. Поскольку сборщики автоматически выбрасывают результаты
крафта в соседние инвентари (а значит, и в возвратные слоты поставщика шаблонов), сборщика на поставщике шаблонов
достаточно для автоматизации крафтовых шаблонов.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/assembler_tower.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Улучшения

Молекулярный сборщик поддерживает следующие [улучшения](upgrade_cards.md):

*   <ItemLink id="speed_card" />

## Рецепт

<RecipeFor id="molecular_assembler" />

## Примечание

Optifine ломает функцию «выталкивать в соседние инвентари», поэтому большинство конструкций крафта со сборщиками не будут работать.