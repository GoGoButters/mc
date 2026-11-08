// Живой мир: выдаём книгу новичка при первом входе и по /guide.
// Книга — справочник GuideMe: guideme:guide[guideme:guide_id="zhivoymir:start"]
console.info('[zhivoymir] guide.js loaded');

const GUIDE_ITEM = Item.of('guideme:guide[guideme:guide_id="zhivoymir:start"]');
const GUIDE_FLAG = 'zm_guide_given';

// Выдать книгу разово при первом входе в мир.
PlayerEvents.loggedIn(event => {
    const player = event.player;
    const data = player.persistentData;
    if (data.getBoolean(GUIDE_FLAG)) return; // уже получал
    data.putBoolean(GUIDE_FLAG, true); // отметить заранее, чтобы не задвоить при сбое

    player.give(GUIDE_ITEM.copy());
    player.tell([
        { text: 'Получена книга «', color: 'dark_green' },
        { text: 'Живой мир: Летопись странника', color: 'gold' },
        { text: '». Открой её (ПКМ) и начни свой путь. Если потерял — введи /guide.', color: 'dark_green' },
    ]);
    console.info('[zhivoymir] выдана книга новичка игроку ' + player.username);
});

// Повторная выдача по /guide, если книги нет в инвентаре.
ServerEvents.commandRegistry(event => {
    const { commands: Commands } = event;

    event.register(
        Commands.literal('guide')
            .requires(source => source.player != null)
            .executes(ctx => {
                const player = ctx.source.player;
                const hasBook = player.inventory.find(item => item.id === 'guideme:guide') !== -1;
                if (hasBook || !player.inventory.insertItem(GUIDE_ITEM.copy(), false).empty) {
                    player.tell({ text: 'Книга уже есть в инвентаре или в нём нет места.', color: 'gold' });
                } else {
                    player.tell({ text: 'Вот снова твоя книга. Удачи, странник!', color: 'dark_green' });
                }
                return 1;
            })
    );
    console.info('[zhivoymir] зарегистрирована команда /guide');
});