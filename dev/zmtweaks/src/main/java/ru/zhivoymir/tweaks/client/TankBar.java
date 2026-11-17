package ru.zhivoymir.tweaks.client;

import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Индикатор бака в окне машины IA. Место — от первого слота котла: полоса 16×4 под его спрайтом
 * (спрайт котла SlotRenderer: x−4…x+20, y−18…y+21) с зазором 1 px. Если там другой слот или подпись
 * «Инвентарь» — вертикальная полоса 4×16 справа от спрайта. Координаты — от левого верхнего угла окна.
 */
public record TankBar(int x, int y, int width, int height, boolean vertical) {
    private static final int BACKGROUND = 0xFF373737;
    private static final int KEROSENE = 0xFFE0A030;
    private static final int LOW = 0xFFC83C3C;

    /** null — у машины нет котла. */
    public static TankBar of(VehicleInventoryDescription description) {
        List<SlotDescription> boilers = description.getSlots(VehicleInventoryDescription.BOILER);
        if (boilers.isEmpty()) return null;
        SlotDescription boiler = boilers.getFirst();
        TankBar below = new TankBar(boiler.x(), boiler.y() + 22, 16, 4, false);
        // 1 px над полосой и 1 px под ней тоже свободны; подпись инвентаря — с getHeight() + 10
        boolean fits = below.y + below.height + 1 <= description.getHeight() + 10;
        for (SlotDescription slot : description.getSlots()) {
            if (slot == boiler || !fits) continue;
            int[] r = sprite(slot);
            fits = !overlaps(below.x, below.y - 1, below.width, below.height + 2, r[0], r[1], r[2], r[3]);
        }
        return fits ? below : new TankBar(boiler.x() + 22, boiler.y(), 4, 16, true);
    }

    /** Прямоугольник спрайта слота по SlotRenderer IA 1.7.0. */
    private static int[] sprite(SlotDescription slot) {
        return switch (slot.type()) {
            case "inventory" -> new int[]{slot.x() - 1, slot.y() - 1, 18, 18};
            case "boiler" -> new int[]{slot.x() - 4, slot.y() - 18, 24, 39};
            default -> new int[]{slot.x() - 3, slot.y() - 3, 22, 22};
        };
    }

    private static boolean overlaps(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
        return ax < bx + bw && bx < ax + aw && ay < by + bh && by < ay + ah;
    }

    public void render(GuiGraphics g, int left, int top, int amount, int capacity, int low) {
        int x0 = left + x, y0 = top + y;
        g.fill(x0, y0, x0 + width, y0 + height, BACKGROUND);
        int color = amount < low ? LOW : KEROSENE;
        if (vertical) {
            int filled = Math.round(height * Math.min(1.0f, amount / (float) capacity));
            g.fill(x0, y0 + height - filled, x0 + width, y0 + height, color);
        } else {
            int filled = Math.round(width * Math.min(1.0f, amount / (float) capacity));
            g.fill(x0, y0, x0 + filled, y0 + height, color);
        }
    }

    public boolean isHovered(int left, int top, double mouseX, double mouseY) {
        return mouseX >= left + x && mouseX < left + x + width && mouseY >= top + y && mouseY < top + y + height;
    }
}
