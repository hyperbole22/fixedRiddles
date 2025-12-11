import java.io.*;
import java.util.*;

public class Riddle {

    public static final String rules = """
                                       welcome to Riddle Me This! 
                                       
                                       you will be given five random riddles to solve 
                                       all answers will be one word answers, other kinds of answers will not be accepted 
                                       after three guesses you will be offered a hint
                                       there are three hints for every riddle
                                       solve all five riddles to win the game
                                       if you exceed ten guesses on one riddle, you will automatically lose the game
                                       if you are confident in your guess but it says that it's wrong, try again 
                                       with or without an 's' (ex. boat and boats)""";

    public static final String loss = """
                                        wow you suck at this...
                                        better luck next time I guess""";
    
    public static final String win = """
                                     congrats you actually managed to solve all the riddles! 
                                     too bad even a 3rd grader could do that...""";

    public static final String RESET = "\033[0m";
    public static final String YELLOW = "\033[33m";
    
    private HashMap<String, String> riddleMap;
    private HashMap<String, List<String>> hintMap;
    private Scanner scanner;
    
    public Riddle() {
        this.riddleMap = new HashMap<>();
        this.hintMap = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        Riddle game = new Riddle();
        game.loadRiddles();
        game.loadHints();
        game.playGame();
    }
    
    /**
     * Loads riddles from riddles.txt file into riddleMap
     */
    public void loadRiddles() {
        try {
            File riddleFile = new File("riddles.txt");
            Scanner scannerRiddles = new Scanner(riddleFile);
            while (scannerRiddles.hasNextLine()) {
                String line = scannerRiddles.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String answer = parts[0].trim();
                    String riddle = parts[1];
                    riddleMap.put(answer, riddle);
                }
            }
            scannerRiddles.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("error reading riddles file");
            System.exit(1);
        }
    }
    
    /**
     * Loads hints from hints.txt file into hintMap
     */
    public void loadHints() {
        try {
            File hintFile = new File("hints.txt");
            Scanner scannerHints = new Scanner(hintFile);
            while (scannerHints.hasNextLine()) {
                String line = scannerHints.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String answer = parts[0].trim();
                    String[] hints = parts[1].split(",");
                    hintMap.put(answer, Arrays.asList(hints));
                }
            }
            scannerHints.close();
        }
        catch (Exception e) {
            System.out.println("error reading hints file");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Creates a queue of 5 random riddles to solve
     * @return Queue containing 5 random riddles
     */
    public Queue<String> selectRandomRiddles() {
        ArrayList<String> shuffledRiddles = new ArrayList<>();
        for (String riddle : riddleMap.values()) {
            shuffledRiddles.add(riddle);
        }
        Collections.shuffle(shuffledRiddles);

        Queue<String> riddlesToDo = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            riddlesToDo.offer(shuffledRiddles.get(i));
        }
        return riddlesToDo;
    }
    
    /**
     * Main game loop - plays through all riddles
     */
    public void playGame() {
        System.out.println(rules+"\n");
        Queue<String> riddlesToDo = selectRandomRiddles();
        
        while (!riddlesToDo.isEmpty()) {
            String currentRiddle = riddlesToDo.poll();
            boolean solved = solveRiddle(currentRiddle);
            
            if (!solved) {
                System.out.println(loss);
                
                return;
            }
        }
        
        displayVictory();
    }
    
    /**
     * Handles the solving process for a single riddle
     * @param currentRiddle The riddle to solve
     * @return true if solved, false if player exceeded max attempts
     */
    public boolean solveRiddle(String currentRiddle) {
        boolean solved = false;
        int attempts = 0;
        int hintsUsed = 0;
        
        while (!solved) {
            System.out.println(currentRiddle);
            System.out.println("enter your guess: ");
            String userGuess = scanner.nextLine();
            
            if (isCorrectAnswer(currentRiddle, userGuess)) {
                System.out.println("good job! you actually got one!");
                solved = true;
            }
            else {
                attempts++;
                if (attempts == 10) {
                    return false; // Player lost
                }
                System.out.println("wrong! how can you not get it?");
                
                if (attempts >= 3) {
                    hintsUsed = offerHint(currentRiddle, attempts, hintsUsed);
                }
            }
        }
        return true;
    }
    
    /**
     * Checks if the user's guess matches the correct answer
     * @param riddle The current riddle
     * @param guess The user's guess
     * @return true if correct, false otherwise
     */
    public boolean isCorrectAnswer(String riddle, String guess) {
        String correctAnswer = getAnswerForRiddle(riddle);
        return guess.equalsIgnoreCase(correctAnswer);
    }
    
    /**
     * Finds the answer for a given riddle
     * @param riddle The riddle to find the answer for
     * @return The answer string, or empty string if not found
     */
    public String getAnswerForRiddle(String riddle) {
        return riddleMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(riddle))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }
    
    /**
     * Offers a hint to the player if available
     * @param riddle The current riddle
     * @param attempts Number of attempts made
     * @param hintsUsed Number of hints already used
     * @return Updated number of hints used
     */
    public int offerHint(String riddle, int attempts, int hintsUsed) {
        if (hintsUsed < 3) {
            System.out.println("would you like a hint? (y/n): ");
            String wantHint = scanner.nextLine();
            
            if (wantHint.equalsIgnoreCase("y")) {
                displayHint(riddle, hintsUsed);
                hintsUsed++;
            }
            else if (wantHint.equalsIgnoreCase("n")) {
                System.out.println("really? okay... try again I guess");
            }
            else {
                System.out.println("that wasn't either option... try again");
            }
        }
        else {
            System.out.println("you have used all your hints for this riddle, try again!");
        }
        return hintsUsed;
    }
    
    /**
     * Displays a hint for the current riddle
     * @param riddle The current riddle
     * @param hintIndex The index of the hint to display
     */
    public void displayHint(String riddle, int hintIndex) {
        String answer = getAnswerForRiddle(riddle);
        List<String> hints = hintMap.get(answer);
        
        if (hints != null && hintIndex < hints.size()) {
            System.out.println("here is your hint: ");
            System.out.println(hints.get(hintIndex));
        }
    }
    
    /**
     * Displays the victory message and trophy
     */
    public void displayVictory() {
        System.out.println(win);
        System.out.println("congratulations! you have solved all the riddles!");
        System.out.println("here is your prize:");
        System.out.println(YELLOW + " .  .  .  .");
        System.out.println(YELLOW + "/\\_/\\_/\\_/\\");
        System.out.println(YELLOW + "|          |");
        System.out.println(YELLOW + "|          |");
        System.out.println(YELLOW + "------------" + RESET);
    }
    
    public static String getRules() {
        return rules;
    }
    
    public HashMap<String, String> getRiddleMap() {
        return riddleMap;
    }
    
    public HashMap<String, List<String>> getHintMap() {
        return hintMap;
    }
    
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}