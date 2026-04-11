import java.util.ArrayList;
import java.util.List;

/**
 * Représente le labyrinthe sous forme de matrice 2D de Cell.
 * Contient la grille, la position de départ et d'arrivée.
 *
 
 */
public class Maze {

    private Cell[][] grid; // Matrice 2D contenant toutes les cellules
    private int rows;      // Nombre de lignes de la grille
    private int cols;      // Nombre de colonnes de la grille
    private Cell start;    // Cellule de départ (type START)
    private Cell end;      // Cellule d'arrivée (type END)

    /**
     * Construit un labyrinthe vide de dimensions rows × cols.
     *
     * @param rows nombre de lignes
     * @param cols nombre de colonnes
     */
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols]; // Alloue la grille (cellules à null pour l'instant)
    }

    /**
     * Initialise la grille à partir d'une matrice de caractères.
     * '#' = mur, '=' = passage, 'S' = départ, 'E' = arrivée.
     *
     * @param chars matrice de caractères décrivant le labyrinthe
     */
    public void buildFromChars(char[][] chars) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                // Convertir le caractère en type de cellule
                Cell.Type type;
                switch (chars[r][c]) {
                    case '#': type = Cell.Type.WALL;  break;
                    case 'S': type = Cell.Type.START; break;
                    case 'E': type = Cell.Type.END;   break;
                    default:  type = Cell.Type.PATH;  break; // '=' et autres = passage
                }

                // Créer la cellule et la placer dans la grille
                grid[r][c] = new Cell(r, c, type);

                // Mémoriser le départ et l'arrivée pour y accéder rapidement
                if (type == Cell.Type.START) start = grid[r][c];
                if (type == Cell.Type.END)   end   = grid[r][c];
            }
        }
    }

    /**
     * Retourne la liste des voisins accessibles (non murs) d'une cellule.
     * Ordre exploré : haut, bas, gauche, droite.
     *
     * @param cell cellule dont on veut les voisins
     * @return liste des cellules voisines franchissables
     */
    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();

        // Les 4 directions possibles : haut, bas, gauche, droite
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : directions) {
            int nr = r + d[0]; // Ligne du voisin
            int nc = c + d[1]; // Colonne du voisin

            // Vérifier que le voisin est dans les limites et non un mur
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                Cell neighbor = grid[nr][nc];
                if (!neighbor.isWall()) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    /**
     * Réinitialise les cellules marquées comme visitées ou solution
     * afin de pouvoir relancer un algorithme proprement.
     * Les cellules START et END sont préservées.
     */
    public void resetVisited() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                // Remettre à PATH uniquement les cases marquées par un algo précédent
                if (cell.getType() == Cell.Type.VISITED || cell.getType() == Cell.Type.SOLUTION) {
                    cell.setType(Cell.Type.PATH);
                }
            }
        }
        // Rétablir explicitement S et E qui auraient pu être écrasés
        if (start != null) start.setType(Cell.Type.START);
        if (end   != null) end.setType(Cell.Type.END);
    }

    // --- Getters ---

    /** Retourne la grille complète de cellules. */
    public Cell[][] getGrid() { return grid; }

    /** Retourne le nombre de lignes. */
    public int getRows()      { return rows; }

    /** Retourne le nombre de colonnes. */
    public int getCols()      { return cols; }

    /** Retourne la cellule de départ (S). */
    public Cell getStart()    { return start; }

    /** Retourne la cellule d'arrivée (E). */
    public Cell getEnd()      { return end; }

    /** Retourne la cellule à la position (r, c). */
    public Cell getCell(int r, int c) { return grid[r][c]; }
}
