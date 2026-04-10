import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

/**
 * Interface graphique (Swing) pour visualiser et résoudre le labyrinthe.
 * Fonctionnalités : charger, générer, résoudre avec DFS ou BFS, animer.
 */
public class MazeGUI extends JFrame {

    // Taille en pixels de chaque cellule du labyrinthe à l'écran
    private static final int CELL_SIZE = 20;

    // --- Palette de couleurs de l'interface ---
    private static final Color COLOR_WALL     = new Color(40, 40, 60);    // Mur : bleu très foncé
    private static final Color COLOR_PATH     = new Color(240, 240, 245); // Passage libre : blanc cassé
    private static final Color COLOR_START    = new Color(50, 200, 100);  // Départ : vert
    private static final Color COLOR_END      = new Color(220, 50, 50);   // Arrivée : rouge
    private static final Color COLOR_SOLUTION = new Color(255, 200, 50);  // Chemin solution : jaune/or
    private static final Color COLOR_VISITED  = new Color(150, 200, 255); // Cellule explorée : bleu clair
    private static final Color COLOR_BG       = new Color(20, 20, 35);    // Fond général : noir bleuté

    // --- Données métier ---
    private Maze maze;               // Le labyrinthe actuellement affiché
    private SolverResult lastResult; // Résultat du dernier algorithme exécuté

    // --- Composants de l'interface ---
    private JPanel  mazePanel;   // Zone de dessin du labyrinthe
    private JLabel  statusLabel; // Barre de statut en bas
    private JButton btnLoadFile; // Bouton "Charger un fichier"
    private JButton btnGenerate; // Bouton "Générer un labyrinthe aléatoire"
    private JButton btnDFS;      // Bouton "Résoudre avec DFS"
    private JButton btnBFS;      // Bouton "Résoudre avec BFS"
    private JButton btnCompare;  // Bouton "Comparer DFS et BFS"
    private JSpinner sizeSpinner; // Sélecteur de taille du labyrinthe

