FROM mcr.microsoft.com/devcontainers/java:21

# Installation des bibliothèques pour l'interface graphique
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*