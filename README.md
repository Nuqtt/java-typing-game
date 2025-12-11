# English Typing Runner Game (Java)

Simple Swing typing runner inspired by the Chrome offline dinosaur game. Type the word on each obstacle before contact; the runner auto-jumps or slides to avoid it. Miss, and the game ends.

## Features

- Random words on obstacles that must be typed exactly
- Two obstacle types: jump (low) and slide (tall)
- Difficulty scales over time (faster + more frequent spawns)
- Score is survival time in seconds
- Press `R` to restart after game over

## Project Structure

```text
typing-game/
└── src/
    └── typinggame/
        ├── GameConfig.java     # constants
        ├── GameEngine.java     # game state, updates, collisions
        ├── GamePanel.java      # Swing panel, input, rendering
        ├── Obstacle.java       # obstacle model + drawing
        ├── Player.java         # player model + physics
        ├── TypingGame.java     # app entry point
        └── WordGenerator.java  # supplies random words
```

## Building and Running

Requires only a JDK (11+ recommended). Ensure `javac` and `java` are on your `PATH`.

1) Compile
```sh
mkdir -p bin
javac -d bin src/typinggame/*.java
```

2) Run
```sh
java -cp bin typinggame.TypingGame
```

## Playing the Game

- Type the word above an obstacle; on the last letter, the runner auto-avoids it.
- Manual controls: `Space` to jump, `Down Arrow` to slide.
- After game over, press `R` to restart.

## Notes

- Difficulty ramps every few seconds (speed up + shorter spawn interval).
- Only alphabetic characters are accepted in the typing input; case is ignored.

Enjoy the game and feel free to extend or modify it!
