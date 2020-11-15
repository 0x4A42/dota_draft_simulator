# Dota Draft Simulator

# Background

DOTA 2 is an online multiplayer game, with each game consisting of five players on two teams. There are currently 119 heroes (characters) to choose from, each with unique abilities and stats. 

One important aspect of a DOTA game is the 'drafting process', where each team takes it in turn to ban and pick heroes. A hero can only be picked once per game, and banning it prohibits either team from selecting it. As such, the draft is a highly contested portion of a game, and some games can largely be decided in this portion as many heroes' strengthes are varied, with some abilities strongly countering other heroes. 

Each team has 35 seconds to select a hero (whether banning or picking), refreshed every time the draft returns to them. Each team also has a bank of 130 seconds of reserve time, which is only used when this initial 30 seconds is used. This reserve time does not refresh at any point during the draft.

A video showing the drafting process of a professional game can be seen [here](https://www.youtube.com/watch?v=8wcHc2gOgbI).

# What it does

This tool mimics the drafting process within DOTA 2, as detailed above. Upon running the tool, the user will be prompted for which side will get first pick. Following this, each team will take it in turn to pick, or ban, a hero. 

A hero can only be uniquely banned or picked once, resulting in a repeated prompt if a user attempts to pick or ban an unavailable hero. Failure to pick within the alloted time, or entering 'random', will randomly pick a hero for the team.

# How to use
This tool was developed using Java 8.

1. Download and extract the code.  
2. Navigate the command line to the code (go inside src/drafting to where the .java files are).  
3. Compile the code. *(See below if unsure how to!)
4. Run the code using the command 'java drafting.Drafter'.  
6. Have fun!  

Run the following commands to compile the code (works best following this order): 
* javac DraftTimeThread.java  
* javac ReserveTimeThread.java  
* javac Side.java
* cd ..
* javac drafting/Team.java
* javac drafting/Drafter.java  

# Possible Future Developments  
  
Allow looping of drafts (play more than once per execution)  
Develop a GUI  
Port to a webapp  