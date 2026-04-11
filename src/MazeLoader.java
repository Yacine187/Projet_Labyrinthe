import java.io.*;
import java.util.*;

/**
 * Charge un labyrinthe depuis un fichier texte.
 * Format attendu : lignes de caractères (#, =, S, E).
 *
 
 */
public class MazeLoader {

    /**
     * Charge un labyrinthe depuis un fichier texte.
     * Chaque ligne du fichier correspond à une ligne de la grille.
     *
     * @param filepath chemin vers le fichier .txt
     * @return Maze construit à partir du fichier
     * @throws IOException si le fichier est introuvable, vide ou mal formaté
     */
    public static Maze loadFromFile(String filepath) throws IOException {
        List<String> lines = new ArrayList<>();

        // Lire toutes les lignes non vides du fichier
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line); // Ignorer les lignes vides
                }
            }
        }

        // Vérifier que le fichier n'est pas vide
        if (lines.isEmpty()) {
            throw new IOException("Le fichier labyrinthe est vide.");
        }

        int rows = lines.size();            // Nombre de lignes = nombre de lignes lues
        int cols = lines.get(0).length();   // Nombre de colonnes = longueur de la première ligne

        // Construire la matrice de caractères
        char[][] chars = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols && c < line.length(); c++) {
                chars[r][c] = line.charAt(c);
            }
        }

        // Construire l'objet Maze à partir de la matrice
        Maze maze = new Maze(rows, cols);
        maze.buildFromChars(chars);

        // Valider la présence obligatoire de S et E
        if (maze.getStart() == null) throw new IOException("Pas de point de départ 'S' trouvé.");
        if (maze.getEnd()   == null) throw new IOException("Pas de point d'arrivée 'E' trouvé.");

        return maze;
    }

    /**
     * Sauvegarde l'état actuel d'un labyrinthe dans un fichier texte.
     * Les cases solution sont marquées '+' et les cases visitées '.'.
     *
     * @param maze     labyrinthe à sauvegarder
     * @param filepath chemin du fichier de sortie
     * @throws IOException si l'écriture échoue
     */
    public static void saveToFile(Maze maze, String filepath) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            Cell[][] grid = maze.getGrid();
            for (int r = 0; r < maze.getRows(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < maze.getCols(); c++) {
                    sb.append(cellToChar(grid[r][c])); // Convertir chaque cellule en caractère
                }
                pw.println(sb.toString()); // Écrire la ligne dans le fichier
            }
        }
    }

    /**
     * Convertit une cellule en son caractère représentatif pour la sauvegarde.
     *
     * @param cell cellule à convertir
     * @return caractère correspondant au type de la cellule
     */
    private static char cellToChar(Cell cell) {
        switch (cell.getType()) {
            case WALL:     return '#'; // Mur
            case START:    return 'S'; // Départ
            case END:      return 'E'; // Arrivée
            case SOLUTION: return '+'; // Chemin solution
            case VISITED:  return '.'; // Case explorée
            default:       return '='; // Passage libre
        }
    }
}