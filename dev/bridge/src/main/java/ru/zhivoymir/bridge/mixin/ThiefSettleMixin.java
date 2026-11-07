package ru.zhivoymir.bridge.mixin;

import com.minecolonies.api.colony.IColony;
import me.lovkar.thieves.ai.EntityAIWorkThief;
import me.lovkar.thieves.job.Heist;
import me.lovkar.thieves.job.Outcome;
import me.lovkar.thieves.target.Target;
import me.lovkar.thieves.target.TargetKind;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.bridge.Reputation;

/** Colonist Thieves подводит итог кражи в settle(): если вора заметили в деревне — деревня это запоминает. */
@Mixin(value = EntityAIWorkThief.class, remap = false)
public abstract class ThiefSettleMixin {
    @Shadow
    private IColony colony() { throw new AssertionError(); }

    @Inject(method = "settle", at = @At("HEAD"))
    private void zmbridge$settle(Heist heist, Outcome outcome, CallbackInfo ci) {
        Target target = heist.target();
        if (target.kind() != TargetKind.VILLAGE || !outcome.discovered()) return;
        IColony colony = colony();
        if (colony == null || !(colony.getWorld() instanceof ServerLevel world)) return;
        ServerLevel level = world.getServer().getLevel(target.dimension());
        if (level != null) Reputation.onTheft(level, target.pos(), colony, outcome == Outcome.CAUGHT);
    }
}
