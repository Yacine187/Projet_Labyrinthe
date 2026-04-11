import java.util.*;

/**
 * Génère aléatoirement un labyrinthe parfait (sans cycles) en utilisant
 * l'algorithme de Recursive Backtracker (DFS).
 *
 * 
 */
public class MazeGenerator {

    private Random random; // Source d'aléa pour mélanger les directions

    /** Constructeur avec graine aléatoire non fixée. */
    public MazeGenerator() {
        this.random = new Random();
    }

    /**
     * Constructeur avec graine fixe pour des résultats reproductibles.
     *
     * @param seed graine du générateur aléatoire
     */
    public MazeGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Génère un labyrinthe de dimensions (rows × cols).
     * Les dimensions sont automatiquement ajustées pour être impaires
     * afin que la structure mur/passage soit cohérente.
     *
     * @param rows nombre de lignes souhaité (min 5)
     * @param cols nombre de colonnes souhaitées (min 5)
     * @return Maze généré avec S en haut-gauche et E en bas-droite
     */
    public Maze generate(int rows, int cols) {
        // Forcer des dimensions impaires pour que murs et passages alternent correctement
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        // Initialiser la grille entièrement en murs
        char[][] chars = new char[rows][cols];
        for (char[] row : chars) Arrays.fill(row, '#');

        // Creuser les passages en partant de la case (1,1)
        carve(chars, 1, 1);

        // Placer le départ en haut-gauche et l'arrivée en bas-droite
        chars[1][1]             = 'S';
        chars[rows - 2][cols - 2] = 'E';

        // Construire et retourner l'objet Maze
        Maze maze = new Maze(rows, cols);
        maze.buildFromChars(chars);
        return maze;
    }

    /**
     * Algorithme Recursive Backtracker : creuse des passages récursivement.
     * À chaque case, on explore les voisins à 2 pas dans un ordre aléatoire.
     * Si le voisin est encore un mur, on abat le mur intermédiaire et on avance.
     *
     * @param chars grille en cours de construction
     * @param r     ligne courante
     * @param c     colonne courante
     */
    private void carve(char[][] chars, int r, int c) {
        // Marquer la case courante comme passage
        chars[r][c] = '=';

        // Préparer les 4 directions et les mélanger pour l'aléatoire
        int[][] dirs = {{-2,0},{2,0},{0,-2},{0,2}};
        shuffleArray(dirs);

        for (int[] d : dirs) {
            int nr = r + d[0]; // Ligne du voisin (2 cases plus loin)
            int nc = c + d[1]; // Colonne du voisin

            // Si le voisin est dans les limites et encore un mur
            if (inBounds(chars, nr, nc) && chars[nr][nc] == '#') {
                // Abattre le mur intermédiaire (case entre la position courante et le voisin)
                chars[r + d[0]/2][c + d[1]/2] = '=';
                // Continuer le creusage depuis le voisin (récursion)
                carve(chars, nr, nc);
            }
        }
    }

    /**
     * Vérifie qu'une position est à l'intérieur de la grille (hors bordure).
     *
     * @param chars grille de référence
     * @param r     ligne à tester
     * @param c     colonne à tester
     * @return true si la position est valide
     */
    private boolean inBounds(char[][] chars, int r, int c) {
        // On exclut la bordure (indice 0 et dernière ligne/colonne) pour garder les murs extérieurs
        return r > 0 && r < chars.length - 1 && c > 0 && c < chars[0].length - 1;
    }

    /**
     * Mélange aléatoirement un tableau de directions (algorithme de Fisher-Yates).
     *
     * @param arr tableau à mélanger
     */
    private void shuffleArray(int[][] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1); // Indice aléatoire entre 0 et i
            // Échanger arr[i] et arr[j]
            int[] tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}
