#!/bin/bash -eu

BASE=$(cd $(dirname $0) && pwd)

VERSION=`cat pom.xml | grep '<version>' | head -1 | tail -1 | sed 's_.*<version>\([^<]*\)</version>.*_\1_'`
POM_JAVA_VERSION=`grep '<source>' pom.xml | sed 's_.*<source>\([^<]*\)</source>.*_\1_'`

if [ -z "${JAVA_HOME:-}" ]; then
  if [ -x /usr/libexec/java_home ]; then
    # MacOS. Fails with error if required version not supported.
    FOUND_JAVA=`/usr/libexec/java_home -v ${POM_JAVA_VERSION}+`
  elif [ -x "$(which javac)" ]; then
    FOUND_JAVA=$(dirname $(dirname $(readlink -e $(which javac))))
  fi
  echo "JAVA_HOME not set, setting to ${FOUND_JAVA}"
else
  FOUND_JAVA=$JAVA_HOME
fi

CURR_JAVA_VERSION=`${FOUND_JAVA}/bin/java -fullversion 2>&1 | head -1 | awk -F\" '{print $2}'`
if [[ "${CURR_JAVA_VERSION}" < "${POM_JAVA_VERSION}" ]]; then
  echo "Build needs java ${POM_JAVA_VERSION}, but JAVA_HOME (${FOUND_JAVA}) is ${CURR_JAVA_VERSION}."
  exit -1
fi

echo "Building Version [${VERSION}]"

JAVA_HOME=${FOUND_JAVA} mvn -U -B clean package

JARS=$(find "$BASE" -name "*-$VERSION-selfcontained.jar" | sed -e 's/^/  /')

cat <<EOF

The following self-contained jars (and more) have been built:
$JARS
EOF
