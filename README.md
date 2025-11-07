# 💰 FinTrack

**Plateforme de gestion financière personnelle**

Une application web complète permettant de suivre, analyser et optimiser vos finances personnelles avec des outils de budgétisation, de visualisation et d'alertes intelligentes.

---

## 🎯 Objectifs

FinTrack vous permet de :

- ✅ **Suivre** vos revenus et dépenses en temps réel
- 📊 **Catégoriser** automatiquement vos transactions
- 📈 **Visualiser** vos statistiques financières (graphiques, tendances, soldes mensuels)
- 🎯 **Planifier** des budgets et objectifs d'épargne
- 🔔 **Recevoir** des alertes personnalisées (dépassement de budget, revenus manquants, etc.)

---

## 🛠️ Stack Technique

### Backend (Java / Spring Boot)

- **Spring Boot 3.3.5** - Framework pour les API REST
- **Spring Security + JWT** - Authentification et gestion des rôles (utilisateur/admin)
- **Hibernate / JPA** - Couche ORM
- **Spring Data JPA** - Repositories et requêtes
- **PostgreSQL** - Base de données relationnelle
- **JUnit & Mockito** - Tests unitaires
- **Lombok** - Réduction du boilerplate

### Frontend (Angular)

- **Angular 17+** - Framework frontend avec Reactive Forms, Routing, Guards
- **Angular Material** - Design System et composants UI
- **NgRx** ou **RxJS** - Gestion de l'état applicatif
- **Chart.js** ou **ngx-charts** - Visualisations et graphiques
- **HTTP Client** - Appels API sécurisés via JWT

---

## 🚀 Démarrage Rapide

### Prérequis

- **Java 17+** (OpenJDK ou Oracle JDK)
- **Node.js 18+** et **npm** (pour Angular)
- **Docker & Docker Compose** (pour PostgreSQL)
- **Git**

---

### Démarrer l'infrastructure (PostgreSQL)

```bash
docker-compose up -d
```

### Lancer le backend

```bash
cd backend
chmod +x install-maven-wrapper.sh
./install-maven-wrapper.sh
./mvnw spring-boot:run
```
