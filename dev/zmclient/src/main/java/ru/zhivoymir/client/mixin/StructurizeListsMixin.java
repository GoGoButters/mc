package ru.zhivoymir.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.zhivoymir.client.BlueprintNames;

/** Инструмент строителя: списки вариантов ($3) и уровней ($4) — анонимные DataProvider. */
public final class StructurizeListsMixin {
    private StructurizeListsMixin() {}

    /** updateAlternatives: ordinal 0 — скрытый id, ordinal 1 — надпись кнопки варианта. */
    @Pseudo
    @Mixin(targets = "com.ldtteam.structurize.client.gui.WindowExtendedBuildTool$3", remap = false)
    public static class Alternatives {
        @ModifyArg(method = "updateElement", at = @At(value = "INVOKE", ordinal = 1,
                target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"), index = 0)
        private String zmclient$alternative(String s) {
            return BlueprintNames.name(s);
        }
    }

    /** updateLevels: «Level: N» → «Уровень N»; скрытые id под шаблон не подходят. */
    @Pseudo
    @Mixin(targets = "com.ldtteam.structurize.client.gui.WindowExtendedBuildTool$4", remap = false)
    public static class Levels {
        @ModifyArg(method = "updateElement", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"), index = 0)
        private String zmclient$level(String s) {
            return BlueprintNames.level(s);
        }
    }
}
