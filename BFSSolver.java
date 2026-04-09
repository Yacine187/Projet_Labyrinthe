import java.util.*;

/**
 * Implémentation de l'algorithme BFS (Breadth First Search)
 * pour résoudre un labyrinthe.
 * 
 * BFS explore le labyrinthe niveau par niveau
 * et garantit le plus court chemin (si existe).
 */
public class BFSSolver implements MazeSolver {

    /**
     * Retourne le nom de l'algorithme
     */
    @Override
    public String getName() {
        return "BFS (Breadth First Search)";
    }

    /**
     * Méthode principale qui résout le labyrinthe
     */
    @Override
    public SolverResult solve(Maze maze) {

        // Réinitialise les cases visitées
        maze.resetVisited();

        // Début du chronométrage
        long startTime = System.nanoTime();

        // Récupération des points de départ et d'arrivée
        Cell start = maze.getStart();
        Cell end   = maze.getEnd();

        // Tableau pour mémoriser le parent de chaque cellule (pour reconstruire le chemin)
        Cell[][] parent = new Cell[maze.getRows()][maze.getCols()];

        // Tableau pour marquer les cellules déjà visitées
        boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];

        // File (Queue) utilisée par BFS
        Queue<Cell> queue = new LinkedList<>();

        // Ajouter le point de départ à la file
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        int stepsExplored = 0; // Compteur de cases explorées
        boolean found = false; // Indique si on a trouvé la sortie

        /**
         * Boucle principale BFS
         */
        while (!queue.isEmpty()) {

            // Récupère et supprime le premier élément de la file
            Cell current = queue.poll();
            stepsExplored++;

            // Si on atteint la fin → solution trouvée
            if (current.equals(end)) {
                found = true;
                break;
            }

            // Marquer la cellule comme visitée (affichage)
            if (!current.isStart() && !current.isEnd()) {
                current.setType(Cell.Type.VISITED);
            }

            // Parcourir les voisins de la cellule courante
            for (Cell neighbor : maze.getNeighbors(current)) {

                int nr = neighbor.getRow();
                int nc = neighbor.getCol();

                // Si le voisin n'a pas encore été visité
                if (!visited[nr][nc]) {

                    visited[nr][nc] = true;

                    // On mémorise d'où on vient (important pour reconstruire le chemin)
                    parent[nr][nc] = current;

                    // Ajouter le voisin dans la file
                    queue.add(neighbor);
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

        // Retourne le résultat (chemin, nombre d'étapes, temps)
        return new SolverResult(getName(), path, stepsExplored, endTime - startTime);
    }

    /**
     * Reconstruit le chemin de la fin vers le début
     * en utilisant le tableau des parents
     */
    private List<Cell> reconstructPath(Cell[][] parent, Cell start, Cell end) {

        LinkedList<Cell> path = new LinkedList<>();
        Cell current = end;

        // Remonter depuis la fin jusqu'au départ
        while (current != null) {

            path.addFirst(current); // ajouter au début

            // Si on arrive au point de départ → stop
            if (current.equals(start)) break;

            // Aller au parent
            current = parent[current.getRow()][current.getCol()];
        }

        return path;
    }
}
