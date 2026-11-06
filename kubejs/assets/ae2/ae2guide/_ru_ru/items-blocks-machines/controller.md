---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Контроллер
  icon: controller
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:controller
---

# Контроллер

<BlockImage id="controller" p:state="online" scale="8" />

Контроллер — маршрутизирующий узел [МЭ-сети](../ae2-mechanics/me-network-connections.md).
Без него сеть работает «от случая к случаю» (ad-hoc) и может иметь не более 8 [устройств](../ae2-mechanics/devices.md), использующих каналы.

Невозможно иметь 2 контроллера в одной [МЭ-сети](../ae2-mechanics/me-network-connections.md).

Контроллер предоставляет 32 [канала](../ae2-mechanics/channels.md) на каждую грань.

Контроллер требует 6 AE/тик на каждый блок контроллера для
работы. Каждый блок контроллера может хранить 8000 AE, поэтому для более крупных сетей может потребоваться дополнительное
хранилище энергии. Подробности см. в [энергии](../ae2-mechanics/energy.md).

Мультиблочные контроллеры можно строить в достаточно свободной форме.

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controllers.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако есть несколько правил, которым необходимо следовать:

1.  Все блоки контроллера в [МЭ-сети](../ae2-mechanics/me-network-connections.md) должны быть соединены; иначе блоки станут красными.
2.  Размер контроллера должен быть в пределах 7x7x7; иначе он станет красным.
3.  Контроллер может иметь 2 соседних блока максимум по 1 оси; если блок нарушает это правило, он отключается и становится красным.

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controller_rules.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Пока соблюдены все правила и подано питание, контроллер должен светиться и
перебирать цвета.

Щёлкнув по контроллеру правой кнопкой мыши, вы получите тот же интерфейс, что и у <ItemLink id="network_tool" />

## Рецепт

<RecipeFor id="controller" />