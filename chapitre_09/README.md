# Chapitre 9 — Utilisation des Composants Graphiques

## Objectifs du chapitre

Concevoir et développer des interfaces graphiques (**GUI**) en Java avec **Java Swing** :
composants de base, gestion des événements et mise en page.

## Contenu du cours

### 9.1 Composants graphiques de base

**Java Swing** est la bibliothèque graphique standard de Java (intégrée au JDK).

**Fenêtre principale :**

```java
import javax.swing.*;
import java.awt.*;

public class FenetreApp {
    public static void main(String[] args) {
        // Toujours créer l'interface dans le thread graphique (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Étudiants");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 600);
            frame.setLocationRelativeTo(null); // centrer à l'écran
            frame.setVisible(true);
        });
    }
}
```

**Composants courants :**

| Composant | Classe | Description |
|---|---|---|
| Fenêtre | `JFrame` | Fenêtre principale de l'application |
| Panneau | `JPanel` | Conteneur pour organiser les composants |
| Étiquette | `JLabel` | Texte statique |
| Champ de texte | `JTextField` | Saisie d'une ligne de texte |
| Bouton | `JButton` | Bouton cliquable |
| Liste déroulante | `JComboBox` | Sélection parmi une liste |
| Case à cocher | `JCheckBox` | Option binaire |
| Zone de texte | `JTextArea` | Saisie multi-lignes |
| Tableau | `JTable` | Affichage de données tabulaires |
| Défilement | `JScrollPane` | Barre de défilement autour d'un composant |
| Boîte de dialogue | `JOptionPane` | Fenêtre modale (messages, saisies) |

**Exemple — formulaire de saisie étudiant :**

```java
JPanel pnlFormulaire = new JPanel(new GridLayout(3, 4, 5, 5));
pnlFormulaire.setBorder(BorderFactory.createTitledBorder("Informations Étudiant"));

JTextField txtMatricule = new JTextField();
JTextField txtNom       = new JTextField();
JTextField txtPrenoms   = new JTextField();
JComboBox<String> comboGenre = new JComboBox<>(new String[]{"Masculin", "Féminin"});
JTextField txtDate      = new JTextField("AAAA-MM-JJ");

pnlFormulaire.add(new JLabel("Matricule :"));  pnlFormulaire.add(txtMatricule);
pnlFormulaire.add(new JLabel("Nom :"));         pnlFormulaire.add(txtNom);
pnlFormulaire.add(new JLabel("Prénoms :"));     pnlFormulaire.add(txtPrenoms);
pnlFormulaire.add(new JLabel("Genre :"));       pnlFormulaire.add(comboGenre);
pnlFormulaire.add(new JLabel("Date Naiss :"));  pnlFormulaire.add(txtDate);
```

**Tableau de données (`JTable`) :**

```java
String[] colonnes = {"Matricule", "Nom", "Prénoms", "Genre", "Date de Naissance"};
DefaultTableModel modele = new DefaultTableModel(colonnes, 0);
JTable tableau = new JTable(modele);
JScrollPane scrollPane = new JScrollPane(tableau);

// Ajouter une ligne
Vector<String> ligne = new Vector<>();
ligne.add("E001"); ligne.add("Dupont"); ligne.add("Alice");
ligne.add("Féminin"); ligne.add("2003-05-15");
modele.addRow(ligne);

// Supprimer une ligne sélectionnée
int ligneSelectionnee = tableau.getSelectedRow();
if (ligneSelectionnee != -1) {
    modele.removeRow(ligneSelectionnee);
}

// Récupérer la valeur d'une cellule
String matricule = modele.getValueAt(ligneSelectionnee, 0).toString();
```

### 9.2 Gestion des événements

Les événements sont gérés par des **listeners** (écouteurs).

```java
JButton btnAjouter = new JButton("Ajouter l'étudiant");

// Avec expression lambda (Java 8+)
btnAjouter.addActionListener(e -> {
    String nom = txtNom.getText();
    if (nom.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Le nom est obligatoire !");
        return;
    }
    // Logique d'ajout...
    txtNom.setText(""); // vider le champ après ajout
});

// Bouton de suppression
JButton btnSupprimer = new JButton("Supprimer");
btnSupprimer.setBackground(new Color(220, 53, 69));
btnSupprimer.setForeground(Color.WHITE);

btnSupprimer.addActionListener(e -> {
    int ligne = tableau.getSelectedRow();
    if (ligne == -1) {
        JOptionPane.showMessageDialog(frame, "Sélectionnez une ligne à supprimer.");
        return;
    }
    int confirmation = JOptionPane.showConfirmDialog(
        frame, "Confirmer la suppression ?", "Confirmation",
        JOptionPane.YES_NO_OPTION
    );
    if (confirmation == JOptionPane.YES_OPTION) {
        modele.removeRow(ligne);
    }
});
```

**Types d'événements courants :**

| Listener | Événement | Méthode |
|---|---|---|
| `ActionListener` | Clic sur bouton, validation | `actionPerformed(e)` |
| `MouseListener` | Clic, survol souris | `mouseClicked(e)`, etc. |
| `KeyListener` | Touche clavier | `keyPressed(e)`, etc. |
| `WindowListener` | Fermeture fenêtre | `windowClosing(e)` |
| `ListSelectionListener` | Sélection dans un tableau | `valueChanged(e)` |

### 9.3 Mise en page

Les **gestionnaires de mise en page** (Layout Managers) organisent les composants.

| Layout Manager | Description | Utilisation |
|---|---|---|
| `BorderLayout` | 5 zones : NORTH, SOUTH, EAST, WEST, CENTER | Structure générale d'une fenêtre |
| `GridLayout(r, c)` | Grille uniforme de r lignes × c colonnes | Formulaires |
| `FlowLayout` | Composants alignés horizontalement | Barres de boutons |
| `GridBagLayout` | Grille flexible avec contraintes | Mises en page complexes |

**Exemple — structure classique d'une application CRUD :**

```java
frame.setLayout(new BorderLayout());

// Nord : formulaire de saisie
JPanel pnlNord = new JPanel(new GridLayout(3, 4, 5, 5));
// ... ajout des champs ...

// Centre : tableau des données
JScrollPane scrollPane = new JScrollPane(tableau);

// Sud : barre de boutons
JPanel pnlSud = new JPanel(new FlowLayout());
pnlSud.add(btnAjouter);
pnlSud.add(btnSupprimer);
pnlSud.add(btnActualiser);

frame.add(pnlNord,    BorderLayout.NORTH);
frame.add(scrollPane, BorderLayout.CENTER);
frame.add(pnlSud,     BorderLayout.SOUTH);
```

## Points clés à retenir

- Toujours créer et modifier l'interface graphique dans l'**EDT** (`SwingUtilities.invokeLater`)
- `JTable` + `DefaultTableModel` pour afficher des données tabulaires
- Les `ActionListener` avec lambdas simplifient la gestion des clics
- `BorderLayout` pour la structure globale, `GridLayout` pour les formulaires
- `JOptionPane` pour les messages et confirmations utilisateur
