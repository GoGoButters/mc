#!/usr/bin/env python3
"""Мобильный экспорт сборки для Zalith Launcher 2 (Android): dist/android/ZhivoyMir-Android.mrpack.

Моды с Modrinth — ссылками в modrinth.index.json, остальные jar (CurseForge, наши zm*) — в overrides/mods.
config/, kubejs/, servers.dat — в overrides, как у десктопа; options.txt и голосовой чат — мобильные.
После сборки прогоняет depcheck.py на мобильном наборе модов.

Заодно пишет раскладку экранных кнопок Zalith: dist/android/ZhivoyMir-controls.json,
и packwiz-пак android/ (Pages: https://gogobutters.github.io/mc/android/pack.toml) — лаунчер синхронизируется с ним
при каждом запуске. После любого изменения сборки: запустить и закоммитить android/.

usage: android-export.py [--cache DIR] [--controls | --pack-only]   (кэш jar общий с depcheck, по умолчанию /tmp/depcache)
"""
import argparse, datetime, hashlib, json, os, shutil, subprocess, sys, tempfile, tomllib, urllib.parse, urllib.request, zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "dist", "android", "ZhivoyMir-Android.mrpack")
DEPCHECK = os.path.join(os.path.dirname(ROOT), "tools", "depcheck.py")
ANDROID_PACK = os.path.join(ROOT, "android")  # packwiz-пак для Android на Pages: /mc/android/pack.toml

# Исключения: slug (имя mods/<slug>.pw.toml) -> причина. Пополнять по итогам тестов на планшете.
# Кандидат: "sound-physics-remastered" — снять, если крашит/тормозит на Android.
EXCLUDE = {
    "distanthorizons": "Distant Horizons: тяжёлый LOD-рендер, на мобильном GPU (MobileGlues/GL ES) не работает или сильно тормозит",
}
# Файлы overrides, которые нужны только исключённым модам (префиксы путей).
EXCLUDE_FILES = ["config/DistantHorizons.toml", "kubejs/assets/distanthorizons/"]

# Мобильные настройки поверх options.txt сборки (клавиши остаются десктопные).
MOBILE_OPTIONS = {
    "version": "3955",
    "renderDistance": "8",
    "simulationDistance": "6",
    "graphicsMode": "0",  # fast
    "renderClouds": '"false"',
    "particles": "1",  # decreased
    "maxFps": "60",
    "guiScale": "3",
}
# SVC 2.6.x (ClientConfig): на Android нет нативных opus/rnnoise; активация голосом — как у десктопа (zmclient).
VOICECHAT_CLIENT = {
    "use_natives": "false",
    "denoiser": "false",
    "microphone_activation_type": "VOICE",
}

MODRINTH_HOSTS = {"cdn.modrinth.com"}  # белый список Modrinth-формата: что не отсюда — в overrides


def fetch(url, dst):
    req = urllib.request.Request(url, headers={"User-Agent": "zm-android-export"})
    with urllib.request.urlopen(req, timeout=180) as r, open(dst + ".part", "wb") as o:
        shutil.copyfileobj(r, o)
    os.replace(dst + ".part", dst)


def digest(data, fmt):
    return hashlib.new(fmt, data).hexdigest()


def jar_bytes(t, cache):
    """jar мода: кэш depcheck → локальный dist/ (наши моды на github.io) → URL / CDN CurseForge. Хэш сверяется с pw.toml."""
    fn, d = t["filename"], t["download"]
    path = os.path.join(cache, fn)
    if not os.path.exists(path):
        url = d.get("url", "")
        local = os.path.join(ROOT, url.split("/mc/", 1)[1]) if "/mc/dist/" in url else ""
        if local and os.path.exists(local):
            shutil.copy(local, path)
        elif url:
            fetch(url, path)
        elif "curseforge" in t.get("update", {}):
            fid = str(t["update"]["curseforge"]["file-id"])
            fetch(f"https://edge.forgecdn.net/files/{fid[:4]}/{int(fid[4:])}/{urllib.parse.quote(fn)}", path)
        else:
            sys.exit(f"ERROR {fn}: нет jar в кэше и нет ссылки (попробуй ../tools/cf-cache-sync.sh)")
    data = open(path, "rb").read()
    if digest(data, d["hash-format"]) != d["hash"]:
        sys.exit(f"ERROR {fn}: хэш не совпадает с pw.toml ({path})")
    return data


