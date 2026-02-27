# Chapitre 7 — TD : Automatisation des Tâches avec Jenkins

## Objectifs du chapitre

Mettre en place un pipeline **CI/CD** avec **Jenkins** : configurer des projets,
écrire des `Jenkinsfile`, et gérer les notifications et rapports de build.

> Ce chapitre est entièrement pratique (TD).
> Jenkins est lancé dans un conteneur Docker.

## Contenu du TD

### 7.1 Configuration de projets Jenkins

**Lancer Jenkins avec Docker :**

```bash
# Lancer Jenkins en conteneur
docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  --name jenkins \
  jenkins/jenkins:lts

# Récupérer le mot de passe d'initialisation
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Accédez ensuite à Jenkins via `http://localhost:8080`.

**Étapes de configuration initiale :**

1. Entrer le mot de passe d'initialisation
2. Installer les plugins suggérés
3. Créer le compte administrateur
4. Configurer l'URL de Jenkins

**Plugins essentiels à installer :**

| Plugin | Rôle |
|---|---|
| Git | Intégration avec les dépôts Git |
| Maven Integration | Support des projets Maven |
| GitHub | Connexion avec GitHub |
| Pipeline | Support des Jenkinsfile |
| JUnit | Publication des rapports de tests |
| Email Extension | Notifications par email |

**Créer un projet Pipeline :**

1. `Nouveau Item` → nom du projet → `Pipeline`
2. Dans la section **Pipeline**, choisir `Pipeline script from SCM`
3. SCM : `Git`, URL du dépôt GitHub
4. Chemin du Jenkinsfile : `Jenkinsfile` (racine du projet)
5. Sauvegarder

### 7.2 Création de pipelines de CI/CD avec Jenkinsfile

Un **Jenkinsfile** décrit toutes les étapes du pipeline sous forme de code.

**Jenkinsfile de base pour un projet Java Maven :**

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
        jdk   'Java 21'
    }

    stages {

        stage('Récupération du code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/VOTRE_USERNAME/cours_devops.git'
            }
        }

        stage('Compilation') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Tests unitaires') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Publier les résultats JUnit
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Packaging') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                sh 'docker build -t app-cours:${BUILD_NUMBER} .'
            }
        }

    }

    post {
        success {
            echo 'Build réussi !'
        }
        failure {
            echo 'Build échoué — vérifiez les logs ci-dessus'
        }
    }
}
```

**Variables d'environnement Jenkins utiles :**

| Variable | Description |
|---|---|
| `BUILD_NUMBER` | Numéro du build courant |
| `BUILD_URL` | URL de la page du build |
| `GIT_BRANCH` | Branche Git en cours de build |
| `GIT_COMMIT` | Hash du commit courant |
| `WORKSPACE` | Chemin du dossier de travail |

**Déclencheurs automatiques :**

```groovy
triggers {
    // Déclencher à chaque push GitHub (nécessite un webhook)
    githubPush()

    // Vérifier toutes les 5 minutes si le code a changé
    pollSCM('H/5 * * * *')

    // Build planifié (tous les jours à 2h du matin)
    cron('0 2 * * *')
}
```

### 7.3 Gestion des notifications et des rapports

**Notifications par email :**

```groovy
post {
    failure {
        mail to: 'equipe@exemple.com',
             subject: "Build échoué : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
             body: """
                 Le build a échoué.
                 
                 Projet  : ${env.JOB_NAME}
                 Build   : ${env.BUILD_NUMBER}
                 URL     : ${env.BUILD_URL}
                 Branche : ${env.GIT_BRANCH}
                 
                 Consultez les logs pour plus de détails.
             """
    }
    success {
        mail to: 'equipe@exemple.com',
             subject: "Build réussi : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
             body: "Le build #${env.BUILD_NUMBER} s'est terminé avec succès."
    }
}
```

**Publication des rapports de tests JUnit :**

```groovy
post {
    always {
        // Rapport JUnit (tests unitaires Maven)
        junit 'target/surefire-reports/*.xml'

        // Archiver les artefacts de build
        archiveArtifacts artifacts: 'target/*.jar',
                         allowEmptyArchive: true
    }
}
```

**Webhook GitHub → Jenkins :**

1. Dans GitHub : `Settings` → `Webhooks` → `Add webhook`
2. Payload URL : `http://ADRESSE_JENKINS:8080/github-webhook/`
3. Content type : `application/json`
4. Événements : `Just the push event`
5. Dans Jenkins : activer `GitHub hook trigger for GITScm polling` dans le projet

## Points clés à retenir

- Le **Jenkinsfile** doit être versionné dans le dépôt Git (Infrastructure as Code)
- Chaque `stage` représente une étape distincte du pipeline
- Le bloc `post` gère les actions en fin de pipeline (succès, échec, toujours)
- Les webhooks permettent de déclencher les builds automatiquement à chaque push
- Les rapports de tests sont archivés et consultables depuis l'interface Jenkins
