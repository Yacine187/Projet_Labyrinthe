import java.util.*;

/**
 * Implémentation de l'algorithme DFS (Depth First Search)
 * pour résoudre un labyrinthe.
 * 
 * DFS explore en profondeur : il va le plus loin possible
 * avant de revenir en arrière (backtracking).
 */
public class DFSSolver implements MazeSolver {

    /**
     * Retourne le nom de l'algorithme
     */
    @Override
    public String getName() {
        return "DFS (Depth First Search)";
    }

    /**
     * Méthode principale de résolution du labyrinthe
     */
    @Override
    public SolverResult solve(Maze maze) {

        // Réinitialiser l'état des cellules
        maze.resetVisited();

        // Début du chronométrage
        long startTime = System.nanoTime();

        // Récupération du départ et de l'arrivée
        Cell start = maze.getStart();
        Cell end   = maze.getEnd();

        // Tableau pour mémoriser le parent de chaque cellule
        Cell[][] parent = new Cell[maze.getRows()][maze.getCols()];

        // Tableau pour savoir si une cellule a été visitée
        boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];

        // Pile utilisée pour DFS (structure LIFO)
        Stack<Cell> stack = new Stack<>();

        // On commence par le point de départ
        stack.push(start);
        visited[start.getRow()][start.getCol()] = true;

        int stepsExplored = 0; // Compteur de cases explorées
        boolean found = false; // Indique si la solution est trouvée

        /**
         * Boucle principale DFS
         */
        while (!stack.isEmpty()) {

            // Récupérer et supprimer le dernier élément ajouté (LIFO)
            Cell current = stack.pop();
            stepsExplored++;

            // Si on atteint la sortie → solution trouvée
            if (current.equals(end)) {
                found = true;
                break;
            }

            // Marquer la cellule comme visitée (affichage)
            if (!current.isStart() && !current.isEnd()) {
                current.setType(Cell.Type.VISITED);
            }

            // Explorer les voisins
            for (Cell neighbor : maze.getNeighbors(current)) {

                int nr = neighbor.getRow();
                int nc = neighbor.getCol();

                // Si le voisin n'a pas encore été visité
                if (!visited[nr][nc]) {

                    visited[nr][nc] = true;

                    // Sauvegarder le parent (pour reconstruire le chemin)
                    parent[nr][nc] = current;

                    // Ajouter à la pile (sera exploré en priorité)
                    stack.push(neighbor);
                }
            }
        }

        // Fin du chronométrage
        long endTime = System.nanoTime();

        // Reconstruction du chemin si trouvé
        List<Cell> path = found ? reconstructPath(parent, start, end) : new ArrayList<>();

        // Marquer le chemin solution dans le labyrinthe
        for (Cell c : path) {
            if (!c.isStart() && !c.isEnd()) {
                c.setType(Cell.Type.SOLUTION);
            }
        }

        // Retourner les résultats
        return new SolverResult(getName(), path, stepsExplored, endTime - startTime);
    }

    /**
     * Reconstruit le chemin depuis la fin jusqu'au début
     * en suivant les parents
     */
    private List<Cell> reconstructPath(Cell[][] parent, Cell start, Cell end) {

        LinkedList<Cell> path = new LinkedList<>();
        Cell current = end;

        // Remonter du point d'arrivée vers le départ
        while (current != null) {

            path.addFirst(current); // ajouter au début

            // Arrêt si on atteint le départ
            if (current.equals(start)) break;

            // Aller au parent
            current = parent[current.getRow()][current.getCol()];
        }

        return path;
    }
}
