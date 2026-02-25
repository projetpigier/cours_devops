# TP 4 — Construire et lancer l'image Docker du cours

## Fichiers fournis

```
tp4/
├── Dockerfile        ← Décrit comment construire l'image
└── app/
    └── serveur.py    ← Application web simple (Python stdlib)
```

> 💡 Vous n'avez pas besoin de comprendre le code Python pour ce TP.
> Concentrez-vous sur la lecture du **Dockerfile** et les commandes Docker.

## Commandes du TP

```bash
# 1. Se placer dans le bon dossier
cd chapitre_03/tp4

# 2. Construire l'image
docker build -t app-cours:1.0 .

# 3. Vérifier que l'image est créée
docker images

# 4. Lancer un conteneur
docker run -d -p 8080:8080 --name app-tp4 app-cours:1.0

# 5. Vérifier que le conteneur tourne
docker ps

# 6. Tester l'application
curl http://localhost:8080
curl http://localhost:8080/info
curl http://localhost:8080/sante

# 7. Consulter les logs
docker logs app-tp4

# 8. Arrêter et supprimer le conteneur
docker stop app-tp4
docker rm app-tp4
```

## Routes de l'application

| Route | Description |
|---|---|
| `/` | Page d'accueil HTML |
| `/info` | Informations JSON (nom, environnement, heure) |
| `/sante` | Contrôle de santé JSON (`{"statut": "ok"}`) |

## Personnaliser avec des variables d'environnement

```bash
docker run -d -p 8080:8080 \
  -e NOM_APP="Mon Application" \
  -e ENV="production" \
  --name app-perso app-cours:1.0
```
