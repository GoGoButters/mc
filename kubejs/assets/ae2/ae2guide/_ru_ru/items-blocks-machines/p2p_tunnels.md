---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: P2P-туннели
  icon: me_p2p_tunnel
  position: 210
categories:
- devices
item_ids:
- ae2:me_p2p_tunnel
- ae2:redstone_p2p_tunnel
- ae2:item_p2p_tunnel
- ae2:fluid_p2p_tunnel
- ae2:fe_p2p_tunnel
- ae2:light_p2p_tunnel
---

# Туннели «точка-точка»

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

P2P-туннели — это способ перемещать такие вещи, как предметы, жидкости, редстоуновые сигналы, энергию, свет и [каналы](../ae2-mechanics/channels.md),
по сети без их прямого взаимодействия с сетью. Существует множество вариантов P2P-туннелей, но каждый
транспортирует только свой конкретный тип вещей. По сути, они действуют как порталы, напрямую соединяющие
две грани блоков на расстоянии. Они не двунаправленные, у них есть заданные входы и выходы.

![Портал](../assets/assemblies/p2p_portal.png)

Например, воронка, обращённая к предметному P2P, будет действовать так, будто она напрямую подключена к бочке, и предметы будут течь.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_hopper_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако две рядом стоящие бочки не будут передавать предметы между собой.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_barrel_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Существуют и другие варианты, например Redstone P2P.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_redstone.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

И ME P2P, который перемещает каналы.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Типы P2P-туннелей и настройка

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="180" pitch="90" />
</GameScene>

Существует много типов P2P-туннелей. Напрямую можно скрафтить только ME P2P-туннель, остальные создаются щелчком правой кнопкой мыши по другим
P2P-туннелям с определёнными предметами:
- ME P2P-туннели выбираются щелчком ПКМ с любым [кабелем](../items-blocks-machines/cables.md).
- Redstone P2P-туннели выбираются щелчком ПКМ с различными редстоуновыми компонентами.
- Предметные P2P-туннели выбираются щелчком ПКМ с сундуком или воронкой.
- Жидкостные P2P-туннели выбираются щелчком ПКМ с ведром или бутылкой.
- Энергетические P2P-туннели выбираются щелчком ПКМ почти с любым предметом, содержащим энергию.
- Световые P2P-туннели выбираются щелчком ПКМ с факелом или светокамнем

У некоторых типов туннелей есть особенности. Например, каналы ME P2P-туннелей не могут проходить через другие ME P2P-туннели, а
энергетические P2P-туннели неявно взимают 2,5% налога на протекающий через них FE, увеличивая своё
потребление [энергии](../ae2-mechanics/energy.md).

## Наиболее распространённая форма P2P

Наиболее частый вариант использования P2P-туннелей — применение ME P2P-туннеля для уплотнения плотности передачи [каналов](../ae2-mechanics/channels.md).
Вместо связки плотных кабелей один плотный кабель может переносить вокруг множество каналов.

В этом примере 8 ME P2P-входов берут 256 каналов (8*32) с <ItemLink id="controller" /> основной сети, а 8 ME P2P-выходов
выводят их в другое место. Обратите внимание, как каждый вход или выход P2P-туннеля занимает 1 канал. Так мы можем провести множество каналов
через тонкий кабель. А поскольку наши P2P-туннели находятся в отдельной [подсети](../ae2-mechanics/subnetworks.md), мы даже
не расходуем каналы основной сети для этого! Также обратите внимание, что хотя P2P-туннели можно размещать прямо
на контроллере, между ними можно поместить [плотный умный кабель](../items-blocks-machines/cables.md#smart-cable), чтобы легче визуализировать каналы.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/p2p_compact_channels.snbt" />

  <BoxAnnotation color="#dddddd" min="1.3 1.3 6.3" max="2 2.7 6.7">
        Кварцевое волокно делит энергию между основной сетью и P2P-подсетью.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4.1 0 5.7" max="5 2.3 6.4">
        Можно либо поставить вход туннеля прямо на контроллер, либо подвести кабель к нему.
  </BoxAnnotation>

  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

Ещё один пример (включая использование с [Квантовыми мостами](quantum_bridge.md)) см. на этой диаграмме из MS Paint, которую мне лень
доводить:

![P2P и квантовые мосты](../assets/diagrams/p2p_quantum_network.png)

## Вложение

Однако нельзя использовать это для отправки бесконечных каналов через один кабель. Канал для ME P2P-туннеля не
пройдёт через другой ME P2P-туннель, поэтому их нельзя рекурсивно вкладывать. Обратите внимание, как внешний слой ME P2P-туннелей
на красных кабелях отключён. Заметьте, что это относится только к ME P2P-туннелям, другие типы P2P-туннелей могут проходить через ME P2P-туннель,
как видно по Redstone P2P-туннелям, работающим нормально.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_nesting.snbt" />
  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

## Связывание

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_linking_frequency.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Концы соединения P2P-туннеля можно связать с помощью <ItemLink id="memory_card" />. Частота будет отображаться
как массив цветов 2x2 на задней стороне туннеля.
- Shift + ПКМ, чтобы сгенерировать новую частоту связи P2P.
- ПКМ, чтобы вставить настройки, карты улучшений или частоту связи.

Туннель, по которому вы щёлкните с зажатым Shift, станет входом, а туннель, по которому вы щёлкните ПКМ, — выходом. Выходов может быть несколько,
но с ME P2P-туннелями каналы, входящие во вход, будут разделены между выходами, поэтому дублировать каналы нельзя.

## Рецепт

<RecipeFor id="me_p2p_tunnel" />