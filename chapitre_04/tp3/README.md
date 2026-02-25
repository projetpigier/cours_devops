# TP 3 — Découverte de Docker

Ce dossier ne contient pas de fichiers à modifier : le TP 3 utilise uniquement
des images publiques disponibles sur **Docker Hub**.

## Commandes du TP

Suivez les instructions du document de cours. Voici un rappel des commandes utilisées :

```bash
# Vérifier Docker
docker --version
docker info

# Premier conteneur
docker run hello-world

# Serveur web Nginx
docker run -d -p 8080:80 --name mon-nginx nginx

# Gérer les conteneurs
docker ps
docker ps -a
docker stop mon-nginx
docker start mon-nginx
docker logs mon-nginx
docker exec -it mon-nginx sh
docker rm mon-nginx

# Conteneur interactif Ubuntu
docker run -it ubuntu bash

# Nettoyer
docker system prune
```

## Aide-mémoire Docker

| Commande | Description |
|---|---|
| `docker run IMAGE` | Télécharger et lancer un conteneur |
| `docker ps` | Conteneurs actifs |
| `docker ps -a` | Tous les conteneurs |
| `docker stop NOM` | Arrêter un conteneur |
| `docker rm NOM` | Supprimer un conteneur |
| `docker images` | Images locales |
| `docker rmi IMAGE` | Supprimer une image |
| `docker logs NOM` | Logs d'un conteneur |
| `docker exec -it NOM sh` | Shell dans un conteneur |
