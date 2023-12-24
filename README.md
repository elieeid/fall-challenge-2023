# codingame-java-starter

> A starter project to solve CodinGame puzzles as a standard Java project

## Usage    

### Setup

- Fork/export this project and import it in your favorite IDE.
  
- Open the game you want to play on CodinGame.
  
- Select the language "Java".
  
- In the code generated by CodinGame, locate the method `main`, and the `while(true)` loop within it. Everything before the loop will be executed once, at the beginning of the game. Everything inside the loop will be executed during each game turn. _In some games, there is no initial game input. In this case, just ignore anything related to this part of the code in the instructions below._
  
- Copy all the code located **_before the loop_** in the method `readGameInput` of this project's `Player` class (except for the `Scanner`instantiation).
  
- Copy all the code located **_within the loop_** and **_before the comment_** `// Write an action using System.out.println()` in the method `readTurnInput` of this project's `Player` class.
  
- copy all the code located **_within the loop_** and **_after the comment_** `// Write an action using System.out.println()` in the method `gameTurn` of this project's `Player` class, after the same comment.
  
- Modify the content of the methods `readGameInput` and `readTurnInput` :
    - Use the `InputReader` to read data instead of the `Scanner` passed in argument (see "Debug" below for more details). It might require some adjustments, as the methods available are not exactly the same.
    - Properly store the read data in objects.
    - Return these objects.
    - Fix possible compilation error, in case some data read in the method `readGameInput` is required in the method `readTurnInput`.

- In the method `gameTurn`, replace the use of `System.out` by the `out` argument of the method (see "Unit testing" below for more details).
  
- Run `FileBuilder`, passing it the path of the `Player` class, to generate a single file (see "Submission" below for more details).

- Copy/paste the content of this file into the CodinGame IDE.

- Run the code

- If the code compiles and the game is displayed, you're ready to start coding for real! If not, fix the errors displayed in the "console output" of the CodinGame IDE and retry.

### Simulate a game turn locally

In the method `Player::gameTurn`, using the argument `out` instead of `System.out` will allow you to unit test this code (see the `PlayerTest` class for an example).

It will also allow you to simulate a game turn, using a turn input sample (see "Debug" below for more details).

### Debug

It can be really difficult to debug your code using CodinGame IDE, as the only tool you can use to do it is the standard error output.

In the input-reading methods, `Player::readGameInput` and `Player::readTurnInput`, using `InputTracer` to read data, instead of the `Scanner` argument, will allow you to print in the "output console" of the CodinGame IDE one line of text containing the full input data provided during each turn.

`InputTracer` registers every line of data you read through it. When you've finished reading the input, you can call the method `trace`, which will return all the read input data in the form of one line of text, containing line separators. Then you can display it in the output console of CodinGame IDE, using `System.err.println`.

This turn input data will be extremely valuable when you will need to debug your bot! (in case it misbehaves, and you want to understand why).

In this case, copy this input line from the output console of CodinGame IDE, paste it in the method `PlayerTest::simulateGameTurn` and run this method in debug mode with your IDE.

### Timer

In every multi-turn game, there will be constraints relating the **maximum execution time your script is allowed**. Generally, around 50 or 100 milliseconds per turn. A bit more when the game designers judge it necessary to produce a performant bot.

It prevents endless loops and also prevents the players from coding too eager algorithms.

**You must ensure your code execute within this time limit.** Otherwise, your code meets a timeout error. And you lose the current battle.

In lower leagues, it's generally not a concern, as your algorithm might be quite simple. But when you reach Silver or Gold league, I strongly advise you to use every available millisecond! If you don't, you lose precious time during which your bot could have computed more data. Your opponents probably won't make the same mistake...

The `Timer` class can be useful to visualize one turn execution time. Call `Timer.reset()` right **_after_** the first line you read. And `Timer.print()` at the very end of a game turn, after you print the last turn output. Each turn, this will display your execution time in CodinGame IDE console.

> **Do not call `Timer.reset()` _before_ the first game input you read!**
> It may seem more accurate, but don't forget your code runs within an endless loop. Each turn, before reading the first line in the standard input, it waits for the standard input to contain something. Basically, it waits for the game referee to finish computing the players outputs for the previous turn and producing the game inputs of the new turn to come. If you call `Timer.reset()` before the first input reading, the waiting time will be counted too...

