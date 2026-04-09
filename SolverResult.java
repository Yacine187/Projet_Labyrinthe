import java.util.List;

/**
 * Classe représentant le résultat d'une résolution de labyrinthe.
 * 
 * Elle encapsule toutes les informations importantes après l'exécution
 * d'un algorithme (BFS, DFS, etc.).
 */
public class SolverResult {

    // Liste des cellules représentant le chemin trouvé (du départ à l'arrivée)
    private List<Cell> path;

    // Nombre total de cases explorées pendant la recherche
    private int stepsExplored;

    // Temps d'exécution de l'algorithme en nanosecondes
    private long executionTimeNs;

    // Nom de l'algorithme utilisé (ex: BFS, DFS)
    private String algorithmName;

    /**
     * Constructeur permettant d'initialiser le résultat
     */
    public SolverResult(String algorithmName, List<Cell> path, int stepsExplored, long executionTimeNs) {
        this.algorithmName   = algorithmName;
        this.path            = path;
        this.stepsExplored   = stepsExplored;
        this.executionTimeNs = executionTimeNs;
    }

    /**
     * Indique si un chemin a été trouvé
     * 
     * @return true si le chemin existe, sinon false
     */
    public boolean isSolved() {
        return path != null && !path.isEmpty();
    }

    /**
     * Retourne le chemin solution
     */
    public List<Cell> getPath() {
        return path;
    }

    /**
     * Retourne le nombre de cases explorées
     */
    public int getStepsExplored() {
        return stepsExplored;
    }

    /**
     * Retourne le temps d'exécution en nanosecondes
     */
    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    /**
     * Retourne le nom de l'algorithme utilisé
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Convertit le temps d'exécution en millisecondes
     * (plus lisible pour l'utilisateur)
     */
    public double getExecutionTimeMs() {
        return executionTimeNs / 1_000_000.0;
    }

    /**
     * Retourne la longueur du chemin trouvé
     * 
     * @return nombre de cellules dans le chemin
     */
    public int getPathLength() {
        return path != null ? path.size() : 0;
    }

    /**
     * Représentation textuelle du résultat
     * (utile pour affichage console ou debug)
     */
    @Override
    public String toString() {
        return String.format(
            "[%s] Résolu: %b | Chemin: %d cases | Explorées: %d | Temps: %.3f ms",
            algorithmName,
            isSolved(),
            getPathLength(),
            stepsExplored,
            getExecutionTimeMs()
        );
    }
}
