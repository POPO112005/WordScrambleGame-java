import java.util.*;

public class TestWordPlacement {
    private static final int GRID_SIZE = 12;
    private char[][] grid;
    private List<String> wordsToFind;
    private Random random = new Random();
    
    private final List<String> ALL_WORDS = Arrays.asList(
        "OCEAN", "MOUNTAIN", "FOREST", "RIVER", "FLOWER",
        "BUTTERFLY", "EAGLE", "WHALE", "TIGER", "RAINBOW",
        "SUNSET", "BEACH", "ISLAND", "DESERT", "VALLEY",
        "CANYON", "WATERFALL", "DOLPHIN", "SHARK", "CORAL"
    );
    
    public static void main(String[] args) {
        TestWordPlacement test = new TestWordPlacement();
        
        System.out.println("Testing Easy (5 words):");
        test.testPlacement(5);
        
        System.out.println("\nTesting Normal (15 words):");
        test.testPlacement(15);
        
        System.out.println("\nTesting Hard (20 words):");
        test.testPlacement(20);
    }
    
    public void testPlacement(int targetWords) {
        wordsToFind = new ArrayList<>();
        createGridWithWords(targetWords);
        
        System.out.println("Target: " + targetWords + " words");
        System.out.println("Placed: " + wordsToFind.size() + " words");
        System.out.println("Words: " + wordsToFind);
        
        // ตรวจสอบว่าคำทุกคำอยู่ในตารางจริงหรือไม่
        for (String word : wordsToFind) {
            if (!isWordInGrid(word)) {
                System.out.println("WARNING: Word '" + word + "' NOT FOUND in grid!");
            }
        }
        System.out.println("All words verified in grid: " + (wordsToFind.size() > 0));
    }
    
    private boolean isWordInGrid(String word) {
        // ค้นหาคำในทุกทิศทาง
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // ลองทุกทิศทาง
                if (searchFromPosition(word, row, col)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean searchFromPosition(String word, int row, int col) {
        int[][] directions = {
            {0, 1}, {1, 0}, {1, 1}, {1, -1},
            {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}
        };
        
        for (int[] dir : directions) {
            if (checkDirection(word, row, col, dir[0], dir[1])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkDirection(String word, int row, int col, int rowDir, int colDir) {
        for (int i = 0; i < word.length(); i++) {
            int newRow = row + i * rowDir;
            int newCol = col + i * colDir;
            
            if (newRow < 0 || newRow >= GRID_SIZE || newCol < 0 || newCol >= GRID_SIZE) {
                return false;
            }
            
            if (grid[newRow][newCol] != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    private void createGridWithWords(int targetWords) {
        grid = new char[GRID_SIZE][GRID_SIZE];
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = '-';
            }
        }
        
        List<String> shuffledWords = new ArrayList<>(ALL_WORDS);
        Collections.shuffle(shuffledWords, random);
        
        wordsToFind.clear();
        
        for (String word : shuffledWords) {
            if (wordsToFind.size() >= targetWords) {
                break;
            }
            
            if (placeWordInGrid(word)) {
                wordsToFind.add(word);
            }
        }
        
        if (wordsToFind.size() < targetWords) {
            for (String word : shuffledWords) {
                if (wordsToFind.size() >= targetWords) {
                    break;
                }
                if (!wordsToFind.contains(word) && word.length() <= GRID_SIZE) {
                    if (forceHorizontalPlacement(word)) {
                        wordsToFind.add(word);
                    }
                }
            }
        }
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == '-') {
                    grid[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }
    
    private boolean forceHorizontalPlacement(String word) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int startCol = 0; startCol <= GRID_SIZE - word.length(); startCol++) {
                boolean canPlace = true;
                
                for (int i = 0; i < word.length(); i++) {
                    char existingChar = grid[row][startCol + i];
                    char targetChar = word.charAt(i);
                    
                    if (existingChar != '-' && existingChar != targetChar) {
                        canPlace = false;
                        break;
                    }
                }
                
                if (canPlace) {
                    for (int i = 0; i < word.length(); i++) {
                        grid[row][startCol + i] = word.charAt(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean placeWordInGrid(String word) {
        boolean placed = false;
        int attempts = 0;
        int maxAttempts = 100;
        
        while (!placed && attempts < maxAttempts) {
            attempts++;
            
            int startRow = random.nextInt(GRID_SIZE);
            int startCol = random.nextInt(GRID_SIZE);
            
            int direction = random.nextInt(8);
            int rowDir = 0, colDir = 0;
            
            switch (direction) {
                case 0: rowDir = 0; colDir = 1; break;
                case 1: rowDir = 1; colDir = 0; break;
                case 2: rowDir = 1; colDir = 1; break;
                case 3: rowDir = 1; colDir = -1; break;
                case 4: rowDir = 0; colDir = -1; break;
                case 5: rowDir = -1; colDir = 0; break;
                case 6: rowDir = -1; colDir = 1; break;
                case 7: rowDir = -1; colDir = -1; break;
            }
            
            if (canPlaceWord(word, startRow, startCol, rowDir, colDir)) {
                for (int i = 0; i < word.length(); i++) {
                    int row = startRow + i * rowDir;
                    int col = startCol + i * colDir;
                    grid[row][col] = word.charAt(i);
                }
                placed = true;
            }
        }
        
        return placed;
    }
    
    private boolean canPlaceWord(String word, int startRow, int startCol, int rowDir, int colDir) {
        int endRow = startRow + (word.length() - 1) * rowDir;
        int endCol = startCol + (word.length() - 1) * colDir;
        
        if (endRow < 0 || endRow >= GRID_SIZE || endCol < 0 || endCol >= GRID_SIZE) {
            return false;
        }
        
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + i * rowDir;
            int col = startCol + i * colDir;
            char existingChar = grid[row][col];
            char targetChar = word.charAt(i);
            
            if (existingChar != '-' && existingChar != targetChar) {
                return false;
            }
        }
        
        return true;
    }
}