If your bot performs long operations (simulations, tree search, etc.) you can regularly check the result returned by `Timer.getMilliseconds()`. If it approaches the maximal execution time (minus a safety margin), end your loops and return the best computed result so far, before you get a timeout.

### Code sample

Consider this initial code, generated by CodinGame for the game [Xmas Rush](https://www.codingame.com/multiplayer/bot-programming/xmas-rush).
```java
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int turnType = in.nextInt();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    String tile = in.next();
                }
            }
            for (int i = 0; i < 2; i++) {
                int numPlayerCards = in.nextInt(); // the total number of quests for a player (hidden and revealed)
                int playerX = in.nextInt();
                int playerY = in.nextInt();
                String playerTile = in.next();
            }
            int numItems = in.nextInt(); // the total number of items available on board and on player tiles
            for (int i = 0; i < numItems; i++) {
                String itemName = in.next();
                int itemX = in.nextInt();
                int itemY = in.nextInt();
                int itemPlayerId = in.nextInt();
            }
            int numQuests = in.nextInt(); // the total number of revealed quests for both players
            for (int i = 0; i < numQuests; i++) {
                String questItemName = in.next();
                int questPlayerId = in.nextInt();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("PUSH 3 RIGHT"); // PUSH <id> <direction> | MOVE <direction> | PASS
        }
    }
}
```

When you apply to it every tool this project provides, it becomes :

```java
class Player {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            gameTurn(in, System.out);
        }
    }

    static void gameTurn(Scanner in, PrintStream out){
        readInput(in);

        // Write an action using out.println()
        // To debug: System.err.println("Debug messages...");
        
        out.println("PUSH 3 RIGHT"); // PUSH <id> <direction> | MOVE <direction> | PASS
        Timer.print();
    }
    
    // In the final implementation, this method will probably return the model object for the game data
    static void readInput(Scanner in){
        InputTracer input = new InputTracer(in);
        
        int turnType = input.nextLineAsSingleInt();
        Timer.reset();
        for (int y = 0; y < 7; y++) {
            String[] tiles = input.nextLine();
            for (int x = 0; x < 7; x++) {
                String tile = tiles[x];
            }
        }
        for (int i = 0; i < 2; i++) {
            String[] playerData = input.nextLine();
            int numPlayerCards = Integer.parseInt(playerData[0]); // the total number of quests for a player (hidden and revealed)
            int playerX = Integer.parseInt(playerData[1]);
            int playerY = Integer.parseInt(playerData[2]);
            String playerTile = playerData[3];
        }
        int numItems = input.nextLineAsSingleInt(); // the total number of items available on board and on player tiles
        for (int i = 0; i < numItems; i++) {
            String[] itemData = input.nextLine();
            String itemName = itemData[0];
            int itemX = Integer.parseInt(itemData[1]);
            int itemY = Integer.parseInt(itemData[2]);
            int itemPlayerId = Integer.parseInt(itemData[3]);
        }
        int numQuests = input.nextLineAsSingleInt(); // the total number of revealed quests for both players
        for (int i = 0; i < numQuests; i++) {
            String[] questItem = input.nextLine();
            String questItemName = questItem[0];
            int questPlayerId = Integer.parseInt(questItem[1]);
        }
        
        // Print the input data every turn, so you can easily reproduce any game turn of any battle in your IDE
        System.err.println( input.trace() );
    }
}
```

### Submission

To submit your code into CodinGame IDE, you need to concat all your source code into a single file.
You can achieve that using the `FileBuilder` class.

Simply run this class as a Java application, passing the path of the `Player` class as argument.

> **Warning !**
The FileBuilder does not support wildcards at the end of package imports (ex: import java.util.*). Please use separate classes imports.

You can now copy/paste the content of the generated `Player.java` file into the CodinGame IDE, submit and cross your fingers...

## Credits

The `FileBuilder` class has been developed by [Grégory Ribéron, aka Manwe](https://github.com/Manwe56). Thanks to him for sharing this with the community.

If needed, please address your thanks (or complaints) to him.