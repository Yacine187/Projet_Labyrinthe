/**
 * Classe utilitaire permettant de comparer les performances des algorithmes
 * DFS (Depth First Search) et BFS (Breadth First Search) sur un même labyrinthe.
 *
 * <p>Cette classe exécute les deux algorithmes successivement sur le labyrinthe
 * fourni, collecte leurs résultats via {@link SolverResult}, puis affiche :</p>
 * <ul>
 *   <li>Un tableau comparatif formaté en ASCII (résolu, longueur du chemin,
 *       cases explorées, temps d'exécution)</li>
 *   <li>Une analyse automatique désignant le meilleur algorithme selon les critères</li>
 *   <li>Une conclusion théorique sur les forces et faiblesses de chaque algorithme</li>
 * </ul>
 *
 * <p>Tous les membres sont {@code static} : cette classe n'a pas besoin d'être
 * instanciée, elle s'utilise directement via {@code PerformanceComparator.compare(maze)}.</p>
 *
 * <p><b>Exemple d'utilisation :</b></p>
 * <pre>{@code
 *   Maze maze = MazeLoader.loadFromFile("labyrinthe.txt");
 *   PerformanceComparator.compare(maze);
 * }</pre>
 *
 * @author  Groupe X – Master 1 GLSI/SRT, ESP Dakar
 * @version 1.0
 * @see     DFSSolver
 * @see     BFSSolver
 * @see     SolverResult
 */
public class PerformanceComparator {

    // ══════════════════════════════════════════════════════════════
    //  CONSTANTES ANSI POUR LA MISE EN FORME CONSOLE
    // ══════════════════════════════════════════════════════════════

    /**
     * Code ANSI de réinitialisation : remet la couleur et le style par défaut
     * après chaque séquence colorée. Sans ce code, toute la suite du terminal
     * hériterait de la dernière couleur appliquée.
     */
    private static final String RESET  = "\u001B[0m";

    /**
     * Code ANSI pour afficher le texte en <b>gras</b>.
     * Utilisé pour les titres de section (ex: "ANALYSE", "CONCLUSION THÉORIQUE").
     */
    private static final String BOLD   = "\u001B[1m";

    /**
     * Code ANSI pour la couleur <span style="color:green">verte</span>.
     * Utilisée pour signaler le meilleur résultat dans l'analyse comparative.
     */
    private static final String GREEN  = "\u001B[32m";

    /**
     * Code ANSI pour la couleur <span style="color:yellow">jaune</span>.
     * Utilisée quand les deux algorithmes obtiennent le même résultat (égalité).
     */
    private static final String YELLOW = "\u001B[33m";

    /**
     * Code ANSI pour la couleur <span style="color:cyan">cyan</span>.
     * Utilisée dans les cas particuliers (ex: DFS plus court que BFS).
     */
    private static final String CYAN   = "\u001B[36m";


    // ══════════════════════════════════════════════════════════════
    //  MÉTHODE PUBLIQUE
    // ══════════════════════════════════════════════════════════════

