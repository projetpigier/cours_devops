# ============================================================
#  Dockerfile — Application TP 5 (avec SQLite)
# ============================================================
#
#  SQLite est integre a Python : aucune dependance a installer.
#  Les donnees sont stockees dans /data/donnees.db
#  Ce dossier est monte en volume dans docker-compose.yml.
#
# ============================================================

FROM python:3.12-slim

LABEL maintainer="cours-devops"
LABEL description="Application TP5 — Docker Compose + SQLite"

WORKDIR /app

# Copie du code (pas de pip install : sqlite3 est dans la stdlib)
COPY app/ .

# Dossier de données — sera monté en volume par Docker Compose
RUN mkdir -p /data

ENV NOM_APP="Application cours DevOps"
ENV ENV="developpement"
ENV PORT=8080
ENV DB_PATH=/data/donnees.db

EXPOSE 8080

CMD ["python", "serveur.py"]