def excluded_file(rel):
    return any(rel == p or (p.endswith("/") and rel.startswith(p)) for p in EXCLUDE_FILES)


# Экранные кнопки Zalith Launcher 2 (2.8.x, формат LayerController, editorVersion 12).
# База — их стандартная раскладка (tools/zalith-default-layout.json = assets/default_layout.json из ZalithLauncher2,
# GPL-3), к ней слой «Живой мир». Клавиши — из options.txt сборки, иначе умолчание мода.
CONTROLS_OUT = os.path.join(ROOT, "dist", "android", "ZhivoyMir-controls.json")
CONTROLS_BASE = os.path.join(ROOT, "tools", "zalith-default-layout.json")
SCREEN = (2944, 1840)  # планшет 12.7", альбомная; позиции Zalith — доля свободного места, размеры — доля высоты экрана
# (надпись, привязка в options.txt, умолчание мода, левый верхний угол в px, размер в 1/10000 высоты экрана)
ZM_BUTTONS = [
    ("Голос", "key.voice_chat", "key.keyboard.v", (1030, 0), (1500, 900)),
    ("Карта", "key.journeymap.map_toggle_alt", "key.keyboard.j", (1320, 0), (1500, 900)),
    ("Рюкзак", "key.backpacked.open_backpack", "key.keyboard.b", (1610, 0), (1500, 900)),
    ("Рюкзак: упр.", "key.backpacked.open_management", "key.keyboard.v", (1900, 0), (1500, 900)),
    # удерживаемая: пока кнопка зажата, зажат Caps Lock — разговор с жителями (zmclient)
    ("Жители", "key.zmclient.talk", "key.keyboard.caps.lock", (None, 610), (1900, 1300)),
]
# Модификаторы options.txt (key:...:SHIFT) жмутся раньше основной клавиши — Zalith шлёт события по порядку.
MODIFIER_KEYS = {"SHIFT": "GLFW_KEY_LEFT_SHIFT", "CONTROL": "GLFW_KEY_LEFT_CONTROL", "ALT": "GLFW_KEY_LEFT_ALT"}


def bound_keys(name, default):
    """options.txt «key_<name>:key.keyboard.b:SHIFT» -> ["GLFW_KEY_LEFT_SHIFT", "GLFW_KEY_B"]."""
    value = default
    for ln in open(os.path.join(ROOT, "options.txt"), encoding="utf-8").read().splitlines():
        if ln.startswith(f"key_{name}:"):
            value = ln.split(":", 1)[1]
    key, *mods = value.split(":")
    if not key.startswith("key.keyboard.") or key == "key.keyboard.unknown":
        raise SystemExit(f"ERROR: {name} = {value}: нужна клавиша клавиатуры")
    return [MODIFIER_KEYS[m] for m in mods] + ["GLFW_KEY_" + key[len("key.keyboard."):].replace(".", "_").upper()]


def zm_uuid(text, n):
    return hashlib.sha1(("zhivoymir:" + text).encode()).hexdigest()[:n]


def controls_layout():
    lay = json.load(open(CONTROLS_BASE, encoding="utf-8"))
    lay["info"] = {
        "name": {"default": "Живой мир (планшет)", "matchQueue": []},
        "author": {"default": "Живой мир", "matchQueue": []},
        "description": {"default": "Стандартная раскладка Zalith + кнопки сборки «Живой мир»", "matchQueue": []},
        "versionCode": 1, "versionName": "1.0",
    }
    for layer in lay["layers"]:
        for b in layer.get("normalButtons", []):
            if b["clickEvents"] == [{"type": "key", "key": "GLFW_KEY_T"}]:
                b["text"]["default"] = "Чат"
                b["clickEvents"] = [{"type": "key", "key": k} for k in bound_keys("key.chat", "key.keyboard.t")]
    W, H = SCREEN
    buttons = []
    for text, name, default, (x, y), (w, h) in ZM_BUTTONS:
        wpx, hpx = H * w / 10000, H * h / 10000
        buttons.append({
            "text": {"default": text, "matchQueue": []},
            "uuid": zm_uuid(text, 18),
            "position": {"x": 10000 if x is None else round(x / (W - wpx) * 10000), "y": round(y / (H - hpx) * 10000)},
            "buttonSize": {"type": "percentage", "widthDp": 50.0, "heightDp": 50.0, "widthPercentage": w,
                           "heightPercentage": h, "widthReference": "screen_height", "heightReference": "screen_height"},
            "buttonStyle": "cac8c754ffa0" if text == "Жители" else "d1096cf91caa",  # rounded / default из базы
            "textAlignment": "Center",  # enum без @SerialName — имя константы
            "visibilityType": "in_game",
            "clickEvents": [{"type": "key", "key": k} for k in bound_keys(name, default)],
            "isSwipple": False, "isPenetrable": False, "isToggleable": False,
        })
    zm = {"name": "zhivoymir", "uuid": zm_uuid("layer", 12), "hide": False, "visibilityType": "in_game",
          "normalButtons": buttons}
    lay["layers"].insert(0, zm)
    for layer in lay["layers"]:  # кнопка GUI прячет/показывает и наш слой
        for b in layer.get("normalButtons", []):
            if b["clickEvents"] and all(e["type"] == "switch_layer" for e in b["clickEvents"]):
                b["clickEvents"].append({"type": "switch_layer", "key": zm["uuid"]})
    return lay


