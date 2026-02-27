# Chapitre 11 — TD : Développement d'une Application Windows (Swing)

## Objectifs du chapitre

Concevoir, implémenter et déployer une **application de bureau complète**
en Java Swing, connectée à une base de données MySQL, dans un environnement Docker.

> Ce chapitre est entièrement pratique (TD).
> Vous développez l'application CRUD de gestion des étudiants de bout en bout.

## Contenu du TD

### 11.1 Analyse des besoins

**Contexte :** Développer une application de gestion des étudiants pour l'établissement.

**Fonctionnalités requises :**

| Fonctionnalité | Description |
|---|---|
| Afficher la liste | Afficher tous les étudiants dans un tableau |
| Ajouter un étudiant | Saisir et enregistrer un nouvel étudiant |
| Supprimer un étudiant | Supprimer l'étudiant sélectionné |
| Actualiser | Recharger la liste depuis la base de données |

**Modèle de données :**

```sql
CREATE TABLE etudiants (
    matricule         VARCHAR(20) PRIMARY KEY,
    nom               VARCHAR(50)  NOT NULL,
    prenoms           VARCHAR(100) NOT NULL,
    genre             VARCHAR(10),
    date_de_naissance DATE
);
```

**Architecture de l'application :**

```
┌─────────────────────────────────┐
│       Interface Swing (UI)      │  ← Chapitre 9
│  JFrame + JTable + JTextField   │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│       Couche d'accès aux        │  ← Chapitre 10
│    données (JDBC + MySQL)       │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│     MySQL dans Docker Compose   │  ← Chapitre 4
└─────────────────────────────────┘
```

### 11.2 Implémentation

**Structure du projet :**

```
src/main/java/com/monprojet/
├── App.java              ← Point d'entrée (main) + interface Swing complète
└── (classes à créer)
    ├── Etudiant.java     ← Classe de données (Lombok @Data)
    └── EtudiantDAO.java  ← Accès base de données (CRUD)
```

**Classe de données `Etudiant.java` (avec Lombok) :**

```java
package com.monprojet;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Etudiant {
    private String matricule;
    private String nom;
    private String prenoms;
    private String genre;
    private String dateDeNaissance;
}
```

**Classe d'accès aux données `EtudiantDAO.java` :**

```java
package com.monprojet;

import java.sql.*;
import java.util.*;

public class EtudiantDAO {

    private static final String URL  = "jdbc:mysql://db:3306/mon_app_db";
    private static final String USER = "root";
    private static final String PASS = "rootpassword";

    public void initialiserBase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS etudiants (" +
                "  matricule VARCHAR(20) PRIMARY KEY, " +
                "  nom VARCHAR(50), prenoms VARCHAR(100), " +
                "  genre VARCHAR(10), date_de_naissance DATE)");
        }
    }

    public List<Etudiant> lireTous() throws SQLException {
        List<Etudiant> liste = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             ResultSet rs = conn.createStatement()
                               .executeQuery("SELECT * FROM etudiants")) {
            while (rs.next()) {
                liste.add(new Etudiant(
                    rs.getString("matricule"), rs.getString("nom"),
                    rs.getString("prenoms"),   rs.getString("genre"),
                    rs.getString("date_de_naissance")));
            }
        }
        return liste;
    }

    public void ajouter(Etudiant e) throws SQLException {
        String sql = "INSERT INTO etudiants VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getMatricule()); ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenoms());   ps.setString(4, e.getGenre());
            ps.setString(5, e.getDateDeNaissance());
            ps.executeUpdate();
        }
    }

    public void supprimer(String matricule) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM etudiants WHERE matricule = ?")) {
            ps.setString(1, matricule);
            ps.executeUpdate();
        }
    }
}
```

### 11.3 Déploiement

**`Dockerfile` pour l'application Swing :**

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/java-gui-mysql-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**`docker-compose.yml` pour l'ensemble de l'application :**

```yaml
services:
  app:
    build: .
    depends_on:
      db:
        condition: service_healthy
    environment:
      - DISPLAY=${DISPLAY}
    volumes:
      - /tmp/.X11-unix:/tmp/.X11-unix  # affichage graphique (Linux)
    networks:
      - reseau-app

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mon_app_db
    volumes:
      - donnees_mysql:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5
    networks:
      - reseau-app

volumes:
  donnees_mysql:

networks:
  reseau-app:
```

```bash
# Construire et démarrer toute l'application
mvn package -DskipTests
docker compose up -d --build

# Vérifier l'état des services
docker compose ps

# Consulter les logs de l'application
docker compose logs app

# Arrêter l'application
docker compose down
```

### 11.4 Gestion des mises à jour de l'application

**Workflow de mise à jour :**

```bash
# 1. Modifier le code source
# 2. Recompiler
mvn package -DskipTests

# 3. Reconstruire l'image Docker
docker compose build app

# 4. Redémarrer le conteneur applicatif
docker compose up -d --no-deps app

# 5. Vérifier que tout fonctionne
docker compose ps
docker compose logs app
```

**Versionner les images Docker :**

```bash
# Taguer une version stable
docker tag app-cours:latest app-cours:v1.0.0

# Lister les versions disponibles
docker images app-cours

# Revenir à une version précédente
docker tag app-cours:v0.9.0 app-cours:latest
docker compose up -d --no-deps app
```

## Points clés à retenir

- Séparer le code en couches : UI (Swing), données (Etudiant), accès DB (EtudiantDAO)
- `depends_on` + `healthcheck` garantit que MySQL est prêt avant de démarrer l'app
- `docker compose build` + `docker compose up -d --no-deps` pour mettre à jour sans tout redémarrer
- Versionner les images Docker permet de revenir à une version antérieure en cas de problème
