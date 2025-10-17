import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class WordSearchGame extends JFrame {
    private static final int GRID_SIZE = 12;
    private static final int CELL_SIZE = 50;
    private char[][] grid;
    private JButton[][] buttons;
    private List<String> wordsToFind;
    private List<String> foundWords;
    private Map<String, JLabel> wordLabels;
    private Point startCell = null;
    private Point endCell = null;
    private List<Point> selectedCells = new ArrayList<>();
    private Map<Point, Color> foundCellColors = new HashMap<>();
    
    // สีธีม
    private final Color PURPLE_BG = new Color(200, 180, 220);
    private final Color PURPLE_DARK = new Color(120, 100, 140);
    private final Color PURPLE_LIGHT = new Color(220, 210, 240);
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 150);
    
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
    
    public WordSearchGame() {
        setTitle("Word Search Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(PURPLE_LIGHT);
        
        // กำหนดคำที่ต้องค้นหา (ตามรูป)
        wordsToFind = Arrays.asList(
            "CASTLE", "CURSE", "DARK", "DRAGON", "DREAM",
            "ENCHANT", "KING", "KNIGHT", "MAGIC", "SPELL",
            "STORY", "TALE", "WIZARD"
        );
        foundWords = new ArrayList<>();
        
        // สร้างตาราง
        createGrid();
        
        // สร้าง UI
        createUI();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createGrid() {
        grid = new char[][] {
            {'T', 'Q', 'E', 'S', 'R', 'U', 'C', 'L', 'L', 'E', 'P', 'S'},
            {'D', 'D', 'R', 'A', 'G', 'O', 'N', 'E', 'K', 'R', 'A', 'D'},
            {'C', 'R', 'K', 'Q', 'R', 'M', 'R', 'W', 'U', 'Z', 'F', 'L'},
            {'B', 'E', 'S', 'V', 'T', 'E', 'K', 'A', 'Q', 'H', 'V', 'Y'},
            {'L', 'A', 'T', 'C', 'K', 'T', 'D', 'R', 'A', 'Z', 'I', 'W'},
            {'F', 'M', 'O', 'I', 'I', 'X', 'T', 'N', 'G', 'Q', 'C', 'T'},
            {'T', 'V', 'R', 'G', 'N', 'N', 'H', 'E', 'O', 'S', 'A', 'L'},
            {'A', 'J', 'Y', 'A', 'G', 'U', 'G', 'Q', 'X', 'W', 'S', 'Y'},
            {'C', 'S', 'H', 'M', 'E', 'I', 'I', 'H', 'X', 'M', 'T', 'E'},
            {'L', 'T', 'N', 'A', 'H', 'C', 'N', 'E', 'H', 'J', 'L', 'F'},
            {'Z', 'Q', 'O', 'X', 'O', 'B', 'K', 'F', 'A', 'A', 'E', 'K'},
            {'B', 'D', 'X', 'C', 'X', 'Q', 'K', 'U', 'T', 'R', 'K', 'A'}
        };
    }
    
    private void createUI() {
        // Panel สำหรับตาราง
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBackground(PURPLE_DARK);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton btn = new JButton(String.valueOf(grid[row][col]));
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(PURPLE_DARK, 2));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setOpaque(true);
                
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
        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
        wordPanel.setBackground(PURPLE_LIGHT);
        wordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Words to Find:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PURPLE_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wordPanel.add(titleLabel);
        wordPanel.add(Box.createVerticalStrut(10));
        
        wordLabels = new HashMap<>();
        for (String word : wordsToFind) {
            JLabel label = new JLabel(word);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setForeground(PURPLE_DARK);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            wordLabels.put(word, label);
            wordPanel.add(label);
            wordPanel.add(Box.createVerticalStrut(5));
        }
        
        // เพิ่ม Reset Button
        JButton resetButton = new JButton("New Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(PURPLE_DARK);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.addActionListener(e -> resetGame());
        wordPanel.add(Box.createVerticalStrut(20));
        wordPanel.add(resetButton);
        
        add(gridPanel, BorderLayout.CENTER);
        add(wordPanel, BorderLayout.EAST);
    }
    
    private void highlightSelection() {
        // ล้างสีเดิม
        for (Point p : selectedCells) {
            if (!foundCellColors.containsKey(p)) {
                buttons[p.x][p.y].setBackground(Color.WHITE);
            }
        }
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
                if (!foundCellColors.containsKey(p)) {
                    buttons[currentRow][currentCol].setBackground(HIGHLIGHT_COLOR);
                }
                currentRow += rowStep;
                currentCol += colStep;
            }
        }
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
            
            // ทำเครื่องหมายเซลล์ที่พบด้วยสีที่แตกต่างกัน
            for (Point p : selectedCells) {
                foundCellColors.put(p, wordColor);
                buttons[p.x][p.y].setBackground(wordColor);
            }
            
            // ขีดฆ่าคำในรายการ
            JLabel label = wordLabels.get(foundWord);
            label.setText("<html><strike>" + foundWord + "</strike></html>");
            label.setForeground(new Color(100, 150, 100));
            
            // ตรวจสอบว่าชนะหรือยัง
            if (foundWords.size() == wordsToFind.size()) {
                JOptionPane.showMessageDialog(this, 
                    "Congratulations! You found all words!", 
                    "You Win!", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void clearSelection() {
        for (Point p : selectedCells) {
            if (!foundCellColors.containsKey(p)) {
                buttons[p.x][p.y].setBackground(Color.WHITE);
            }
        }
        selectedCells.clear();
        startCell = null;
        endCell = null;
    }
    
    private void resetGame() {
        foundWords.clear();
        foundCellColors.clear();
        currentColorIndex = 0;  // รีเซ็ต index สี
        
        // รีเซ็ตสีปุ่ม
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                buttons[row][col].setBackground(Color.WHITE);
            }
        }
        
        // รีเซ็ตรายการคำ
        for (String word : wordsToFind) {
            JLabel label = wordLabels.get(word);
            label.setText(word);
            label.setForeground(PURPLE_DARK);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordSearchGame());
    }
}
