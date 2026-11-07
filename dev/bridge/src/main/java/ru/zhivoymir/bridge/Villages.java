package ru.zhivoymir.bridge;

import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.server.world.data.Village;
import net.conczin.mca.server.world.data.VillageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Деревни MCA: поиск по расстоянию до границы и изменение репутации игрока. */
final class Villages {
    private Villages() {}

    /** Расстояние по горизонтали от точки до границы деревни; 0 — внутри. */
    static double borderDistance(Village village, BlockPos pos) {
        BoundingBox box = village.getBox();
        int dx = Math.max(Math.max(box.minX() - pos.getX(), pos.getX() - box.maxX()), 0);
        int dz = Math.max(Math.max(box.minZ() - pos.getZ(), pos.getZ() - box.maxZ()), 0);
        return Math.sqrt((double) dx * dx + (double) dz * dz);
    }

    /** Ближайшая деревня, до границы которой не дальше {@code maxDistance}. */
    static Optional<Village> nearest(ServerLevel level, BlockPos pos, double maxDistance) {
        return VillageManager.get(level).findVillages(v -> borderDistance(v, pos) <= maxDistance)
                .min(Comparator.comparingDouble(v -> borderDistance(v, pos)));
    }

    /**
     * Меняет отношение жителей деревни к игроку на {@code delta} сердец в сумме, распределяя по загруженным жителям.
     * Репутация деревни в MCA — сумма сердец её жителей. Возвращает false, если рядом нет ни одного жителя.
     */
    static boolean addReputation(ServerLevel level, Village village, ServerPlayer player, int delta) {
        if (delta == 0) return true;
        List<VillagerEntityMCA> residents = new ArrayList<>(village.getResidents(level));
        if (residents.isEmpty()) return false;
        Collections.shuffle(residents);
        int n = residents.size();
        int base = delta / n;
        int rest = Math.abs(delta % n);
        int sign = Integer.signum(delta);
        for (int i = 0; i < n; i++) {
            int d = base + (i < rest ? sign : 0);
            if (d == 0) continue;
            VillagerEntityMCA villager = residents.get(i);
            var memories = villager.getVillagerBrain().getMemoriesForPlayer(player);
            memories.modHearts(d);
            // MCA сам пересчитывает репутацию раз в минуту — обновляем сразу, чтобы ранг менялся без задержки
            village.setReputation(player, villager, memories.getHearts());
        }
        return true;
    }
}
