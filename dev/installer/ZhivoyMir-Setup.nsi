Unicode true
!include "MUI2.nsh"
!define MUI_ICON "files\ZhivoyMir.ico"
!define MUI_UNICON "files\ZhivoyMir.ico"
!include "nsDialogs.nsh"
!include "LogicLib.nsh"
!include "WordFunc.nsh"
!include "WinMessages.nsh"
!include "x64.nsh"

!define PACK_NAME "Живой мир — AI Colony Pack"
!define INSTANCE_ID "ZhivoyMir"

; Лаунчер и Java качаются при установке с официальных адресов и сверяются по SHA-256.
!define POLYMC_URL "https://github.com/PolyMC/PolyMC/releases/download/8.1/PolyMC-Windows-Portable-amd64-8.1.zip"
!define POLYMC_SHA256 "bd6be0f7371521dc5bb9622e6f77eb9dfc632d5bf22ecdb1f1e78a7fe3ee2a0a"
!define JRE_URL "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.13%2B9/OpenJDK21U-jre_x64_windows_hotspot_21.0.13_9.zip"
!define JRE_SHA256 "2332c0b4cff6bed722da3567c91d5e47568bcba863fa4945d0a8d6fc76158628"
!define JRE_DIR "jdk-21.0.13+9-jre"

Name "${PACK_NAME}"
OutFile "ZhivoyMir-Setup.exe"
InstallDir "$LOCALAPPDATA\ZhivoyMir"
RequestExecutionLevel user
ShowInstDetails show
BrandingText "Живой мир"

Var Mode        ; 0 — всё в одном (PolyMC + Java + сборка), 1 — Prism, 2 — уже установленный PolyMC
Var Target
Var RadioAio
Var RadioPrism
Var RadioPoly
Var DirEdit

Page custom PageSetup PageSetupLeave
!insertmacro MUI_PAGE_INSTFILES

!define MUI_FINISHPAGE_TITLE "Готово"
!define MUI_FINISHPAGE_TEXT "Запускайте игру ярлыком «Живой мир» на рабочем столе (или выберите сборку «Живой мир» в своём лаунчере).$\r$\n$\r$\nПри первом запуске лаунчер скачает Minecraft 1.21.1, NeoForge и моды сборки — это 5–10 минут. При первом запуске игра спросит твой ник — дальше ярлык будет запускать её сразу.$\r$\n$\r$\nВ главном меню игры нажмите «▶ Играть: Живой мир»."
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT "Запустить «Живой мир»"
!define MUI_FINISHPAGE_RUN_FUNCTION LaunchGame
!define MUI_PAGE_CUSTOMFUNCTION_SHOW FinishShow
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "Russian"

Function .onInit
  StrCpy $Mode 0
  StrCpy $Target "$INSTDIR"
FunctionEnd

; ---------- страница выбора ----------

Function PageSetup
  !insertmacro MUI_HEADER_TEXT "Установка «Живой мир»" "Как ставить и куда"

  nsDialogs::Create 1018
  Pop $0
  ${If} $0 == error
    Abort
  ${EndIf}

  ${NSD_CreateRadioButton} 0 0 100% 12u "Всё в одном (рекомендуется): лаунчер PolyMC + Java 21 + сборка"
  Pop $RadioAio
  ${NSD_AddStyle} $RadioAio ${WS_GROUP}
  ${NSD_OnClick} $RadioAio ModeChanged
  ${NSD_CreateLabel} 12u 13u -12u 20u "Портативно: всё лежит в одной папке, её можно перенести или записать на флешку. Скачается около 90 МБ."
  Pop $0

  ${NSD_CreateRadioButton} 0 38u 100% 12u "Добавить сборку в установленный Prism Launcher"
  Pop $RadioPrism
  ${NSD_OnClick} $RadioPrism ModeChanged

  ${NSD_CreateRadioButton} 0 52u 100% 12u "Добавить сборку в установленный PolyMC"
  Pop $RadioPoly
  ${NSD_OnClick} $RadioPoly ModeChanged

  ${NSD_CreateLabel} 0 74u 100% 10u "Папка:"
  Pop $0
  ${NSD_CreateText} 0 86u -70u 12u "$Target"
  Pop $DirEdit
  ${NSD_CreateBrowseButton} -66u 85u 66u 14u "Обзор…"
  Pop $0
  ${NSD_OnClick} $0 BrowseFolder

  ${NSD_CreateLabel} 0 106u 100% 30u "Для «Всё в одном» — пустая папка или папка прежней установки. Для Prism/PolyMC — их папка instances."
  Pop $0

  ${If} $Mode == 1
    ${NSD_Check} $RadioPrism
  ${ElseIf} $Mode == 2
    ${NSD_Check} $RadioPoly
  ${Else}
    ${NSD_Check} $RadioAio
  ${EndIf}

  nsDialogs::Show
FunctionEnd