    /**
     * Lance DFS et BFS sur le labyrinthe donné, puis affiche les résultats comparatifs.
     *
     * <p><b>Ordre des opérations :</b></p>
     * <ol>
     *   <li>Exécution de DFS → résultat stocké dans {@code dfResult}</li>
     *   <li>Exécution de BFS → résultat stocké dans {@code bfsResult}</li>
     *   <li>Affichage du tableau comparatif</li>
     *   <li>Affichage de l'analyse et de la conclusion théorique</li>
     *   <li>Réinitialisation du labyrinthe et re-exécution de BFS pour conserver
     *       le chemin optimal marqué dans le labyrinthe (utile si l'appelant
     *       veut afficher le labyrinthe résolu après la comparaison)</li>
     * </ol>
     *
     * @param maze Le labyrinthe à résoudre. Ne doit pas être {@code null}.
     *             Il doit contenir un point de départ (S) et une sortie (E).
     */
    public static void compare(Maze maze) {
        System.out.println(BOLD + "\n📊 COMPARAISON DES PERFORMANCES\n" + RESET);

        // Instanciation des deux solveurs
        // Chaque solveur implémente l'interface MazeSolver et fournit sa propre logique
        DFSSolver dfs = new DFSSolver();
        BFSSolver bfs = new BFSSolver();

        // Exécution successive des deux algorithmes sur le même labyrinthe.
        // Chaque appel à solve() retourne un SolverResult contenant :
        //   - isSolved()          → labyrinthe résolu ou non
        //   - getPathLength()     → nombre de cases dans le chemin trouvé
        //   - getStepsExplored()  → nombre total de cases explorées (visitées)
        //   - getExecutionTimeMs()→ temps de résolution en millisecondes
        SolverResult dfResult  = dfs.solve(maze);
        SolverResult bfsResult = bfs.solve(maze);

        // Affichage du tableau ASCII formaté avec les 4 critères de comparaison
        printComparativeTable(dfResult, bfsResult);

        // Affichage de l'analyse automatique + conclusion théorique
        printWinner(dfResult, bfsResult);

        // ⚠️ Après les deux résolutions, l'état interne du labyrinthe (cases visitées,
        // chemin marqué) correspond au dernier algorithme exécuté (BFS).
        // On remet le labyrinthe à zéro puis on re-exécute BFS pour s'assurer
        // que le chemin le plus court (BFS) est bien ce qui sera affiché si
        // l'appelant appelle MazeDisplay.print() juste après.
        maze.resetVisited();
        bfs.solve(maze);
    }


    // ══════════════════════════════════════════════════════════════
    //  MÉTHODES PRIVÉES D'AFFICHAGE
    // ══════════════════════════════════════════════════════════════

    /**
     * Affiche un tableau ASCII comparant les résultats de DFS et BFS
     * selon quatre critères : résolution, longueur du chemin, cases explorées
     * et temps d'exécution.
     *
     * <p>Le format utilisé est {@link System#out#printf} avec des spécificateurs
     * de format précis pour aligner les colonnes :</p>
     * <ul>
     *   <li>{@code %-25s} → texte aligné à gauche sur 25 caractères</li>
     *   <li>{@code %-13d} → entier aligné à gauche sur 13 caractères</li>
     *   <li>{@code %-10.4f ms} → flottant avec 4 décimales sur 10 caractères</li>
     * </ul>
     *
     * @param dfs Résultat de l'exécution de l'algorithme DFS.
     * @param bfs Résultat de l'exécution de l'algorithme BFS.
     */
    private static void printComparativeTable(SolverResult dfs, SolverResult bfs) {

        // Ligne de bordure supérieure du tableau
        System.out.println("┌───────────────────────────┬───────────────┬───────────────┐");
        // En-tête du tableau avec les noms des deux colonnes
        System.out.println("│ Critère                   │      DFS      │      BFS      │");
        // Ligne de séparation entre l'en-tête et les données
        System.out.println("├───────────────────────────┼───────────────┼───────────────┤");

        // Ligne 1 : Le labyrinthe a-t-il été résolu ?
        // isSolved() retourne true si un chemin entre S et E a été trouvé
        System.out.printf("│ %-25s │ %-13s │ %-13s │%n",
                "Résolu",
                dfs.isSolved() ? "✅ Oui" : "❌ Non",
                bfs.isSolved() ? "✅ Oui" : "❌ Non");

        // Ligne 2 : Longueur du chemin (nombre de cases de S à E inclus)
        // BFS garantit toujours la valeur minimale possible ici
        System.out.printf("│ %-25s │ %-13d │ %-13d │%n",
                "Longueur du chemin",
                dfs.getPathLength(),
                bfs.getPathLength());

        // Ligne 3 : Nombre de cases explorées (cases visitées avant de trouver la sortie)
        // Plus ce nombre est petit, plus l'algorithme est efficace en mémoire
        System.out.printf("│ %-25s │ %-13d │ %-13d │%n",
                "Cases explorées",
                dfs.getStepsExplored(),
                bfs.getStepsExplored());

        // Ligne 4 : Temps d'exécution mesuré en millisecondes avec 4 décimales
        // Calculé dans solve() via System.nanoTime() converti en ms
        System.out.printf("│ %-25s │ %-10.4f ms │ %-10.4f ms │%n",
                "Temps d'exécution",
                dfs.getExecutionTimeMs(),
                bfs.getExecutionTimeMs());

        // Ligne de bordure inférieure du tableau
        System.out.println("└───────────────────────────┴───────────────┴───────────────┘");
        System.out.println();
    }

