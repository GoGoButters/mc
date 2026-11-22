---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: Каналы
  icon: controller
---

# Каналы

[МЭ-сетям](me-network-connections.md) Applied Energistics 2 требуются
каналы для поддержки [устройств](../ae2-mechanics/devices.md), использующих сетевое хранилище, или других сетевых
сервисов. Думайте о каналах как о USB-кабелях ко всем вашим устройствам. У компьютера лишь столько USB-портов, и он может поддерживать лишь
столько подключённых устройств. Большинство машин, полноразмерных устройств и обычных кабелей могут пропускать через себя максимум
8 каналов. Можно думать о полноразмерных устройствах и обычных кабелях как о пучке из 8 «проводов-каналов». Однако [плотные кабели](../items-blocks-machines/cables.md#dense-cable) могут поддерживать до
32 каналов. Единственные другие устройства, способные передавать 32, — это <ItemLink id="me_p2p_tunnel" />
и [Квантовый мост](../items-blocks-machines/quantum_bridge.md). Каждый раз, когда устройство занимает канал, представьте, что вы выдёргиваете usb-«провод» из
пучка, что, очевидно, означает, что этого «провода» больше нет дальше по линии.

<GameScene zoom="7" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_demonstration_1.snbt" />

  <LineAnnotation color="#33ff33" from="1 .4 .7" to="2.4 .4 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .7" to="2.4 .6 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .6" to="2.6 .4 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.4 .6 .7" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .4 .7" to="2.4 .4 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .6 .6" to="2.6 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 .6" to="2.6 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.1 .6 1.5" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 1.5" to="2.9 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.6 .6 1.5" to="2.6 .9 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .1 1.5" to="2.4 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .4" to="3.5 .6 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="3.5 .6 .4" to="3.5 .9 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="3.5 .1 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .3" to="1.5 .6 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1.5 .6 .3" to="1.5 .9 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1.5 .1 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#ff3333" from="3.5 .5 .5" to="5.5 .5 .5" alwaysOnTop={true}>
  Все 8 каналов в кабеле уже использованы, поэтому накопитель не получает свой.
  </LineAnnotation>

  <LineAnnotation color="#993333" from="1 .5 .5" to="1.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="1.5 .5 .5" to="1.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2 .5 .5" to="2.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2.5 .5 .5" to="2.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="3 .5 .5" to="3.25 .5 .5" alwaysOnTop={true}/>

  <DiamondAnnotation pos="3.6 0.5 0.5" color="#ff0000">
        Все 8 каналов в кабеле уже использованы, поэтому накопитель не получает свой.
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

Лёгкий способ увидеть, как каналы используются и маршрутизируются по вашей сети, — это использовать [умные кабели](../items-blocks-machines/cables.md), которые будут отображать на себе пути и использование каналов.

Каналы потребляют 1⁄128 ae/т за каждый узел, который они проходят, это означает, что,
добавив <ItemLink id="controller" /> для
сети из 8 устройств и более 96 узлов, вы можете на самом деле
снизить потребление энергии, потому что это меняет способ распределения каналов.

Стоит отметить: **КАНАЛЫ НЕ ИМЕЮТ НИКАКОГО ОТНОШЕНИЯ К ЦВЕТУ КАБЕЛЯ**, весь цвет кабеля нужен лишь для того, чтобы кабели не соединялись.

## Маршрутизация каналов

При использовании <ItemLink id="controller" />а
каналы маршрутизируются в 3 шага. Сначала они идут по кратчайшему пути через соседние машины к ближайшему [обычному кабелю](../items-blocks-machines/cables.md)
(стеклянному, покрытому или умному). Затем они идут по кратчайшему пути через этот обычный кабель к ближайшему [плотному кабелю](../items-blocks-machines/cables.md)
(плотному или плотному умному). Затем они идут по кратчайшему пути через этот плотный кабель к <ItemLink id="controller" />у.
Если кратчайший путь уже исчерпан, некоторые [устройства](devices.md) могут не получить свои каналы; используйте
цветные кабели, кабельные анкеры и туннели в своих интересах, чтобы каналы шли по желаемому вами пути.

Например, в этом случае некоторые накопители не получают каналы, потому что, хотя в кабелях достаточно ёмкости, каналы
пытаются идти по кратчайшему пути, перегружая одни кабели и оставляя другие пустыми.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 3.6" to="1.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.4 0.5 3.6" to="1.4 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 1.6" to="0.6 .5 1.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 1.6" to="0.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 3.4" to="1.4 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 3.4" to="1.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <BoxAnnotation color="#dddddd" min="1.2 0.2 3.2" max="1.8 0.8 3.8" alwaysOnTop={true} thickness="0.05">
        Через это место пытаются пройти более 8 каналов, поэтому некоторые отрезаются.
  </BoxAnnotation>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

Это можно исправить, более тщательно ограничив пути, по которым каналы могут идти. Сети должны быть древовидными (или кустообразными).
Петли и неоднозначные пути каналов следует минимизировать.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue_fix.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 5.6" to="1 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

## Специальные сети (Ad-Hoc)

Сеть без <ItemLink id="controller" />а
считается специальной (Ad-Hoc) и может поддерживать до 8 устройств, использующих каналы.
Как только вы превысите 8 устройств, устройства сети, использующие каналы, отключатся:
вы можете либо убрать устройства, либо добавить <ItemLink id="controller" />.

В отличие от сетей с контроллером, [умные кабели](../items-blocks-machines/cables.md) в специальных сетях будут показывать количество
каналов, используемых по всей сети, вместо количества каналов, протекающих через этот конкретный кабель.

Во время использования специальных сетей каждое устройство будет
использовать 1 канал сети целиком; это сильно отличается от того, как <ItemLink id="controller" /> выделяет каналы на основе
кратчайшего маршрута.

## Конструкция

Как упоминалось ранее в [маршрутизации каналов](channels.md#channel-routing), лучше всего проектировать сеть в виде дерева: плотные кабели расходятся от контроллера, обычные кабели
ответвляются от плотных, а [устройства](../ae2-mechanics/devices.md) располагаются кластерами по 8 или меньше на обычных кабелях.

Вот пример того, как делать не надо:

Следуя по путям каналов,

1. Сразу выйдя из контроллера вправо, мы упираемся в ограничение в 8 каналов, потому что накопитель ведёт себя как обычный кабель.
Однако, поскольку здесь мы не используем умный кабель, мы не видим, сколько каналов используется. Осталось 8 каналов.
2. Накопитель занимает канал.
Осталось 7 каналов.
3. 2 канала уходят вверх к терминалам.
Осталось 5 каналов.
4. Продолжая вправо, интерфейс занимает ещё один канал.
Осталось 4 канала.
5. 1 канал уходит вверх к поставщику шаблонов.
Осталось 3 канала.
6. Продолжая вправо, 1 канал уходит вверх к шине импорта.
Осталось 2 канала.
7. Кластер поставщиков шаблонов, питающих сборщики, получает только 2 канала, поэтому 2 из поставщиков не получают каналы.

В конечном счёте ошибка в создании узкого места для каналов и в том, что не продумано, как каналы будут распределяться.

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/bad_network_structure.snbt" />

<LineAnnotation color="#33ff33" from="6.5 .5 1.5" to="6 .5 1.5" alwaysOnTop={true} thickness="0.4">
  32 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="6 .5 1.5" to="5.5 .5 1.5" alwaysOnTop={true} thickness="0.2">
  8 каналов
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 1.5 1.5" alwaysOnTop={true} thickness="0.1">
  2 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 1.5 1.5" to="5.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 2.5 1.5" to="5.5 2.5 1.1" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="4.5 .5 1.5" alwaysOnTop={true} thickness="0.158">
  5 каналов
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 1.5 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="3.5 .5 1.5" alwaysOnTop={true} thickness="0.122">
  3 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="3.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 2.5 1.5" to="3.7 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="1.5 .5 1.5" alwaysOnTop={true} thickness="0.1">
  2 канала
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="1.5 0.3 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="0.5 0.5 1.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#33ff33" from="0.5 0.5 1.5" to="0.5 0.5 0.5" alwaysOnTop={true} thickness="0.071">
  1 канал
</LineAnnotation>

<LineAnnotation color="#ff3333" from="0.5 1.5 1.5" to="0.5 1.3 1.5" alwaysOnTop={true} thickness="0.071">
  нет каналов
</LineAnnotation>

<LineAnnotation color="#ff3333" from="1.5 1.5 0.5" to="1.5 1.3 0.5" alwaysOnTop={true} thickness="0.071">
  нет каналов
</LineAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

---

Вот пример хорошей структуры:

<GameScene zoom="2.5" interactive={true}>
  <ImportStructure src="../assets/assemblies/treelike_network_structure.snbt" />

    <BoxAnnotation color="#dddddd" min="6.9 0 4.9" max="9.1 4 7.1" thickness="0.05">
        Обратите внимание, что поставщики шаблонов сгруппированы в отдельные группы по 8.
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 4 4" max="8 5 5" thickness="0.05">
        Два обычных кабеля полных каналов, сходящихся вместе, означают, что нужен плотный кабель.
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 0 13" max="8 1 14" thickness="0.05">
        Разные цвета кабеля используются, чтобы соседние кабели не соединялись.
    </BoxAnnotation>


  <IsometricCamera yaw="315" pitch="30" />
</GameScene>

## Режимы каналов

AE2 10.0.0 для Minecraft 1.18 вводит новые опции для изменения того, как каналы AE2 ведут себя в вашем мире.
Есть новая опция конфигурации в общем разделе (`channels`), которая управляет этой опцией, а также новая внутриигровая
команда для операторов, чтобы менять режим и конфигурацию прямо из игры. Команда — `/ae2 channelmode <mode>`
для изменения и `/ae2 channelmode` для показа текущего режима. Когда режим меняется в игре, все существующие сетки
перезагружаются и немедленно используют новый режим.

Это возрождает и улучшает опцию, которая была доступна в Minecraft 1.12, и вводит лучшие опции для
игроков, которые просто хотят немного более спокойного геймплея, но не хотят, чтобы механика была полностью удалена.

В следующей таблице перечислены доступные режимы как в файле конфигурации, так и в команде.

| Настройка   | Описание                                                                                                                                                                                                                                 |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `default`  | Стандартный режим с ёмкостями каналов кабелей и специальных сетей, как описано на этом сайте                                                                                                                                             |
| `x2`       | Все ёмкости каналов удвоены (16 на обычном кабеле, 64 на плотном кабеле, специальные сети поддерживают 16 каналов)                                                                                                                        |
| `x3`       | Все ёмкости каналов утроены (24 на обычном кабеле, 92 на плотном кабеле, специальные сети поддерживают 24 канала)                                                                                                                        |
| `x4`       | Все ёмкости каналов учетверены (32 на обычном кабеле, 128 на плотном кабеле, специальные сети поддерживают 32 канала)                                                                                                                    |
| `infinite` | Все ограничения каналов сняты. Контроллеры по-прежнему *значительно* снижают потребление энергии сетками. Умные кабели будут только переключаться между полностью выключенными (без каналов) и полностью включёнными (1 или более каналов). |