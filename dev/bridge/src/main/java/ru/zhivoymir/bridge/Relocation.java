package ru.zhivoymir.bridge;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.eventbus.events.colony.citizens.CitizenAddedModEvent;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.ai.Relationship;
import net.conczin.mca.entity.ai.relationship.AgeState;
import net.conczin.mca.entity.ai.relationship.Gender;
import net.conczin.mca.server.world.data.Village;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Comparator;
import java.util.Optional;

/** «Приглашение в колонию»: житель MCA, который доверяет игроку, переезжает в его колонию колонистом. */
final class Relocation {
    private Relocation() {}

    static final String INVITE_TAG = "zmbridge_invite";

    static boolean isInvite(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(INVITE_TAG);
    }

    static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getHand() != InteractionHand.MAIN_HAND || !(e.getEntity() instanceof ServerPlayer player)) return;
        if (!(e.getTarget() instanceof VillagerEntityMCA villager) || !isInvite(e.getItemStack())) return;
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
        invite(player, villager, e.getItemStack());
    }

    private static void invite(ServerPlayer player, VillagerEntityMCA villager, ItemStack invite) {
        ServerLevel level = player.serverLevel();
        String name = villager.getName().getString();

        Optional<IColony> colony = managedColony(player, villager);
        if (colony.isEmpty()) {
            player.sendSystemMessage(Component.literal("Приглашать в колонию может только её владелец или офицер.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        if (villager.getAgeState() != AgeState.ADULT) {
            say(player, name, "Мне рано уходить из дома — спроси родителей.");
            return;
        }
        if (Relationship.IS_ROMANTIC_PARTNER.test(villager, player.getUUID())) {
            say(player, name, "Мы же семья! Для этого не нужно приглашение.");
            return;
        }
        Optional<Village> home = villager.getResidency().getHomeVillage();
        if (home.isEmpty()) {
            say(player, name, "У меня и дома-то своего нет — сначала мне нужно где-то осесть.");
            return;
        }
        Village village = home.get();
        if (village.getReputation(player) < Bridge.INVITE_MIN_REPUTATION.get()) {
            say(player, name, "Тебя в " + village.getName() + " пока мало знают. Помоги деревне — тогда и поговорим.");
            return;
        }
        if (villager.getVillagerBrain().getMemoriesForPlayer(player).getHearts() < Bridge.INVITE_MIN_HEARTS.get()) {
            say(player, name, "Мы с тобой едва знакомы. С чего бы мне всё бросать?");
            return;
        }
        int keep = Math.max(Bridge.VILLAGE_MIN_RESIDENTS.get(),
                (int) Math.ceil(Bridge.VILLAGE_KEEP_FRACTION.get() * village.getMaxPopulation()));
        if (village.getPopulation() - 1 < keep) {
            say(player, name, "Нас в " + village.getName() + " и так мало. Не могу бросить своих.");
            return;
        }
        BridgeData data = BridgeData.get(level.getServer());
        String key = BridgeData.villageKey(level, village.getId());
        long now = level.getGameTime();
        long cooldown = (long) (Bridge.INVITE_COOLDOWN_DAYS.get() * 24000);
        Long last = data.lastRelocation.get(key);
        if (last != null && now - last < cooldown) {
            say(player, name, "Недавно от нас уже уехали. Дай деревне привыкнуть, потом поговорим.");
            return;
        }
        IColony target = colony.get();
        var citizens = target.getCitizenManager();
        if (citizens.getCurrentCitizenCount() >= citizens.getPotentialMaxCitizens()) {
            say(player, name, "А жить-то я где буду? В «" + target.getName() + "» нет свободных домов.");
            return;
        }

        ICitizenData citizen = citizens.createAndRegisterCivilianData();
        citizen.setGender(villager.getGenetics().getGender() == Gender.FEMALE);
        citizen.setName(name);
        citizen.setIsChild(false);
        citizen.setParents("", "");
        citizens.spawnOrCreateCitizen(citizen, target.getWorld(), target.getCenter());
        IMinecoloniesAPI.getInstance().getEventBus().post(new CitizenAddedModEvent(citizen, CitizenAddedModEvent.CitizenAddedSource.HIRED));

        CompoundTag settler = new CompoundTag();
        settler.putString("name", name);
        settler.putUUID("mca", villager.getUUID());
        settler.putString("village", key);
        settler.putString("villageName", village.getName());
        settler.putString("colonyDim", target.getDimension().location().toString());
        settler.putInt("colony", target.getID());
        settler.putInt("citizen", citizen.getId());
        settler.putUUID("player", player.getUUID());
        settler.putLong("time", now);
        data.settlers.add(settler);
        data.lastRelocation.put(key, now);
        data.setDirty();

        say(player, name, "Хорошо, соберу вещи и приду в «" + target.getName() + "». Не забывай " + village.getName() + "!");
        villager.getResidency().leaveHome();
        villager.discard();
        if (!player.getAbilities().instabuild) invite.shrink(1);
        Bridge.LOG.info("[zmbridge] {} moved from village {} ({}) to colony {} as citizen {} (invited by {})",
                name, village.getName(), key, target.getName(), citizen.getId(), player.getGameProfile().getName());
    }

    /** Ближайшая к жителю колония, где игрок — владелец или офицер. */
    private static Optional<IColony> managedColony(ServerPlayer player, VillagerEntityMCA villager) {
        return IMinecoloniesAPI.getInstance().getColonyManager().getAllColonies().stream()
                .filter(c -> c.getPermissions().getRank(player).isColonyManager())
                .min(Comparator.comparingDouble(c -> c.getDimension() == villager.level().dimension()
                        ? c.getCenter().distSqr(villager.blockPosition()) : Double.MAX_VALUE));
    }

    private static void say(ServerPlayer player, String name, String text) {
        player.sendSystemMessage(Component.literal(name + ": ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(text).withStyle(ChatFormatting.WHITE)));
    }
}
