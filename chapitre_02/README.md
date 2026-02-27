# Chapitre 2 — Présentation de l'Écosystème Java

## Objectifs du chapitre

Comprendre l'architecture de la plateforme Java, le rôle de la **JVM**,
et se familiariser avec les outils et bibliothèques incontournables de l'écosystème Java.

## Contenu du cours

### 2.1 Composants de base de l'écosystème Java

| Composant | Rôle |
|---|---|
| **JDK** (Java Development Kit) | Ensemble d'outils pour développer en Java (compilateur, débogueur…) |
| **JRE** (Java Runtime Environment) | Environnement d'exécution des programmes Java |
| **JVM** (Java Virtual Machine) | Machine virtuelle qui exécute le bytecode Java |
| **javac** | Le compilateur Java (transforme `.java` en `.class`) |
| **java** | La commande pour lancer une application Java |

```
Code source (.java)
       │
       │  javac (compilation)
       ▼
  Bytecode (.class)
       │
       │  JVM (interprétation / JIT)
       ▼
  Exécution sur l'OS
```

### 2.2 Bytecode Java et JVM

- Le **bytecode** est un format intermédiaire, indépendant de l'architecture matérielle
- La **JVM** traduit le bytecode en instructions natives pour le processeur
- Principe **WORA** : *Write Once, Run Anywhere* (le même bytecode tourne sur Windows, Linux, macOS)
- La compilation **JIT** (*Just-In-Time*) optimise les parties fréquemment exécutées

```bash
# Compiler un fichier source Java
javac App.java

# Exécuter le programme
java com.monprojet.App

# Lister le bytecode (usage avancé)
javap -c App.class
```

### 2.3 Outils de développement Java

| Outil | Description |
|---|---|
| **VS Code** + Extension Pack for Java | Éditeur utilisé dans ce cours (via Codespaces) |
| **IntelliJ IDEA** | IDE très utilisé en entreprise |
| **Eclipse** | IDE open-source populaire |
| **Maven** | Outil de build et de gestion des dépendances |
| **Gradle** | Alternative à Maven, plus flexible |
| **Git** | Gestion de version du code source |

Dans ce cours, nous utilisons **VS Code** dans **GitHub Codespaces** et **Maven** comme outil de build.

### 2.4 Principales bibliothèques et frameworks Java

| Domaine | Bibliothèque / Framework |
|---|---|
| Interface graphique | **Java Swing**, JavaFX |
| Accès base de données | **JDBC**, Hibernate, JPA |
| Tests unitaires | **JUnit 5**, Mockito |
| Réduction du boilerplate | **Lombok** |
| API REST | Spring Boot, Quarkus, Jakarta EE |
| Logging | Log4j, SLF4J |

Dans ce cours, nous utilisons principalement :
- **Java Swing** pour l'interface graphique
- **JDBC** avec le pilote `mysql-connector-j` pour la base de données
- **Lombok** pour simplifier les classes de données
- **JUnit** pour les tests unitaires

## Points clés à retenir

- La JVM rend Java portable : un seul bytecode, plusieurs systèmes d'exploitation
- Le JDK contient le JRE et les outils de développement
- Maven gère le cycle de vie du projet et les dépendances externes
- Lombok simplifie le code en générant automatiquement getters, setters et constructeurs
