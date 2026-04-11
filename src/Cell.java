/**
 * Représente une cellule (case) du labyrinthe.
 * Chaque cellule a une position (ligne, colonne) et un type.
 *
 
 */
public class Cell {

    /**
     * Enumération des types possibles d'une cellule.
     */
    public enum Type {
        WALL,     // Mur '#' : case non franchissable
        PATH,     // Passage libre '=' : case franchissable
        START,    // Point de départ 'S'
        END,      // Point d'arrivée 'E'
        VISITED,  // Case visitée durant la résolution '.'
        SOLUTION  // Case faisant partie du chemin solution '+'
    }

    private int row;   // Indice de la ligne dans la grille
    private int col;   // Indice de la colonne dans la grille
    private Type type; // Type actuel de la cellule

    /**
     * Construit une cellule avec sa position et son type.
     *
     * @param row  ligne de la cellule
     * @param col  colonne de la cellule
     * @param type type de la cellule (mur, passage, départ…)
     */
    public Cell(int row, int col, Type type) {
        this.row  = row;
        this.col  = col;
        this.type = type;
    }

    // --- Getters et setters ---

    /** Retourne la ligne de la cellule. */
    public int getRow() { return row; }

    /** Retourne la colonne de la cellule. */
    public int getCol() { return col; }

    /** Retourne le type actuel de la cellule. */
    public Type getType() { return type; }

    /** Modifie le type de la cellule. */
    public void setType(Type type) { this.type = type; }

    // --- Méthodes de test ---

    /** Retourne true si la cellule est un mur. */
    public boolean isWall()  { return type == Type.WALL; }

    /** Retourne true si la cellule est le point de départ. */
    public boolean isStart() { return type == Type.START; }

    /** Retourne true si la cellule est le point d'arrivée. */
    public boolean isEnd()   { return type == Type.END; }

    /**
     * Retourne true si la cellule est franchissable,
     * c'est-à-dire un passage, le départ ou l'arrivée.
     */
    public boolean isPath()  { return type == Type.PATH || type == Type.START || type == Type.END; }

    /**
     * Représentation textuelle de la cellule sous la forme Cell(row,col).
     */
    @Override
    public String toString() {
        return "Cell(" + row + "," + col + ")";
    }

    /**
     * Deux cellules sont égales si elles ont les mêmes coordonnées,
     * indépendamment de leur type.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                  // même référence
        if (!(o instanceof Cell)) return false;      // type différent
        Cell c = (Cell) o;
        return row == c.row && col == c.col;         // même position
    }

    /**
     * Code de hachage basé uniquement sur la position de la cellule.
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}