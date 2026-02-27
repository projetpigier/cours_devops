# Chapitre 5 — Programmation Impérative en Java

## Objectifs du chapitre

Maîtriser les fondements de la programmation **impérative en Java** :
variables, structures de contrôle, méthodes, et gestion des erreurs.

## Contenu du cours

### 5.1 Variables et types de données en Java

Java est un langage **fortement typé** : chaque variable a un type déclaré explicitement.

**Types primitifs :**

| Type | Taille | Exemple |
|---|---|---|
| `int` | 32 bits | `int age = 20;` |
| `long` | 64 bits | `long population = 8000000000L;` |
| `double` | 64 bits | `double prix = 9.99;` |
| `float` | 32 bits | `float taux = 3.14f;` |
| `boolean` | 1 bit | `boolean actif = true;` |
| `char` | 16 bits | `char lettre = 'A';` |

**Types référence :**

```java
String  nom     = "Alice";               // chaîne de caractères
int[]   notes   = {12, 15, 8, 18};      // tableau
ArrayList<String> liste = new ArrayList<>();  // liste dynamique
```

**Opérateurs :**

```java
// Arithmétiques
int somme   = 5 + 3;    // 8
int produit = 4 * 3;    // 12
int reste   = 10 % 3;   // 1 (modulo)

// Comparaison (retournent boolean)
boolean egal      = (5 == 5);   // true
boolean different = (5 != 3);   // true

// Logiques
boolean etLogique = (true && false); // false
boolean ouLogique = (true || false); // true
boolean negation  = !true;           // false
```

### 5.2 Structures de contrôle de flux

```java
// Condition if / else if / else
int note = 14;
if (note >= 16) {
    System.out.println("Très bien");
} else if (note >= 12) {
    System.out.println("Bien");
} else {
    System.out.println("Insuffisant");
}

// Switch expression (Java 14+)
String mention = switch (note / 2) {
    case 10, 9 -> "Très bien";
    case 8, 7  -> "Bien";
    default    -> "Insuffisant";
};

// Boucle for classique
for (int i = 0; i < 5; i++) {
    System.out.println("Tour " + i);
}

// Boucle for-each (parcours de collection)
int[] notes = {12, 15, 8, 18};
for (int n : notes) {
    System.out.println(n);
}

// Boucle while
int compteur = 0;
while (compteur < 3) {
    System.out.println("Compteur : " + compteur);
    compteur++;
}

// Boucle do-while (exécutée au moins une fois)
do {
    System.out.println("Au moins une fois");
    compteur++;
} while (compteur < 5);
```

### 5.3 Fonctions et méthodes en Java

```java
public class Calculatrice {

    // Méthode avec paramètres et valeur de retour
    public static int additionner(int a, int b) {
        return a + b;
    }

    // Surcharge de méthode (même nom, paramètres différents)
    public static double additionner(double a, double b) {
        return a + b;
    }

    // Méthode sans valeur de retour
    public static void afficher(String message) {
        System.out.println("[INFO] " + message);
    }

    // Méthode récursive
    public static int factorielle(int n) {
        if (n <= 1) return 1;
        return n * factorielle(n - 1);
    }

    public static void main(String[] args) {
        int resultat = additionner(3, 7);
        afficher("3 + 7 = " + resultat);
        afficher("5! = " + factorielle(5));
    }
}
```

### 5.4 Gestion des exceptions et des erreurs en Java

Les **exceptions** permettent de gérer les erreurs de façon structurée.

```java
// Bloc try-catch-finally
try {
    int[] tableau = new int[5];
    tableau[10] = 42;  // ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.err.println("Erreur : indice hors limites — " + e.getMessage());
} finally {
    // Toujours exécuté (même si exception)
    System.out.println("Bloc finally exécuté");
}

// Exceptions courantes
// NullPointerException    : accès sur un objet null
// NumberFormatException   : conversion de chaîne en nombre invalide
// SQLException            : erreur base de données

// Lancer une exception personnalisée
public static int diviser(int a, int b) {
    if (b == 0) {
        throw new IllegalArgumentException("Impossible de diviser par zéro");
    }
    return a / b;
}

// Try-with-resources (fermeture automatique des ressources)
try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
    // utilisation de la connexion
} catch (SQLException e) {
    e.printStackTrace();
}
// La connexion est fermée automatiquement
```

**Hiérarchie des exceptions Java :**

```
Throwable
├── Error (erreurs JVM — ne pas capturer)
└── Exception
    ├── RuntimeException (non vérifiées — optionnel de les capturer)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── IllegalArgumentException
    └── Exceptions vérifiées (checked — obligatoire de les gérer)
        └── SQLException, IOException, etc.
```

## Points clés à retenir

- Toujours déclarer le **type** d'une variable en Java
- Le bloc `finally` s'exécute toujours, même en cas d'exception
- Préférer **try-with-resources** pour les ressources à fermer (fichiers, connexions)
- Ne jamais capturer une exception silencieusement sans la traiter (`catch (Exception e) {}`)
