package com.monprojet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class App {
    
    private static final String URL = "jdbc:mysql://localhost:3306/mon_app_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rootpassword";

    public static void main(String[] args) {
        // 1. Création de la fenêtre
        JFrame frame = new JFrame("Gestion des Étudiants - Liste");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout()); // Layout plus adapté pour un tableau

        // 2. Préparation du tableau (JTable)
        String[] colonnes = {"Matricule", "Nom", "Prénoms", "Genre", "Date de Naissance"};
        DefaultTableModel model = new DefaultTableModel(colonnes, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 3. Bouton d'action
        JButton btnRefresh = new JButton("Initialiser et Afficher les Étudiants");
        
        btnRefresh.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                // Initialisation de la table SQL
                initialiserBaseDeDonnees(conn);
                
                // Chargement des données dans le JTable
                chargerDonnees(conn, model);
                
                JOptionPane.showMessageDialog(frame, "Données synchronisées avec succès !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // 4. Ajout des composants à la fenêtre
        frame.add(btnRefresh, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void initialiserBaseDeDonnees(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        // Création de la table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS etudiants (" +
                "matricule VARCHAR(20) PRIMARY KEY, " +
                "nom VARCHAR(50), " +
                "prenoms VARCHAR(100), " +
                "genre VARCHAR(10), " +
                "date_de_naissance DATE)");

        // Insertion des 3 étudiants de test
        String insertSQL = "INSERT IGNORE INTO etudiants VALUES " +
                "('MAT001', 'Koffi', 'Jean-Luc', 'Masculin', '2002-05-15'), " +
                "('MAT002', 'Traoré', 'Fatoumata', 'Féminin', '2001-11-20'), " +
                "('MAT003', 'Diallo', 'Moussa', 'Masculin', '2003-02-10')";
        stmt.executeUpdate(insertSQL);
    }

    private static void chargerDonnees(Connection conn, DefaultTableModel model) throws SQLException {
        // On vide le tableau avant de le remplir
        model.setRowCount(0);

        String query = "SELECT * FROM etudiants";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            // Extraction des données de chaque ligne
            Vector<String> row = new Vector<>();
            row.add(rs.getString("matricule"));
            row.add(rs.getString("nom"));
            row.add(rs.getString("prenoms"));
            row.add(rs.getString("genre"));
            row.add(rs.getString("date_de_naissance"));
            
            // Ajout de la ligne au modèle du tableau
            model.addRow(row);
        }
    }
}