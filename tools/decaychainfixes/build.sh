#!/bin/sh
# Builds mods/decaychainfixes-<version>.jar. Needs a JDK (javac --release 8) and the jars named below in $JARS.
# Usage: JARS=/path/to/jars sh tools/decaychainfixes/build.sh
# mc-srg-dump.jar holds the Minecraft/Forge classes with SRG names, as Forge loads them at runtime: start a 1.12.2 Forge
# server of the pack once with -Dlegacy.debugClassLoadingSave=true, then convert its CLASSLOADER_TEMP folder with
# dumptool/StripDump.java (java -cp asm-9.x.jar;. StripDump CLASSLOADER_TEMP mc-srg-dump.jar).
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
VERSION=1.0.0
OUT="$HERE/../../mods/decaychainfixes-$VERSION.jar"
SEP=:; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';'; JARS=$(cygpath -w "$JARS");; esac
CP="$JARS/mc-srg-dump.jar$SEP$JARS/!mixinbooter-11.17.jar$SEP$JARS/Immersive Vehicles-1.12.2-24.0.0.jar$SEP$JARS/EpicSiegeMod-13.169.jar$SEP$JARS/techguns-2.2.0.1.jar$SEP$JARS/Pam's HarvestCraft 1.12.2zg.jar$SEP$JARS/lostcities-1.12-2.0.22.jar$SEP$JARS/firstaid-1.6.22.jar$SEP$JARS/athenaeum-1.12.2-1.19.6.jar$SEP$JARS/coroutil-1.12.1-1.2.37.jar$SEP$JARS/NTM-CE-1.12.2-2.5.0.4.jar$SEP$JARS/SRParasites-1.10.9.jar$SEP$JARS/Reskillable-1.12.2-1.13.0.jar$SEP$JARS/log4j-api.jar$SEP$JARS/forge-1.12.2-14.23.5.2864.jar"
BUILD="$HERE/build"; rm -rf "$BUILD"; mkdir -p "$BUILD/classes"
javac --release 8 -nowarn -proc:none -cp "$CP" -d "$BUILD/classes" $(find "$HERE/src/main/java" -name '*.java')
cp "$HERE/src/main/resources/mixins.decaychainfixes.json" "$HERE/src/main/resources/mcmod.info" "$BUILD/classes/"
jar cfm "$OUT" "$HERE/src/main/resources/META-INF/MANIFEST.MF" -C "$BUILD/classes" .
rm -rf "$BUILD"
echo "built $OUT"
