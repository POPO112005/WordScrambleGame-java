import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class WordSearchGame extends JFrame {
    private static final int GRID_SIZE = 12;
    private static final int CELL_SIZE = 45; // ลดขนาดเล็กลงนิดหน่อยเพื่อให้พอดีกับหน้าจอ
    
    // Game state
    private char[][] grid;
    private JButton[][] buttons;
    private List<String> wordsToFind;
    private List<String> foundWords;
    private Map<String, JLabel> wordLabels;
    private Point startCell = null;
    private Point endCell = null;
    private List<Point> selectedCells = new ArrayList<>();
    private List<FoundWord> foundWordsList = new ArrayList<>();
    private JPanel wordPanel;
    private JPanel gridPanel;
    private JPanel highlightPanel;
    private Random random = new Random();
    
    // Player and difficulty
    private String playerName = "";
    private DifficultyLevel currentDifficulty = null;
    private DifficultyLevel nextDifficulty = null;
    
    // Timer
    private javax.swing.Timer gameTimer;
    private int timeRemaining; // in seconds
    private JLabel timerLabel;
    
    // Screen management
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private static final String WELCOME_SCREEN = "welcome";
    private static final String OPTIONS_SCREEN = "options";
    private static final String GAME_SCREEN = "game";
    private static final String VICTORY_SCREEN = "victory";
    
    // Difficulty enum
    enum DifficultyLevel {
        EASY("Easy", 5, 600, new Color(173, 216, 230), "Easy Level"), // 10 minutes, light blue
        NORMAL("Normal", 15, 600, new Color(255, 200, 124), "Normal Level"), // 10 minutes, orange
        HARD("Hard", 20, 900, new Color(255, 160, 160), "Hard Level"); // 15 minutes, red
        
        final String name;
        final int wordCount;
        final int timeLimit; // in seconds
        final Color themeColor;
        final String displayName;
        
        DifficultyLevel(String name, int wordCount, int timeLimit, Color themeColor, String displayName) {
            this.name = name;
            this.wordCount = wordCount;
            this.timeLimit = timeLimit;
            this.themeColor = themeColor;
            this.displayName = displayName;
        }
    }
    
    // สีสำหรับคำที่พบ - หลากหลายสี
    private final Color[] WORD_COLORS = {
        new Color(255, 200, 200),  // ชมพูอ่อน
        new Color(200, 255, 200),  // เขียวอ่อน
        new Color(200, 220, 255),  // ฟ้าอ่อน
        new Color(255, 255, 180),  // เหลืองอ่อน
        new Color(255, 220, 200),  // ส้มอ่อน
        new Color(230, 200, 255),  // ม่วงอ่อน
        new Color(200, 255, 255),  // เขียวน้ำทะเลอ่อน
        new Color(255, 200, 255),  // ชมพูม่วง
        new Color(220, 255, 220),  // เขียวมิ้นต์
        new Color(255, 230, 200),  // พีช
        new Color(200, 240, 255),  // ฟ้าน้ำทะเล
        new Color(255, 210, 230),  // ชมพูโรส
        new Color(230, 255, 200)   // เขียวมะนาว
    };
    private int currentColorIndex = 0;
    
    // คลาสสำหรับเก็บข้อมูลคำที่พบ
    private static class FoundWord {
        List<Point> cells;
        Color color;
        
        FoundWord(List<Point> cells, Color color) {
            this.cells = new ArrayList<>(cells);
            this.color = color;
        }
    }
    
    // ชุดคำทั้งหมดที่มีในตาราง - ธีมคำเกี่ยวกับธรรมชาติและสัตว์
    private final List<String> ALL_WORDS = Arrays.asList(
        "OCEAN", "MOUNTAIN", "FOREST", "RIVER", "FLOWER",
        "BUTTERFLY", "EAGLE", "WHALE", "TIGER", "RAINBOW",
        "SUNSET", "BEACH", "ISLAND", "DESERT", "VALLEY",
        "CANYON", "WATERFALL", "DOLPHIN", "SHARK", "CORAL"
    );
    
    public WordSearchGame() {
        setTitle("Word Search Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // อนุญาตให้ปรับขนาดหน้าต่างได้
        
        foundWords = new ArrayList<>();
        wordsToFind = new ArrayList<>();
        
        // Setup CardLayout for screen management
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        // Create all screens
        mainContainer.add(createWelcomeScreen(), WELCOME_SCREEN);
        mainContainer.add(createOptionsScreen(), OPTIONS_SCREEN);
        // Game screen will be created when difficulty is selected
        
        add(mainContainer);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // ===== WELCOME SCREEN =====
    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        
        JLabel titleLabel = new JLabel("Welcome to Word Search Game!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(80, 60, 120));
        
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setMaximumSize(new Dimension(300, 40));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(100, 180, 100));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        startButton.setMaximumSize(new Dimension(200, 50));
        
        startButton.addActionListener(e -> {
            playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!", "Name Required", JOptionPane.WARNING_MESSAGE);
            } else {
                cardLayout.show(mainContainer, OPTIONS_SCREEN);
            }
        });
        
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(30));
        panel.add(startButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    // ===== OPTIONS SCREEN =====
    private JPanel createOptionsScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Select Difficulty Level");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(60, 60, 100));
        
        panel.add(Box.createVerticalStrut(30));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(50));
        
        // Easy button
        JButton easyButton = createDifficultyButton("EASY", DifficultyLevel.EASY, 
            "5 Words • 10 Minutes • Blue Theme");
        panel.add(easyButton);
        panel.add(Box.createVerticalStrut(20));
        
        // Normal button
        JButton normalButton = createDifficultyButton("NORMAL", DifficultyLevel.NORMAL, 
            "15 Words • 10 Minutes • Orange Theme");
        panel.add(normalButton);
        panel.add(Box.createVerticalStrut(20));
        
        // Hard button
        JButton hardButton = createDifficultyButton("HARD", DifficultyLevel.HARD, 
            "20 Words • 15 Minutes • Red Theme");
        panel.add(hardButton);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createDifficultyButton(String text, DifficultyLevel difficulty, String description) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(400, 100));
        buttonPanel.setBackground(difficulty.themeColor);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(difficulty.themeColor.darker(), 3),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel nameLabel = new JLabel(text);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(50, 50, 50));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(new Color(70, 70, 70));
        
        buttonPanel.add(nameLabel);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(descLabel);
        
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(buttonPanel);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 100));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> startGame(difficulty));
        
        return button;
    }
    
    private void startGame(DifficultyLevel difficulty) {
        currentDifficulty = difficulty;
        nextDifficulty = getNextDifficulty(difficulty);
        
        // Remove old game screen if exists
        Component[] components = mainContainer.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(GAME_SCREEN)) {
                mainContainer.remove(comp);
            }
        }
        
        // Create and add new game screen
        JPanel gameScreen = createGameScreen();
        gameScreen.setName(GAME_SCREEN);
        mainContainer.add(gameScreen, GAME_SCREEN);
        
        // Start the game
        resetGame();
        cardLayout.show(mainContainer, GAME_SCREEN);
    }
    
    private DifficultyLevel getNextDifficulty(DifficultyLevel current) {
        switch (current) {
            case EASY: return DifficultyLevel.NORMAL;
            case NORMAL: return DifficultyLevel.HARD;
            case HARD: return null; // No next level after hard
            default: return null;
        }
    }
    
    private void createGridWithWords() {
        int maxRetries = 50; // เพิ่มจำนวนครั้งในการลองใหม่
        int retryCount = 0;
        int targetWords = currentDifficulty != null ? currentDifficulty.wordCount : 10;
        boolean success = false;
        
        // พยายามสร้างตารางจนกว่าจะได้คำครบตามจำนวนที่ต้องการ
        while (retryCount < maxRetries && !success) {
            // สร้างตารางว่างเปล่าโดยใช้ตัวอักษรพิเศษเพื่อแสดงว่ายังไม่ได้ใช้
            grid = new char[GRID_SIZE][GRID_SIZE];
            
            // เติมด้วย '-' เพื่อแสดงว่ายังไม่ได้ใช้งาน
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    grid[i][j] = '-';
                }
            }
            
            // สุ่มคำและพยายามวางลงในตาราง
            List<String> shuffledWords = new ArrayList<>(ALL_WORDS);
            Collections.shuffle(shuffledWords, random);
            
            // ล้างรายการคำที่จะใช้
            wordsToFind.clear();
            
            // ลำดับความสำคัญในการวาง: วางคำยาวก่อน
            shuffledWords.sort((a, b) -> b.length() - a.length());
            
            // พยายามวางคำให้ได้ตามจำนวนเป้าหมาย - เพิ่มจำนวนครั้งที่พยายามวางแต่ละคำ
            for (String word : shuffledWords) {
                if (wordsToFind.size() >= targetWords) {
                    break;
                }
                
                if (placeWordInGridWithRetry(word, 500)) { // เพิ่มเป็น 500 ครั้ง
                    wordsToFind.add(word);
                }
            }
            
            // ถ้าวางได้น้อยกว่าเป้าหมาย ให้พยายามวางอีกรอบด้วยวิธีบังคับวาง
            if (wordsToFind.size() < targetWords) {
                // สุ่มใหม่เพื่อลองคำอื่น
                Collections.shuffle(shuffledWords, random);
                
                for (String word : shuffledWords) {
                    if (wordsToFind.size() >= targetWords) {
                        break;
                    }
                    if (!wordsToFind.contains(word) && word.length() <= GRID_SIZE) {
                        // พยายามวางแนวนอนที่แถวว่าง
                        if (forceHorizontalPlacement(word)) {
                            wordsToFind.add(word);
                        } else if (forceVerticalPlacement(word)) {
                            // ถ้าวางแนวนอนไม่ได้ ลองแนวตั้ง
                            wordsToFind.add(word);
                        } else if (forceDiagonalPlacement(word)) {
                            // ถ้าวางแนวตั้งไม่ได้ ลองแนวทแยง
                            wordsToFind.add(word);
                        }
                    }
                }
            }
            
            // ถ้าวางได้ครบแล้ว ออกจาก loop
            if (wordsToFind.size() >= targetWords) {
                success = true;
                System.out.println("✓ สร้างตารางสำเร็จ! วางคำได้ " + wordsToFind.size() + " คำ");
                System.out.println("คำที่วาง: " + wordsToFind);
                break;
            }
            
            // ถ้าวางไม่ครบ ลองใหม่
            retryCount++;
            if (retryCount < maxRetries) {
                System.out.println("พยายามครั้งที่ " + (retryCount + 1) + " - วางได้เพียง " + wordsToFind.size() + "/" + targetWords + " คำ");
            }
        }
        
        // ตรวจสอบว่าวางคำครบหรือไม่
        if (!success) {
            System.out.println("⚠ เตือน: วางคำได้เพียง " + wordsToFind.size() + " จาก " + targetWords + " คำที่ต้องการ");
            System.out.println("คำที่วางได้: " + wordsToFind);
        }
        
        // หลังจากวางคำเสร็จแล้ว ถึงค่อยเติมช่องว่างด้วยตัวอักษรสุ่ม
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == '-') {
                    grid[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }
    
    // เมธอดสำหรับบังคับวางคำแนวนอนเมื่อวิธีปกติไม่ได้ผล
    private boolean forceHorizontalPlacement(String word) {
        // พยายามวางแนวนอนในแต่ละแถว
        for (int row = 0; row < GRID_SIZE; row++) {
            // ลองทุกตำแหน่งเริ่มต้นในแถว
            for (int startCol = 0; startCol <= GRID_SIZE - word.length(); startCol++) {
                boolean canPlace = true;
                
                // ตรวจสอบว่าวางได้หรือไม่
                for (int i = 0; i < word.length(); i++) {
                    char existingChar = grid[row][startCol + i];
                    char targetChar = word.charAt(i);
                    
                    // อนุญาตถ้าช่องว่าง (-) หรือตัวอักษรเหมือนกัน
                    if (existingChar != '-' && existingChar != targetChar) {
                        canPlace = false;
                        break;
                    }
                }
                
                // ถ้าวางได้ ให้วางเลย
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
    
    // เมธอดสำหรับบังคับวางคำแนวตั้ง
    private boolean forceVerticalPlacement(String word) {
        // พยายามวางแนวตั้งในแต่ละคอลัมน์
        for (int col = 0; col < GRID_SIZE; col++) {
            // ลองทุกตำแหน่งเริ่มต้นในคอลัมน์
            for (int startRow = 0; startRow <= GRID_SIZE - word.length(); startRow++) {
                boolean canPlace = true;
                
                // ตรวจสอบว่าวางได้หรือไม่
                for (int i = 0; i < word.length(); i++) {
                    char existingChar = grid[startRow + i][col];
                    char targetChar = word.charAt(i);
                    
                    // อนุญาตถ้าช่องว่าง (-) หรือตัวอักษรเหมือนกัน
                    if (existingChar != '-' && existingChar != targetChar) {
                        canPlace = false;
                        break;
                    }
                }
                
                // ถ้าวางได้ ให้วางเลย
                if (canPlace) {
                    for (int i = 0; i < word.length(); i++) {
                        grid[startRow + i][col] = word.charAt(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    // เมธอดสำหรับบังคับวางคำแนวทแยง
    private boolean forceDiagonalPlacement(String word) {
        // ลองวางแนวทแยงทั้ง 4 ทิศทาง
        int[][] directions = {
            {1, 1},   // ขวาล่าง
            {1, -1},  // ซ้ายล่าง
            {-1, 1},  // ขวาบน
            {-1, -1}  // ซ้ายบน
        };
        
        for (int[] dir : directions) {
            int rowDir = dir[0];
            int colDir = dir[1];
            
            // ลองทุกตำแหน่งเริ่มต้นที่เป็นไปได้
            for (int startRow = 0; startRow < GRID_SIZE; startRow++) {
                for (int startCol = 0; startCol < GRID_SIZE; startCol++) {
                    // คำนวณตำแหน่งสุดท้าย
                    int endRow = startRow + (word.length() - 1) * rowDir;
                    int endCol = startCol + (word.length() - 1) * colDir;
                    
                    // ตรวจสอบว่าอยู่ในขอบเขตหรือไม่
                    if (endRow >= 0 && endRow < GRID_SIZE && endCol >= 0 && endCol < GRID_SIZE) {
                        boolean canPlace = true;
                        
                        // ตรวจสอบว่าวางได้หรือไม่
                        for (int i = 0; i < word.length(); i++) {
                            int row = startRow + i * rowDir;
                            int col = startCol + i * colDir;
                            char existingChar = grid[row][col];
                            char targetChar = word.charAt(i);
                            
                            // อนุญาตถ้าช่องว่าง (-) หรือตัวอักษรเหมือนกัน
                            if (existingChar != '-' && existingChar != targetChar) {
                                canPlace = false;
                                break;
                            }
                        }
                        
                        // ถ้าวางได้ ให้วางเลย
                        if (canPlace) {
                            for (int i = 0; i < word.length(); i++) {
                                int row = startRow + i * rowDir;
                                int col = startCol + i * colDir;
                                grid[row][col] = word.charAt(i);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // เมธอดใหม่สำหรับวางคำพร้อมการลองหลายครั้ง
    private boolean placeWordInGridWithRetry(String word, int maxAttempts) {
        boolean placed = false;
        int attempts = 0;
        
        while (!placed && attempts < maxAttempts) {
            attempts++;
            
            // สุ่มตำแหน่งเริ่มต้น
            int startRow = random.nextInt(GRID_SIZE);
            int startCol = random.nextInt(GRID_SIZE);
            
            // สุ่มทิศทาง (0-7: 8 ทิศทาง)
            int direction = random.nextInt(8);
            int rowDir = 0, colDir = 0;
            
            switch (direction) {
                case 0: rowDir = 0; colDir = 1; break;   // ขวา
                case 1: rowDir = 1; colDir = 0; break;   // ลง
                case 2: rowDir = 1; colDir = 1; break;   // ขวาล่าง
                case 3: rowDir = 1; colDir = -1; break;  // ซ้ายล่าง
                case 4: rowDir = 0; colDir = -1; break;  // ซ้าย
                case 5: rowDir = -1; colDir = 0; break;  // บน
                case 6: rowDir = -1; colDir = 1; break;  // ขวาบน
                case 7: rowDir = -1; colDir = -1; break; // ซ้ายบน
            }
            
            // ตรวจสอบว่าวางคำได้หรือไม่
            if (canPlaceWord(word, startRow, startCol, rowDir, colDir)) {
                // วางคำลงในตาราง
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
    
    private boolean placeWordInGrid(String word) {
        boolean placed = false;
        int attempts = 0;
        int maxAttempts = 100;
        
        while (!placed && attempts < maxAttempts) {
            attempts++;
            
            // สุ่มตำแหน่งเริ่มต้น
            int startRow = random.nextInt(GRID_SIZE);
            int startCol = random.nextInt(GRID_SIZE);
            
            // สุ่มทิศทาง (0-7: 8 ทิศทาง)
            int direction = random.nextInt(8);
            int rowDir = 0, colDir = 0;
            
            switch (direction) {
                case 0: rowDir = 0; colDir = 1; break;   // ขวา
                case 1: rowDir = 1; colDir = 0; break;   // ลง
                case 2: rowDir = 1; colDir = 1; break;   // ขวาล่าง
                case 3: rowDir = 1; colDir = -1; break;  // ซ้ายล่าง
                case 4: rowDir = 0; colDir = -1; break;  // ซ้าย
                case 5: rowDir = -1; colDir = 0; break;  // บน
                case 6: rowDir = -1; colDir = 1; break;  // ขวาบน
                case 7: rowDir = -1; colDir = -1; break; // ซ้ายบน
            }
            
            // ตรวจสอบว่าวางคำได้หรือไม่
            if (canPlaceWord(word, startRow, startCol, rowDir, colDir)) {
                // วางคำลงในตาราง
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
        // ตรวจสอบว่าคำจะออกนอกตารางหรือไม่
        int endRow = startRow + (word.length() - 1) * rowDir;
        int endCol = startCol + (word.length() - 1) * colDir;
        
        if (endRow < 0 || endRow >= GRID_SIZE || endCol < 0 || endCol >= GRID_SIZE) {
            return false;
        }
        
        // ตรวจสอบว่าช่องว่างพอหรือไม่ (ยอมให้ทับกันถ้าตัวอักษรเหมือนกัน หรือเป็นช่องว่าง)
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + i * rowDir;
            int col = startCol + i * colDir;
            char existingChar = grid[row][col];
            char targetChar = word.charAt(i);
            
            // อนุญาตถ้าช่องว่าง (-) หรือตัวอักษรเหมือนกัน
            if (existingChar != '-' && existingChar != targetChar) {
                return false;
            }
        }
        
        return true;
    }
    
    // ===== GAME SCREEN =====
    private JPanel createGameScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(currentDifficulty.themeColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with player info and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(currentDifficulty.themeColor);
        
        JLabel playerLabel = new JLabel("Player: " + playerName + " | " + currentDifficulty.displayName);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerLabel.setForeground(new Color(50, 50, 50));
        
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(new Color(180, 0, 0));
        
        topPanel.add(playerLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // Create game UI
        JPanel gamePanel = createGameUI();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        // ตั้งขนาดหน้าต่างให้แน่ใจว่าเห็นตารางครบ 12x12
        setPreferredSize(new Dimension(900, 750));
        pack();
        
        return mainPanel;
    }
    
    private JPanel createGameUI() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(currentDifficulty.themeColor);
        
        // Panel หลักที่จะใช้ LayeredPane เพื่อวาด highlight ทับด้านบน
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(
            GRID_SIZE * (CELL_SIZE + 2) + 20,
            GRID_SIZE * (CELL_SIZE + 2) + 20
        ));
        
        // Panel สำหรับตาราง
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        Color darkColor = currentDifficulty.themeColor.darker();
        gridPanel.setBackground(darkColor);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBounds(0, 0, 
            GRID_SIZE * (CELL_SIZE + 2) + 20,
            GRID_SIZE * (CELL_SIZE + 2) + 20
        );
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        
        // Panel สำหรับวาด highlight ทับด้านบน
        highlightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color highlightColor = new Color(255, 255, 150);
                
                // วาด highlight สำหรับคำที่พบแล้ว
                for (FoundWord fw : foundWordsList) {
                    // ทำให้สีโปร่งใสนิดหน่อย
                    Color transparentColor = new Color(fw.color.getRed(), fw.color.getGreen(), 
                                                       fw.color.getBlue(), 180);
                    g2d.setColor(transparentColor);
                    g2d.setStroke(new BasicStroke(CELL_SIZE * 0.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    if (fw.cells.size() > 0) {
                        Point first = fw.cells.get(0);
                        Point last = fw.cells.get(fw.cells.size() - 1);
                        
                        // คำนวณตำแหน่งกลางเซลล์
                        int x1 = first.y * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                        int y1 = first.x * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                        int x2 = last.y * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                        int y2 = last.x * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                        
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
                
                // วาด highlight สำหรับการเลือกปัจจุบัน
                if (!selectedCells.isEmpty()) {
                    // ทำให้สีโปร่งใสนิดหน่อย
                    Color transparentHighlight = new Color(highlightColor.getRed(), 
                                                           highlightColor.getGreen(), 
                                                           highlightColor.getBlue(), 180);
                    g2d.setColor(transparentHighlight);
                    g2d.setStroke(new BasicStroke(CELL_SIZE * 0.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    Point first = selectedCells.get(0);
                    Point last = selectedCells.get(selectedCells.size() - 1);
                    
                    int x1 = first.y * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                    int y1 = first.x * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                    int x2 = last.y * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                    int y2 = last.x * (CELL_SIZE + 2) + CELL_SIZE / 2 + 10;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        };
        highlightPanel.setOpaque(false);
        highlightPanel.setBounds(0, 0,
            GRID_SIZE * (CELL_SIZE + 2) + 20,
            GRID_SIZE * (CELL_SIZE + 2) + 20
        );
        
        // เพิ่ม panels เข้า layered pane
        layeredPane.add(gridPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(highlightPanel, JLayeredPane.PALETTE_LAYER);
        
        Color lightColor = currentDifficulty.themeColor.brighter();
        Color mediumColor = currentDifficulty.themeColor;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton btn = new JButton(grid != null ? String.valueOf(grid[row][col]) : "");
                btn.setFont(new Font("Arial", Font.BOLD, 18)); // ลดขนาดฟอนต์จาก 20 เป็น 18
                btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                btn.setBackground(lightColor);
                btn.setForeground(darkColor);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(mediumColor, 1));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                
                final int r = row;
                final int c = col;
                
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        startCell = new Point(r, c);
                        endCell = new Point(r, c);
                        highlightSelection();
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        checkWord();
                        clearSelection();
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (startCell != null) {
                            endCell = new Point(r, c);
                            highlightSelection();
                        }
                    }
                });
                
                buttons[row][col] = btn;
                gridPanel.add(btn);
            }
        }
        
        // Panel สำหรับรายการคำ
        wordPanel = new JPanel();
        wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
        wordPanel.setBackground(lightColor);
        wordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateWordList();
        
        container.add(layeredPane, BorderLayout.CENTER);
        container.add(wordPanel, BorderLayout.EAST);
        
        return container;
    }
    
    private void highlightSelection() {
        // ไม่ต้องเปลี่ยนสีปุ่มแล้ว เพียงแค่ repaint highlightPanel
        selectedCells.clear();
        
        if (startCell == null || endCell == null) return;
        
        // คำนวณทิศทาง
        int rowDiff = endCell.x - startCell.x;
        int colDiff = endCell.y - startCell.y;
        
        // ตรวจสอบว่าเป็นแนวตรง แนวนอน หรือแนวทแยง
        boolean isValidDirection = false;
        int steps = 0;
        int rowStep = 0, colStep = 0;
        
        if (rowDiff == 0 && colDiff != 0) {
            // แนวนอน
            isValidDirection = true;
            steps = Math.abs(colDiff);
            colStep = colDiff > 0 ? 1 : -1;
        } else if (colDiff == 0 && rowDiff != 0) {
            // แนวตั้ง
            isValidDirection = true;
            steps = Math.abs(rowDiff);
            rowStep = rowDiff > 0 ? 1 : -1;
        } else if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            // แนวทแยง
            isValidDirection = true;
            steps = Math.abs(rowDiff);
            rowStep = rowDiff > 0 ? 1 : -1;
            colStep = colDiff > 0 ? 1 : -1;
        }
        
        if (isValidDirection) {
            int currentRow = startCell.x;
            int currentCol = startCell.y;
            
            for (int i = 0; i <= steps; i++) {
                Point p = new Point(currentRow, currentCol);
                selectedCells.add(p);
                currentRow += rowStep;
                currentCol += colStep;
            }
        }
        
        gridPanel.repaint();
        highlightPanel.repaint();
    }
    
    private void checkWord() {
        if (selectedCells.isEmpty()) return;
        
        StringBuilder word = new StringBuilder();
        StringBuilder reverseWord = new StringBuilder();
        
        for (Point p : selectedCells) {
            word.append(grid[p.x][p.y]);
        }
        reverseWord.append(word).reverse();
        
        String wordStr = word.toString();
        String reverseStr = reverseWord.toString();
        
        // ตรวจสอบว่าพบคำหรือไม่
        String foundWord = null;
        if (wordsToFind.contains(wordStr) && !foundWords.contains(wordStr)) {
            foundWord = wordStr;
        } else if (wordsToFind.contains(reverseStr) && !foundWords.contains(reverseStr)) {
            foundWord = reverseStr;
        }
        
        if (foundWord != null) {
            foundWords.add(foundWord);
            
            // เลือกสีสำหรับคำนี้
            Color wordColor = WORD_COLORS[currentColorIndex % WORD_COLORS.length];
            currentColorIndex++;
            
            // เพิ่มคำที่พบลงในลิสต์พร้อมสี
            foundWordsList.add(new FoundWord(selectedCells, wordColor));
            
            // ขีดฆ่าคำในรายการ
            JLabel label = wordLabels.get(foundWord);
            label.setText("<html><strike>" + foundWord + "</strike></html>");
            label.setForeground(new Color(100, 150, 100));
            
            // Repaint เพื่อแสดง highlight
            highlightPanel.repaint();
            
            // ตรวจสอบว่าชนะหรือยัง
            if (foundWords.size() == wordsToFind.size()) {
                stopTimer();
                showLevelCompletionDialog();
            }
        }
    }
    
    private void showLevelCompletionDialog() {
        String message;
        String[] options;
        
        if (nextDifficulty == null) {
            // Completed Hard level - show victory screen
            showVictoryScreen();
            return;
        } else {
            // Not at hard level yet
            message = "Congratulations! You completed " + currentDifficulty.displayName + "!\n\nWhat would you like to do?";
            options = new String[]{"Next Level", "End Game"};
        }
        
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "Level Complete!",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 0) {
            // Next Level
            if (nextDifficulty != null) {
                startGame(nextDifficulty);
            }
        } else {
            // End Game - back to options
            cardLayout.show(mainContainer, OPTIONS_SCREEN);
        }
    }
    
    private void showVictoryScreen() {
        // Remove old victory screen if exists
        Component[] components = mainContainer.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(VICTORY_SCREEN)) {
                mainContainer.remove(comp);
            }
        }
        
        JPanel victoryPanel = createVictoryScreen();
        victoryPanel.setName(VICTORY_SCREEN);
        mainContainer.add(victoryPanel, VICTORY_SCREEN);
        cardLayout.show(mainContainer, VICTORY_SCREEN);
    }
    
    private JPanel createVictoryScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        
        JLabel congratsLabel = new JLabel("🎉 CONGRATULATIONS! 🎉");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 42));
        congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        congratsLabel.setForeground(new Color(200, 100, 0));
        
        JLabel playerLabel = new JLabel(playerName + "!");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 36));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerLabel.setForeground(new Color(100, 50, 150));
        
        JLabel messageLabel = new JLabel("You have completed all difficulty levels!");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(new Color(80, 80, 80));
        
        JButton startAgainButton = new JButton("Start Again");
        startAgainButton.setFont(new Font("Arial", Font.BOLD, 22));
        startAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startAgainButton.setBackground(new Color(100, 180, 255));
        startAgainButton.setForeground(Color.WHITE);
        startAgainButton.setFocusPainted(false);
        startAgainButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        startAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Arial", Font.BOLD, 22));
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setBackground(new Color(220, 100, 100));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        quitButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        startAgainButton.addActionListener(e -> {
            cardLayout.show(mainContainer, OPTIONS_SCREEN);
        });
        
        quitButton.addActionListener(e -> {
            System.exit(0);
        });
        
        panel.add(Box.createVerticalGlue());
        panel.add(congratsLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(playerLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(60));
        panel.add(startAgainButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(quitButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void clearSelection() {
        selectedCells.clear();
        startCell = null;
        endCell = null;
        highlightPanel.repaint();
    }
    
    private void updateWordList() {
        wordPanel.removeAll();
        
        Color darkColor = currentDifficulty.themeColor.darker().darker();
        
        JLabel titleLabel = new JLabel("Words to Find ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(darkColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wordPanel.add(titleLabel);
        wordPanel.add(Box.createVerticalStrut(10));
        
        wordLabels = new HashMap<>();
        for (String word : wordsToFind) {
            JLabel label = new JLabel(word);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setForeground(darkColor);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            wordLabels.put(word, label);
            wordPanel.add(label);
            wordPanel.add(Box.createVerticalStrut(5));
        }
        
        wordPanel.revalidate();
        wordPanel.repaint();
    }
    
    // ===== TIMER METHODS =====
    private void startTimer() {
        timeRemaining = currentDifficulty.timeLimit;
        updateTimerDisplay();
        
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        gameTimer = new javax.swing.Timer(1000, e -> {
            timeRemaining--;
            updateTimerDisplay();
            
            if (timeRemaining <= 0) {
                stopTimer();
                JOptionPane.showMessageDialog(this,
                    "Time's up! You didn't find all the words.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainContainer, OPTIONS_SCREEN);
            }
        });
        gameTimer.start();
    }
    
    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        
        // Change color when time is running out
        if (timeRemaining <= 60) {
            timerLabel.setForeground(new Color(200, 0, 0));
        } else if (timeRemaining <= 180) {
            timerLabel.setForeground(new Color(200, 100, 0));
        } else {
            timerLabel.setForeground(new Color(50, 100, 50));
        }
    }
    
    private void resetGame() {
        // Stop any existing timer
        stopTimer();
        
        // รีเซ็ตข้อมูลเกม
        foundWords.clear();
        foundWordsList.clear();
        currentColorIndex = 0;  // รีเซ็ต index สี
        
        // สร้างตารางและคำใหม่
        createGridWithWords();
        
        // อัพเดทปุ่มในตาราง
        if (buttons != null) {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    buttons[row][col].setText(String.valueOf(grid[row][col]));
                }
            }
        }
        
        // อัพเดทรายการคำใหม่
        updateWordList();
        
        // Repaint ทุกอย่าง
        if (gridPanel != null) gridPanel.repaint();
        if (highlightPanel != null) highlightPanel.repaint();
        
        // Start the timer
        startTimer();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordSearchGame());
    }
}
