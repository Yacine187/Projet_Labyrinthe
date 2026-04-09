/**
 * Interface représentant un solveur de labyrinthe.
 * 
 * Elle définit les méthodes que doivent implémenter
 * tous les algorithmes de résolution (BFS, DFS, etc.).
 */
public interface MazeSolver {

    /**
     * Méthode principale pour résoudre un labyrinthe.
     * 
     * @param maze le labyrinthe à résoudre
     * @return un objet SolverResult contenant :
     *         - le chemin trouvé
     *         - le nombre d'étapes explorées
     *         - le temps d'exécution
     */
    SolverResult solve(Maze maze);

    /**
     * Retourne le nom de l'algorithme utilisé.
     * 
     * Exemple : "BFS", "DFS"
     * 
     * @return le nom du solveur
     */
    String getName();
}
