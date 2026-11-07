package ru.zhivoymir.livingvoice.mixin;

import me.bytefox.livingcolonists.talk.LinePacing;
import me.bytefox.livingcolonists.talk.TalkService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.zhivoymir.livingvoice.LivingVoice;

import java.util.Random;

/**
 * LC перед ответом игроку выжидает «паузу» 70–140 тиков (3,5–7 с) и только потом «печатает».
 * В голосовом разговоре это выглядит как зависание — паузу для личных ответов задаёт конфиг.
 */
@Mixin(value = TalkService.class, remap = false)
public class ReplyPacingMixin {
    @Redirect(method = "sayPrivately", at = @At(value = "INVOKE",
            target = "Lme/bytefox/livingcolonists/talk/LinePacing;gap(Ljava/util/Random;)J"))
    private long livingvoice$replyGap(Random random) {
        int gap = LivingVoice.REPLY_GAP_TICKS.get();
        return gap < 0 ? LinePacing.gap(random) : gap;
    }
}
