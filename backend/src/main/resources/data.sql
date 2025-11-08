-- =====================================================
-- Script de données de test pour FinTrack
-- =====================================================
-- Note: Ce fichier est exécuté automatiquement au démarrage de l'application
-- si spring.jpa.hibernate.ddl-auto=create ou create-drop
-- Pour l'utiliser en mode 'update', commentez ou supprimez ce fichier

-- =====================================================
-- 1. UTILISATEURS DE TEST
-- =====================================================
-- Mot de passe: "password123" encodé en BCrypt
-- Généré avec: BCryptPasswordEncoder().encode("password123")
INSERT INTO users (username, email, password, enabled, created_at, updated_at) VALUES
('demo_user', 'demo@fintrack.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('john_doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 2. RÔLES DES UTILISATEURS
-- =====================================================
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

-- =====================================================
-- 3. CATÉGORIES DE DÉPENSES
-- =====================================================
INSERT INTO categories (name, description, icon, color, type, user_id, is_default, created_at, updated_at) VALUES
-- Catégories de dépenses (EXPENSE)
('Alimentation', 'Courses, restaurants, cafés', 'shopping-cart', '#FF6B6B', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Transport', 'Essence, transports en commun, parking', 'car', '#4ECDC4', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Logement', 'Loyer, charges, électricité, eau', 'home', '#95E1D3', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Loisirs', 'Cinéma, sorties, hobbies', 'film', '#F38181', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Santé', 'Médecin, pharmacie, assurance santé', 'heart', '#AA96DA', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Éducation', 'Livres, formations, cours', 'book', '#FCBAD3', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Shopping', 'Vêtements, accessoires, gadgets', 'shopping-bag', '#FFD93D', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Abonnements', 'Netflix, Spotify, salle de sport', 'repeat', '#6BCF7F', 'EXPENSE', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Catégories de revenus (INCOME)
('Salaire', 'Salaire mensuel', 'dollar-sign', '#4CAF50', 'INCOME', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Freelance', 'Missions freelance', 'briefcase', '#2196F3', 'INCOME', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Investissements', 'Dividendes, intérêts', 'trending-up', '#9C27B0', 'INCOME', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Autres revenus', 'Cadeaux, remboursements', 'gift', '#FF9800', 'INCOME', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 4. TRANSACTIONS
-- =====================================================
INSERT INTO transactions (amount, description, notes, transaction_date, type, user_id, category_id, payment_method, merchant, is_recurring, created_at, updated_at) VALUES
-- Revenus
(3500.00, 'Salaire Novembre 2024', 'Salaire mensuel', '2024-11-01', 'INCOME', 1, 9, 'Virement', 'Entreprise ABC', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(800.00, 'Mission Freelance', 'Développement site web', '2024-11-05', 'INCOME', 1, 10, 'Virement', 'Client XYZ', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Dépenses fixes
(1200.00, 'Loyer Novembre', 'Loyer mensuel appartement', '2024-11-01', 'EXPENSE', 1, 3, 'Virement', 'Agence Immobilière', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(150.00, 'Électricité + Eau', 'Factures mensuelles', '2024-11-03', 'EXPENSE', 1, 3, 'Prélèvement', 'EDF / Veolia', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(49.99, 'Abonnements', 'Netflix + Spotify + Salle de sport', '2024-11-05', 'EXPENSE', 1, 8, 'Carte Bancaire', 'Divers', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Dépenses courantes
(85.50, 'Courses Carrefour', 'Courses hebdomadaires', '2024-11-02', 'EXPENSE', 1, 1, 'Carte Bancaire', 'Carrefour', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(45.00, 'Restaurant', 'Déjeuner avec collègues', '2024-11-04', 'EXPENSE', 1, 1, 'Carte Bancaire', 'Le Bistrot', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(60.00, 'Essence', 'Plein d''essence', '2024-11-06', 'EXPENSE', 1, 2, 'Carte Bancaire', 'Total Energies', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25.00, 'Cinéma', 'Séance + popcorn', '2024-11-07', 'EXPENSE', 1, 4, 'Espèces', 'UGC', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(120.00, 'Vêtements', 'Pull d''hiver', '2024-11-08', 'EXPENSE', 1, 7, 'Carte Bancaire', 'Zara', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(35.00, 'Pharmacie', 'Médicaments', '2024-11-09', 'EXPENSE', 1, 5, 'Carte Bancaire', 'Pharmacie Centrale', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(92.30, 'Courses Auchan', 'Courses hebdomadaires', '2024-11-10', 'EXPENSE', 1, 1, 'Carte Bancaire', 'Auchan', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15.50, 'Café', 'Café + croissant', '2024-11-11', 'EXPENSE', 1, 1, 'Espèces', 'Café de la Gare', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 5. BUDGETS
-- =====================================================
INSERT INTO budgets (name, amount, spent_amount, start_date, end_date, user_id, category_id, is_recurring, recurrence_frequency, alert_enabled, alert_threshold, is_active, created_at, updated_at) VALUES
('Budget Alimentation Novembre', 400.00, 238.30, '2024-11-01', '2024-11-30', 1, 1, true, 'MONTHLY', true, 80.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Budget Transport Novembre', 200.00, 60.00, '2024-11-01', '2024-11-30', 1, 2, true, 'MONTHLY', true, 80.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Budget Loisirs Novembre', 150.00, 25.00, '2024-11-01', '2024-11-30', 1, 4, true, 'MONTHLY', true, 75.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Budget Shopping Novembre', 250.00, 120.00, '2024-11-01', '2024-11-30', 1, 7, true, 'MONTHLY', true, 85.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Budget Santé Novembre', 100.00, 35.00, '2024-11-01', '2024-11-30', 1, 5, true, 'MONTHLY', true, 70.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 6. OBJECTIFS D'ÉPARGNE
-- =====================================================
INSERT INTO goals (name, description, target_amount, current_amount, target_date, status, priority, user_id, icon, color, is_archived, created_at, updated_at) VALUES
('Vacances d''été 2025', 'Voyage en Grèce pour 2 semaines', 3000.00, 850.00, '2025-06-01', 'IN_PROGRESS', 'HIGH', 1, 'plane', '#FF6B6B', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nouvelle voiture', 'Achat d''une voiture d''occasion', 15000.00, 3200.00, '2025-12-31', 'IN_PROGRESS', 'MEDIUM', 1, 'car', '#4ECDC4', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fonds d''urgence', 'Épargne de sécurité (6 mois de dépenses)', 10000.00, 5500.00, '2025-03-31', 'IN_PROGRESS', 'HIGH', 1, 'shield', '#95E1D3', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nouvel ordinateur', 'MacBook Pro pour le travail', 2500.00, 1800.00, '2025-01-31', 'IN_PROGRESS', 'MEDIUM', 1, 'laptop', '#F38181', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Formation en ligne', 'Cours de développement avancé', 500.00, 500.00, '2024-10-31', 'COMPLETED', 'LOW', 1, 'book', '#AA96DA', false, CURRENT_TIMESTAMP, '2024-10-31 10:00:00');

-- =====================================================
-- RÉSUMÉ DES DONNÉES DE TEST
-- =====================================================
-- Utilisateurs: 2 (demo_user avec ROLE_ADMIN, john_doe avec ROLE_USER)
-- Catégories: 12 (8 dépenses + 4 revenus)
-- Transactions: 13 (2 revenus + 11 dépenses)
-- Budgets: 5 (tous actifs pour novembre 2024)
-- Objectifs: 5 (4 en cours + 1 complété)
--
-- Connexion:
-- Username: demo_user
-- Password: password123
-- =====================================================
