# Word Search Game - Java Edition

A fully-featured Word Search puzzle game with multiple difficulty levels, countdown timers, and progressive gameplay.

## 🎮 Features

- **Multi-Screen Interface**: Welcome screen, difficulty selection, gameplay, and victory screens
- **Three Difficulty Levels**:
  - 🟦 **Easy**: 5 words, 10 minutes, Blue theme
  - 🟧 **Normal**: 15 words, 10 minutes, Orange theme
  - 🟥 **Hard**: 20 words, 15 minutes, Red theme
- **Countdown Timer**: Visual timer with color-coded warnings
- **Level Progression**: Advance from Easy → Normal → Hard
- **Colorful Highlights**: Each found word gets a unique color highlight
- **Victory Screen**: Special congratulations screen after completing Hard level
- **Replay Options**: Start again at any difficulty or quit

## 🚀 How to Run

```bash
# Compile
javac WordSearchGame.java

# Run
java WordSearchGame
```

## 📖 How to Play

1. **Enter Your Name**: Start by entering your name on the welcome screen
2. **Choose Difficulty**: Select Easy, Normal, or Hard difficulty
3. **Find Words**: 
   - Drag your mouse across letters to select words
   - Words can be horizontal, vertical, or diagonal
   - Words can be forwards or backwards
4. **Complete Level**: Find all words before time runs out
5. **Progress**: Choose "Next Level" to advance or "End Game" to return to menu
6. **Win**: Complete Hard level to see the victory screen!

## 🎨 Color Themes

Each difficulty level has its own unique color scheme:
- **Easy**: Calming light blue background
- **Normal**: Energetic orange background
- **Hard**: Challenging red background

## ⏱️ Timer System

- Countdown timer displays remaining time
- Timer color changes based on urgency:
  - 🟢 Green: More than 3 minutes left
  - 🟠 Orange: 1-3 minutes remaining
  - 🔴 Red: Less than 1 minute - hurry!

## 🎯 Game Mechanics

- **12x12 Grid**: Each level uses a 12x12 letter grid
- **Nature-Themed Words**: Words related to nature and animals
- **Multiple Directions**: Words can appear in 8 different directions
- **Visual Feedback**: Found words are crossed out and highlighted
- **Progress Tracking**: See which words you've found in real-time

## 📄 Documentation

See [GAME_FEATURES.md](GAME_FEATURES.md) for detailed feature documentation.

## 🛠️ Technical Details

- **Language**: Java
- **GUI Framework**: Swing
- **Components**: CardLayout for screen management, Timer for countdown
- **Java Version**: Compatible with Java 8+

Enjoy the game! 🎉
