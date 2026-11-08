import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayDeque;

/**
 * Иконка из сгенерированной картинки: фон убирается заливкой от краёв до чёрного контура,
 * результат — icon.png (256) и ZhivoyMir.ico (256/48/32/16, PNG внутри ICO).
 * java MakeIcon.java <вход.png> <выход-png> <выход-ico>
 */
public class MakeIcon {
    public static void main(String[] a) throws Exception {
        BufferedImage src = ImageIO.read(new File(a[0]));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(src, 0, 0, null);
        boolean[] bg = new boolean[w * h];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int x = 0; x < w; x++) { q.add(x); q.add((h - 1) * w + x); }
        for (int y = 0; y < h; y++) { q.add(y * w); q.add(y * w + w - 1); }
        while (!q.isEmpty()) {
            int i = q.poll();
            if (bg[i]) continue;
            int c = img.getRGB(i % w, i / w);
            int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
            if (r * 0.3 + g * 0.59 + b * 0.11 < 70) continue; // контур
            bg[i] = true;
            int x = i % w, y = i / w;
            if (x > 0) q.add(i - 1);
            if (x < w - 1) q.add(i + 1);
            if (y > 0) q.add(i - w);
            if (y < h - 1) q.add(i + w);
        }
        int x0 = w, y0 = h, x1 = 0, y1 = 0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int c = img.getRGB(x, y), r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
                if (r > 150 && b > 150 && g < 120) bg[y * w + x] = true; // остатки пурпурного фона внутри контура
                if (bg[y * w + x]) img.setRGB(x, y, 0);
                else { x0 = Math.min(x0, x); y0 = Math.min(y0, y); x1 = Math.max(x1, x); y1 = Math.max(y1, y); }
            }
        int side = Math.max(x1 - x0, y1 - y0) + 1;
        BufferedImage sq = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
        sq.getGraphics().drawImage(img.getSubimage(x0, y0, x1 - x0 + 1, y1 - y0 + 1),
                (side - (x1 - x0 + 1)) / 2, (side - (y1 - y0 + 1)) / 2, null);

        int[] sizes = {256, 48, 32, 16};
        byte[][] pngs = new byte[sizes.length][];
        for (int k = 0; k < sizes.length; k++) {
            BufferedImage s = scale(sq, sizes[k]);
            if (k == 0) ImageIO.write(s, "png", new File(a[1]));
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ImageIO.write(s, "png", bo);
            pngs[k] = bo.toByteArray();
        }
        try (FileOutputStream out = new FileOutputStream(a[2])) {
            out.write(le(0, 2)); out.write(le(1, 2)); out.write(le(sizes.length, 2));
            int off = 6 + 16 * sizes.length;
            for (int k = 0; k < sizes.length; k++) {
                int s = sizes[k] == 256 ? 0 : sizes[k];
                out.write(new byte[]{(byte) s, (byte) s, 0, 0});
                out.write(le(1, 2)); out.write(le(32, 2));
                out.write(le(pngs[k].length, 4)); out.write(le(off, 4));
                off += pngs[k].length;
            }
            for (byte[] p : pngs) out.write(p);
        }
    }

    static BufferedImage scale(BufferedImage s, int size) {
        Image sc = s.getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING);
        BufferedImage o = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        o.getGraphics().drawImage(sc, 0, 0, null);
        return o;
    }

    static byte[] le(int v, int n) {
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) b[i] = (byte) (v >> (8 * i));
        return b;
    }
}
