#!/bin/bash
# Сборка окна запуска: build/ZhivoyMir.jar (Java 21, без зависимостей).
set -e
cd "$(dirname "$0")"
rm -rf build/classes; mkdir -p build/classes
javac --release 21 -encoding UTF-8 -d build/classes $(find src -name '*.java')
cp res/* build/classes/
printf 'Main-Class: ru.zhivoymir.launcher.Main\n' > build/manifest.txt
jar cfm build/ZhivoyMir.jar build/manifest.txt -C build/classes .
ls -la build/ZhivoyMir.jar
