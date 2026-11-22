#!/usr/bin/env python3
"""Проверка мобильного mrpack: валидный modrinth.index.json и список модов = клиентский набор минус исключения.

usage: android-check.py [path.mrpack]
"""
import glob, importlib.util, json, os, sys, tomllib, zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("android_export", os.path.join(HERE, "android-export.py"))
ex = importlib.util.module_from_spec(spec)
spec.loader.exec_module(ex)

path = sys.argv[1] if len(sys.argv) > 1 else ex.OUT
z = zipfile.ZipFile(path)
m = json.loads(z.read("modrinth.index.json"))
errors = []
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

for e in errors:
    print("FAIL", e)
print(f"{path}: модов {len(have)} (ссылками {len(in_pack)}, overrides {len(ovr)}), ожидалось {len(want)}, "
      f"расхождений {len(errors)}")
sys.exit(1 if errors else 0)