def write_controls():
    os.makedirs(os.path.dirname(CONTROLS_OUT), exist_ok=True)
    with open(CONTROLS_OUT, "w", encoding="utf-8") as f:
        json.dump(controls_layout(), f, ensure_ascii=False, indent=1)
        f.write("\n")
    print(f"OK {CONTROLS_OUT}")


def mobile_options():
    lines = open(os.path.join(ROOT, "options.txt"), encoding="utf-8").read().splitlines()
    seen, out = set(), []
    for ln in lines:
        k = ln.split(":", 1)[0]
        if k in MOBILE_OPTIONS:
            ln = f"{k}:{MOBILE_OPTIONS[k]}"
            seen.add(k)
        out.append(ln)
    out += [f"{k}:{v}" for k, v in MOBILE_OPTIONS.items() if k not in seen]
    return ("\n".join(out) + "\n").encode()


def client_mods():
    """(slug, pw.toml) клиентского набора минус исключения — общий источник для экспорта и проверки."""
    index = tomllib.load(open(os.path.join(ROOT, "index.toml"), "rb"))
    for f in index["files"]:
        if not f.get("metafile"):
            continue
        t = tomllib.load(open(os.path.join(ROOT, f["file"]), "rb"))
        slug = os.path.basename(f["file"]).removesuffix(".pw.toml")
        if t.get("side", "both") in ("client", "both") and slug not in EXCLUDE:
            yield slug, f["file"], t


def voicechat_client():
    return "".join(f"{k}={v}\n" for k, v in VOICECHAT_CLIENT.items()).encode()


def toml_str(s):
    return json.dumps(s, ensure_ascii=False)  # базовая строка TOML совпадает с JSON для наших путей


