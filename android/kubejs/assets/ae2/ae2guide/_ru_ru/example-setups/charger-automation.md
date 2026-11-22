---
navigation:
  parent: example-setups/example-setups-index.md
  title: Автоматизация Зарядника
  icon: charger
---

# Автоматизация Зарядника

Учтите, что поскольку здесь используется <ItemLink id="pattern_provider" />, эта схема предназначена для интеграции в вашу [автокрафт-установку](../ae2-mechanics/autocrafting.md).
Если вы хотите автоматизировать <ItemLink id="charger" /> отдельно, используйте воронки, сундуки и тому подобное.

Автоматизация <ItemLink id="charger" /> довольно проста. <ItemLink id="pattern_provider" /> вталкивает ингредиент в зарядник, затем [трубная подсеть](pipe-subnet.md)
или другая труба для предметов вталкивает результат обратно в поставщика.

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/charger_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) Поставщик шаблонов: В конфигурации по умолчанию, с соответствующими шаблонами обработки. Также обеспечивает зарядник энергией.

        ![Шаблон Зарядника](../assets/diagrams/charger_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="1 1.3 1">
        (2) Шина импорта: В конфигурации по умолчанию.
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (3) Шина хранения: В конфигурации по умолчанию.
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        К основной сети
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## Конфигурации

* <ItemLink id="pattern_provider" /> (1) находится в конфигурации по умолчанию, с соответствующими <ItemLink id="processing_pattern" />.
  Он также обеспечивает <ItemLink id="charger" /> [энергией](../ae2-mechanics/energy.md), поскольку действует как [кабель](../items-blocks-machines/cables.md).
  
    ![Шаблон Зарядника](../assets/diagrams/charger_pattern.png)

* <ItemLink id="import_bus" /> (2) находится в конфигурации по умолчанию.
* <ItemLink id="storage_bus" /> (3) находится в конфигурации по умолчанию.

## Как это работает

1. <ItemLink id="pattern_provider" /> вталкивает ингредиенты в <ItemLink id="charger" />.
2. Зарядник делает своё зарядное дело.
3. <ItemLink id="import_bus" /> в зелёной подсети забирает результат из зарядника и пытается сохранить его в
   [хранилище сети](../ae2-mechanics/import-export-storage.md).
4. Единственное хранилище в зелёной подсети — <ItemLink id="storage_bus" />, который сохраняет полученные предметы в поставщике шаблонов, возвращая их в основную сеть.