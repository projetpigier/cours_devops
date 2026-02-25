"""
Application web simple du cours DevOps.
Utilise uniquement la bibliothèque standard Python — aucune installation requise.

Note : vous étudierez le code Python et Java au chapitre suivant.
Pour l'instant, concentrez-vous sur le Dockerfile et les commandes Docker.
"""

import os
import json
from http.server import HTTPServer, BaseHTTPRequestHandler
from datetime import datetime


class Handler(BaseHTTPRequestHandler):

    def do_GET(self):
        # Route /sante — vérification que l'app fonctionne (utilisée par Docker Compose)
        if self.path == "/sante":
            reponse = json.dumps({
                "statut": "ok",
                "message": "Application en bonne santé"
            })
            self._repondre(200, "application/json", reponse)

        # Route /info — informations sur l'environnement
        elif self.path == "/info":
            infos = {
                "application": os.getenv("NOM_APP", "App DevOps"),
                "environnement": os.getenv("ENV", "developpement"),
                "port": os.getenv("PORT", "8080"),
                "heure_serveur": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            }
            reponse = json.dumps(infos, ensure_ascii=False, indent=2)
            self._repondre(200, "application/json", reponse)

        # Route / — page d'accueil HTML
        else:
            nom_app = os.getenv("NOM_APP", "Application du cours DevOps")
            env = os.getenv("ENV", "developpement")
            reponse = f"""<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>{nom_app}</title>
  <style>
    body {{ font-family: Arial, sans-serif; max-width: 600px;
           margin: 60px auto; text-align: center; color: #333; }}
    h1   {{ color: #2E75B6; }}
    .badge {{ background: #E2EFDA; border-radius: 8px;
              padding: 10px 20px; display: inline-block; margin: 10px; }}
  </style>
</head>
<body>
  <h1>🐳 {nom_app}</h1>
  <p>Application fonctionnelle dans un conteneur Docker !</p>
  <div class="badge">Environnement : <strong>{env}</strong></div>
  <p><a href="/info">/info</a> &nbsp;|&nbsp; <a href="/sante">/sante</a></p>
</body>
</html>"""
            self._repondre(200, "text/html; charset=utf-8", reponse)

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
    port = int(os.getenv("PORT", "8080"))
    print(f"=== {os.getenv('NOM_APP', 'App DevOps')} ===")
    print(f"Serveur démarré sur le port {port}")
    print(f"Environnement : {os.getenv('ENV', 'developpement')}")
    print("Appuyez sur Ctrl+C pour arrêter.")
    serveur = HTTPServer(("0.0.0.0", port), Handler)
    serveur.serve_forever()
