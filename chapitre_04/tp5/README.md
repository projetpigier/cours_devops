# TP 5 — Orchestrer avec Docker Compose

## Fichiers fournis

```
tp5/
├── docker-compose.yml   ← Orchestre le service et le volume
├── Dockerfile           ← Image de l'application
└── app/
    └── serveur.py       ← Application web avec SQLite
```

> 💡 SQLite est intégré à Python : **aucune dépendance à installer**.  
> Un seul fichier `donnees.db` contient toute la base de données.

## Architecture

```
Navigateur / curl
      │
      │ HTTP :8080
      ▼
 ┌──────────────────┐
 │       app        │
 │  Python + SQLite │──► Volume donnees_sqlite
 │      :8080       │    (/data/donnees.db)
 └──────────────────┘
```

La différence avec le TP 4 : le fichier `donnees.db` est dans un **volume nommé**.  
Il persiste même si le conteneur est supprimé et recréé.

## Commandes du TP

```bash
# 1. Se placer dans le bon dossier
cd chapitre_03/tp5

# 2. Démarrer le service (build + lancement)
docker compose up --build

# 3. En arrière-plan (libère le terminal)
docker compose up -d --build

# 4. Vérifier l'état du service
docker compose ps

# 5. Tester l'application
curl http://localhost:8080           # page d'accueil + compteur visites
curl http://localhost:8080/info      # infos JSON + dernières visites
curl http://localhost:8080/sante     # contrôle de santé

# 6. Consulter les logs
docker compose logs
docker compose logs -f               # en temps réel

# 7. Inspecter la base SQLite directement dans le conteneur
docker compose exec app python3 -c "
import sqlite3
conn = sqlite3.connect('/data/donnees.db')
rows = conn.execute('SELECT * FROM visites ORDER BY id DESC LIMIT 10').fetchall()
for r in rows: print(r)
conn.close()
"

# 8. Arrêter (données conservées dans le volume)
docker compose down

# 9. Redémarrer — le compteur repart du nombre précédent
docker compose up -d

# 10. Arrêter + effacer les données (reset complet)
docker compose down -v
```

## Ce que vous observez

1. Rechargez `http://localhost:8080` plusieurs fois → le compteur s'incrémente.
2. Faites `docker compose down` puis `docker compose up -d` →  
   le compteur **repart du nombre précédent** (volume conservé ✅).
3. Faites `docker compose down -v` puis `docker compose up -d` →  
   le compteur **repart à zéro** (volume supprimé 🗑️).
