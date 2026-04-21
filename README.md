# RINGwars Agent 

This project implements an intelligent agent for the competitive strategy game **RINGwars**, developed as part of a programming lab in the B.Sc. Computer Science program at FernUniversität in Hagen.

## Overview

RINGwars is a turn-based, two-player strategy game. The goal is to control as many nodes as possible on a ring-shaped map and ultimately eliminate the opponent.

This project focuses on designing and implementing an **adaptive AI agent** that:
- reacts dynamically to the game state  
- analyzes opponent behavior  
- combines multiple strategies  
- learns from previous rounds  

---

## Key Features

### Adaptive Strategy System
The agent does not follow a fixed strategy. Instead, it:
- evaluates the current game situation (visibility, strength, opponent behavior)
- selects or combines strategies dynamically
- adjusts behavior based on past performance

### Multiple Strategy Types
The agent uses four core strategies:

- **Expansion** → occupy free nodes to maximize growth  
- **Attack** → conquer opponent nodes (targeting weak or strong nodes)  
- **Consolidation** → reinforce vulnerable positions  
- **Defensive** → retreat and preserve resources when outmatched  

These strategies can be combined into a **Mixed Strategy**, whose ratios evolve over time.

### Learning Component
The agent stores and updates knowledge between rounds:
- opponent aggressiveness / defensiveness  
- success rate of own actions  
- historical game data  

This enables **adaptive behavior across turns**, inspired by basic AI learning principles.

### Robustness & Error Handling
Special care was taken to ensure reliability:
- multiple validation layers prevent illegal moves  
- fallback strategies ensure a valid move is always executed  
- custom exceptions handle edge cases (e.g. invalid states, overflow)

---

## Architecture

The project is implemented in **Java** and follows a clean modular structure:

### Model Layer
- **Ring** → represents the game state  
- **Node** → represents a single position on the ring  
- **Output** → handles move generation  
- **Notes** → stores persistent agent knowledge  

### Service Layer
- **Strategy** → abstract base class for all strategies  
- **Analyzer** → core decision-making logic  
- **Util** → file I/O and parsing utilities  

---

## How it Works

Each turn follows this cycle:

1. **Read input**
   - current game state  
   - stored knowledge from previous rounds  

2. **Analyze**
   - compare predicted vs. actual outcomes  
   - update internal knowledge  

3. **Decide**
   - use rule-based logic (reflex agent) OR  
   - apply adaptive mixed strategy (learning agent)  

4. **Execute**
   - generate valid moves  
   - write output for the game engine  

---

## Performance Evaluation

Extensive experiments (~100+ games per setting) revealed:

- Strong performance on **larger maps** (more expansion potential)
- Significant advantage when **node bonus is high**
- Strategy effectiveness depends heavily on **game parameters**
- Visibility has a surprisingly large impact on outcomes

---

## Technologies

- Java  
- Object-Oriented Design  
- Basic AI concepts (based on Russell & Norvig):
  - Reflex agents  
  - Learning agents  
  - Belief states  

---

## Background & Inspiration

The strategy design is inspired by:
- **Artificial Intelligence theory** (Russell & Norvig)
- **Nature-based strategies**, e.g.:
  - territorial expansion (moose)
  - cooperative and aggressive behavior (ants)

---


## For more information

This project was developed in an academic context. For more information consider the Documentation directory. A co-student and I are currently working on a scientific
paper on RINGwars, and our agents, which will be presented at the [SCIN 2026](https://scin26.nicepage.io/) conference and published in the Springer conference volume.
