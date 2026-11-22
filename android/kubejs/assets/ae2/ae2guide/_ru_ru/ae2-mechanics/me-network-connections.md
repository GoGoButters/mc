---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: Сетевые соединения
  icon: fluix_glass_cable
---

# Сетевые соединения

## Что означает «сеть»?

«Сеть» — это группа [устройств](../ae2-mechanics/devices.md), связанных блоками, которые могут пропускать [каналы](../ae2-mechanics/channels.md),
такими как [кабели](../items-blocks-machines/cables.md) или полноразмерные машины и [устройства](../ae2-mechanics/devices.md).
(<ItemLink id="charger" />, <ItemLink id="interface" />, <ItemLink id="drive" /> и т.д.)
Технически одиночный кабель — это уже сеть, вообще-то.

## Отступление о расположении устройств

Для [устройств](../ae2-mechanics/devices.md), имеющих некоторую специфическую сетевую функцию (например, <ItemLink id="interface" />,
вталкивающий в [сетевое хранилище](../ae2-mechanics/import-export-storage.md) и выталкивающий из него, <ItemLink id="level_emitter" />,
считывающий содержимое сетевого хранилища, <ItemLink id="drive" />, являющийся сетевым хранилищем, и т.д.)
физическое положение устройства не имеет значения.

Опять же, **физическое положение устройства не имеет значения**. Всё, что важно, — это то, что устройство подключено к сети
(и, конечно, к какой именно сети оно подключено).

## Сетевые соединения

Лёгкий способ определить, что подключено в сети, — использовать <ItemLink id="network_tool" />. Он покажет каждый
компонент в сети, поэтому если вы видите то, чего не должно быть, или не видите то, что должно быть, у вас проблема.

Например, это 2 отдельные сети.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
        Сеть 1
  </BoxAnnotation>

<BoxAnnotation color="#5CA7CD" min="2 0 0" max="3 2 2">
        Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это тоже 2 отдельные сети, потому что <ItemLink id="quartz_fiber" /> делится [энергией](../ae2-mechanics/energy.md),
не предоставляя сетевого соединения.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
        Сеть 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="1.3 0 0" max="3 2 2">
        Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Однако это лишь 1 сеть, а не 2 отдельные. [Квантовый мост](../items-blocks-machines/quantum_bridge.md) ведёт себя как
беспроводный [плотный кабель](../items-blocks-machines/cables.md#dense-cable), поэтому оба конца находятся в одной сети.

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="7 3 3">
        Всё 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Это тоже лишь 1 сеть, так как [цвет кабеля](../items-blocks-machines/cables.md) не имеет отношения к сетевым соединениям, кроме того, что кабели разных цветов не
соединяются друг с другом. Все цвета соединяются с кабелями флюикса (или «бесцветными»).

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
        Всё 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Соединения в контексте подсетей

[Подсети](../ae2-mechanics/subnetworks.md) используют сетевые соединения (и конкретно — отсутствие соединения **NOT**)
для ограничения того, к каким другим устройствам какие [устройства](../ae2-mechanics/devices.md) имеют доступ.

Всё, чем на самом деле является подсеть, — это отдельная сеть.

Например, возьмём [Автоматический фортунер руды](../example-setups/ore-fortuner.md). Здесь 3 отдельные сети,
и все они выполняют определённую роль в установке.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 2" max="3 1 3">
        Сеть 1, ведёт себя как трубопроводная подсеть, ограничивает доступ шины импорта, поэтому она «хранит» рудные блоки через
        плоскости формирования.
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="0 0 0" max="3 1 1">
        Сеть 2, ведёт себя как ещё одна трубопроводная подсеть, ограничивает доступ плоскостей аннигиляции, поэтому они хранят
        обработанные фортуной куски руды в бочке, а не в вашей основной сети. Это также значит, что они не тратят каналы
        основной сети.
  </BoxAnnotation>

  <BoxAnnotation color="#82CD5C" min="2 0 1" max="4 1 2">
        Сеть 3, основная сеть со всем вашим хранилищем и крафтом на ней. Здесь просто для подачи энергии, честно говоря, и конкретно
        *не* соединена с двумя подсетями.
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Соединения в контексте P2P

Один из вариантов [P2P-туннеля](../items-blocks-machines/p2p_tunnels.md) перемещает [каналы](channels.md) вместо предметов, жидкостей
или редстоун-сигналов, и это почему-то путает людей. Сеть, к которой подключён туннель, не имеет отношения к
сети, которую туннель переносит. Они *могут* быть одной сетью, но не обязаны, и обычно не являются.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels_network_connection.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 1">
        Сеть 1, переносимая сеть (обычно ваша основная сеть)
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="3.98 1 1">
        Сеть 2, сеть, управляющая ME P2P-туннелями (обычно *не* ваша основная сеть)
  </BoxAnnotation>

  <BoxAnnotation color="#915dcd" min="4.02 0 0" max="6 1 1">
        Сеть 1, переносимая сеть (обычно ваша основная сеть)
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Менее интуитивные соединения

В этом случае это лишь 1 сеть, потому что <ItemLink id="pattern_provider" />, будучи полноразмерным устройством, ведёт себя как
кабель, и <ItemLink id="inscriber" /> ведёт себя аналогично. Таким образом, сетевое соединение проходит через
поставщика и высекатель.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
        Всё 1 сеть
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Чтобы предотвратить это (полезно для многих установок автокрафта с [подсетями](../ae2-mechanics/subnetworks.md)),
вы можете нажать по поставщику правой кнопкой мыши с <ItemLink id="certus_quartz_wrench" />, чтобы сделать его направленным; в этом случае он
не будет пропускать каналы через одну сторону.

<Row gap="40">
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 2">
        Сеть 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="4 2 2">
        Сеть 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_directional_connection.snbt" />

  <BoxAnnotation color="#ee3333" min="1 .3 .3" max="1.3 .7 .7">
        Обратите внимание, как кабель не соединяется
  </BoxAnnotation>

  <IsometricCamera yaw="255" pitch="30" />
</GameScene>
</Row>

Другие части, которые не предоставляют направленных сетевых соединений, — это большинство [сабпартовых](../ae2-mechanics/cable-subparts.md)
[устройств](../ae2-mechanics/devices.md), таких как <ItemLink id="import_bus" />ы, <ItemLink id="storage_bus" />ы и
<ItemLink id="cable_interface" />ы.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/subpart_no_connection.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>