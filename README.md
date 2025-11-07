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

- **Spring Boot** - Framework pour les API REST
- **Spring Security + JWT** - Authentification et gestion des rôles (utilisateur/admin)
- **Hibernate / JPA** - Couche ORM
- **Spring Data REST** - Simplification des endpoints CRUD
- **PostgreSQL** (ou MySQL) - Base de données relationnelle
- **JUnit & Mockito** - Tests unitaires

### Frontend (Angular)

- **Angular 17+** - Framework frontend avec Reactive Forms, Routing, Guards
- **Angular Material** - Design System et composants UI
- **NgRx** ou **RxJS** - Gestion de l'état applicatif
- **Chart.js** ou **ngx-charts** - Visualisations et graphiques
- **HTTP Client** - Appels API sécurisés via JWT