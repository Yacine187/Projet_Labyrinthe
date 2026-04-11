/**
 * Affichage textuel (console) du labyrinthe et des résultats.
 *
 
 */
public class MazeDisplay {

    // Codes couleur ANSI pour coloriser l'affichage dans le terminal
    private static final String RESET   = "\u001B[0m";  // Réinitialiser la couleur
    private static final String RED     = "\u001B[31m"; // Rouge
    private static final String GREEN   = "\u001B[32m"; // Vert
    private static final String YELLOW  = "\u001B[33m"; // Jaune
    private static final String BLUE    = "\u001B[34m"; // Bleu
    private static final String CYAN    = "\u001B[36m"; // Cyan
    private static final String WHITE   = "\u001B[37m"; // Blanc
    private static final String BOLD    = "\u001B[1m";  // Gras

    /**
     * Affiche le labyrinthe dans la console avec coloration ANSI.
     * Chaque type de cellule a une couleur et un symbole distincts.
     *
     * @param maze le labyrinthe à afficher
     */
    public static void print(Maze maze) {
        Cell[][] grid = maze.getGrid();
        System.out.println();
        // Parcourir la grille ligne par ligne
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                System.out.print(cellToColoredChar(grid[r][c])); // Afficher chaque cellule colorée
            }
            System.out.println(); // Retour à la ligne après chaque ligne de la grille
        }
        System.out.println();
    }

    /**
     * Affiche le labyrinthe avec le chemin solution mis en évidence.
     * Si aucun chemin n'a été trouvé, affiche un message d'erreur.
     *
     * @param maze   le labyrinthe à afficher
     * @param result le résultat de la résolution (contient le chemin et les stats)
     */
    public static void printWithSolution(Maze maze, SolverResult result) {
        if (!result.isSolved()) {
            // Aucun chemin trouvé : afficher un message d'erreur en rouge
            System.out.println(RED + BOLD + "Aucun chemin trouvé !" + RESET);
            return;
        }
        // Afficher l'en-tête avec le nom de l'algorithme utilisé
        System.out.println(GREEN + BOLD + "=== Solution (" + result.getAlgorithmName() + ") ===" + RESET);
        print(maze); // Afficher la grille (les cases SOLUTION sont déjà marquées '+')
    }

    /**
     * Convertit une cellule en caractère coloré selon son type.
     *
     * @param cell la cellule à convertir
     * @return chaîne ANSI représentant visuellement la cellule
     */
    private static String cellToColoredChar(Cell cell) {
        switch (cell.getType()) {
            case WALL:     return WHITE  + "█" + RESET; // Mur : bloc blanc plein
            case PATH:     return " ";                  // Passage : espace vide
            case START:    return GREEN  + BOLD + "S" + RESET; // Départ : S vert gras
            case END:      return RED    + BOLD + "E" + RESET; // Arrivée : E rouge gras
            case SOLUTION: return YELLOW + "+" + RESET;        // Chemin : + jaune
            case VISITED:  return CYAN   + "·" + RESET;        // Exploré : point cyan
            default:       return "?";                          // Inconnu
        }
    }

    /**
     * Affiche un résumé des performances d'un algorithme sous forme de tableau.
     * Inclut : nom de l'algo, longueur du chemin, cases explorées, temps d'exécution.
     *
     * @param result résultat de la résolution à afficher
     */
    public static void printResult(SolverResult result) {
        // Ligne du haut du tableau
        System.out.println(BOLD + "┌─────────────────────────────────────────────┐" + RESET);
        // Nom de l'algorithme
        System.out.printf(BOLD  + "│ %-43s │%n" + RESET, result.getAlgorithmName());
        // Séparateur
        System.out.println(BOLD + "├─────────────────────────────────────────────┤" + RESET);

        if (result.isSolved()) {
            // Afficher les stats si le labyrinthe a été résolu
            System.out.printf("│ ✅ Résolu           : %-22s│%n", "Oui");
            System.out.printf("│ 📏 Longueur chemin  : %-22d│%n", result.getPathLength());
        } else {
            System.out.printf("│ ❌ Résolu           : %-22s│%n", "Non");
        }

        // Stats communes : cases explorées et temps d'exécution
        System.out.printf("│ 🔍 Cases explorées  : %-22d│%n", result.getStepsExplored());
        System.out.printf("│ ⏱️  Temps d'exécution: %-19.4f ms│%n", result.getExecutionTimeMs());

        // Ligne du bas du tableau
        System.out.println(BOLD + "└─────────────────────────────────────────────┘" + RESET);
        System.out.println();
    }

    /**
     * Affiche le titre et les informations du programme en console.
     */
    public static void printHeader() {
        System.out.println(BLUE + BOLD);
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        RÉSOLUTION DE LABYRINTHE              ║");
        System.out.println("║   DFS vs BFS  -  ESP Dakar  -  Master 1     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println(RESET);
    }
}