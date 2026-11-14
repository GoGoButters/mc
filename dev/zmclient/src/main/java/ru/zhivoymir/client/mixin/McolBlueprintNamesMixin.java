package ru.zhivoymir.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.zhivoymir.client.BlueprintNames;

/** Окна MineColonies, где стиль или путь чертежа выводятся через Component.literal. */
public final class McolBlueprintNamesMixin {
    private static final String LITERAL = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;";

    private McolBlueprintNamesMixin() {}

    /** Окно постройки хижины: выпадающий список стилей. */
    @Pseudo
    @Mixin(targets = "com.minecolonies.core.client.gui.WindowBuildBuilding$1", remap = false)
    public static class StyleList {
        @ModifyArg(method = "getLabel", at = @At(value = "INVOKE", target = LITERAL), index = 0)
        private String zmclient$style(String s) {
            return BlueprintNames.pack(s);
        }
    }

    /** Ратуша, главная страница: кнопка стиля колонии (ordinal 1; ordinal 0 — другая надпись). */
    @Pseudo
    @Mixin(targets = "com.minecolonies.core.client.gui.townhall.WindowMainPage", remap = false)
    public static class TownHallStyle {
        @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = LITERAL, ordinal = 1), index = 0)
        private String zmclient$style(String s) {
            return BlueprintNames.pack(s);
        }
    }

    /** Обзор построек: стиль (ordinal 0) и путь чертежа (ordinal 1). */
    @Pseudo
    @Mixin(targets = "com.minecolonies.core.client.gui.WindowBuildingBrowser$1", remap = false)
    public static class BuildingBrowser {
        @ModifyArg(method = "updateElement", at = @At(value = "INVOKE", target = LITERAL, ordinal = 0), index = 0)
        private String zmclient$pack(String s) {
            return BlueprintNames.pack(s);
        }

        @ModifyArg(method = "updateElement", at = @At(value = "INVOKE", target = LITERAL, ordinal = 1), index = 0)
        private String zmclient$path(String s) {
            return BlueprintNames.path(s, "/");
        }
    }

    /** Контроллер декора: путь чертежа построчно. */
    @Pseudo
    @Mixin(targets = "com.minecolonies.core.client.gui.WindowDecorationController", remap = false)
    public static class Decoration {
        @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = LITERAL), index = 0)
        private String zmclient$path(String s) {
            return BlueprintNames.path(s, "\n");
        }
    }
}