    /**
     * Analyse les résultats et affiche une conclusion en deux parties :
     * une analyse automatique basée sur les valeurs mesurées, et une
     * conclusion théorique générale sur DFS et BFS.
     *
     * <p><b>Logique de l'analyse automatique :</b></p>
     * <ul>
     *   <li>Si BFS a un chemin plus court → victoire BFS (comportement théoriquement attendu)</li>
     *   <li>Si égalité de longueur → message neutre</li>
     *   <li>Si DFS plus court → cas particulier signalé (possible sur certains labyrinthes)</li>
     *   <li>Comparaison du nombre de cases explorées pour désigner le plus efficace en mémoire</li>
     * </ul>
     *
     * <p>L'analyse ne s'affiche que si les deux algorithmes ont résolu le labyrinthe
     * ({@code isSolved() == true}), car comparer des résultats partiels n'aurait pas de sens.</p>
     *
     * @param dfs Résultat de l'exécution de l'algorithme DFS.
     * @param bfs Résultat de l'exécution de l'algorithme BFS.
     */
    private static void printWinner(SolverResult dfs, SolverResult bfs) {

        System.out.println(BOLD + "🏆 ANALYSE :" + RESET);

        // On n'effectue la comparaison que si les DEUX algorithmes ont trouvé une solution.
        // Si l'un d'eux n'a pas résolu le labyrinthe, comparer les chemins n'a pas de sens.
        if (dfs.isSolved() && bfs.isSolved()) {

            // ── Comparaison de la longueur du chemin ────────────────────────
            if (bfs.getPathLength() < dfs.getPathLength()) {
                // Cas normal et attendu théoriquement :
                // BFS explore en largeur → garantit le chemin le plus court (optimal)
                System.out.println(GREEN + "  • BFS trouve toujours le chemin le plus court !" + RESET);

            } else if (bfs.getPathLength() == dfs.getPathLength()) {
                // Égalité possible sur des labyrinthes simples avec un seul chemin possible
                System.out.println(YELLOW + "  • Les deux algorithmes ont trouvé des chemins de même longueur." + RESET);

            } else {
                // Cas théoriquement impossible dans un labyrinthe standard,
                // mais peut arriver si la reconstruction du chemin DFS diffère de BFS
                System.out.println(CYAN + "  • DFS a trouvé un chemin plus court dans ce cas." + RESET);
            }

            // ── Comparaison du nombre de cases explorées ─────────────────────
            // Le nombre de cases explorées mesure l'efficacité mémoire de l'algorithme :
            // moins de cases explorées = moins de mémoire consommée pendant la recherche
            if (dfs.getStepsExplored() < bfs.getStepsExplored()) {
                System.out.println(GREEN + "  • DFS a exploré moins de cases ("
                        + dfs.getStepsExplored() + " vs " + bfs.getStepsExplored() + ")." + RESET);
            } else {
                System.out.println(GREEN + "  • BFS a exploré moins de cases ("
                        + bfs.getStepsExplored() + " vs " + dfs.getStepsExplored() + ")." + RESET);
            }
        }

        System.out.println();

        // ── Conclusion théorique générale ────────────────────────────────────
        // Ces conclusions sont valables indépendamment du labyrinthe utilisé ;
        // elles rappellent les propriétés fondamentales des deux algorithmes.
        System.out.println(BOLD + "📚 CONCLUSION THÉORIQUE :" + RESET);

        // BFS utilise une File (Queue) → explore niveau par niveau → chemin le plus court garanti
        System.out.println("  • BFS garantit le chemin le plus court (optimal).");

        // DFS utilise une Pile (Stack) → suit un chemin jusqu'au bout avant de revenir en arrière
        // → consomme moins de mémoire car on ne stocke qu'un chemin à la fois
        System.out.println("  • DFS utilise moins de mémoire (pile vs file).");

        // Recommandation 1 : si l'objectif est de trouver le chemin le plus court → BFS
        System.out.println("  • BFS est préférable pour trouver le chemin optimal.");

        // Recommandation 2 : si la mémoire est contrainte (grand labyrinthe) → DFS
        System.out.println("  • DFS est préférable quand la mémoire est limitée.");

        System.out.println();
    }
}