# Факты раздела «Правитель» (`ruler/`, книга `zhivoymir:start`)

Собрано 2026-11-10 сессией A. См. также общий `docs/guide-facts.md`. Каждый факт указан с источником.

## Каркас раздела (навигация)
- `ruler/index.md` — parent `index.md`, position 20. Подстраницы — parent `ruler/index.md`, position 1–13 (порядок по оглавлению: mca, village, colony, colony-build, colony-and-village, neighbors, recruits, foreign-villages, economy, treasury, war, peace, voice).
- Модель навигации GuideMe (проверено по AE2-руководству `kubejs/assets/ae2/ae2guide/_ru_ru`): подпапки поддерживаются, страница в подпапке ссылается на `parent: <папка>/<index>.md` (путь от корня книги), раздел-индекс использует `<SubPages />`.
- `commands.md` переносится сессией C в `ref/` — здесь не трогал.

## Деревня и жители (MCA Reborn 7.7.36)
- Меню жителя: «Говорить», «Действия» (обнять, подарить), «Торговать», «Деревня» — `assets/mca/lang/ru_ru.json`.
- Ранги: Преступник → Крестьянин → Торговец → Дворянин → Мэр → Монарх; репутация деревни = сумма сердец жителей (zmbridge Villages).
- Источник: существующая `village.md` + факты из `docs/guide-facts.md`.

## Люди и семьи (mca.md, новая)
- Кольца (рецепты из jar `data/mca/recipe/`):
  - `mca:engagement_ring` — золото + алмаз (pattern GDG/G G/GGG). Модель ✓.
  - `mca:wedding_ring` — золотые слитки. Модель ✓. Tooltip «позволяет жениться при достаточном количестве сердец».
  - `mca:matchmakers_ring` — железные слитки. Модель ✓.
- `mca:family_tree` — «Семейное Древо», родословная всех жителей. Модель ✓. (`item.mca.family_tree`).
- Дети/браки, MCA Conversations (mcaconversations-1.8.0) — глубокие диалоги, открываются по уровню сердца.
- ПРИМЕЧАНИЕ: «обручального алтаря» в MCA не найдено (нет altar-блока/слова в lang) — из текста убрал.

## Колония (colony.md, существующая)
- Лагерь `minecolonies:supplycampdeployer` — 16×17 площадка, один на мир; в чертеже ратуша + `structurize:sceptergold` + припасы. (факты из guide-facts.md).
- Предупреждение ближе 20 блоков к деревне (zmbridge).
- Постройки ставятся инструментом строителя.

## Строительство и украшения (colony-build.md, новая)
- `structurize:sceptergold` — «Build Tool» постройка. `structurize:sceptersteel` — «Scan Tool» сканирование в `SCAN_%s`. Модели ✓.
- Domum Ornamentum (`domum_ornamentum-1.0.237-snapshot-main`): декоративные блоки (bricks, frames, doors, floating carpet…), `block.domum_ornamentum.*`. Общее описание, без битых ItemLink одиночных блоков.
- Colony Lifts (`colonylifts-1.1.2`): связывает электрические подъёмники Create с путями жителей. Create-инструмент elevator не подтверждён в depcache — описал концептуально без конкретного ID.

## Деревня и колония вместе (colony-and-village.md, существующая)
- Рецепт `zmbridge:invite` (бумага+изумруд+перо). Условия переселения детально в guide-facts.md (репутация ≥300, сердца ≥40, ≥6 жителей / 60% кроватей, перерыв 2 игровых дня).
- Защита деревни: колонист убил чудовище в деревне/32 блоках → +4 репутации, до 40/сутки.

## Соседи (neighbors.md, существующая)
- Оглавление-ссылка на страницы раздела. Ссылку на trade.md заменил на economy.md (слияние).

## Рекруты (recruits.md, существующая)
- Наём 8/12/16/20/38/40 изумрудов; жалованье 1 изумруд / 20 мин; до 100 рекрутов. Клавиши R/U/M. (guide-facts.md).

## Чужие деревни и короли (foreign-villages.md, существующая)
- Капиталы/короли — mcacapitals-1.3.5; репутация по деревням отдельно; «Книга домов».

## Экономика и торговля (economy.md — слияние economy+trade)
- Баланс изумрудов: `docs/economy-audit.md`.
- Торговля с жителями, доски заданий (bountiful, 1–4 изумруда, обновление 300 с), рынок/торговая станция Trade Post (торговые монеты, пути воздух×10), монеты Numismatics (стартовых нет). Всё из старых экономика.md + trade.md.

## Казна и налоги (treasury.md, существующая)
- Налог за час с постройки, потолок 1000, долг 200, политики NORMAL/LOW/HIGH/WAR, вложения 150/50/200/150. Команды `/wnt`. Клавиша `key.minecolonytax.open_tax_gui`. (guide-facts.md, economy-audit.md)

## Война (war.md, существующая)
- Набег/война/осада/вымогательство/вассалы/оккупация/шпионаж/воры/арена — команды `/wnt …`, `/pvp`. (guide-facts.md)
- Тайник Colonist Thieves, метка, вор ночью.

## Мир и союзы (peace.md, существующая)
- Права в колонии, команды игроков, фракции рекрутов (10 изумр., 5 игроков), земли (64+15/чанк, до 50), фракции колоний WNT (500, союзы ≤3). Клавиши R-team/map.

## Голос жителей (voice.md, новая)
- Simple Voice Chat 2.6.27: клавиши `key.push_to_talk`, `key.voice_chat`, `key.whisper`, `key.disable_voice_chat` (проверены в `assets/voicechat/lang/ru_ru.json`).
- Living Voice (`/root/work/livingvoice`, LivingVoice.java, конфиг COMMON): `range`=12 (взгляд), `callRange`=16 (имя/профессия), `voiceDistance`=48 (голоса колонистов через voice chat), colonists voices on. Имена/профессии распознаются по языку клиента (Targets.java, ColonistBridge).
- Жители MCA + Living Voice; колонисты Living Colonists (livingcolonists-0.7.0) + Living Voice; бог zmgod ловит речь как молитву (Prayers.onVoice).