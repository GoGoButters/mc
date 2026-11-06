---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: Квантовый мост
  icon: quantum_ring
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:quantum_link
- ae2:quantum_ring
---

# Квантовый сетевой мост

![Сформированный квантовый сетевой мост](../assets/diagrams/quantum_bridge_demonstration.png)

Квантовые сетевые мосты могут расширять [сеть](../ae2-mechanics/me-network-connections.md) на бесконечные расстояния и даже между измерениями.
Они могут переносить 32 канала в сумме (независимо от того, как кабели подключены к каждой грани), по сути
действуя как беспроводной [плотный кабель](cables.md#dense-cable).

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_1.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_2.snbt" />

  <BoxAnnotation color="#33dd33" min="1 1 1" max="6 2 3">
        Воображаемый кабель между двумя конечными точками
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

Обратите внимание: **обе стороны должны быть загружены в чанки**, поэтому <ItemLink id="spatial_anchor" /> или другой чанклоадер обязателен,
если 2 стороны далеко друг от друга.

# Квантовое кольцо

<BlockImage id="quantum_ring" scale="8" />

Восемь таких блоков, размещённых вокруг <ItemLink id="quantum_link" />, создадут
Квантовый сетевой мост. Сетевые подключения принимают только 4 кольца <ItemLink id="quantum_ring" />, прилегающие к
камере связи <ItemLink id="quantum_link" />, а 4 угловых блока не могут подключаться к кабелям.

## Рецепт

<RecipeFor id="quantum_ring" />

# Камера квантовой связи

<BlockImage id="quantum_link" scale="8" />

Один такой блок, окружённый <ItemLink id="quantum_ring" />,
создаст Квантовый сетевой мост. Этот блок не подключается ни к каким кабелям и регистрируется
как часть сети только при полностью собранном мосте.

Инвентарь этого блока может хранить только одну <ItemLink id="quantum_entangled_singularity" /> и доступен
для автоматизации.

## Рецепт

<RecipeFor id="quantum_link" />