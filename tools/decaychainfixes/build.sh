#!/bin/sh
# Builds mods/decaychainfixes-<version>.jar. Needs a JDK (javac --release 8) and the jars named below in $JARS.
# Usage: JARS=/path/to/jars sh tools/decaychainfixes/build.sh
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
VERSION=1.0.0
OUT="$HERE/../../mods/decaychainfixes-$VERSION.jar"
SEP=:; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';'; JARS=$(cygpath -w "$JARS");; esac
CP="$JARS/!mixinbooter-11.17.jar$SEP$JARS/Immersive Vehicles-1.12.2-24.0.0.jar$SEP$JARS/EpicSiegeMod-13.169.jar$SEP$JARS/techguns-2.2.0.1.jar$SEP$JARS/Pam's HarvestCraft 1.12.2zg.jar$SEP$JARS/forge-1.12.2-14.23.5.2864.jar"
BUILD="$HERE/build"; rm -rf "$BUILD"; mkdir -p "$BUILD/classes" "$BUILD/stubs"
javac --release 8 -nowarn -d "$BUILD/stubs" $(find "$HERE/src/stubs/java" -name '*.java')
STUBS="$BUILD/stubs"; [ "$SEP" = ';' ] && STUBS=$(cygpath -w "$STUBS")
CP="$CP$SEP$STUBS"
javac --release 8 -nowarn -proc:none -cp "$CP" -d "$BUILD/classes" $(find "$HERE/src/main/java" -name '*.java')
cp "$HERE/src/main/resources/mixins.decaychainfixes.json" "$HERE/src/main/resources/mcmod.info" "$BUILD/classes/"
jar cfm "$OUT" "$HERE/src/main/resources/META-INF/MANIFEST.MF" -C "$BUILD/classes" .
rm -rf "$BUILD"
echo "built $OUT"
