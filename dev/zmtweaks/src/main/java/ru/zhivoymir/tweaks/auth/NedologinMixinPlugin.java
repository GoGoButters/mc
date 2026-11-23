package ru.zhivoymir.tweaks.auth;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

/**
 * Мягкое применение миксинов на Nedologin: если класса или нужных вызовов нет (другая версия
 * Nedologin или его нет вовсе) — предупреждение в логе, миксин пропускается, игра не падает.
 */
public final class NedologinMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOG = LogUtils.getLogger();
    private static final String STORAGE = "ariss/nedologin/common/server/storage/StorageProvider_neoforge";
    private static final String PACKETS = "ariss/nedologin/common/network/NLPackets_neoforge";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean ok;
        try {
            ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(targetClassName.replace('.', '/'));
            ok = mixinClassName.endsWith("LoginHandlerMixin")
                    ? calls(node, "login", STORAGE, "checkPassword") && calls(node, "login", STORAGE, "register")
                    : calls(node, "loginPayload", PACKETS, "passwordDigest");
        } catch (Throwable e) {
            ok = false;
        }
        if (!ok) LOG.warn("[zmtweaks] Nedologin не найден или изменился ({}): пароли платформ отключены", targetClassName);
        return ok;
    }

    private static boolean calls(ClassNode node, String method, String owner, String name) {
        for (MethodNode m : node.methods) {
            if (!m.name.equals(method)) continue;
            for (AbstractInsnNode insn : m.instructions) {
                if (insn instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals(name)) return true;
            }
        }
        return false;
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        LOG.info("[zmtweaks] Пароли платформ: {} применён к {}", mixinClassName, targetClassName);
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
