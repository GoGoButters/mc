#!/usr/bin/env python3
"""Проверка мобильного mrpack и packwiz-пака android/: моды = клиентский набор минус исключения, хэши сходятся.

usage: android-check.py [path.mrpack]
"""
import glob, importlib.util, json, os, sys, tomllib, zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("android_export", os.path.join(HERE, "android-export.py"))
ex = importlib.util.module_from_spec(spec)
spec.loader.exec_module(ex)

path = sys.argv[1] if len(sys.argv) > 1 else ex.OUT
errors = []
mrpack = os.path.exists(path)  # mrpack в git не хранится; без него — только раскладка и android/
if mrpack:
    z = zipfile.ZipFile(path)
    m = json.loads(z.read("modrinth.index.json"))
    if m.get("formatVersion") != 1 or m.get("game") != "minecraft":
        errors.append("formatVersion/game")
    for k in ("versionId", "name", "files", "dependencies"):
        if k not in m:
            errors.append(f"нет поля {k}")
    if not {"minecraft", "neoforge"} <= set(m.get("dependencies", {})):
        errors.append("dependencies без minecraft/neoforge")
    for f in m["files"]:
        h = f.get("hashes", {})
        if not (len(h.get("sha1", "")) == 40 and len(h.get("sha512", "")) == 128 and f.get("fileSize", 0) > 0
                and f.get("downloads") and f["path"].startswith("mods/") and ".." not in f["path"]):
            errors.append(f"плохая запись {f.get('path')}")

    in_pack = {f["path"][5:] for f in m["files"]}
    ovr = {n[len("overrides/mods/"):] for n in z.namelist() if n.startswith("overrides/mods/") and n.endswith(".jar")}
    dup = in_pack & ovr
    want = set()  # считаем заново по mods/*.pw.toml, не через client_mods() экспорта
    for f in glob.glob(os.path.join(ex.ROOT, "mods", "*.pw.toml")):
        t = tomllib.load(open(f, "rb"))
        if t.get("side", "both") != "server" and os.path.basename(f).removesuffix(".pw.toml") not in ex.EXCLUDE:
            want.add(t["filename"])
    have = in_pack | ovr
    for fn in sorted(want - have):
        errors.append(f"нет в архиве: {fn}")
    for fn in sorted(have - want):
        errors.append(f"лишний в архиве: {fn}")
    for fn in sorted(dup):
        errors.append(f"и ссылкой, и в overrides: {fn}")
    opts = z.read("overrides/options.txt").decode()
    if not opts.startswith("version:3955\n"):
        errors.append("options.txt без version:3955 в начале")

# Раскладка Zalith: поля моделей LayerController (ControlLayout/ControlLayer/NormalData/ButtonSize/ClickEvent)
# и клавиши — как в options.txt сборки (bound_keys экспорта).
GLFW_NAMES = {"GLFW_KEY_CAPS_LOCK", "GLFW_KEY_LEFT_SHIFT", "GLFW_KEY_LEFT_CONTROL", "GLFW_KEY_LEFT_ALT",
              "GLFW_KEY_SPACE", "GLFW_KEY_ESCAPE", "GLFW_KEY_TAB", "GLFW_KEY_F3", "GLFW_KEY_F5",
              "GLFW_KEY_RIGHT_BRACKET"} | {f"GLFW_KEY_{c}" for c in "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"}
lay = json.load(open(ex.CONTROLS_OUT, encoding="utf-8"))
if lay.get("editorVersion") != 12 or not {"name", "author", "description", "versionCode", "versionName"} <= set(lay["info"]):
    errors.append("раскладка: editorVersion/info")
