#!/bin/bash

# Script pour télécharger et installer le Maven Wrapper
# Usage: chmod +x install-maven-wrapper.sh && ./install-maven-wrapper.sh

set -e

MAVEN_VERSION="3.9.6"
WRAPPER_VERSION="3.2.0"

echo "📦 Installation du Maven Wrapper..."

# Créer le dossier .mvn/wrapper s'il n'existe pas
mkdir -p .mvn/wrapper

# Télécharger maven-wrapper.jar
echo "⬇️  Téléchargement de maven-wrapper.jar..."
curl -o .mvn/wrapper/maven-wrapper.jar \
  https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/${WRAPPER_VERSION}/maven-wrapper-${WRAPPER_VERSION}.jar

# Créer maven-wrapper.properties
echo "📝 Création de maven-wrapper.properties..."
cat > .mvn/wrapper/maven-wrapper.properties << EOF
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/${WRAPPER_VERSION}/maven-wrapper-${WRAPPER_VERSION}.jar
EOF

# Télécharger mvnw (Unix/Mac)
echo "⬇️  Téléchargement de mvnw..."
curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw
chmod +x mvnw

# Télécharger mvnw.cmd (Windows)
echo "⬇️  Téléchargement de mvnw.cmd..."
curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd

echo "✅ Maven Wrapper installé avec succès!"
echo ""
echo "🚀 Vous pouvez maintenant utiliser:"
echo "   ./mvnw clean install"
echo "   ./mvnw spring-boot:run"
