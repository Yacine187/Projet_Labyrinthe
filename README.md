Projet Labyrinthe - Résolution et Comparaison BFS / DFS
Description

Ce projet a pour objectif de concevoir une application Java permettant de :

Générer ou charger un labyrinthe
Visualiser le labyrinthe (console et interface graphique)
Résoudre le labyrinthe avec différents algorithmes
Comparer leurs performances
 Algorithmes implémentés
🔹 DFS (Depth First Search)
Exploration en profondeur
Utilise une pile (Stack)
Ne garantit pas le plus court chemin
🔹 BFS (Breadth First Search)
Exploration en largeur
Utilise une file (Queue)
Garantit le plus court chemin

 Structure du projet
 
Fonctionnalités
✔ Génération aléatoire de labyrinthes
✔ Chargement depuis fichier
✔ Résolution avec DFS et BFS
✔ Affichage console coloré
✔ Interface graphique (Swing)
✔ Comparaison des performances
✔ Mesure du temps d'exécution

Répartition du travail

Ndeye Yacine Diallo : Structure & Gestion du labyrinthe
Maze.java
Cell.java
MazeLoader.java
MazeGenerator.java

Amy collé Ngom : Algorithmes de résolution
DFSSolver.java
BFSSolver.java
MazeSolver.java
SolverResult.java

Awa Oumoul Khairy Sall: Affichage & Interface
MazeDisplay.java
PerformanceComparator.java
MazeGUI.java
Main.java
