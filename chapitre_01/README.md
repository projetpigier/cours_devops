# Chapitre 1 — Concepts Fondamentaux de DevOps

## Objectifs du chapitre

Comprendre les bases de la culture et des pratiques **DevOps** : collaboration,
automatisation, et panorama des outils utilisés dans l'industrie.

## Contenu du cours

### 1.1 Principes de collaboration entre le développement et les opérations

- La fracture traditionnelle entre équipes Dev et Ops : causes et conséquences
- Le DevOps comme réponse : culture de collaboration et responsabilité partagée
- Méthodes agiles et DevOps : complémentarité
- Le modèle **CALMS** :

| Pilier | Description |
|---|---|
| **C**ulture | Briser les silos, partager les responsabilités |
| **A**utomation | Automatiser les tâches répétitives et le déploiement |
| **L**ean | Livrer en petites itérations, réduire le gaspillage |
| **M**easurement | Mesurer, observer, et améliorer en continu |
| **S**haring | Partager les connaissances et les retours d'expérience |

- Le cycle DevOps :

```
Plan → Code → Build → Test → Release → Deploy → Operate → Monitor
  ↑_______________________________________________________________|
```

### 1.2 Importance de l'automatisation des processus

- Pourquoi automatiser ? Fiabilité, rapidité, répétabilité
- **Intégration Continue (CI)** : chaque commit déclenche un build et des tests automatisés
- **Déploiement Continu (CD)** : les versions validées sont déployées automatiquement
- Le pipeline CI/CD comme épine dorsale du DevOps
- Les gains concrets : moins de bugs en production, cycles de livraison raccourcis

### 1.3 Outils et technologies populaires de DevOps

| Catégorie | Outils populaires | Utilisé dans ce cours |
|---|---|---|
| Gestion de version | Git, GitHub, GitLab | Git + GitHub |
| Conteneurisation | Docker, Podman | Docker |
| Orchestration | Kubernetes, Docker Compose | Docker Compose |
| CI/CD | Jenkins, GitHub Actions, GitLab CI | Jenkins |
| Monitoring | Prometheus, Grafana, ELK Stack | — |
| Infrastructure as Code | Terraform, Ansible | — |
| Cloud | AWS, Azure, GCP | GitHub Codespaces |

## Points clés à retenir

- DevOps est avant tout une **culture**, pas un outil
- L'automatisation réduit les erreurs humaines et accélère les livraisons
- CI/CD permet de livrer des fonctionnalités plus souvent et en toute confiance
- Git, Docker et Jenkins sont les trois piliers pratiques de ce cours
