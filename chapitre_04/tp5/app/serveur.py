"""
Application web du cours DevOps — version avec base de données SQLite.
Utilise uniquement la bibliothèque standard Python — aucune installation requise.

SQLite stocke toutes les données dans un seul fichier (donnees.db).
Le fichier est placé dans /data/ afin d'être conservé dans un volume Docker.

Note : vous étudierez ce type de code au chapitre Java.
Pour l'instant, observez comment l'application persiste des données
et comment un volume Docker préserve ces données entre les redémarrages.
"""

import os
import json
import sqlite3
from http.server import HTTPServer, BaseHTTPRequestHandler
from datetime import datetime

# Chemin du fichier de base de données
# /data/ sera monté comme volume Docker (cf. docker-compose.yml)
DB_PATH = os.getenv("DB_PATH", "/data/donnees.db")


def connecter():
    """Retourne une connexion SQLite. Le fichier est créé s'il n'existe pas."""
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    return sqlite3.connect(DB_PATH)


def initialiser_bdd():
    """Crée la table des visites si elle n'existe pas encore."""
    with connecter() as conn:
        conn.execute("""
            CREATE TABLE IF NOT EXISTS visites (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                chemin     TEXT NOT NULL,
                horodatage TEXT DEFAULT (datetime('now', 'localtime'))
            )
        """)
        conn.commit()
    print(f"[BDD] Base SQLite prete : {DB_PATH}", flush=True)


def enregistrer_visite(chemin):
    """Enregistre une visite dans la base."""
    with connecter() as conn:
        conn.execute("INSERT INTO visites (chemin) VALUES (?)", (chemin,))
        conn.commit()


def compter_visites():
    """Retourne le nombre total de visites."""
    with connecter() as conn:
        return conn.execute("SELECT COUNT(*) FROM visites").fetchone()[0]


def dernieres_visites(n=5):
    """Retourne les n dernières visites."""
    with connecter() as conn:
        rows = conn.execute(
            "SELECT chemin, horodatage FROM visites ORDER BY id DESC LIMIT ?", (n,)
        ).fetchall()
    return [{"chemin": r[0], "horodatage": r[1]} for r in rows]


class Handler(BaseHTTPRequestHandler):

    def do_GET(self):
        enregistrer_visite(self.path)

        if self.path == "/sante":
            reponse = json.dumps({
                "statut": "ok",
                "base_de_donnees": "sqlite",
                "fichier_db": DB_PATH,
            }, ensure_ascii=False)
            self._repondre(200, "application/json", reponse)

        elif self.path == "/info":
            infos = {
                "application":     os.getenv("NOM_APP", "App DevOps"),
                "environnement":   os.getenv("ENV", "developpement"),
                "base_de_donnees": f"SQLite ({DB_PATH})",
                "visites_totales": compter_visites(),
                "dernieres_visites": dernieres_visites(),
                "heure_serveur":   datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            }
            self._repondre(200, "application/json",
                           json.dumps(infos, ensure_ascii=False, indent=2))

        else:
            nom_app = os.getenv("NOM_APP", "Application du cours DevOps")
            env     = os.getenv("ENV", "developpement")
            nb      = compter_visites()
            html = f"""<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>{nom_app}</title>
  <style>
    body  {{ font-family: Arial, sans-serif; max-width: 620px;
             margin: 60px auto; text-align: center; color: #333; }}
    h1    {{ color: #2E75B6; }}
    .badge {{ background: #E2EFDA; border-radius: 8px;
              padding: 10px 20px; display: inline-block; margin: 8px; }}
    .bdd  {{ background: #D6E4F0; }}
  </style>
</head>
<body>
  <h1>Bonjour {nom_app}</h1>
  <p>Application avec persistance via <strong>SQLite</strong> + volume Docker.</p>
  <div class="badge">Environnement : <strong>{env}</strong></div>
  <div class="badge bdd">Visites enregistrees : <strong>{nb}</strong></div>
  <p><a href="/info">/info</a> &nbsp;|&nbsp; <a href="/sante">/sante</a></p>
</body>
</html>"""
            self._repondre(200, "text/html; charset=utf-8", html)

    def _repondre(self, code, content_type, corps):
        contenu = corps.encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", len(contenu))
        self.end_headers()
        self.wfile.write(contenu)

    def log_message(self, format, *args):
        print(f"[{self.log_date_time_string()}] {format % args}", flush=True)


if __name__ == "__main__":
    print("=== Initialisation ===", flush=True)
    initialiser_bdd()
    port = int(os.getenv("PORT", "8080"))
    print(f"=== {os.getenv('NOM_APP', 'App DevOps')} ===", flush=True)
    print(f"Serveur demarre sur le port {port}", flush=True)
    print(f"Base de donnees : {DB_PATH}", flush=True)
    serveur = HTTPServer(("0.0.0.0", port), Handler)
    serveur.serve_forever()
