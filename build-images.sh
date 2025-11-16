#!/bin/bash

echo "🐳 Build des images Docker pour FinTrack"
echo "========================================="

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Vérifier que Docker est installé
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker n'est pas installé${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker détecté${NC}"
echo ""

# Créer le dossier pour les exports
mkdir -p docker-images

# 1. Build de l'image Backend (pour AMD64/x86_64 - Synology)
echo -e "${YELLOW}📦 Build de l'image Backend (Spring Boot) pour AMD64...${NC}"
docker build --platform linux/amd64 -t fintrack-backend:latest ./backend

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Image Backend créée avec succès${NC}"
    
    # Export de l'image Backend
    echo -e "${YELLOW}💾 Export de l'image Backend...${NC}"
    docker save -o docker-images/fintrack-backend.tar fintrack-backend:latest
    echo -e "${GREEN}✅ Image Backend exportée: docker-images/fintrack-backend.tar${NC}"
else
    echo -e "${RED}❌ Erreur lors du build de l'image Backend${NC}"
    exit 1
fi

echo ""

# 2. Build de l'image Frontend (pour AMD64/x86_64 - Synology)
echo -e "${YELLOW}📦 Build de l'image Frontend (Angular + Nginx) pour AMD64...${NC}"
docker build --platform linux/amd64 -t fintrack-frontend:latest ./frontend

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Image Frontend créée avec succès${NC}"
    
    # Export de l'image Frontend
    echo -e "${YELLOW}💾 Export de l'image Frontend...${NC}"
    docker save -o docker-images/fintrack-frontend.tar fintrack-frontend:latest
    echo -e "${GREEN}✅ Image Frontend exportée: docker-images/fintrack-frontend.tar${NC}"
else
    echo -e "${RED}❌ Erreur lors du build de l'image Frontend${NC}"
    exit 1
fi

echo ""

# 3. Pull de l'image PostgreSQL (pour AMD64/x86_64 - Synology)
echo -e "${YELLOW}📦 Téléchargement de l'image PostgreSQL pour AMD64...${NC}"
docker pull --platform linux/amd64 postgres:16-alpine

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Image PostgreSQL téléchargée${NC}"
    
    # Export de l'image PostgreSQL
    echo -e "${YELLOW}💾 Export de l'image PostgreSQL...${NC}"
    docker save -o docker-images/postgres-16-alpine.tar postgres:16-alpine
    echo -e "${GREEN}✅ Image PostgreSQL exportée: docker-images/postgres-16-alpine.tar${NC}"
else
    echo -e "${RED}❌ Erreur lors du téléchargement de PostgreSQL${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}✅ Toutes les images ont été créées !${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "📁 Les fichiers .tar sont dans le dossier: docker-images/"
echo ""
echo "Fichiers créés:"
ls -lh docker-images/*.tar
echo ""
echo -e "${YELLOW}📤 Prochaines étapes:${NC}"
echo "1. Transférer les fichiers .tar vers votre Synology"
echo "2. Importer les images dans Container Manager"
echo "3. Créer les conteneurs avec docker-compose.prod.yml"
echo ""
echo "Voir DEPLOY-SYNOLOGY.md pour les instructions détaillées"
