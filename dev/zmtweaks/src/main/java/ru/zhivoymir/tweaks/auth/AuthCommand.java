package ru.zhivoymir.tweaks.auth;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** /zmauth list <ник>, /zmauth reset <ник> <android|pc|all> — пароли платформ (оп 3-го уровня). */
public final class AuthCommand {
    private AuthCommand() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("zmauth")
                .requires(src -> src.hasPermission(3))
                .then(Commands.literal("list")
                        .then(Commands.argument("nick", StringArgumentType.word()).executes(AuthCommand::list)))
                .then(Commands.literal("reset")
                        .then(Commands.argument("nick", StringArgumentType.word())
                                .then(Commands.argument("platform", StringArgumentType.word())
                                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(new String[]{PlatformAuth.ANDROID, PlatformAuth.PC, "all"}, b))
                                        .executes(AuthCommand::reset)))));
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        String nick = StringArgumentType.getString(ctx, "nick");
        PlatformStore store = NedologinAuth.store(ctx.getSource().getServer());
        Map<String, String> hashes = store.hashes(nick);
        String extra = hashes.isEmpty() ? "нет" : String.join(", ", hashes.keySet());
        ctx.getSource().sendSuccess(() -> Component.literal(nick + ": основной пароль Nedologin — " + store.mainPlatform(nick)
                + "; пароли платформ zmtweaks — " + extra), false);
        return hashes.size();
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        String nick = StringArgumentType.getString(ctx, "nick");
        String platform = StringArgumentType.getString(ctx, "platform");
        if (!platform.equals(PlatformAuth.ANDROID) && !platform.equals(PlatformAuth.PC) && !platform.equals("all")) {
            ctx.getSource().sendFailure(Component.literal("Платформа: android, pc или all"));
            return 0;
        }
        PlatformStore store = NedologinAuth.store(ctx.getSource().getServer());
        String main = store.mainPlatform(nick);
        int removed = store.reset(nick, platform.equals("all") ? null : platform);
        String hint = platform.equals("all") || platform.equals(main)
                ? " Основной пароль (" + main + ") хранит Nedologin: сброс — /nedologin unregister " + nick + "." : "";
        ctx.getSource().sendSuccess(() -> Component.literal(removed > 0
                ? nick + ": снято паролей платформ — " + removed + ". Следующий вход с этой платформы зарегистрирует новый." + hint
                : nick + ": снимать нечего." + hint), true);
        return removed;
    }
}
