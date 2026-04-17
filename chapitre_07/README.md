# Chapitre 7 — TD : Automatisation des Tâches avec GitHub Actions

## Objectifs du chapitre

Mettre en place un pipeline **CI/CD** avec **GitHub Actions** : configurer des workflows,
écrire des fichiers YAML de pipeline, et gérer les notifications et rapports de build.

> Ce chapitre est entièrement pratique (TD).
> Les pipelines s'exécutent directement sur GitHub, sans serveur à gérer.

## Contenu du TD

### 7.1 Concepts et configuration de GitHub Actions

GitHub Actions permet d'automatiser des tâches directement dans votre dépôt GitHub,
à chaque push, pull request ou selon un calendrier.

**Vocabulaire clé :**

| Terme | Description |
|---|---|
| **Workflow** | Fichier YAML décrivant un pipeline automatisé |
| **Event** | Déclencheur du workflow (`push`, `pull_request`, `schedule`…) |
| **Job** | Ensemble de steps exécutés sur un même runner |
| **Step** | Une action réutilisable ou une commande shell |
| **Runner** | Machine virtuelle qui exécute les jobs (ex : `ubuntu-latest`) |
| **Action** | Composant réutilisable publié sur GitHub Marketplace |

**Emplacement des workflows :**

Les fichiers de workflow sont placés dans `.github/workflows/` à la racine du dépôt.

```
cours_devops/
└── .github/
    └── workflows/
        ├── ci.yml        ← pipeline d'intégration continue
        └── release.yml   ← pipeline de livraison
```

**Déclencheurs courants :**

```yaml
on:
  push:
    branches: [main]          # à chaque push sur main
  pull_request:
    branches: [main]          # à chaque pull request vers main
  schedule:
    - cron: '0 2 * * *'       # tous les jours à 2h du matin
  workflow_dispatch:           # déclenchement manuel depuis GitHub
```

### 7.2 Création de pipelines CI/CD

**Workflow de base pour un projet Java Maven :**

```yaml
# .github/workflows/ci.yml
name: CI Java Maven

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Récupération du code
        uses: actions/checkout@v4

      - name: Configuration de Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Compilation
        run: mvn clean compile

      - name: Tests unitaires
        run: mvn test

      - name: Packaging
        run: mvn package -DskipTests

      - name: Archiver le JAR
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar
```

**Variables d'environnement et secrets GitHub Actions :**

| Source | Syntaxe | Usage |
|---|---|---|
| Variable d'environnement | `${{ env.MA_VAR }}` | Valeurs non sensibles |
| Secret GitHub | `${{ secrets.MON_SECRET }}` | Mots de passe, tokens |
| Hash du commit | `${{ github.sha }}` | Identifiant unique du commit |
| Nom de la branche | `${{ github.ref_name }}` | Branche en cours de build |
| Numéro du build | `${{ github.run_number }}` | Numéro d'exécution |

**Configurer un secret dans GitHub :**

1. `Settings` → `Secrets and variables` → `Actions`
2. `New repository secret`
3. Renseigner le nom et la valeur
4. Utiliser dans le workflow avec `${{ secrets.NOM_DU_SECRET }}`

**Workflow avec construction d'image Docker :**

```yaml
# .github/workflows/ci.yml
name: CI + Docker

on:
  push:
    branches: [main]

jobs:
  build-and-push:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build Maven
        run: mvn package -DskipTests

      - name: Connexion à Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Construction et push de l'image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ secrets.DOCKERHUB_USERNAME }}/app-cours:${{ github.run_number }}
```

**Jobs avec dépendances (`needs`) :**

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn test

  package:
    runs-on: ubuntu-latest
    needs: test      # s'exécute seulement si test réussit
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn package -DskipTests
      - uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar
```

### 7.3 Gestion des notifications et des rapports

**Publication des rapports de tests JUnit :**

```yaml
      - name: Tests unitaires
        run: mvn test

      - name: Publier les résultats des tests
        uses: dorny/test-reporter@v1
        if: always()   # exécuté même en cas d'échec
        with:
          name: Résultats JUnit
          path: target/surefire-reports/*.xml
          reporter: java-junit
```

**Résumé de build dans l'interface GitHub :**

```yaml
      - name: Résumé du build
        if: always()
        run: |
          echo "## Résultats du build" >> $GITHUB_STEP_SUMMARY
          echo "- Branche : \`${{ github.ref_name }}\`" >> $GITHUB_STEP_SUMMARY
          echo "- Commit  : \`${{ github.sha }}\`" >> $GITHUB_STEP_SUMMARY
          echo "- Numéro  : #${{ github.run_number }}" >> $GITHUB_STEP_SUMMARY
```

**Notification par email en cas d'échec :**

```yaml
      - name: Notification email en cas d'échec
        if: failure()
        uses: dawidd6/action-send-mail@v3
        with:
          server_address: smtp.gmail.com
          server_port: 587
          username: ${{ secrets.EMAIL_USER }}
          password: ${{ secrets.EMAIL_PASS }}
          to: equipe@exemple.com
          from: ci@exemple.com
          subject: "Build échoué : ${{ github.repository }} #${{ github.run_number }}"
          body: |
            Le pipeline a échoué sur la branche ${{ github.ref_name }}.
            Commit : ${{ github.sha }}
            Lien   : ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}
```

## Points clés à retenir

- Les workflows sont des fichiers YAML versionnés dans `.github/workflows/` (Infrastructure as Code)
- Chaque `job` est une unité d'exécution indépendante ; chaque `step` est une commande ou une action
- Les secrets sont stockés de façon sécurisée dans GitHub et injectés à l'exécution
- La condition `if: always()` exécute un step même si le pipeline a échoué
- Les rapports de tests et résumés sont accessibles directement dans l'onglet **Actions** de GitHub