Function ModeChanged
  Pop $0
  ${NSD_GetState} $RadioPrism $0
  ${NSD_GetState} $RadioPoly $1
  ${If} $0 == ${BST_CHECKED}
    StrCpy $Mode 1
    ${NSD_SetText} $DirEdit "$APPDATA\PrismLauncher\instances"
  ${ElseIf} $1 == ${BST_CHECKED}
    StrCpy $Mode 2
    ${NSD_SetText} $DirEdit "$APPDATA\PolyMC\instances"
  ${Else}
    StrCpy $Mode 0
    ${NSD_SetText} $DirEdit "$LOCALAPPDATA\ZhivoyMir"
  ${EndIf}
FunctionEnd

Function BrowseFolder
  Pop $0
  ${NSD_GetText} $DirEdit $1
  nsDialogs::SelectFolderDialog "Выберите папку" "$1"
  Pop $0
  ${If} $0 != error
    ${NSD_SetText} $DirEdit "$0"
  ${EndIf}
FunctionEnd

; Push dir, Call DirHasFiles → $0 = 1, если в папке есть что-то кроме . и ..
Function DirHasFiles
  Exch $R0
  Push $R1
  Push $R2
  StrCpy $R2 0
  FindFirst $R1 $0 "$R0\*.*"
  ${DoWhile} $0 != ""
    ${If} $0 != "."
    ${AndIf} $0 != ".."
      StrCpy $R2 1
      ${Break}
    ${EndIf}
    FindNext $R1 $0
  ${Loop}
  FindClose $R1
  StrCpy $0 $R2
  Pop $R2
  Pop $R1
  Pop $R0
FunctionEnd

Function PageSetupLeave
  ${NSD_GetText} $DirEdit $Target
  ${If} $Target == ""
    MessageBox MB_ICONEXCLAMATION "Выберите папку."
    Abort
  ${EndIf}

  ${If} $Mode == 0
    ; деинсталлятор удаляет папку целиком — поэтому чужую непустую папку не берём
    ${If} ${FileExists} "$Target\*.*"
    ${AndIfNot} ${FileExists} "$Target\polymc.exe"
      Push $Target
      Call DirHasFiles
      ${If} $0 == 1
        MessageBox MB_ICONEXCLAMATION "Папка не пустая. Выберите пустую папку или папку прежней установки «Живой мир»."
        Abort
      ${EndIf}
    ${EndIf}
  ${Else}
    ${IfNot} ${FileExists} "$Target\*.*"
      MessageBox MB_ICONEXCLAMATION "Папка не найдена: $Target$\r$\nЗапустите лаунчер хотя бы раз или укажите его папку instances вручную."
      Abort
    ${EndIf}
  ${EndIf}
FunctionEnd

; ---------- загрузка и распаковка ----------

; Push папка — ставит Java 21 (Temurin) в <папка>\java, если там нет ровно этой версии
Function InstallJava
  Exch $R7
  ${IfNot} ${FileExists} "$R7\java\zm-${JRE_DIR}"
    Push "${JRE_URL}"
    Push "$PLUGINSDIR\jre.zip"
    Push "${JRE_SHA256}"
    Call Download
    RMDir /r "$R7\${JRE_DIR}"
    Push "$PLUGINSDIR\jre.zip"
    Push "$R7"
    Call Extract
    RMDir /r "$R7\java"
    Rename "$R7\${JRE_DIR}" "$R7\java"
    FileOpen $0 "$R7\java\zm-${JRE_DIR}" w
    FileClose $0
  ${EndIf}
  Pop $R7
FunctionEnd

; Push url, Push file, Push sha256 — скачивает и сверяет хеш, при ошибке прерывает установку
Function Download
  Pop $R2
  Pop $R1
  Pop $R0
  DetailPrint "Скачиваю $R0"
  ${DisableX64FSRedirection}
  StrCpy $0 "error"
  ${If} ${FileExists} "$WINDIR\System32\curl.exe"
    nsExec::ExecToLog '"$WINDIR\System32\curl.exe" -fsSL --retry 3 --connect-timeout 30 -o "$R1" "$R0"'
    Pop $0
  ${EndIf}
  ${If} $0 != 0
    nsExec::ExecToLog `powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$$ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol='Tls12'; Invoke-WebRequest -UseBasicParsing -TimeoutSec 60 -Uri '$R0' -OutFile '$R1'"`
    Pop $0
  ${EndIf}
  ${If} $0 != 0
    ${EnableX64FSRedirection}
    MessageBox MB_ICONSTOP "Не удалось скачать:$\r$\n$R0$\r$\n$\r$\nПроверьте интернет и запустите установщик ещё раз."
    Abort
  ${EndIf}
  nsExec::ExecToLog `powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "if ((Get-FileHash -Algorithm SHA256 -LiteralPath '$R1').Hash -ne '$R2') { exit 3 }"`
  Pop $0
  ${EnableX64FSRedirection}
  ${If} $0 == 3
    MessageBox MB_ICONSTOP "Скачанный файл повреждён (не совпала контрольная сумма):$\r$\n$R0$\r$\n$\r$\nЗапустите установщик ещё раз."
    Abort
  ${EndIf}
