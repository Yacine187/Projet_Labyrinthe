import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

/**
 * Classe principale du programme de résolution de labyrinthe.
 *
 * <p>Cette classe constitue le point d'entrée unique du programme ({@code main}).
 * Elle détecte le mode de lancement souhaité (console ou interface graphique)
 * et délègue l'exécution à la méthode correspondante.</p>
 *
 * <p><b>Modes de lancement :</b></p>
 * <ul>
 *   <li>{@code java Main}        → Mode console avec menu interactif</li>
 *   <li>{@code java Main --gui}  → Interface graphique (Swing)</li>
 * </ul>
 *
 * <p><b>Classes utilisées :</b></p>
 * <ul>
 *   <li>{@link Maze}               – Structure du labyrinthe (matrice 2D)</li>
 *   <li>{@link MazeGenerator}      – Génération aléatoire d'un labyrinthe</li>
 *   <li>{@link MazeLoader}         – Chargement/sauvegarde depuis/vers fichier</li>
 *   <li>{@link MazeDisplay}        – Affichage console du labyrinthe</li>
 *   <li>{@link DFSSolver}          – Algorithme DFS (Depth First Search)</li>
 *   <li>{@link BFSSolver}          – Algorithme BFS (Breadth First Search)</li>
 *   <li>{@link PerformanceComparator} – Comparaison DFS vs BFS</li>
 *   <li>{@link MazeGUI}            – Fenêtre graphique Swing</li>
 * </ul>
 *
 * @author  Groupe X – Master 1 GLSI/SRT, ESP Dakar
 * @version 1.0
 */
public class Main {

