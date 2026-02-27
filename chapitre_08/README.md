# Chapitre 8 — TD : Tests Unitaires

## Objectifs du chapitre

Comprendre et pratiquer les **tests unitaires** en Java avec **JUnit 5** :
écrire des tests fiables, les organiser correctement, et suivre les bonnes pratiques.

> Ce chapitre est entièrement pratique (TD).

## Contenu du TD

### 8.1 Introduction aux tests unitaires

Un **test unitaire** vérifie qu'une méthode ou une classe se comporte
exactement comme prévu, de façon isolée et répétable.

**Pourquoi tester ?**
- Détecter les bugs **tôt** (coût x10 moins cher qu'en production)
- Permettre le **refactoring** sans régression
- Servir de **documentation vivante** du code
- Base indispensable d'un pipeline CI/CD

**Ajouter JUnit 5 dans `pom.xml` :**

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

### 8.2 Utilisation de JUnit

**Structure d'un test JUnit 5 :**

```java
package com.monprojet;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatriceTest {

    private Calculatrice calc;

    @BeforeEach
    void setUp() {
        // Exécuté avant chaque test
        calc = new Calculatrice();
    }

    @AfterEach
    void tearDown() {
        // Exécuté après chaque test
        calc = null;
    }

    @Test
    void additionner_deuxEntiers_retourneLaSomme() {
        // Arrange — préparer les données
        int a = 3, b = 7;

        // Act — appeler la méthode
        int resultat = calc.additionner(a, b);

        // Assert — vérifier le résultat
        assertEquals(10, resultat, "3 + 7 doit valoir 10");
    }

    @Test
    void diviser_parZero_leveUneException() {
        assertThrows(IllegalArgumentException.class, () -> {
            calc.diviser(10, 0);
        });
    }

    @Test
    @DisplayName("La division de nombres négatifs doit fonctionner")
    void diviser_nombresNegatifs_retourneResultatCorrect() {
        assertEquals(-2, calc.diviser(-10, 5));
    }

    @Test
    @Disabled("À implémenter ultérieurement")
    void testEnCours() {
        // Ce test est ignoré
    }
}
```

**Principales assertions JUnit 5 :**

| Assertion | Description |
|---|---|
| `assertEquals(attendu, reel)` | Vérifie l'égalité |
| `assertNotEquals(val1, val2)` | Vérifie l'inégalité |
| `assertTrue(condition)` | Vérifie qu'une condition est vraie |
| `assertFalse(condition)` | Vérifie qu'une condition est fausse |
| `assertNull(objet)` | Vérifie qu'un objet est null |
| `assertNotNull(objet)` | Vérifie qu'un objet n'est pas null |
| `assertThrows(Exception.class, () -> ...)` | Vérifie qu'une exception est levée |
| `assertAll(...)` | Regroupe plusieurs assertions |

**Annotations JUnit 5 :**

| Annotation | Description |
|---|---|
| `@Test` | Méthode de test |
| `@BeforeEach` | Exécuté avant chaque test |
| `@AfterEach` | Exécuté après chaque test |
| `@BeforeAll` | Exécuté une seule fois avant tous les tests |
| `@AfterAll` | Exécuté une seule fois après tous les tests |
| `@DisplayName` | Nom affiché dans les rapports |
| `@Disabled` | Désactive temporairement un test |
| `@ParameterizedTest` | Test avec plusieurs jeux de données |

**Tests paramétrés :**

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 5, 8, 13})
void nombre_positif_retourneVrai(int nombre) {
    assertTrue(nombre > 0);
}

@ParameterizedTest
@CsvSource({
    "3, 7, 10",
    "0, 0, 0",
    "-5, 5, 0"
})
void additionner_diversCas(int a, int b, int attendu) {
    assertEquals(attendu, new Calculatrice().additionner(a, b));
}
```

**Exécuter les tests avec Maven :**

```bash
# Exécuter tous les tests
mvn test

# Exécuter une seule classe de tests
mvn test -Dtest=CalculatriceTest

# Exécuter un seul test
mvn test -Dtest=CalculatriceTest#additionner_deuxEntiers_retourneLaSomme

# Rapport HTML des tests (dans target/surefire-reports/)
mvn surefire-report:report
```

### 8.3 Bonnes pratiques de tests unitaires

**Règle FIRST :**

| Lettre | Signification |
|---|---|
| **F**ast | Les tests doivent être rapides |
| **I**solated | Chaque test est indépendant des autres |
| **R**epeatable | Le résultat est toujours le même |
| **S**elf-validating | Succès ou échec sans intervention manuelle |
| **T**imely | Écrits au moment du développement |

**Convention de nommage :**
```
nomMethode_scenarioTeste_resultatAttendu
additionner_deuxEntiers_retourneLaSomme
diviser_parZero_leveException
```

**Ce qu'il faut éviter :**
- Un test qui teste plusieurs fonctionnalités à la fois
- Des tests qui dépendent de l'ordre d'exécution
- Des tests qui accèdent à une base de données réelle (préférer des mocks)
- Des tests trop couplés à l'implémentation interne

## Points clés à retenir

- Un test = **une seule** chose à vérifier
- Toujours suivre le pattern **Arrange / Act / Assert**
- Les tests sont dans `src/test/java/`, dans le même package que le code testé
- Les tests sont exécutés automatiquement par Jenkins dans le pipeline CI/CD
