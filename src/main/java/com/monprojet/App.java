package com.monprojet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class App {
    // Changement de localhost vers db pour Docker
    private static final String URL = "jdbc:mysql://db:3306/mon_app_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rootpassword";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestion des Étudiants Pigier - CRUD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLayout(new BorderLayout());

        // --- 1. Formulaire de saisie (Nouveau) ---
        JPanel pnlSaisie = new JPanel(new GridLayout(3, 4, 5, 5));
        pnlSaisie.setBorder(BorderFactory.createTitledBorder("Informations Étudiant"));

        JTextField txtMatricule = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtPrenoms = new JTextField();
        JComboBox<String> comboGenre = new JComboBox<>(new String[]{"Masculin", "Féminin"});
        JTextField txtDate = new JTextField("AAAA-MM-JJ");

        pnlSaisie.add(new JLabel("Matricule:")); pnlSaisie.add(txtMatricule);
        pnlSaisie.add(new JLabel("Nom:")); pnlSaisie.add(txtNom);
        pnlSaisie.add(new JLabel("Prénoms:")); pnlSaisie.add(txtPrenoms);
        pnlSaisie.add(new JLabel("Genre:")); pnlSaisie.add(comboGenre);
        pnlSaisie.add(new JLabel("Date Naiss:")); pnlSaisie.add(txtDate);

        JButton btnAdd = new JButton("Ajouter l'étudiant");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        pnlSaisie.add(btnAdd);

        // --- 2. Tableau ---
        String[] colonnes = {"Matricule", "Nom", "Prénoms", "Genre", "Date de Naissance"};
        DefaultTableModel model = new DefaultTableModel(colonnes, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // --- 3. Barre de boutons (Bas) ---
        JPanel pnlActions = new JPanel();
        JButton btnRefresh = new JButton("Actualiser la liste");
        JButton btnDelete = new JButton("Supprimer sélection");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        
        pnlActions.add(btnRefresh);
        pnlActions.add(btnDelete);

        // --- 4. Logique des boutons ---

        // AJOUTER
        btnAdd.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "INSERT INTO etudiants (matricule, nom, prenoms, genre, date_de_naissance) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtMatricule.getText());
                pstmt.setString(2, txtNom.getText());
                pstmt.setString(3, txtPrenoms.getText());
                pstmt.setString(4, comboGenre.getSelectedItem().toString());
                pstmt.setString(5, txtDate.getText());
                
                pstmt.executeUpdate();
                chargerDonnees(conn, model);
                // Vider les champs
                txtMatricule.setText(""); txtNom.setText(""); txtPrenoms.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur Ajout : " + ex.getMessage());
            }
        });

        // SUPPRIMER
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String mat = model.getValueAt(row, 0).toString();
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM etudiants WHERE matricule = ?");
                    pstmt.setString(1, mat);
                    pstmt.executeUpdate();
                    chargerDonnees(conn, model);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur Suppression : " + ex.getMessage());
                }
            }
        });

        // ACTUALISER
        btnRefresh.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                initialiserBaseDeDonnees(conn);
                chargerDonnees(conn, model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage());
            }
        });

        // --- Assemblage ---
        frame.add(pnlSaisie, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(pnlActions, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void initialiserBaseDeDonnees(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS etudiants (" +
                "matricule VARCHAR(20) PRIMARY KEY, " +
                "nom VARCHAR(50), " +
                "prenoms VARCHAR(100), " +
                "genre VARCHAR(10), " +
                "date_de_naissance DATE)");
    }

    private static void chargerDonnees(Connection conn, DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM etudiants");
        while (rs.next()) {
            Vector<String> row = new Vector<>();
            row.add(rs.getString("matricule"));
            row.add(rs.getString("nom"));
            row.add(rs.getString("prenoms"));
            row.add(rs.getString("genre"));
            row.add(rs.getString("date_de_naissance"));
            model.addRow(row);
        }
    }
}