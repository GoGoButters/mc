---
navigation:
  title: Мосты: Mekanism×AE2 и колония
  parent: eng/index.md
  position: 5
---

# Мосты: Mekanism×AE2 и колония

Странник, каждая механика сильна сама по себе, но **работают они вместе**. Этот раздел — про мосты между мирами: газы Mekanism, перенесённые в МЭ-сеть, склады AE2, кормящие стройку колонии, и переработка, замыкающая круг.

## Связка целиком

**Create** даёт кинетику и энергию, **Mekanism** — машины и газы, **AE2** — склад и автокрафт. **Applied Mekanistics** переносит газы в ME-сеть, **ColonyLink** связывает стройку колонии с ME-автокрафтом, а **Create: Recycled** возвращает компоненты Create в ресурсы — всё связано со всем.

## Applied Mekanistics

Мост между химией и цифрой: **химия Mekanism — газы, шламы и прочие химикаты — живёт в ME-сети** рядом с обычными предметами.

- <ItemLink id="appmek:chemical_cell_housing" /> «Корпус химической ячейки ME» — основа любого химического хранилища.
- <ItemLink id="appmek:chemical_storage_cell_1k" /> «Химическая ячейка памяти ME 1К» — есть и 4К/16К/64К/256К.
- <ItemLink id="appmek:portable_chemical_cell_1k" /> «Переносная химическая ячейка 1К» — химия с собой.
- <ItemLink id="appmek:chemical_p2p_tunnel" /> «Химический P2P тоннель» — передача химии между точками сети.

## ColonyLink

Мост между **ME-автокрафтом и колонией**: стройка заказывается через сеть, а не руками.

- <ItemLink id="colonylink:colony_link_wand" /> «Буфер обмена ColonyLink» — главный инструмент моста:

  <RecipeFor id="colonylink:colony_link_wand" />

  Рецепт: эндер-жемчуг, флюисовый кристалл AE2, буфер обмена MineColonies и логический процессор.

- <ItemLink id="colonylink:colonylink_package" /> «Посылка ColonyLink» — упаковка материалов для доставки.
- <ItemLink id="colonylink:warehouse_link_terminal" /> «Терминал связи склада» — связь склада колонии с сетью.
- <ItemLink id="colonylink:colony_link_redirector" /> «Перенаправитель ColonyLink» — маршруты посылок.

## Create: Recycled

Мост переработки: **только рецепты, без новых предметов**. Дробильное колесо Create возвращает компоненты Create — шлюзы, трубы, механизмы — обратно в ресурсы. Круг замкнулся.

## С чего начать

Сначала поставь первые машины <ItemLink id="mekanism:metallurgic_infuser" /> и <ItemLink id="mekanism:enrichment_chamber" />, затем собери МЭ-сеть с <ItemLink id="ae2:drive" />. Когда обе стороны на ногах — добавь Applied Mekanistics для газов, а ColonyLink подключи, когда колония потребует стройматериалов оптом.

## Где смотреть подробнее

- [Mekanism: машины и химия →](mekanism.md)
- [AE2: ME-сеть и автокрафт →](ae2.md)
- Встроенное руководство AE2 (на русском), клавиша <KeyBind id="key.ae2.guide" />.

> Мосты строят не для того, чтобы по ним ходили, а чтобы два берега стали одним делом.
