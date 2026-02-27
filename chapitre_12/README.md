# Chapitre 12 — TD : Intégration d'une Base de Données MySQL

## Objectifs du chapitre

Finaliser l'intégration complète de **MySQL** dans l'application Java Swing :
configurer la base de données, connecter l'application, et maîtriser les transactions.

> Ce chapitre est entièrement pratique (TD).
> Il finalise le projet en intégrant tous les éléments vus dans le cours.

## Contenu du TD

### 12.1 Configuration de la base de données

**Démarrer MySQL avec Docker Compose :**

```bash
# Démarrer uniquement le service MySQL
docker compose up -d db

# Vérifier que MySQL est démarré
docker compose ps

# Se connecter au shell MySQL
docker compose exec db mysql -u root -prootpassword mon_app_db
```

**Scripts SQL d'initialisation :**

```sql
-- Création de la base de données
CREATE DATABASE IF NOT EXISTS mon_app_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE mon_app_db;

-- Table principale
CREATE TABLE IF NOT EXISTS etudiants (
    matricule         VARCHAR(20)  PRIMARY KEY,
    nom               VARCHAR(50)  NOT NULL,
    prenoms           VARCHAR(100) NOT NULL,
    genre             VARCHAR(10)  DEFAULT 'Non précisé',
    date_de_naissance DATE,
    date_inscription  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Données de test
INSERT INTO etudiants (matricule, nom, prenoms, genre, date_de_naissance) VALUES
  ('E001', 'Dupont',  'Alice',   'Féminin',   '2003-05-15'),
  ('E002', 'Martin',  'Bob',     'Masculin',  '2002-11-22'),
  ('E003', 'Bernard', 'Charlie', 'Masculin',  '2003-08-10');
```

**Monter automatiquement le script d'initialisation :**

```yaml
# Dans docker-compose.yml
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mon_app_db
    volumes:
      - donnees_mysql:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql  # exécuté au premier démarrage
```

**Variables d'environnement pour éviter les secrets dans le code :**

```yaml
# docker-compose.yml avec variables d'environnement
services:
  db:
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
```

```bash
# Fichier .env (ne pas committer dans Git !)
MYSQL_ROOT_PASSWORD=motdepassesecret
MYSQL_DATABASE=mon_app_db
```

```gitignore
# .gitignore
.env
```

### 12.2 Intégration avec l'application Java

**Configurer la connexion depuis des variables d'environnement :**

```java
public class ConnexionDB {

    // Lire les paramètres depuis les variables d'environnement
    // avec des valeurs par défaut pour le développement local
    private static final String HOST = System.getenv()
        .getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv()
        .getOrDefault("DB_PORT", "3306");
    private static final String BASE = System.getenv()
        .getOrDefault("DB_NAME", "mon_app_db");
    private static final String USER = System.getenv()
        .getOrDefault("DB_USER", "root");
    private static final String PASS = System.getenv()
        .getOrDefault("DB_PASSWORD", "rootpassword");

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + BASE;

    public static Connection getConnexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
```

**Mettre à jour docker-compose.yml :**

```yaml
services:
  app:
    build: .
    depends_on:
      db:
        condition: service_healthy
    environment:
      - DB_HOST=db
      - DB_PORT=3306
      - DB_NAME=mon_app_db
      - DB_USER=root
      - DB_PASSWORD=${MYSQL_ROOT_PASSWORD}
    networks:
      - reseau-app

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-mon_app_db}
    volumes:
      - donnees_mysql:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost",
             "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - reseau-app

volumes:
  donnees_mysql:

networks:
  reseau-app:
```

**Tester la connexion depuis l'application :**

```java
// Méthode à appeler au démarrage pour vérifier la connexion
public static boolean testerConnexion() {
    try (Connection conn = ConnexionDB.getConnexion()) {
        System.out.println("Connexion à MySQL réussie !");
        return true;
    } catch (SQLException e) {
        System.err.println("Impossible de se connecter à MySQL : " + e.getMessage());
        return false;
    }
}
```

### 12.3 Utilisation des transactions

**Cas d'usage : mise à jour atomique de plusieurs tables :**

```java
public void inscrireEtudiant(Etudiant etudiant, String codeFormation)
        throws SQLException {

    Connection conn = null;
    try {
        conn = ConnexionDB.getConnexion();
        conn.setAutoCommit(false);  // début de la transaction

        // 1. Insérer l'étudiant
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO etudiants (matricule, nom, prenoms, genre, date_de_naissance) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenoms());
            ps.setString(4, etudiant.getGenre());
            ps.setString(5, etudiant.getDateDeNaissance());
            ps.executeUpdate();
        }

        // 2. Enregistrer l'inscription dans une autre table
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO inscriptions (matricule, code_formation, date_inscription) " +
                "VALUES (?, ?, NOW())")) {
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, codeFormation);
            ps.executeUpdate();
        }

        conn.commit();  // valider les deux insertions ensemble
        System.out.println("Inscription enregistrée avec succès.");

    } catch (SQLException e) {
        if (conn != null) conn.rollback();  // annuler en cas d'erreur
        System.err.println("Échec de l'inscription : " + e.getMessage());
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

**Vérifier les données en base depuis le terminal Docker :**

```bash
# Ouvrir le client MySQL dans le conteneur
docker compose exec db mysql -u root -prootpassword mon_app_db

# Requêtes de vérification
SELECT COUNT(*) AS total FROM etudiants;
SELECT * FROM etudiants ORDER BY nom;
SELECT * FROM etudiants WHERE genre = 'Féminin';
DESCRIBE etudiants;
SHOW TABLES;
```

**Sauvegarder et restaurer la base de données :**

```bash
# Exporter (dump) la base de données
docker compose exec db mysqldump \
  -u root -prootpassword mon_app_db > sauvegarde.sql

# Restaurer la base de données
docker compose exec -T db mysql \
  -u root -prootpassword mon_app_db < sauvegarde.sql
```

## Récapitulatif du projet

À la fin de ce chapitre, votre application doit intégrer :

| Composant | Technologie | Chapitre |
|---|---|---|
| Interface graphique | Java Swing | 9, 11 |
| Modèle de données | Java + Lombok | 5, 6 |
| Accès base de données | JDBC + PreparedStatement | 10, 12 |
| Base de données | MySQL 8.0 | 10, 12 |
| Conteneurisation | Docker + Docker Compose | 4, 11 |
| Gestion du code | Git + GitHub | 3 |
| Pipeline CI/CD | Jenkins | 7 |
| Tests | JUnit 5 | 8 |

## Points clés à retenir

- Les secrets (mots de passe) ne doivent jamais être codés en dur ni commités dans Git
- Utiliser des variables d'environnement et un fichier `.env` (dans `.gitignore`)
- Le `healthcheck` Docker garantit que MySQL est prêt avant de lancer l'application
- Les transactions garantissent la cohérence des données en cas d'erreur
- `mysqldump` permet de sauvegarder et restaurer facilement la base de données
