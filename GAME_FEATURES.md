# Word Search Game - Feature Documentation

## Overview
Your Word Search Game has been completely transformed into a multi-screen game with difficulty levels, timers, and proper progression system.

## Game Screens

### 1. Welcome Screen
- **Purpose**: First screen when game starts
- **Features**:
  - Welcome message: "Welcome to Word Search Game!"
  - Text field for player name input
  - "Start Game" button to proceed
  - Name validation (cannot proceed without entering name)

### 2. Options/Difficulty Selection Screen
- **Purpose**: Let players choose difficulty level
- **Features**:
  - Title: "Select Difficulty Level"
  - Three difficulty buttons with distinct color themes:

#### Easy Level (Blue Theme)
- **Word Count**: 5 words
- **Time Limit**: 10 minutes
- **Color**: Light Blue (#ADD8E6)
- **Display**: "5 Words • 10 Minutes • Blue Theme"

#### Normal Level (Orange Theme)
- **Word Count**: 15 words
- **Time Limit**: 10 minutes
- **Color**: Orange (#FFC87C)
- **Display**: "15 Words • 10 Minutes • Orange Theme"

#### Hard Level (Red Theme)
- **Word Count**: 20 words
- **Time Limit**: 15 minutes
- **Color**: Light Red (#FFA0A0)
- **Display**: "20 Words • 15 Minutes • Red Theme"

### 3. Game Screen
- **Features**:
  - Top bar showing:
    - Player name and current difficulty level
    - Countdown timer (changes color as time runs out)
  - 12x12 word search grid with difficulty-themed colors
  - Word list panel on the right
  - Interactive word selection with mouse drag
  - Visual highlighting of found words with different colors
  - Automatic level completion detection

### 4. Victory Screen
- **Purpose**: Shown after completing Hard level
- **Features**:
  - Congratulations message with emoji: "🎉 CONGRATULATIONS! 🎉"
  - Player name display
  - Message: "You have completed all difficulty levels!"
  - **Start Again** button - returns to difficulty selection
  - **Quit Game** button - exits the game

## Game Flow

```
Welcome Screen (Enter Name)
    ↓
Options Screen (Choose Difficulty)
    ↓
Game Screen (Play Easy/Normal/Hard)
    ↓
Level Complete Dialog
    ├─→ "Next Level" (if not Hard) → Next difficulty
    └─→ "End Game" → Back to Options Screen
    
Hard Level Complete
    ↓
Victory Screen
    ├─→ "Start Again" → Options Screen
    └─→ "Quit Game" → Exit
```

## Level Progression System

1. **Easy → Normal → Hard**: Players can progress through levels sequentially
2. **After each level** (except Hard), players see a dialog:
   - "Next Level" - advances to next difficulty
   - "End Game" - returns to difficulty selection
3. **Start Again feature**: From Victory or Options screen, players can choose any difficulty
   - Example: Choosing Normal goes straight to Normal level, then can progress to Hard

## Timer System

- **Countdown timer** displayed in top-right corner
- **Time limits**:
  - Easy/Normal: 10 minutes (600 seconds)
  - Hard: 15 minutes (900 seconds)
- **Color coding**:
  - Green: More than 3 minutes remaining
  - Orange: 1-3 minutes remaining
  - Red: Less than 1 minute remaining
- **Time up**: Shows "Game Over" dialog and returns to Options Screen

## Color Themes

Each difficulty level has its own color scheme:
- **Easy**: Light blue background with darker blue accents
- **Normal**: Orange background with darker orange accents
- **Hard**: Light red background with darker red accents

## How to Run

```bash
javac WordSearchGame.java
java WordSearchGame
```

## Features Implemented

✅ Welcome screen with name input
✅ Options screen with 3 difficulty levels
✅ Difficulty-based word counts (5/15/20)
✅ Difficulty-based time limits (10/10/15 minutes)
✅ Color themes for each difficulty
✅ Countdown timer with visual warnings
✅ Level progression system
✅ "Next Level" and "End Game" buttons after completion
✅ Victory screen after completing Hard level
✅ "Start Again" functionality
✅ "Quit Game" functionality
✅ Full navigation flow between all screens

## Game Play Instructions

1. **Start**: Enter your name on the welcome screen
2. **Choose Difficulty**: Select Easy, Normal, or Hard
3. **Find Words**: Drag mouse across letters to select words (horizontal, vertical, or diagonal)
4. **Watch Timer**: Keep an eye on the countdown timer
5. **Progress**: Complete all words to advance or end game
6. **Victory**: Complete Hard level to see the victory screen
7. **Replay**: Use "Start Again" to play different difficulty levels

Enjoy the game! 🎮