def write_packwiz():
    """packwiz-пак для Android-лаунчера: android/ в корне репо → https://gogobutters.github.io/mc/android/pack.toml.

    Копии pw.toml клиентского набора без EXCLUDE и файлов без EXCLUDE_FILES, мобильные options.txt и голосовой чат
    (preserve = true: синхронизация при запуске не затирает настройки игрока). index.toml и pack.toml пишутся здесь,
    хэши sha256 — как у packwiz refresh. В основном паке android/ скрыт через .packwizignore.
    """
    pack = tomllib.load(open(os.path.join(ROOT, "pack.toml"), "rb"))
    index = tomllib.load(open(os.path.join(ROOT, "index.toml"), "rb"))
    keep = {rel for _, rel, _ in client_mods()}
    files = {}  # rel -> (bytes, metafile, preserve)
    for f in index["files"]:
        rel = f["file"]
        if (f.get("metafile") and rel not in keep) or (not f.get("metafile") and excluded_file(rel)):
            continue
        files[rel] = (open(os.path.join(ROOT, rel), "rb").read(), bool(f.get("metafile")), bool(f.get("preserve")))
    files["options.txt"] = (mobile_options(), False, True)
    files["config/voicechat/voicechat-client.properties"] = (voicechat_client(), False, True)

    shutil.rmtree(ANDROID_PACK, ignore_errors=True)
    lines = ['hash-format = "sha256"']
    for rel in sorted(files):
        data, meta, preserve = files[rel]
        dst = os.path.join(ANDROID_PACK, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        with open(dst, "wb") as o:
            o.write(data)
        lines += ["", "[[files]]", f"file = {toml_str(rel)}", f'hash = "{digest(data, "sha256")}"']
        lines += ["metafile = true"] if meta else []
        lines += ["preserve = true"] if preserve else []
    idx = ("\n".join(lines) + "\n").encode()
    open(os.path.join(ANDROID_PACK, "index.toml"), "wb").write(idx)
    v = pack["versions"]
    open(os.path.join(ANDROID_PACK, "pack.toml"), "w", encoding="utf-8").write(
        f'name = "Живой мир (Android)"\nauthor = {toml_str(pack.get("author", ""))}\n'
        f'version = {toml_str(pack["version"])}\npack-format = {toml_str(pack["pack-format"])}\n\n'
        f'[index]\nfile = "index.toml"\nhash-format = "sha256"\nhash = "{digest(idx, "sha256")}"\n\n'
        f'[versions]\nminecraft = {toml_str(v["minecraft"])}\nneoforge = {toml_str(v["neoforge"])}\n')
    print(f"OK {ANDROID_PACK}/pack.toml: модов {len(keep)}, файлов в индексе {len(files)}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--cache", default="/tmp/depcache")
    ap.add_argument("--controls", action="store_true", help="только раскладку кнопок Zalith, без mrpack")
    ap.add_argument("--pack-only", action="store_true", help="только раскладку и packwiz-пак android/, без mrpack")
    args = ap.parse_args()
    write_controls()
    if args.controls:
        return
    write_packwiz()
    if args.pack_only:
        return
    os.makedirs(args.cache, exist_ok=True)
    pack = tomllib.load(open(os.path.join(ROOT, "pack.toml"), "rb"))
    index = tomllib.load(open(os.path.join(ROOT, "index.toml"), "rb"))
    version = f"{pack['version']}-{datetime.date.today():%Y%m%d}"

    files, overrides, metafiles = [], {}, []
    for slug, rel, t in client_mods():
        metafiles.append(rel)
        data = jar_bytes(t, args.cache)
        url = t["download"].get("url", "")
        if urllib.parse.urlparse(url).netloc in MODRINTH_HOSTS:
            files.append({
                "path": f"mods/{t['filename']}",
                "hashes": {"sha1": digest(data, "sha1"), "sha512": digest(data, "sha512")},
                "env": {"client": "required", "server": "required" if t.get("side", "both") == "both" else "unsupported"},
                "downloads": [url],
                "fileSize": len(data),
            })
        else:
            overrides[f"mods/{t['filename']}"] = data
    for f in index["files"]:
        rel = f["file"]
        if f.get("metafile") or rel == "options.txt" or excluded_file(rel):
            continue
        overrides[rel] = open(os.path.join(ROOT, rel), "rb").read()
    overrides["options.txt"] = mobile_options()
    overrides["config/voicechat/voicechat-client.properties"] = voicechat_client()

    manifest = {
        "formatVersion": 1,
        "game": "minecraft",
        "versionId": version,
        "name": "Живой мир (Android)",
        "summary": "Мобильная версия клиента сборки «Живой мир» для Zalith Launcher 2",
        "files": sorted(files, key=lambda x: x["path"]),
        "dependencies": {"minecraft": pack["versions"]["minecraft"], "neoforge": pack["versions"]["neoforge"]},
    }

    # depcheck на мобильном наборе: временный pack-каталог только с оставшимися pw.toml
    with tempfile.TemporaryDirectory() as tmp:
        os.makedirs(os.path.join(tmp, "mods"))
        for rel in metafiles:
            shutil.copy(os.path.join(ROOT, rel), os.path.join(tmp, "mods"))
        r = subprocess.run([sys.executable, DEPCHECK, tmp, args.cache, "client"], capture_output=True, text=True)
        print("depcheck:", r.stdout.strip().splitlines()[-1] if r.stdout.strip() else r.stderr.strip())
        if r.returncode:
            print("\n".join(r.stdout.splitlines()[-20:]))
            sys.exit("ERROR: depcheck не прошёл для мобильного набора")

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with zipfile.ZipFile(OUT + ".part", "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("modrinth.index.json", json.dumps(manifest, ensure_ascii=False, indent=1))
        for rel in sorted(overrides):
            z.writestr("overrides/" + rel, overrides[rel])
    os.replace(OUT + ".part", OUT)
    n_ovr = sum(1 for k in overrides if k.startswith("mods/"))
    print(f"OK {OUT}: {version}, {os.path.getsize(OUT) / 2**20:.1f} МБ, модов {len(files) + n_ovr} "
          f"(ссылками {len(files)}, в overrides {n_ovr}), исключено: {', '.join(sorted(EXCLUDE))}")


if __name__ == "__main__":
    main()
