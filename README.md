# The Guide's Mansion: Escape Room

A Java/JavaFX-based 2D point and click adventure game created as a course group project. The game is themed around an escape room experience, where the player explores different areas, solves puzzles, interacts with NPCs, manages an inventory, and progresses by unlocking new scenes.

## Overview

The engine supports JSON driven world loading, allowing scenes and area contents to be populated dynamically. Players can click to move around the environment, interact with objects and NPCs, collect and combine items, and solve progression based puzzles to reach a win or lose outcome.

## Features

- Point and click movement and interaction
- JSON-based world and scene population
- Scene switching between different areas
- Dialogue system for interacting with NPCs
- Inventory system with item management
- Item merging/crafting system
- Puzzle and key-based progression
- Win and lose end screens
- JavaFX UI built with Scene Builder support

## Technologies Used

- Java
- JavaFX
- Scene Builder
- JSON-based data loading

## My Contributions

This project was developed as a group project for a software engineering course. My main contributions included:

- Implementing much of the core game logic
- Developing UI functionality using JavaFX
- Designing scene management and screen transitions
- Creating a game controller class to load and switch scenes
- Updating on screen UI elements dynamically
- Building systems related to movement, inventory, and interaction
- Handling much of the bug fixing
- Contributing to project structure and architecture

## How to Run

### Prerequisites
- Java Development Kit (JDK)
- A Java IDE such as VS Code, IntelliJ, or Eclipse

## Running the Project in VS Code

1. Clone the repository.

2. Open VS Code.

3. Select File > Open Folder.

4. Open the escape-room-game folder (the folder that contains pom.xml).

5. Wait for VS Code to finish loading the Java/Maven project.

6. In the Explorer, go to:  src/main/java/ca/uwo/cs2212/group21/App.java

7. Run the project from App.java, or use the terminal command:  mvn clean javafx:run

## Important

Do not open or run App.java by itself outside the full project folder.
The project must be opened from the escape-room-game root directory so that Maven dependencies and the Java classpath load correctly.

## Gameplay Summary

- Click to explore rooms and move between areas
- Collect items and store them in your inventory
- Combine items to create new ones
- Interact with NPCs and solve riddles or puzzles
- Use key items to unlock new scenes and progress
- Escape before time runs out

## Notes

- This repository is based on a course group project
- Assets included in the project are for academic/demo purposes


