// Живой мир: рецепт руководства Advanced Peripherals.
// Мод держит рецепт в старой папке data/advancedperipherals/recipes/ с NBT-результатом —
// в 1.21 он не загружается, и книгу нельзя получить. Повторяем его с компонентом Patchouli:
// книга + проводной модем (как у автора мода).
console.info('[zhivoymir] guide_books.js loaded');

ServerEvents.recipes(event => {
    event.shaped(
        Item.of('patchouli:guide_book[patchouli:book="advancedperipherals:manual"]'),
        ['BM'],
        {
            B: 'minecraft:book',
            M: '#computercraft:wired_modem',
        }
    ).id('zhivoymir:advancedperipherals_manual');
});
