# Chapitre 6 — Programmation Orientée Objet en Java

## Objectifs du chapitre

Maîtriser les principes fondamentaux de la **Programmation Orientée Objet (POO)**
en Java : classes, objets, encapsulation, héritage, polymorphisme, interfaces,
et gestion des packages et dépendances.

## Contenu du cours

### 6.1 Classes et objets en Java

Une **classe** est un plan de construction. Un **objet** est une instance de ce plan.

```java
// Définition d'une classe
public class Etudiant {

    // Attributs (champs)
    private String matricule;
    private String nom;
    private String prenoms;
    private String genre;

    // Constructeur
    public Etudiant(String matricule, String nom, String prenoms, String genre) {
        this.matricule = matricule;
        this.nom       = nom;
        this.prenoms   = prenoms;
        this.genre     = genre;
    }

    // Getters
    public String getMatricule() { return matricule; }
    public String getNom()       { return nom; }
    public String getPrenoms()   { return prenoms; }
    public String getGenre()     { return genre; }

    // Setter
    public void setNom(String nom) { this.nom = nom; }

    // Méthode métier
    public String getNomComplet() {
        return prenoms + " " + nom;
    }

    @Override
    public String toString() {
        return "Etudiant[" + matricule + " — " + getNomComplet() + "]";
    }
}

// Instanciation et utilisation
Etudiant e = new Etudiant("E001", "Dupont", "Alice", "Féminin");
System.out.println(e.getNomComplet());  // Alice Dupont
System.out.println(e);                  // Etudiant[E001 — Alice Dupont]
```

### 6.2 Encapsulation, héritage et polymorphisme

**Encapsulation** : cacher les données internes, n'exposer que ce qui est nécessaire.

```java
// Attributs privés → accès via getters/setters uniquement
private String matricule;
public String getMatricule() { return matricule; }
```

**Héritage** : une classe enfant réutilise et étend une classe parente.

```java
// Classe parente
public class Personne {
    protected String nom;
    protected String prenom;

    public Personne(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public String presenter() {
        return "Je m'appelle " + prenom + " " + nom;
    }
}

// Classe enfant — hérite de Personne
public class Etudiant extends Personne {
    private String matricule;

    public Etudiant(String nom, String prenom, String matricule) {
        super(nom, prenom);  // appel du constructeur parent
        this.matricule = matricule;
    }

    @Override
    public String presenter() {
        return super.presenter() + " — matricule : " + matricule;
    }
}
```

**Polymorphisme** : un même code peut traiter des objets de types différents.

```java
Personne p1 = new Personne("Martin", "Bob");
Personne p2 = new Etudiant("Dupont", "Alice", "E001");

// La méthode appelée dépend du type réel de l'objet (liaison dynamique)
System.out.println(p1.presenter());  // Je m'appelle Bob Martin
System.out.println(p2.presenter());  // Je m'appelle Alice Dupont — matricule : E001
```

### 6.3 Interfaces et classes abstraites

**Interface** : contrat que les classes doivent respecter.

```java
// Définition d'une interface
public interface Affichable {
    void afficher();                         // méthode abstraite (obligatoire)
    default String getType() { return "?"; } // méthode par défaut (optionnelle)
}

// Implémentation
public class Etudiant extends Personne implements Affichable {
    @Override
    public void afficher() {
        System.out.println(presenter());
    }
}
```

**Classe abstraite** : classe qui ne peut pas être instanciée directement.

```java
public abstract class Personne {
    protected String nom;

    // Méthode abstraite — les sous-classes DOIVENT l'implémenter
    public abstract String getRole();

    // Méthode concrète — héritée telle quelle
    public String getNom() { return nom; }
}

public class Etudiant extends Personne {
    @Override
    public String getRole() { return "Étudiant"; }
}
```

| Comparaison | Interface | Classe abstraite |
|---|---|---|
| Instanciable ? | Non | Non |
| Attributs d'état | Non (constantes seulement) | Oui |
| Héritage multiple | Oui (une classe peut implémenter plusieurs interfaces) | Non (une seule classe parente) |
| Usage | Définir un contrat / un comportement | Partager du code commun |

### 6.4 Gestion des packages et des dépendances en Java

**Packages** : organisation du code en espaces de noms.

```java
// Déclaration du package en haut du fichier
package com.monprojet.modele;

// Import d'une classe d'un autre package
import com.monprojet.dao.EtudiantDAO;
import java.util.ArrayList;
```

**Convention de nommage des packages :**

```
com.nomEntreprise.nomProjet.couche
com.monprojet.modele      ← classes de données (Etudiant, etc.)
com.monprojet.dao         ← accès base de données
com.monprojet.ui          ← interface graphique
```

**Gestion des dépendances avec Maven (`pom.xml`) :**

```xml
<dependencies>
    <!-- Pilote MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>

    <!-- Lombok — génération automatique de code -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**Lombok — réduire le code répétitif :**

```java
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data               // génère getters, setters, toString, equals, hashCode
@Builder            // génère un constructeur fluent (Builder pattern)
@AllArgsConstructor // constructeur avec tous les champs
@NoArgsConstructor  // constructeur sans argument
public class Etudiant {
    private String matricule;
    private String nom;
    private String prenoms;
    private String genre;
    private String dateDeNaissance;
}
```

## Points clés à retenir

- Les attributs sont **toujours privés** (encapsulation)
- `extends` pour l'héritage, `implements` pour les interfaces
- `@Override` signale la redéfinition d'une méthode parente
- Un package = un dossier dans l'arborescence du projet
- Maven télécharge automatiquement les dépendances depuis Maven Central
