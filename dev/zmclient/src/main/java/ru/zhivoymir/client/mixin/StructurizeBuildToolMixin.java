package ru.zhivoymir.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.zhivoymir.client.BlueprintNames;

/**
 * Инструмент строителя Structurize: надписи разделов, чертежей и строка пути выводятся через
 * Component.literal из имён папок и файлов. Подменяем только текст надписи (BlueprintNames);
 * идентификаторы кнопок и логика выбора не трогаются.
 */
@Pseudo
@Mixin(targets = "com.ldtteam.structurize.client.gui.WindowExtendedBuildTool", remap = false)
public class StructurizeBuildToolMixin {
    private static final String LITERAL = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;";

    /** Строка «стиль/раздел/чертёж» над списками. */
    @ModifyArg(method = {"init", "onButtonClicked", "setBlueprint"}, at = @At(value = "INVOKE", target = LITERAL), index = 0)
    private String zmclient$tree(String s) {
        return BlueprintNames.tree(s);
    }

    /** Подсказки к значкам разделов верхнего уровня. */
    @ModifyArg(method = "onUpdate", at = @At(value = "INVOKE", target = LITERAL), index = 0)
    private String zmclient$categoryTooltip(String s) {
        return BlueprintNames.folder(s);
    }

    /** Кнопки подразделов и подсказка «back». */
    @ModifyArg(method = "handleSubCat", at = @At(value = "INVOKE", target = LITERAL), index = 0)
    private String zmclient$folder(String s) {
        return BlueprintNames.folder(s);
    }

    /** Кнопки чертежей (у хижин название уже переводится блоком) и их подсказки. */
    @ModifyArg(method = "handleBlueprint", at = @At(value = "INVOKE", target = LITERAL), index = 0)
    private String zmclient$blueprint(String s) {
        return BlueprintNames.name(s);
    }
}
