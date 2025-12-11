import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import java.io.*;
import java.util.*;

public class RiddleTests {
    private Riddle riddle;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    @Before
    public void setUp() {
        riddle = new Riddle();
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    // Test 1: Verify getRules() returns the correct rules string
    @Test
    public void testGetRules() {
        String rules = Riddle.getRules();
        assertNotNull("Rules should not be null", rules);
        assertTrue("Rules should contain 'welcome'", rules.contains("welcome"));
        assertTrue("Rules should mention five riddles", rules.contains("five"));
        assertTrue("Rules should mention three hints", rules.contains("three hints"));
    }
    
    // Test 2: Check that rules constant contains expected content
    @Test
    public void testRulesConstant() {
        assertTrue("Rules should mention one word answers", 
                   Riddle.rules.contains("one word"));
        assertTrue("Rules should mention ten guesses limit", 
                   Riddle.rules.contains("ten guesses"));
    }
    
    // Test 3: Verify loss message exists
    @Test
    public void testLossMessage() {
        assertNotNull("Loss message should not be null", Riddle.loss);
        assertTrue("Loss message should be discouraging", 
                   Riddle.loss.contains("suck") || Riddle.loss.contains("better luck"));
    }
    
    // Test 4: Verify win message exists
    @Test
    public void testWinMessage() {
        assertNotNull("Win message should not be null", Riddle.win);
        assertTrue("Win message should mention congrats or solving", 
                   Riddle.win.contains("congrats") || Riddle.win.contains("solve"));
    }
    
    // Test 5: Test Riddle constructor initializes maps
    @Test
    public void testConstructor() {
        assertNotNull("RiddleMap should be initialized", riddle.getRiddleMap());
        assertNotNull("HintMap should be initialized", riddle.getHintMap());
        assertTrue("RiddleMap should be empty initially", riddle.getRiddleMap().isEmpty());
        assertTrue("HintMap should be empty initially", riddle.getHintMap().isEmpty());
    }
    
    // Test 6: Test loadRiddles populates the riddleMap
    @Test
    public void testLoadRiddles() {
        riddle.loadRiddles();
        assertFalse("RiddleMap should not be empty after loading", 
                    riddle.getRiddleMap().isEmpty());
        assertTrue("RiddleMap should contain ANCHOR", 
                   riddle.getRiddleMap().containsKey("ANCHOR"));
    }
    
    // Test 7: Test loadHints populates the hintMap
    @Test
    public void testLoadHints() {
        riddle.loadHints();
        assertFalse("HintMap should not be empty after loading", 
                    riddle.getHintMap().isEmpty());
        assertTrue("HintMap should contain ANCHOR", 
                   riddle.getHintMap().containsKey("ANCHOR"));
    }
    
    // Test 8: Test hint list has correct size
    @Test
    public void testHintListSize() {
        riddle.loadHints();
        List<String> anchorHints = riddle.getHintMap().get("ANCHOR");
        assertNotNull("ANCHOR hints should exist", anchorHints);
        assertEquals("ANCHOR should have 3 hints", 3, anchorHints.size());
    }
    
    // Test 9: Test selectRandomRiddles returns correct number
    @Test
    public void testSelectRandomRiddlesCount() {
        riddle.loadRiddles();
        Queue<String> selected = riddle.selectRandomRiddles();
        assertEquals("Should select exactly 5 riddles", 5, selected.size());
    }
    
    // Test 10: Test selectRandomRiddles returns valid riddles
    @Test
    public void testSelectRandomRiddlesValid() {
        riddle.loadRiddles();
        Queue<String> selected = riddle.selectRandomRiddles();
        
        for (String selectedRiddle : selected) {
            assertTrue("Each riddle should exist in riddleMap", 
                       riddle.getRiddleMap().containsValue(selectedRiddle));
        }
    }
    
    // Test 11: Test getAnswerForRiddle finds correct answer
    @Test
    public void testGetAnswerForRiddle() {
        riddle.loadRiddles();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        String answer = riddle.getAnswerForRiddle(testRiddle);
        assertEquals("Should find ANCHOR as answer", "ANCHOR", answer);
    }
    
    // Test 12: Test getAnswerForRiddle returns empty for unknown riddle
    @Test
    public void testGetAnswerForUnknownRiddle() {
        riddle.loadRiddles();
        String answer = riddle.getAnswerForRiddle("NON-EXISTENT RIDDLE");
        assertEquals("Should return empty string for unknown riddle", "", answer);
    }
    
    // Test 13: Test isCorrectAnswer with correct answer
    @Test
    public void testIsCorrectAnswerTrue() {
        riddle.loadRiddles();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        assertTrue("ANCHOR should be correct answer", 
                   riddle.isCorrectAnswer(testRiddle, "ANCHOR"));
    }
    
    // Test 14: Test isCorrectAnswer with wrong answer
    @Test
    public void testIsCorrectAnswerFalse() {
        riddle.loadRiddles();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        assertFalse("BOAT should be wrong answer", 
                    riddle.isCorrectAnswer(testRiddle, "BOAT"));
    }
    
    // Test 15: Test isCorrectAnswer is case insensitive
    @Test
    public void testIsCorrectAnswerCaseInsensitive() {
        riddle.loadRiddles();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        assertTrue("anchor (lowercase) should be correct", 
                   riddle.isCorrectAnswer(testRiddle, "anchor"));
        assertTrue("AnChOr (mixed case) should be correct", 
                   riddle.isCorrectAnswer(testRiddle, "AnChOr"));
    }
    
    // Test 16: Test displayHint shows correct hint
    @Test
    public void testDisplayHint() {
        riddle.loadRiddles();
        riddle.loadHints();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        
        riddle.displayHint(testRiddle, 0);
        String output = outContent.toString();
        assertTrue("Output should contain hint message", output.contains("here is your hint"));
    }
    
    // Test 17: Test offerHint with 'y' response increases hints used
    @Test
    public void testOfferHintYes() {
        riddle.loadRiddles();
        riddle.loadHints();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        
        String input = "y\n";
        riddle.setScanner(new Scanner(input));
        
        int hintsUsed = riddle.offerHint(testRiddle, 3, 0);
        assertEquals("Hints used should increase to 1", 1, hintsUsed);
    }
    
    // Test 18: Test offerHint with 'n' response doesn't increase hints used
    @Test
    public void testOfferHintNo() {
        riddle.loadRiddles();
        riddle.loadHints();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        
        String input = "n\n";
        riddle.setScanner(new Scanner(input));
        
        int hintsUsed = riddle.offerHint(testRiddle, 3, 0);
        assertEquals("Hints used should remain 0", 0, hintsUsed);
    }
    
    // Test 19: Test offerHint when all hints used
    @Test
    public void testOfferHintAllUsed() {
        riddle.loadRiddles();
        riddle.loadHints();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        
        int hintsUsed = riddle.offerHint(testRiddle, 6, 3);
        assertEquals("Hints used should remain 3", 3, hintsUsed);
        assertTrue("Should display 'used all hints' message", 
                   outContent.toString().contains("used all your hints"));
    }
    
    // Test 20: Test solveRiddle returns true on correct answer
    @Test
    public void testSolveRiddleCorrect() {
        riddle.loadRiddles();
        riddle.loadHints();
        String testRiddle = riddle.getRiddleMap().get("ANCHOR");
        Stack<String> testMessage = new Stack<>();
        testMessage.push("you're dumber than a box of rocks");
        
        String input = "ANCHOR\n";
        riddle.setScanner(new Scanner(input));
        
        boolean result = riddle.solveRiddle(testRiddle, testMessage);
        assertTrue("Should return true when riddle is solved", result);
    }
    
    // Test 21: Test solveRiddle returns false after 10 wrong attempts
    @Test
public void testSolveRiddleTenAttempts() {
    riddle.loadRiddles();
    riddle.loadHints();
    String testRiddle = riddle.getRiddleMap().get("ANCHOR");
    Stack<String> testMessage = new Stack<>();
    testMessage.push("you're dumber than a box of rocks");
    
    // 2 wrong without hints, then 8 wrong with "n" for each hint offer
    String input = "wrong\nwrong\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\nn\nwrong\n";
    riddle.setScanner(new Scanner(input));
    
    boolean result = riddle.solveRiddle(testRiddle, testMessage);
    assertFalse("Should return false after 10 wrong attempts", result);
}
    
    // Test 22: Test displayVictory outputs correct message
    @Test
    public void testDisplayVictory() {
        riddle.displayVictory();
        String output = outContent.toString();
        assertTrue("Output should contain win message", output.contains("congrats"));
        assertTrue("Output should contain trophy", output.contains("/\\_/\\_/\\_/\\"));
    }
    
    // Test 23: Test color constants are defined
    @Test
    public void testColorConstants() {
        assertNotNull("RESET should be defined", Riddle.RESET);
        assertNotNull("YELLOW should be defined", Riddle.YELLOW);
        assertEquals("RESET should be correct ANSI code", "\033[0m", Riddle.RESET);
        assertEquals("YELLOW should be correct ANSI code", "\033[33m", Riddle.YELLOW);
    }
    
    // Test 24: Test riddleMap contains expected riddles
    @Test
    public void testRiddleMapContents() {
        riddle.loadRiddles();
        assertTrue("Should contain BANK", riddle.getRiddleMap().containsKey("BANK"));
        assertTrue("Should contain EGG", riddle.getRiddleMap().containsKey("EGG"));
        assertTrue("Should contain PIANO", riddle.getRiddleMap().containsKey("PIANO"));
    }
    
    // Test 25: Test hintMap contains expected hints
    @Test
    public void testHintMapContents() {
        riddle.loadHints();
        assertTrue("Should contain BANK hints", riddle.getHintMap().containsKey("BANK"));
        assertTrue("Should contain EGG hints", riddle.getHintMap().containsKey("EGG"));
        assertTrue("Should contain PIANO hints", riddle.getHintMap().containsKey("PIANO"));
    }
}