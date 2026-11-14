package ru.zhivoymir.client.mixin;

import com.ldtteam.structurize.storage.StructurePackMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.zhivoymir.client.BlueprintNames;

/** Окно выбора стиля Structurize (его же открывает ратуша): название (ordinal 0) и описание стиля. */
@Pseudo
@Mixin(targets = "com.ldtteam.structurize.client.gui.WindowSwitchPack$PacksDrawableComponent", remap = false)
public class StructurizeSwitchPackMixin {
    @ModifyArg(method = "fillForMeta", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"), index = 0)
    private String zmclient$packName(String s) {
        return BlueprintNames.pack(s);
    }

    @Redirect(method = "fillForMeta", at = @At(value = "INVOKE",
            target = "Lcom/ldtteam/structurize/storage/StructurePackMeta;getDesc()Ljava/lang/String;"))
    private String zmclient$packDesc(StructurePackMeta meta) {
        return BlueprintNames.desc(meta.getName(), meta.getDesc());
    }
}
