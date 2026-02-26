# Utilise une image Java légère
FROM mcr.microsoft.com/devcontainers/java:21

# Installation de bibliothèques nécessaires pour l'interface graphique (libxext, libxrender, etc.)
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*