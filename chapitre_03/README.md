# Chapitre 3 — TD : Gestion du Code avec Git

## Objectifs du chapitre

Pratiquer **Git** en situation réelle : créer et gérer des dépôts, travailler
avec des branches, et collaborer sur GitHub via des pull requests.

> Ce chapitre est entièrement pratique (TD).
> Vous travaillerez dans votre **GitHub Codespace**.

## Contenu du TD

### 3.1 Création et gestion de référentiels

**Créer un dépôt local et le publier sur GitHub :**

```bash
# Initialiser un nouveau dépôt Git local
git init mon-projet-java
cd mon-projet-java

# Configurer votre identité
git config --global user.name  "Prénom Nom"
git config --global user.email "email@exemple.com"

# Ajouter un fichier et faire le premier commit
echo "# Mon Projet Java" > README.md
git add README.md
git commit -m "Initial commit"

# Lier au dépôt GitHub distant et pousser
git remote add origin https://github.com/VOTRE_USERNAME/mon-projet-java.git
git branch -M main
git push -u origin main
```

**Commandes de gestion quotidienne :**

```bash
git status              # état du dépôt
git add .               # préparer tous les fichiers modifiés
git commit -m "msg"     # enregistrer un commit
git log --oneline       # historique des commits
git push                # envoyer vers GitHub
git pull                # récupérer les modifications distantes
```

**Fichier `.gitignore` pour un projet Java :**

```gitignore
# Dossier de build Maven
target/

# Fichiers de classe compilés
*.class

# Fichiers de configuration IDE
.idea/
*.iml
.vscode/settings.json
```

### 3.2 Gestion des branches

Les branches permettent de développer des fonctionnalités en parallèle
sans perturber le code stable de `main`.

```bash
# Créer une branche et basculer dessus
git checkout -b fonctionnalite/ajout-etudiant

# Vérifier la branche courante
git branch

# Travailler sur la branche (modifier des fichiers, committer)
git add .
git commit -m "Ajoute la classe Etudiant"

# Pousser la branche vers GitHub
git push -u origin fonctionnalite/ajout-etudiant

# Revenir sur main
git checkout main

# Fusionner la branche dans main (en local)
git merge fonctionnalite/ajout-etudiant

# Supprimer la branche locale après fusion
git branch -d fonctionnalite/ajout-etudiant
```

**Stratégie de branches recommandée :**

| Branche | Rôle |
|---|---|
| `main` | Code stable, prêt pour la production |
| `develop` | Intégration des nouvelles fonctionnalités |
| `fonctionnalite/xxx` | Développement d'une fonctionnalité spécifique |
| `correctif/xxx` | Correction d'un bug urgent |

### 3.3 Gestion des contributions

**Flux de travail collaboratif sur GitHub :**

1. **Fork** du dépôt principal (copie dans votre compte)
2. **Clone** de votre fork en local
3. Création d'une **branche** dédiée à votre contribution
4. Développement et **commits**
5. Ouverture d'une **Pull Request** (PR) sur GitHub
6. **Revue de code** par un pair ou le responsable du projet
7. **Merge** de la PR dans `main` après validation

```bash
# Synchroniser votre fork avec le dépôt original
git remote add upstream https://github.com/ORIGINAL/depot.git
git fetch upstream
git merge upstream/main
```

**Résolution de conflits :**

```bash
# En cas de conflit lors du merge
git merge fonctionnalite/ma-branche
# → Git indique les fichiers en conflit

# Ouvrir le fichier conflictuel, choisir les bonnes lignes
# (supprimer les marqueurs <<<<<<, =======, >>>>>>>)

# Valider la résolution
git add fichier-conflit.java
git commit -m "Résout le conflit sur fichier-conflit.java"
```

## Aide-mémoire Git

| Commande | Description |
|---|---|
| `git init` | Initialiser un dépôt |
| `git clone URL` | Cloner un dépôt distant |
| `git status` | État du dépôt |
| `git add .` | Préparer tous les fichiers |
| `git commit -m "msg"` | Enregistrer un commit |
| `git push` | Envoyer vers GitHub |
| `git pull` | Récupérer depuis GitHub |
| `git checkout -b branche` | Créer et basculer sur une branche |
| `git merge branche` | Fusionner une branche |
| `git log --oneline` | Voir l'historique |