    /**
     * Point d'entrée du programme.
     *
     * <p>Analyse le premier argument de la ligne de commande :
     * si {@code --gui} est fourni, lance l'interface graphique ;
     * sinon, lance le mode console interactif.</p>
     *
     * @param args Arguments de la ligne de commande.
     *             Valeur reconnue : {@code --gui} pour activer l'interface graphique.
     */
    public static void main(String[] args) {
        // Vérifie si l'utilisateur a passé l'argument "--gui"
        // args.length > 0 : s'assure qu'au moins un argument a été fourni
        // pour éviter un ArrayIndexOutOfBoundsException
        if (args.length > 0 && args[0].equals("--gui")) {
            launchGUI(); // Mode interface graphique
        } else {
            launchConsole(); // Mode console par défaut
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  MODE CONSOLE
    // ══════════════════════════════════════════════════════════════

    /**
     * Lance le mode console avec un menu interactif.
     *
     * <p>La méthode affiche un en-tête, puis entre dans une boucle infinie
     * qui présente le menu principal et traite le choix de l'utilisateur.
     * La boucle se termine uniquement quand l'utilisateur choisit l'option 0.</p>
     *
     * <p>Un objet {@link Maze} est conservé en mémoire entre les opérations ;
     * il vaut {@code null} tant qu'aucun labyrinthe n'a été généré ou chargé.</p>
     */
    private static void launchConsole() {

        // Affiche le titre/bannière ASCII du programme
        MazeDisplay.printHeader();

        // Scanner pour lire les saisies de l'utilisateur depuis le terminal
        Scanner sc = new Scanner(System.in);

        // Variable qui contiendra le labyrinthe courant.
        // Elle reste null jusqu'à ce que l'utilisateur en génère ou en charge un.
        Maze maze = null;

        // Boucle principale : tourne indéfiniment jusqu'au choix "0" (Quitter)
        while (true) {

            printMenu(); // Affiche les options du menu

            // Lit la saisie de l'utilisateur et supprime les espaces superflus
            String choice = sc.nextLine().trim();

            // Traitement du choix via un switch pour séparer clairement chaque cas
            switch (choice) {

                // ── Option 1 : Générer un labyrinthe aléatoire ──────────────
                case "1":
                    System.out.print("Taille (ex: 15) : ");
                    try {
                        // parseInt() peut lever NumberFormatException si la saisie n'est pas un entier
                        int size = Integer.parseInt(sc.nextLine().trim());

                        // Taille minimale fixée à 5 pour avoir un labyrinthe exploitable
                        if (size < 5) {
                            System.out.println("Taille minimale : 5");
                            break;
                        }

                        // Génère un labyrinthe carré size×size
                        // MazeGenerator utilise un algorithme (ex: DFS récursif) en interne
                        maze = new MazeGenerator().generate(size, size);

                        System.out.println("✅ Labyrinthe " + maze.getRows() + "×" + maze.getCols() + " généré.");

                        // Affiche le labyrinthe brut dans le terminal
                        MazeDisplay.print(maze);

                    } catch (NumberFormatException e) {
                        // L'utilisateur a tapé autre chose qu'un nombre entier
                        System.out.println("❌ Taille invalide.");
                    }
                    break;

                // ── Option 2 : Charger un labyrinthe depuis un fichier texte ─
                case "2":
                    System.out.print("Chemin du fichier : ");
                    String path = sc.nextLine().trim();
                    try {
                        // MazeLoader lit le fichier ligne par ligne et construit la matrice char[][]
                        // IOException est levée si le fichier est introuvable ou illisible
                        maze = MazeLoader.loadFromFile(path);

                        System.out.println("✅ Labyrinthe chargé (" + maze.getRows() + "×" + maze.getCols() + ")");
                        MazeDisplay.print(maze);

                    } catch (IOException e) {
                        // Affiche le message d'erreur Java (ex: "No such file or directory")
                        System.out.println("❌ Erreur : " + e.getMessage());
                    }
                    break;

                // ── Option 3 : Résoudre avec DFS (Depth First Search) ────────
                case "3":
                    // Impossible de résoudre si aucun labyrinthe n'est chargé
                    if (maze == null) {
                        System.out.println("❌ Aucun labyrinthe chargé.");
                        break;
                    }

                    // DFSSolver.solve() retourne un SolverResult contenant :
                    // - la liste des cases du chemin trouvé
                    // - le nombre de cases explorées
                    // - le temps d'exécution en ms
                    SolverResult dfsResult = new DFSSolver().solve(maze);

                    // Affiche les statistiques (étapes, temps, longueur du chemin)
                    MazeDisplay.printResult(dfsResult);

                    // Affiche le labyrinthe avec le chemin marqué par des '+'
                    MazeDisplay.printWithSolution(maze, dfsResult);
                    break;

                // ── Option 4 : Résoudre avec BFS (Breadth First Search) ──────
                case "4":
                    if (maze == null) {
                        System.out.println("❌ Aucun labyrinthe chargé.");
                        break;
                    }

                    // BFSSolver garantit le chemin le PLUS COURT (propriété du BFS)
                    // contrairement au DFS qui trouve un chemin mais pas nécessairement le plus court
                    SolverResult bfsResult = new BFSSolver().solve(maze);

                    MazeDisplay.printResult(bfsResult);
                    MazeDisplay.printWithSolution(maze, bfsResult);
                    break;

                // ── Option 5 : Comparer les performances DFS vs BFS ──────────
                case "5":
                    if (maze == null) {
                        System.out.println("❌ Aucun labyrinthe chargé.");
                        break;
                    }

                    // Lance les deux algorithmes sur le même labyrinthe
                    // et affiche un tableau comparatif (étapes, temps, longueur chemin)
                    PerformanceComparator.compare(maze);

                    // Ré-affiche le labyrinthe pour rappeler visuellement le contexte
                    MazeDisplay.print(maze);
                    break;

                // ── Option 6 : Réafficher le labyrinthe courant ──────────────
                case "6":
                    if (maze == null) {
                        System.out.println("❌ Aucun labyrinthe chargé.");
                        break;
                    }
                    MazeDisplay.print(maze);
                    break;

                // ── Option 7 : Sauvegarder le labyrinthe dans un fichier ─────
                case "7":
                    if (maze == null) {
                        System.out.println("❌ Aucun labyrinthe chargé.");
                        break;
                    }

                    System.out.print("Nom du fichier de sortie : ");
                    String out = sc.nextLine().trim();

                    try {
                        // Écrit le labyrinthe au format texte (même format que le chargement)
                        // pour pouvoir le recharger plus tard avec l'option 2
                        MazeLoader.saveToFile(maze, out);
                        System.out.println("✅ Labyrinthe sauvegardé dans " + out);

                    } catch (IOException e) {
                        System.out.println("❌ Erreur sauvegarde : " + e.getMessage());
                    }
                    break;

                // ── Option 0 : Quitter le programme ──────────────────────────
                case "0":
                    System.out.println("Au revoir ! 👋");
                    sc.close(); // Libère la ressource Scanner (bonne pratique)
                    return;     // Quitte la méthode launchConsole() → fin du programme

                // ── Cas par défaut : saisie non reconnue ──────────────────────
                default:
                    System.out.println("❌ Option invalide.");
            }
            // Fin du switch → retour au début de la boucle while(true) → affiche à nouveau le menu
        }
    }

    /**
     * Affiche le menu principal dans le terminal avec une mise en forme colorée ANSI.
     *
     * <p>Le code {@code \u001B[36m} active la couleur cyan dans les terminaux compatibles,
     * et {@code \u001B[0m} réinitialise la couleur par défaut en fin de bloc.</p>
     *
     * <p>Cette méthode est appelée à chaque itération de la boucle dans
     * {@link #launchConsole()} afin de toujours afficher le menu après chaque action.</p>
     */
    private static void printMenu() {
        // \u001B[36m = couleur cyan ANSI ; \u001B[0m = reset couleur
        System.out.println("\u001B[36m┌─────────────────────────────────────┐");
        System.out.println("│            MENU PRINCIPAL           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. 🎲 Générer un labyrinthe        │");
        System.out.println("│  2. 📂 Charger depuis un fichier    │");
        System.out.println("│  3. 🔍 Résoudre avec DFS            │");
        System.out.println("│  4. 🌊 Résoudre avec BFS            │");
        System.out.println("│  5. 📊 Comparer DFS vs BFS          │");
        System.out.println("│  6. 👁  Afficher le labyrinthe      │");
        System.out.println("│  7. 💾 Sauvegarder                  │");
        System.out.println("│  0. 🚪 Quitter                      │");
        System.out.println("└─────────────────────────────────────┘\u001B[0m");
        System.out.print("Votre choix : ");
    }

    // ══════════════════════════════════════════════════════════════
    //  MODE GUI (INTERFACE GRAPHIQUE)
    // ══════════════════════════════════════════════════════════════

    /**
     * Lance l'interface graphique Swing du programme.
     *
     * <p>L'instanciation de {@link MazeGUI} est effectuée via
     * {@link SwingUtilities#invokeLater(Runnable)}, ce qui garantit que
     * tous les composants graphiques sont créés et modifiés <b>depuis le
     * thread dédié à l'interface Swing (EDT – Event Dispatch Thread)</b>.
     * Ne pas respecter cette règle provoquerait des comportements imprévisibles
     * ou des bugs graphiques difficiles à reproduire.</p>
     *
     * <p>On tente également d'appliquer le Look &amp; Feel natif du système
     * d'exploitation (Windows, macOS, Linux) pour que la fenêtre ressemble
     * aux applications locales. L'exception est ignorée si cela échoue :
     * Swing utilisera alors son Look &amp; Feel par défaut (Metal).</p>
     */
    private static void launchGUI() {

        // invokeLater() planifie l'exécution du Runnable dans l'EDT (Event Dispatch Thread)
        // → obligatoire pour toute création/modification de composants Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Applique le style graphique natif du système (ex: style Windows ou macOS)
                // getSystemLookAndFeelClassName() retourne le nom de la classe LookAndFeel adaptée
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (Exception ignored) {
                // Les exceptions possibles (ClassNotFound, InstantiationException, etc.)
                // ne sont pas bloquantes : Swing fonctionne quand même avec son style par défaut
            }

            // Crée et affiche la fenêtre principale de l'interface graphique
            // Le constructeur de MazeGUI est responsable de l'initialisation des composants
            new MazeGUI();
        });
    }
}