styles = {s["uuid"] for s in lay["styles"]}
layers = {l["uuid"] for l in lay["layers"]}
need = {"text", "uuid", "position", "buttonSize", "visibilityType", "isSwipple", "isPenetrable", "isToggleable"}
uuids = []
for l in lay["layers"]:
    if not {"name", "uuid", "hide", "visibilityType"} <= set(l) or l["visibilityType"] not in ("always", "in_game", "in_menu"):
        errors.append(f"раскладка: слой {l.get('name')}")
    for b in l.get("normalButtons", []):
        t = b.get("text", {}).get("default")
        uuids.append(b.get("uuid"))
        s, p = b.get("buttonSize", {}), b.get("position", {})
        ok = (need <= set(b) and b.get("buttonStyle") in styles | {None}
              and b.get("textAlignment", "Left") in ("Left", "Center", "Right")
              and all(0 <= p.get(k, -1) <= 10000 for k in "xy")
              and all(100 <= s.get(k, 0) <= 10000 for k in ("widthPercentage", "heightPercentage")))
        for e in b.get("clickEvents", []):
            if e["type"] == "key" and e["key"] not in GLFW_NAMES:
                ok = False
            if e["type"] == "switch_layer" and e["key"] not in layers:
                ok = False
        if not ok:
            errors.append(f"раскладка: кнопка {t}")
if len(uuids) != len(set(uuids)):
    errors.append("раскладка: повтор uuid кнопок")
zm = {b["text"]["default"]: [e["key"] for e in b["clickEvents"]] for b in lay["layers"][0]["normalButtons"]}
for text, name, default, *_ in ex.ZM_BUTTONS:
    if zm.get(text) != ex.bound_keys(name, default):
        errors.append(f"раскладка: {text} не совпадает с options.txt")

# packwiz-пак android/ (Pages /mc/android/pack.toml): моды = client_mods(), хэши индекса и pack.toml сходятся,
# мобильные options.txt и голосовой чат с preserve = true.
ap = ex.ANDROID_PACK
if not os.path.exists(os.path.join(ap, "pack.toml")):
    sys.exit(f"FAIL нет {ap}/pack.toml — запусти android-export.py --pack-only")
apack = tomllib.load(open(os.path.join(ap, "pack.toml"), "rb"))
idx_raw = open(os.path.join(ap, apack["index"]["file"]), "rb").read()
if ex.digest(idx_raw, apack["index"]["hash-format"]) != apack["index"]["hash"]:
    errors.append("android/pack.toml: хэш index.toml не сходится")
if apack["versions"] != tomllib.load(open(os.path.join(ex.ROOT, "pack.toml"), "rb"))["versions"]:
    errors.append("android/pack.toml: versions не как у основного пака")
aidx = tomllib.loads(idx_raw.decode())
for f in aidx["files"]:
    p = os.path.join(ap, f["file"])
    if not os.path.isfile(p) or os.path.islink(p):
        errors.append(f"android: нет файла (или симлинк) {f['file']}")
    elif ex.digest(open(p, "rb").read(), f.get("hash-format", aidx["hash-format"])) != f["hash"]:
        errors.append(f"android: хэш не сходится {f['file']}")
    elif ex.excluded_file(f["file"]):
        errors.append(f"android: исключённый файл {f['file']}")
a_mods = {f["file"] for f in aidx["files"] if f.get("metafile")}
want_mods = {rel for _, rel, _ in ex.client_mods()}
for rel in sorted(a_mods ^ want_mods):
    errors.append(f"android: {'лишний' if rel in a_mods else 'нет'} мод {rel}")
pres = {f["file"] for f in aidx["files"] if f.get("preserve")}
if not {"options.txt", "config/voicechat/voicechat-client.properties"} <= pres:
    errors.append("android: options.txt/voicechat-client.properties без preserve")
if open(os.path.join(ap, "options.txt"), "rb").read() != ex.mobile_options():
    errors.append("android: options.txt не мобильный")
print(f"android/: модов {len(a_mods)}, файлов {len(aidx['files'])}")

for e in errors:
    print("FAIL", e)
if mrpack:
    print(f"{path}: модов {len(have)} (ссылками {len(in_pack)}, overrides {len(ovr)}), ожидалось {len(want)}")
else:
    print(f"{path}: нет — проверка mrpack пропущена")
print(f"расхождений {len(errors)}")
sys.exit(1 if errors else 0)
