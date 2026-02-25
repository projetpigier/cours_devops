# Chapitre 03 — Docker & GitHub Codespaces

## Objectif du chapitre

Découvrir l'environnement de développement que vous utiliserez tout au long de la formation :
**GitHub Codespaces** (VS Code dans le navigateur) et **Docker** (conteneurs applicatifs).

> ⚠️ **Le cours Java commence au chapitre suivant.**  
> Ces TP portent uniquement sur l'environnement. Vous n'avez pas besoin de connaître Java.

## Travaux Pratiques

| TP | Titre | Dossier |
|---|---|---|
| TP 1 | Forker le dépôt et créer un Codespace | *(pas de dossier — actions GitHub)* |
| TP 2 | Explorer VS Code et l'environnement Codespace | *(exploration de ce dépôt)* |
| TP 3 | Découverte de Docker | `tp3/` |
| TP 4 | Construire et lancer l'image Docker du cours | `tp4/` |
| TP 5 | Orchestrer avec Docker Compose | `tp5/` |

## Pour commencer

1. **Forkez** ce dépôt (bouton `Fork` en haut à droite)
2. **Ouvrez un Codespace** depuis votre fork (`Code` → `Codespaces` → `Create codespace on main`)
3. Ouvrez le document PDF du cours et suivez les instructions TP par TP

## Vérification de l'environnement

Une fois dans votre Codespace, ouvrez le terminal (`Ctrl+\``) et tapez :

```bash
git --version
docker --version
docker compose version
```

Les trois commandes doivent afficher un numéro de version.
