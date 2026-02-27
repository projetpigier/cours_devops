# Chapitre 4 — TD : Lancement d'une Application avec Docker

## Objectifs du chapitre

Découvrir l'environnement **Docker** et **GitHub Codespaces** en pratique :
créer et gérer des conteneurs, écrire des Dockerfiles, configurer des réseaux
et volumes, et orchestrer des services avec Docker Compose.

> Ce chapitre est entièrement pratique (TD).
> Vous travaillerez dans votre **GitHub Codespace**.

## Travaux Pratiques

| TP | Titre | Dossier |
|---|---|---|
| TP 1 | Forker le dépôt et créer un Codespace | *(actions GitHub)* |
| TP 2 | Explorer VS Code et l'environnement Codespace | *(exploration du dépôt)* |
| TP 3 | Découverte de Docker | `tp3/` |
| TP 4 | Construire et lancer l'image Docker du cours | `tp4/` |
| TP 5 | Orchestrer avec Docker Compose | `tp5/` |

## Contenu du TD

### 4.1 Création et gestion de conteneurs Docker

Un **conteneur** est une unité d'exécution légère et isolée.

```bash
# Vérifier l'installation
docker --version
docker info

# Lancer un premier conteneur
docker run hello-world

# Lancer un serveur web Nginx
docker run -d -p 8080:80 --name mon-nginx nginx

# Gérer les conteneurs
docker ps               # conteneurs actifs
docker ps -a            # tous les conteneurs
docker stop mon-nginx   # arrêter
docker start mon-nginx  # redémarrer
docker logs mon-nginx   # consulter les logs
docker exec -it mon-nginx sh   # ouvrir un shell dans le conteneur
docker rm mon-nginx     # supprimer le conteneur

# Gérer les images
docker images           # images locales
docker rmi nginx        # supprimer une image

# Nettoyer les ressources inutilisées
docker system prune
```

### 4.2 Utilisation des Dockerfiles pour créer des images personnalisées

Un **Dockerfile** décrit les étapes pour construire une image.

```dockerfile
# Image de base
FROM eclipse-temurin:21-jre

# Dossier de travail dans le conteneur
WORKDIR /app

# Copier le JAR compilé
COPY target/java-gui-mysql-1.0-SNAPSHOT.jar app.jar

# Variable d'environnement
ENV NOM_APP="Mon Application"

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Construire l'image depuis le Dockerfile
docker build -t app-cours:1.0 .

# Vérifier que l'image est créée
docker images

# Lancer un conteneur à partir de l'image
docker run -d -p 8080:8080 --name app-tp4 app-cours:1.0

# Passer des variables d'environnement
docker run -d -p 8080:8080 \
  -e NOM_APP="Application DevOps" \
  --name app-perso app-cours:1.0
```

### 4.3 Configuration de réseaux et de volumes dans Docker

**Volumes** : persistance des données entre redémarrages du conteneur.

```bash
# Créer un volume nommé
docker volume create donnees_mysql

# Lancer un conteneur avec un volume
docker run -d \
  -v donnees_mysql:/var/lib/mysql \
  --name mysql-db mysql:8.0

# Lister les volumes
docker volume ls

# Supprimer un volume
docker volume rm donnees_mysql
```

**Réseaux** : communication entre conteneurs.

```bash
# Créer un réseau personnalisé
docker network create reseau-cours

# Lancer des conteneurs dans le même réseau
docker run -d --network reseau-cours --name db mysql:8.0
docker run -d --network reseau-cours --name app app-cours:1.0

# Lister les réseaux
docker network ls
```

### 4.4 Utilisation de Docker Compose

**Docker Compose** orchestre plusieurs conteneurs décrits dans un fichier `docker-compose.yml`.

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
    depends_on:
      - db
    networks:
      - reseau-cours

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mon_app_db
    volumes:
      - donnees_mysql:/var/lib/mysql
    networks:
      - reseau-cours

volumes:
  donnees_mysql:

networks:
  reseau-cours:
```

```bash
# Démarrer tous les services
docker compose up -d --build

# Vérifier l'état des services
docker compose ps

# Consulter les logs
docker compose logs -f

# Arrêter les services (données conservées)
docker compose down

# Arrêter et supprimer les volumes (reset complet)
docker compose down -v
```

## Vérification de l'environnement

Dans votre Codespace, ouvrez le terminal et vérifiez :

```bash
git --version
docker --version
docker compose version
java --version
mvn --version
```

Toutes les commandes doivent afficher un numéro de version.
