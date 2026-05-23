# ARKANOID

A Java brick-breaker game with a neon cyberpunk aesthetic built with AWT/Swing.

![Java](https://img.shields.io/badge/Java-8+-orange) ![Version](https://img.shields.io/badge/version-1.1.0-cyan) ![License](https://img.shields.io/badge/license-MIT-green)

---

## Download

**[→ Download v1.1.0](https://github.com/ayushvaish234/arkanoid/releases/tag/v1.1)**

---

## Gameplay

Break all the bricks to complete a level. Miss the ball and it's game over.

- Move the paddle with the **← →** arrow keys
- Ball bounces off walls, the paddle, and bricks
- Each brick hit scores **5 points**
- Clear all bricks to advance to the next level
- 6 levels total — grids get larger and denser as you progress

---

## Levels

| Level | Grid | Bricks |
|-------|------|--------|
| 1 | 5 × 3 | 15 |
| 2 | 5 × 6 | 30 |
| 3 | 6 × 3 | 18 |
| 4 | 5 × 4 | 20 |
| 5 | 7 × 3 | 21 |
| 6 | 6 × 8 | 48 |

---

## Running the Game

**Requirements:** Java 8 or higher, no external dependencies.

```bash
# Clone the repo
git clone https://github.com/ayushvaish234/arkanoid.git
cd arkanoid

# Compile
javac -d out src/mypackage/*.java

# Run
java -cp out mypackage.MainClass
```

Or just grab the `.jar` from the [latest release](https://github.com/ayushvaish234/arkanoid/releases/tag/v1.1) and double-click it.

---

## Project Structure

```
arkanoid/
├── src/mypackage/
│   ├── MainClass.java       — entry point, JFrame setup
│   ├── GamePlay.java        — game loop, input, rendering
│   └── MapGenerator.java    — brick grid, drawing, collision data
└── README.md
```

---

## Controls

| Key | Action |
|-----|--------|
| `←` | Move paddle left |
| `→` | Move paddle right |
| `Enter` | Start next level / restart after game over |

---

## Known Issues

- No sound effects
- Leaderboard button is non-functional
- Ball speed is constant across all levels
- Mouse control not supported during gameplay

---

## Changelog

- [v1.1.0](https://github.com/ayushvaish234/arkanoid/releases/tag/v1.1) — Neon cyberpunk visual overhaul, performance improvements
- [v1.0.0](https://github.com/ayushvaish234/arkanoid/releases/tag/v1) — Initial release
