package ru.zhivoymir.livingvoice;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.util.UUID;

/** Отладка без микрофона: /livingvoice selftest <текст>, /livingvoice test <житель> <фраза>. Только для операторов. */
final class DebugCommands {
    private static final GameProfile TESTER = new GameProfile(UUID.fromString("7e57e57e-0000-4000-8000-00000000a11c"), "livingvoice_test");

    private DebugCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(net.minecraft.commands.Commands.literal("livingvoice").requires(s -> s.hasPermission(2))
                .then(net.minecraft.commands.Commands.literal("selftest")
                        .then(net.minecraft.commands.Commands.argument("text", StringArgumentType.greedyString()).executes(c -> selftest(c.getSource(), StringArgumentType.getString(c, "text")))))
                .then(net.minecraft.commands.Commands.literal("test")
                        .then(net.minecraft.commands.Commands.argument("target", EntityArgument.entity())
                                .then(net.minecraft.commands.Commands.argument("text", StringArgumentType.greedyString()).executes(c ->
                                        test(c.getSource(), EntityArgument.getEntity(c, "target"), StringArgumentType.getString(c, "text")))))));
    }

    /** Озвучка -> 48 кГц (как в голосовом чате) -> 16 кГц -> распознавание. */
    private static int selftest(CommandSourceStack src, String text) {
        long t0 = System.currentTimeMillis();
        Http.voices().thenCompose(v -> Http.speak(text, v.get(0)[0])).thenCompose(pcm -> {
            short[] p48 = Http.resample(pcm, 22050, 48000);
            LivingVoice.LOG.info("[livingvoice] selftest: TTS {} ms, {} s audio", System.currentTimeMillis() - t0, p48.length / 48000.0);
            return Http.recognize(Http.down48to16(p48, p48.length));
        }).whenComplete((heard, err) -> src.getServer().execute(() -> {
            String msg = err != null ? "selftest FAILED: " + err : "selftest: said «" + text + "», heard «" + heard + "» in " + (System.currentTimeMillis() - t0) + " ms";
            LivingVoice.LOG.info("[livingvoice] {}", msg);
            src.sendSystemMessage(Component.literal(msg));
        }));
        return 1;
    }

    /** Подставной игрок в 3 блоках от жителя смотрит на него и «говорит» фразу. */
    private static int test(CommandSourceStack src, Entity target, String text) {
        ServerLevel level = (ServerLevel) target.level();
        FakePlayer fake = FakePlayerFactory.get(level, TESTER);
        Vec3 away = target.getLookAngle().multiply(1, 0, 1).normalize().scale(3);
        if (away.lengthSqr() < 0.01) away = new Vec3(3, 0, 0);
        Vec3 pos = target.position().add(away);
        fake.moveTo(pos.x, pos.y, pos.z, 0, 0);
        fake.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        Entity looked = Targets.looked(fake);
        Entity to = Targets.resolve(fake, looked, text);
        StringBuilder near = new StringBuilder();
        for (Entity e : Targets.nearby(fake)) near.append(e.getName().getString()).append(String.format(" %.0fm %s; ", e.distanceTo(fake), Targets.jobsOf(fake, e)));
        String msg = "test: looked=" + name(looked) + " -> " + name(to) + " «" + text + "» | nearby: " + near;
        LivingVoice.LOG.info("[livingvoice] {}", msg);
        src.sendSystemMessage(Component.literal(msg));
        if (to != null) Targets.say(fake, to, text);
        return 1;
    }

    private static String name(Entity e) {
        return e == null ? "nobody" : e.getName().getString() + " (" + e.getType().getDescriptionId() + ")";
    }
}
