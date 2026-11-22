#!/usr/bin/env python3
"""Мобильный экспорт сборки для Zalith Launcher 2 (Android): dist/android/ZhivoyMir-Android.mrpack.

Моды с Modrinth — ссылками в modrinth.index.json, остальные jar (CurseForge, наши zm*) — в overrides/mods.
config/, kubejs/, servers.dat — в overrides, как у десктопа; options.txt и голосовой чат — мобильные.
После сборки прогоняет depcheck.py на мобильном наборе модов.

usage: android-export.py [--cache DIR]   (кэш jar общий с depcheck, по умолчанию /tmp/depcache)
"""
import argparse, datetime, hashlib, json, os, shutil, subprocess, sys, tempfile, tomllib, urllib.parse, urllib.request, zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "dist", "android", "ZhivoyMir-Android.mrpack")
DEPCHECK = os.path.join(os.path.dirname(ROOT), "tools", "depcheck.py")

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


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--cache", default="/tmp/depcache")
    args = ap.parse_args()
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
    overrides["config/voicechat/voicechat-client.properties"] = (
        "".join(f"{k}={v}\n" for k, v in VOICECHAT_CLIENT.items())).encode()

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