    /**
     * Constructeur : initialise la fenêtre, construit l'UI,
     * génère un labyrinthe de démonstration 15×15 au démarrage.
     */
    public MazeGUI() {
        super("🧩 Résolution de Labyrinthe - ESP Dakar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Quitter l'app à la fermeture
        setBackground(COLOR_BG);
        buildUI();          // Construction de tous les composants graphiques
        generateMaze(15);   // Labyrinthe de démo dès l'ouverture
        pack();             // Ajuste la taille de la fenêtre au contenu
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setVisible(true);
    }

    /**
     * Construit et assemble tous les composants graphiques de la fenêtre.
     * Organisation : BorderLayout principal avec 4 zones (North, Center, East, South).
     */
    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        // ── Zone NORD : barre de boutons et contrôles ──────────────────────
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controlPanel.setBackground(new Color(30, 30, 50));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel sizeLabel = styledLabel("Taille :");

        // Spinner : valeur entre 5 et 51 (pas de 2 pour garder une structure impaire)
        sizeSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 51, 2));
        styleSpinner(sizeSpinner);

        // Création des boutons avec couleurs distinctives par action
        btnGenerate = styledButton("🎲 Générer",   new Color(60, 100, 180));
        btnLoadFile = styledButton("📂 Charger",   new Color(60, 130, 80));
        btnDFS      = styledButton("🔍 DFS",       new Color(120, 60, 160));
        btnBFS      = styledButton("🌊 BFS",       new Color(30, 130, 160));
        btnCompare  = styledButton("📊 Comparer",  new Color(160, 100, 30));

        controlPanel.add(sizeLabel);
        controlPanel.add(sizeSpinner);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnLoadFile);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL)); // Séparateur visuel
        controlPanel.add(btnDFS);
        controlPanel.add(btnBFS);
        controlPanel.add(btnCompare);

        // ── Zone CENTRE : panneau de dessin du labyrinthe ──────────────────
        // paintComponent est surchargé pour dessiner le labyrinthe à chaque repaint()
        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Efface le fond avant de redessiner
                if (maze != null) drawMaze((Graphics2D) g); // Dessin uniquement si un labyrinthe est chargé
            }
        };
        mazePanel.setBackground(COLOR_BG);

        // ── Zone SUD : barre de statut ─────────────────────────────────────
        statusLabel = new JLabel("Bienvenue ! Générez ou chargez un labyrinthe.");
        statusLabel.setForeground(new Color(200, 200, 220));
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(25, 25, 40));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // ── Zone EST : panneau de légende des couleurs ─────────────────────
        JPanel legend = buildLegendPanel();

        // Assemblage dans le BorderLayout principal
        add(controlPanel,             BorderLayout.NORTH);
        add(new JScrollPane(mazePanel), BorderLayout.CENTER); // ScrollPane si le labyrinthe est grand
        add(legend,                   BorderLayout.EAST);
        add(statusPanel,              BorderLayout.SOUTH);

        // ── Listeners : associe chaque bouton à son action ─────────────────
        btnGenerate.addActionListener(e -> generateMaze((int) sizeSpinner.getValue()));
        btnLoadFile.addActionListener(e -> loadFromFile());
        btnDFS.addActionListener(e -> solveWith(new DFSSolver())); // Lance la résolution DFS
        btnBFS.addActionListener(e -> solveWith(new BFSSolver())); // Lance la résolution BFS
        btnCompare.addActionListener(e -> compareAlgorithms());    // Lance la comparaison des deux
    }

    /**
     * Génère un nouveau labyrinthe aléatoire de taille size×size
     * et réinitialise l'état de résolution.
     */
    private void generateMaze(int size) {
        MazeGenerator gen = new MazeGenerator();
        maze = gen.generate(size, size); // Génération aléatoire (ex : DFS récursif ou Prim)
        lastResult = null;               // Efface le résultat précédent
        updateMazePanelSize();           // Redimensionne le panneau selon la nouvelle taille
        statusLabel.setText("Labyrinthe " + maze.getRows() + "×" + maze.getCols() + " généré.");
        mazePanel.repaint();
    }

    /**
     * Ouvre un sélecteur de fichier pour charger un labyrinthe depuis un fichier .txt.
     * Affiche une erreur si le fichier est invalide ou illisible.
     */
    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser(".");  // Ouvre dans le répertoire courant
        chooser.setDialogTitle("Charger un labyrinthe (.txt)");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                maze = MazeLoader.loadFromFile(file.getAbsolutePath());
                lastResult = null;
                updateMazePanelSize();
                statusLabel.setText("Labyrinthe chargé : " + file.getName() +
                        " (" + maze.getRows() + "×" + maze.getCols() + ")");
                mazePanel.repaint();
            } catch (IOException ex) {
                // Affiche une boîte de dialogue d'erreur si le chargement échoue
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(),
                        "Erreur de chargement", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Résout le labyrinthe avec l'algorithme passé en paramètre (DFS ou BFS),
     * met à jour la barre de statut avec les métriques et redessine.
     *
     * @param solver L'implémentation de MazeSolver à utiliser
     */
    private void solveWith(MazeSolver solver) {
        if (maze == null) return; // Rien à résoudre si pas de labyrinthe chargé
        maze.resetVisited();      // Réinitialise les cellules marquées "visitées"
        lastResult = solver.solve(maze);

        // Construction du message : résolu ou non, avec métriques de performance
        String msg = lastResult.isSolved()
                ? String.format("%s → Chemin: %d cases | Explorées: %d | %.3f ms",
                    solver.getName(), lastResult.getPathLength(),
                    lastResult.getStepsExplored(), lastResult.getExecutionTimeMs())
                : solver.getName() + " → Aucun chemin trouvé !";

        statusLabel.setText(msg);
        mazePanel.repaint(); // Redessine pour afficher la solution sur la grille
    }

    /**
     * Compare les algorithmes DFS et BFS sur le labyrinthe courant :
     * exécute les deux séquentiellement, affiche les résultats dans
     * la barre de statut et dans une boîte de dialogue récapitulative.
     */
    private void compareAlgorithms() {
        if (maze == null) return;

        DFSSolver dfs = new DFSSolver();
        BFSSolver bfs = new BFSSolver();

        SolverResult dfsR = dfs.solve(maze);
        maze.resetVisited();        // Reset entre les deux exécutions pour équité
        SolverResult bfsR = bfs.solve(maze);

        // Résumé en une ligne dans la barre de statut
        String msg = String.format(
                "DFS → %d cases, %d explorées, %.3f ms    |    BFS → %d cases, %d explorées, %.3f ms",
                dfsR.getPathLength(), dfsR.getStepsExplored(), dfsR.getExecutionTimeMs(),
                bfsR.getPathLength(), bfsR.getStepsExplored(), bfsR.getExecutionTimeMs());
        statusLabel.setText(msg);

        // Tableau de comparaison dans une boîte de dialogue modale
        JOptionPane.showMessageDialog(this,
                String.format(
                        "Comparaison DFS vs BFS\n\n" +
                        "─────────────────────────────────────\n" +
                        "                    DFS         BFS\n" +
                        "Longueur chemin  : %6d      %6d\n" +
                        "Cases explorées  : %6d      %6d\n" +
                        "Temps (ms)       : %9.4f  %9.4f\n" +
                        "─────────────────────────────────────\n\n" +
                        "BFS garantit le chemin le plus court.\n" +
                        "DFS est plus rapide en mémoire.",
                        dfsR.getPathLength(), bfsR.getPathLength(),
                        dfsR.getStepsExplored(), bfsR.getStepsExplored(),
                        dfsR.getExecutionTimeMs(), bfsR.getExecutionTimeMs()),
                "Comparaison DFS vs BFS",
                JOptionPane.INFORMATION_MESSAGE);

        mazePanel.repaint();
    }

    /**
     * Dessine toutes les cellules du labyrinthe dans le panneau graphique.
     * Pour chaque cellule : fond coloré selon son type, contour léger pour les passages,
     * et lettre "S"/"E" pour le départ et l'arrivée.
     *
     * @param g Le contexte graphique 2D fourni par Swing
     */
    private void drawMaze(Graphics2D g) {
        // Active l'antialiasing pour un rendu plus lisse
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Cell[][] grid = maze.getGrid();
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                // 1. Remplissage de la cellule avec la couleur correspondant à son type
                g.setColor(cellColor(grid[r][c]));
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // 2. Contour semi-transparent uniquement sur les passages (pas les murs)
                if (grid[r][c].getType() != Cell.Type.WALL) {
                    g.setColor(new Color(200, 200, 220, 30)); // Alpha 30 = très transparent
                    g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                }

                // 3. Lettre centrée "S" (Start) ou "E" (End) pour identifier les bornes
                if (grid[r][c].isStart() || grid[r][c].isEnd()) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("SansSerif", Font.BOLD, CELL_SIZE - 4));
                    String letter = grid[r][c].isStart() ? "S" : "E";
                    FontMetrics fm = g.getFontMetrics();
                    // Calcul du centrage horizontal et vertical de la lettre
                    int x = c * CELL_SIZE + (CELL_SIZE - fm.stringWidth(letter)) / 2;
                    int y = r * CELL_SIZE + (CELL_SIZE + fm.getAscent() - fm.getDescent()) / 2;
                    g.drawString(letter, x, y);
                }
            }
        }
    }

    /**
     * Retourne la couleur d'affichage associée au type d'une cellule.
     * Utilise un switch sur l'énumération Cell.Type.
     *
     * @param cell La cellule dont on veut la couleur
     * @return La couleur correspondante
     */
    private Color cellColor(Cell cell) {
        switch (cell.getType()) {
            case WALL:     return COLOR_WALL;
            case PATH:     return COLOR_PATH;
            case START:    return COLOR_START;
            case END:      return COLOR_END;
            case SOLUTION: return COLOR_SOLUTION;
            case VISITED:  return COLOR_VISITED;
            default:       return Color.GRAY; // Type inconnu : gris de secours
        }
    }

    /**
     * Redimensionne le panneau de dessin pour correspondre exactement
     * à la taille du labyrinthe courant, puis repack la fenêtre.
     */
    private void updateMazePanelSize() {
        if (maze == null) return;
        int w = maze.getCols() * CELL_SIZE; // Largeur en pixels
        int h = maze.getRows() * CELL_SIZE; // Hauteur en pixels
        mazePanel.setPreferredSize(new Dimension(w, h));
        mazePanel.revalidate(); // Force le recalcul du layout
        pack();                 // Redimensionne la fenêtre selon le nouveau contenu
    }

    /**
     * Construit le panneau de légende affiché à droite,
     * associant chaque couleur à sa signification.
     */
    private JPanel buildLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Empilage vertical
        panel.setBackground(new Color(25, 25, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(styledLabel("Légende"));
        panel.add(Box.createVerticalStrut(8)); // Espace entre le titre et les items
        panel.add(legendItem(COLOR_WALL,     "Mur"));
        panel.add(legendItem(COLOR_PATH,     "Passage"));
        panel.add(legendItem(COLOR_START,    "Départ (S)"));
        panel.add(legendItem(COLOR_END,      "Arrivée (E)"));
        panel.add(legendItem(COLOR_SOLUTION, "Solution (+)"));
        panel.add(legendItem(COLOR_VISITED,  "Exploré (·)"));
        return panel;
    }

    /**
     * Crée une ligne de légende : carré coloré + texte descriptif.
     *
     * @param color La couleur à afficher
     * @param text  Le libellé correspondant
     */
    private JPanel legendItem(Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row.setBackground(new Color(25, 25, 40));

        // Carré coloré opaque de 16×16 pixels
        JLabel colorBox = new JLabel("  ");
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 220));
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));

        row.add(colorBox);
        row.add(label);
        return row;
    }

    /**
     * Crée un bouton stylisé avec couleur de fond, texte noir,
     * sans bordure native Swing, pour un rendu moderne et cohérent.
     *
     * @param text Le libellé du bouton
     * @param bg   La couleur de fond
     */
    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);    // Texte noir pour contraste sur fonds colorés
        btn.setFocusPainted(false);        // Supprime le contour de focus Swing par défaut
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setOpaque(true);              // Nécessaire pour que setBackground() soit visible
        btn.setBorderPainted(false);      // Pas de bordure Swing native
        btn.setContentAreaFilled(true);   // Assure que la couleur de fond est bien peinte
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12)); // Padding interne
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Curseur pointeur
        return btn;
    }

    /**
     * Crée un JLabel stylisé avec la typographie et la couleur de l'interface.
     *
     * @param text Le texte à afficher
     */
    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 220));
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    /**
     * Applique le style commun au JSpinner (police monospace, taille fixe).
     *
     * @param spinner Le spinner à styliser
     */
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Monospaced", Font.PLAIN, 12));
        spinner.setPreferredSize(new Dimension(60, 28));
    }
}