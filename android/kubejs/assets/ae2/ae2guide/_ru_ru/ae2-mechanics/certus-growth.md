---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: Рост истинного кварца
  icon: quartz_cluster
---

# Рост истинного кварца

## По сути, просто скопировано-вставлено со страницы начала пути

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/budding_certus_1.snbt" />
</GameScene>

Почки истинного кварца вырастут из [почек истинного кварца (budding certus blocks)](../items-blocks-machines/budding_certus.md), подобно аметисту. Если вы сломаете почку, которая не закончила
расти, она выпадет одной <ItemLink id="certus_quartz_dust" />, без влияния «удачи». Если вы сломаете полностью выросшую гроздь, она выпадет четырьмя
<ItemLink id="certus_quartz_crystal" />, и «удача» увеличит это число.

Существует 4 тира цветущих блоков истинного кварца: Безупречный, С изъяном, Сколотый и Повреждённый, и изначально вы
найдёте их в [метеоритах](../ae2-mechanics/meteorites.md).

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_blocks.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Каждый раз, когда почка вырастает на ещё одну стадию, цветущий блок имеет шанс деградировать на один тир, в конечном счёте превращаясь
в обычный блок истинного кварца. Их можно починить (и создать новые цветущие блоки), бросив цветущий блок (или
блок истинного кварца) в воду с одним или несколькими <ItemLink id="charged_certus_quartz_crystal" />.

<RecipeFor id="damaged_budding_quartz" />

Безупречные цветущие блоки истинного кварца не деградируют и будут бесконечно генерировать истинный кварц. Однако их нельзя создать или переместить
киркой, даже с «шёлковым касанием». (хотя их *можно* перемещать с помощью [пространственного хранения](../ae2-mechanics/spatial-io.md))

Сами по себе почки истинного кварца растут очень медленно. К счастью, <ItemLink id="growth_accelerator" /> значительно
ускоряет этот процесс, если размещён рядом с цветущим блоком. Вам стоит построить несколько таких в первую очередь.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_certus_2.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Сложные взаимодействия означают, что каждая закрытая сторона цветущего блока замедляет совокупную скорость роста цветущего блока,
что в конечном счёте пересиливает эффект от большего числа ускорителей. Эмпирические испытания показывают следующее:

![Предметов/мин для соотношений](../assets/diagrams/certus_farm_speed_chart_1.png)

![распространённые установки](../assets/diagrams/certus_farm_speed_chart_2.png)

Если у вас недостаточно кварца, чтобы также сделать <ItemLink id="energy_acceptor" /> или <ItemLink id="vibration_chamber" />,
вы можете сделать <ItemLink id="crank" /> и прикрепить его к концу вашего ускорителя.

Сбор истинного кварца автоматически [описан здесь](../example-setups/simple-certus-farm.md).