package ru.zhivoymir.livingvoice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Отладка распознавания: фраза игрока как пришла из голосового чата (WAV) и что из неё распознано (TXT). */
final class Dump {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private Dump() {
    }

    /** Возвращает базовый путь без расширения или null, если отладка выключена. */
    static String save(String player, short[] pcm, int len, int rate, String stats) {
        String dir = LivingVoice.DUMP_DIR.get();
        if (dir.isEmpty()) return null;
        try {
            Path d = Path.of(dir);
            Files.createDirectories(d);
            String base = d.resolve(LocalDateTime.now().format(TS) + "-" + player).toString();
            ByteBuffer b = ByteBuffer.allocate(44 + len * 2).order(ByteOrder.LITTLE_ENDIAN);
            b.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(36 + len * 2).put("WAVEfmt ".getBytes(StandardCharsets.US_ASCII))
                    .putInt(16).putShort((short) 1).putShort((short) 1).putInt(rate).putInt(rate * 2).putShort((short) 2).putShort((short) 16)
                    .put("data".getBytes(StandardCharsets.US_ASCII)).putInt(len * 2);
            for (int i = 0; i < len; i++) b.putShort(pcm[i]);
            Files.write(Path.of(base + ".wav"), b.array());
            Files.writeString(Path.of(base + ".txt"), stats + "\n", StandardCharsets.UTF_8);
            return base;
        } catch (IOException e) {
            LivingVoice.LOG.warn("[livingvoice] dump failed: {}", e.toString());
            return null;
        }
    }

    static void text(String base, String text) {
        if (base == null) return;
        try {
            Files.writeString(Path.of(base + ".txt"), text + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LivingVoice.LOG.warn("[livingvoice] dump failed: {}", e.toString());
        }
    }
}
