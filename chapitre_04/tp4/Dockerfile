# ============================================================
#  Dockerfile — Application du cours DevOps (Chapitre 03)
# ============================================================
#
#  Ce fichier décrit comment construire l'image Docker
#  de l'application fournie dans ce TP.
#
#  Commandes utiles (à taper dans le terminal) :
#
#    Construire l'image :
#      docker build -t app-cours:1.0 .
#
#    Lancer un conteneur :
#      docker run -d -p 8080:8080 --name app-tp4 app-cours:1.0
#
#    Tester :
#      curl http://localhost:8080
#
# ============================================================

# --- Image de base -------------------------------------------
# On part d'une image Python officielle, version "slim" (légère)
FROM python:3.12-slim

# --- Métadonnées (optionnelles mais bonnes pratiques) --------
LABEL maintainer="cours-devops"
LABEL description="Application d'exemple — Chapitre 03"

# --- Répertoire de travail dans le conteneur -----------------
# Toutes les commandes suivantes s'exécutent depuis /app
WORKDIR /app

# --- Copie des fichiers de l'application ---------------------
# On copie le contenu du dossier app/ dans le conteneur
COPY app/ .

# --- Variables d'environnement par défaut --------------------
# Elles peuvent être surchargées au lancement avec -e
ENV NOM_APP="Application du cours DevOps"
ENV ENV="developpement"
ENV PORT=8080

# --- Port exposé par l'application ---------------------------
EXPOSE 8080

# --- Commande de démarrage -----------------------------------
# Lance le serveur Python au démarrage du conteneur
CMD ["python", "serveur.py"]