FunctionEnd

; Push zip, Push dest
Function Extract
  Pop $R1
  Pop $R0
  DetailPrint "Распаковываю в $R1"
  CreateDirectory "$R1"
  ${DisableX64FSRedirection}
  StrCpy $0 "error"
  ${If} ${FileExists} "$WINDIR\System32\tar.exe"
    nsExec::ExecToLog '"$WINDIR\System32\tar.exe" -xf "$R0" -C "$R1"'
    Pop $0
  ${EndIf}
  ${If} $0 != 0
    nsExec::ExecToLog `powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force -LiteralPath '$R0' -DestinationPath '$R1'"`
    Pop $0
  ${EndIf}
  ${EnableX64FSRedirection}
  ${If} $0 != 0
    MessageBox MB_ICONSTOP "Не удалось распаковать $R0.$\r$\nЕсли лаунчер открыт — закройте его и запустите установщик ещё раз."
    Abort
  ${EndIf}
FunctionEnd

; ---------- установка ----------

Section "Install"
  InitPluginsDir

  ${If} $Mode == 0
    StrCpy $INSTDIR $Target
    CreateDirectory "$INSTDIR"

    Push "${POLYMC_URL}"
    Push "$PLUGINSDIR\polymc.zip"
    Push "${POLYMC_SHA256}"
    Call Download
    Push "$PLUGINSDIR\polymc.zip"
    Push "$INSTDIR"
    Call Extract

    Push "$INSTDIR"
    Call InstallJava

    ; настройки лаунчера: русский язык, Java из папки установки, без мастера первого запуска
    SetOutPath "$INSTDIR"
    ${IfNot} ${FileExists} "$INSTDIR\polymc.cfg"
      File "files\polymc.cfg"
    ${EndIf}
    ; окно запуска: при первом запуске спрашивает ник и создаёт автономный аккаунт
    File "files\ZhivoyMir.jar"
    File "files\ZhivoyMir.ico"

    SetOutPath "$INSTDIR\instances\${INSTANCE_ID}"
    File "/oname=instance.cfg" "files\instance-aio.cfg"
    File "/oname=mmc-pack.json" "files\mmc-pack-polymc.json"
    StrCpy $R9 "$INSTDIR\instances\${INSTANCE_ID}"

    SetOutPath "$INSTDIR"
    CreateShortCut "$DESKTOP\Живой мир.lnk" "$INSTDIR\java\bin\javaw.exe" '-jar "$INSTDIR\ZhivoyMir.jar"' "$INSTDIR\ZhivoyMir.ico" 0
    WriteUninstaller "$INSTDIR\Удалить Живой мир.exe"
  ${Else}
    StrCpy $R9 "$Target\${INSTANCE_ID}"
    SetOutPath "$R9"
    File "/oname=instance.cfg" "files\instance-ext.cfg"
    ${If} $Mode == 1
      File "/oname=mmc-pack.json" "files\mmc-pack-prism.json"
    ${Else}
      File "/oname=mmc-pack.json" "files\mmc-pack-polymc.json"
    ${EndIf}
    ; своя Java 21 в папке сборки: не зависим от того, какая Java есть у лаунчера и в системе
    Push "$R9"
    Call InstallJava
    ${WordReplace} "$R9\java\bin\javaw.exe" "\" "/" "+" $R8
    FileOpen $0 "$R9\instance.cfg" a
    FileSeek $0 0 END
    FileWrite $0 "$\r$\nOverrideJava=true$\r$\nOverrideJavaLocation=true$\r$\nJavaPath=$R8$\r$\n"
    FileClose $0
    StrCpy $INSTDIR "$R9"
    WriteUninstaller "$R9\Удалить Живой мир.exe"
  ${EndIf}

  SetOutPath "$R9\.minecraft"
  File "files\packwiz-installer-bootstrap.jar"
  File "files\README.txt"
  ; список серверов игрока не перезаписываем
  SetOverwrite off
  File "files\servers.dat"
  SetOverwrite on
SectionEnd

Function FinishShow
  ${If} $Mode != 0
    SendMessage $mui.FinishPage.Run ${BM_SETCHECK} ${BST_UNCHECKED} 0
    ShowWindow $mui.FinishPage.Run ${SW_HIDE}
  ${EndIf}
FunctionEnd

Function LaunchGame
  ${If} $Mode == 0
    SetOutPath "$INSTDIR"
    Exec '"$INSTDIR\java\bin\javaw.exe" -jar "$INSTDIR\ZhivoyMir.jar"'
  ${EndIf}
FunctionEnd

; ---------- удаление ----------
; Деинсталлятор лежит в удаляемой папке: корень «Всё в одном» или папка сборки в instances.

Section "Uninstall"
  MessageBox MB_YESNO|MB_ICONQUESTION "Удалить папку$\r$\n$INSTDIR$\r$\nвместе с мирами, настройками и скриншотами?" IDYES +2
  Abort
  Delete "$DESKTOP\Живой мир.lnk"
  RMDir /r "$INSTDIR"
SectionEnd
