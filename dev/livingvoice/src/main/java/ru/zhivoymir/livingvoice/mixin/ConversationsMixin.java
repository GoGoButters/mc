package ru.zhivoymir.livingvoice.mixin;

import me.bytefox.livingcolonists.talk.Conversations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.livingvoice.ColonistBridge;

/** LC держит единственный экземпляр Conversations в приватном поле мода — запоминаем его при создании. */
@Mixin(value = Conversations.class, remap = false)
public class ConversationsMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void livingvoice$created(CallbackInfo ci) {
        ColonistBridge.conversations = (Conversations) (Object) this;
        ru.zhivoymir.livingvoice.LivingVoice.LOG.info("[livingvoice] Living Colonists conversations hooked");
    }
}
