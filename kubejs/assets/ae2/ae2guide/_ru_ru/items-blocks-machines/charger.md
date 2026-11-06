---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Зарядник
  icon: charger
  position: 310
categories:
- machines
item_ids:
- ae2:charger
---

# Зарядник

<BlockImage id="charger" scale="8" />

Зарядник позволяет заряжать поддерживаемые инструменты, а также <ItemLink id="certus_quartz_crystal" />.

Питание может подаваться сверху или снизу — либо через кабели AE2 ([cables](cables.md)), либо через кабели питания других модов. Он
принимает как энергию AE2 (AE), так и энергию Forge (FE). Предметы можно вставлять и извлекать с любой стороны. Извлекаться могут
только результаты работы, поэтому фильтры не нужны — неповреждённые кристаллы не будут извлечены вместо заряженных. Зарядник можно повернуть с помощью
<ItemLink id="certus_quartz_wrench" /> для облегчения автоматизации.

Может использоваться для создания <ItemLink id="charged_certus_quartz_crystal" />
из <ItemLink id="certus_quartz_crystal" />, а также <ItemLink id="meteorite_compass" /> из <ItemLink id="minecraft:compass" />.

Чтобы зарядить его вручную, разместите <ItemLink id="crank" /> сверху или снизу и щёлкайте по нему правой кнопкой мыши, пока предмет не зарядится.

Он также служит рабочим местом для [исследователя флюикса](fluix_researcher.md).

## Простая автоматизация

Например, возможность вращать зарядник позволяет полуавтоматизировать его следующим образом:

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/charger_hopper.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Рецепт

<RecipeFor id="charger" />