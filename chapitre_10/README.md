# Chapitre 10 — Intégration des Bases de Données

## Objectifs du chapitre

Connecter une application Java Swing à une base de données **MySQL** via **JDBC** :
établir la connexion, exécuter des requêtes SQL, et gérer les transactions.

## Contenu du cours

### 10.1 Connexion à la base de données

**Architecture JDBC :**

```
Application Java (Swing)
         │
         │  java.sql.DriverManager
         ▼
      JDBC API
         │
         │  mysql-connector-j (dépendance Maven)
         ▼
    Serveur MySQL
  (conteneur Docker)
```

**Dépendance Maven à ajouter dans `pom.xml` :**

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

**Établir la connexion :**

```java
import java.sql.*;

public class ConnexionDB {

    // Format : jdbc:mysql://hôte:port/nom_base
    private static final String URL      = "jdbc:mysql://db:3306/mon_app_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "rootpassword";

    public static Connection getConnexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Initialiser la structure de la base si elle n'existe pas
    public static void initialiserBase(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS etudiants (" +
                     "  matricule         VARCHAR(20) PRIMARY KEY, " +
                     "  nom               VARCHAR(50), " +
                     "  prenoms           VARCHAR(100), " +
                     "  genre             VARCHAR(10), " +
                     "  date_de_naissance DATE" +
                     ")";
        conn.createStatement().executeUpdate(sql);
    }
}
```

> En développement local sans Docker, remplacer `db` par `localhost`.

### 10.2 Exécution de requêtes SQL

**Lire des données (`SELECT`) :**

```java
public List<Etudiant> lireTousLesEtudiants() throws SQLException {
    List<Etudiant> liste = new ArrayList<>();
    String sql = "SELECT * FROM etudiants ORDER BY nom";

    try (Connection conn = ConnexionDB.getConnexion();
         Statement stmt = conn.createStatement();
         ResultSet rs   = stmt.executeQuery(sql)) {

        while (rs.next()) {
            liste.add(new Etudiant(
                rs.getString("matricule"),
                rs.getString("nom"),
                rs.getString("prenoms"),
                rs.getString("genre"),
                rs.getString("date_de_naissance")
            ));
        }
    }
    return liste;
}
```

**Insérer des données (`INSERT`) avec `PreparedStatement` :**

```java
public void ajouterEtudiant(Etudiant e) throws SQLException {
    String sql = "INSERT INTO etudiants " +
                 "(matricule, nom, prenoms, genre, date_de_naissance) " +
                 "VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = ConnexionDB.getConnexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, e.getMatricule());
        pstmt.setString(2, e.getNom());
        pstmt.setString(3, e.getPrenoms());
        pstmt.setString(4, e.getGenre());
        pstmt.setString(5, e.getDateDeNaissance());

        int lignesInseres = pstmt.executeUpdate();
        System.out.println(lignesInseres + " étudiant(s) ajouté(s)");
    }
}
```

**Mettre à jour des données (`UPDATE`) :**

```java
public void mettreAJourEtudiant(Etudiant e) throws SQLException {
    String sql = "UPDATE etudiants SET nom=?, prenoms=?, genre=?, " +
                 "date_de_naissance=? WHERE matricule=?";

    try (Connection conn = ConnexionDB.getConnexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, e.getNom());
        pstmt.setString(2, e.getPrenoms());
        pstmt.setString(3, e.getGenre());
        pstmt.setString(4, e.getDateDeNaissance());
        pstmt.setString(5, e.getMatricule());  // clé primaire dans le WHERE
        pstmt.executeUpdate();
    }
}
```

**Supprimer des données (`DELETE`) :**

```java
public void supprimerEtudiant(String matricule) throws SQLException {
    String sql = "DELETE FROM etudiants WHERE matricule = ?";

    try (Connection conn = ConnexionDB.getConnexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, matricule);
        pstmt.executeUpdate();
    }
}
```

> **Toujours** utiliser `PreparedStatement` avec des `?` plutôt que la concaténation
> de chaînes : cela protège contre les **injections SQL**.

### 10.3 Gestion des transactions

Une **transaction** garantit que plusieurs opérations SQL sont exécutées de façon
atomique : soit toutes réussissent, soit aucune n'est appliquée.

```java
public void transfertEtudiant(String matriculeSource, String nouvelleSection)
        throws SQLException {

    Connection conn = null;
    try {
        conn = ConnexionDB.getConnexion();

        // Désactiver l'auto-commit pour démarrer une transaction manuelle
        conn.setAutoCommit(false);

        // Opération 1
        PreparedStatement stmt1 = conn.prepareStatement(
            "UPDATE etudiants SET section = ? WHERE matricule = ?");
        stmt1.setString(1, nouvelleSection);
        stmt1.setString(2, matriculeSource);
        stmt1.executeUpdate();

        // Opération 2 (autre table, autre logique...)
        PreparedStatement stmt2 = conn.prepareStatement(
            "INSERT INTO historique_transferts (matricule, nouvelle_section) VALUES (?, ?)");
        stmt2.setString(1, matriculeSource);
        stmt2.setString(2, nouvelleSection);
        stmt2.executeUpdate();

        // Valider la transaction si tout s'est bien passé
        conn.commit();
        System.out.println("Transaction validée.");

    } catch (SQLException e) {
        // Annuler toutes les modifications en cas d'erreur
        if (conn != null) {
            conn.rollback();
            System.err.println("Transaction annulée : " + e.getMessage());
        }
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true); // rétablir le mode auto-commit
            conn.close();
        }
    }
}
```

**Niveaux d'isolation des transactions :**

| Niveau | Constante Java | Description |
|---|---|---|
| READ UNCOMMITTED | `TRANSACTION_READ_UNCOMMITTED` | Lit les données non commitées (risqué) |
| READ COMMITTED | `TRANSACTION_READ_COMMITTED` | Lit uniquement les données validées |
| REPEATABLE READ | `TRANSACTION_REPEATABLE_READ` | Même lecture durant toute la transaction |
| SERIALIZABLE | `TRANSACTION_SERIALIZABLE` | Transactions séquentielles (plus sûr, plus lent) |

## Points clés à retenir

- `PreparedStatement` est **obligatoire** pour toute requête avec des paramètres
- `try-with-resources` ferme automatiquement la connexion, même en cas d'exception
- Une transaction : `setAutoCommit(false)` → opérations → `commit()` ou `rollback()`
- L'URL de connexion change selon l'environnement (`db` en Docker, `localhost` en local)
