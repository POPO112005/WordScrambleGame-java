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
    private List<FoundWord> foundWordsList = new ArrayList<>();
    private JPanel wordPanel;
    private JPanel gridPanel;
    private JPanel highlightPanel;
    private Random random = new Random();
    
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
        "SUNSET", "BEACH", "ISLAND"
    );
    
    public WordSearchGame() {
        setTitle("Word Search Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(PURPLE_LIGHT);
        
        // สุ่มเลือกคำที่ต้องค้นหา (7-10 คำ)
        selectRandomWords();
        foundWords = new ArrayList<>();
        
        // สร้างตาราง
        createGrid();
        
        // สร้าง UI
        createUI();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void selectRandomWords() {
        // สุ่มจำนวนคำที่จะใช้ (7-10 คำ)
        int numberOfWords = 7 + random.nextInt(4); // 7, 8, 9, หรือ 10 คำ
        
        // สร้างลิสต์คำทั้งหมดแล้วสับเปลี่ยน
        List<String> shuffledWords = new ArrayList<>(ALL_WORDS);
        Collections.shuffle(shuffledWords, random);
        
        // เลือกเฉพาะจำนวนที่ต้องการ
        wordsToFind = new ArrayList<>(shuffledWords.subList(0, numberOfWords));
    }
    
    private void createGrid() {
        grid = new char[][] {
            {'M', 'O', 'U', 'N', 'T', 'A', 'I', 'N', 'B', 'E', 'A', 'C'},
            {'F', 'L', 'O', 'W', 'E', 'R', 'W', 'H', 'A', 'L', 'E', 'H'},
            {'O', 'C', 'E', 'A', 'N', 'A', 'I', 'G', 'B', 'L', 'E', 'A'},
            {'R', 'E', 'S', 'U', 'N', 'S', 'E', 'T', 'U', 'E', 'A', 'G'},
            {'E', 'L', 'I', 'R', 'A', 'I', 'N', 'B', 'O', 'W', 'G', 'L'},
            {'S', 'U', 'V', 'I', 'R', 'T', 'I', 'G', 'E', 'R', 'L', 'E'},
            {'T', 'R', 'E', 'V', 'E', 'R', 'O', 'F', 'T', 'E', 'E', 'Y'},
            {'Y', 'E', 'R', 'I', 'V', 'E', 'R', 'E', 'L', 'L', 'Y', 'R'},
            {'I', 'S', 'L', 'A', 'N', 'D', 'E', 'K', 'L', 'Y', 'D', 'E'},
            {'B', 'U', 'T', 'T', 'E', 'R', 'F', 'L', 'Y', 'X', 'A', 'T'},
            {'C', 'B', 'E', 'A', 'C', 'H', 'X', 'Q', 'O', 'W', 'N', 'M'},
            {'H', 'X', 'Q', 'M', 'T', 'N', 'U', 'O', 'M', 'Z', 'O', 'M'}
        };
    }
    
    private void createUI() {
        // Panel หลักที่จะใช้ LayeredPane เพื่อวาด highlight ทับด้านบน
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(
            GRID_SIZE * (CELL_SIZE + 2) + 20,
            GRID_SIZE * (CELL_SIZE + 2) + 20
        ));
        
        // Panel สำหรับตาราง
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBackground(PURPLE_DARK);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBounds(0, 0, 
            GRID_SIZE * (CELL_SIZE + 2) + 20,
            GRID_SIZE * (CELL_SIZE + 2) + 20
        );
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        
        // Panel สำหรับวาด highlight ทับด้านบน
        JPanel highlightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
                    Color transparentHighlight = new Color(HIGHLIGHT_COLOR.getRed(), 
                                                           HIGHLIGHT_COLOR.getGreen(), 
                                                           HIGHLIGHT_COLOR.getBlue(), 180);
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
        
        // เก็บ reference ของ highlightPanel
        this.highlightPanel = highlightPanel;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton btn = new JButton(String.valueOf(grid[row][col]));
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                btn.setBackground(PURPLE_LIGHT);
                btn.setForeground(PURPLE_DARK);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(PURPLE_BG, 1));
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
        wordPanel.setBackground(PURPLE_LIGHT);
        wordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateWordList();
        
        add(layeredPane, BorderLayout.CENTER);
        add(wordPanel, BorderLayout.EAST);
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
                JOptionPane.showMessageDialog(this, 
                    "Congratulations! You found all words!", 
                    "You Win!", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void clearSelection() {
        selectedCells.clear();
        startCell = null;
        endCell = null;
        highlightPanel.repaint();
    }
    
    private void updateWordList() {
        wordPanel.removeAll();
        
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
        
        wordPanel.revalidate();
        wordPanel.repaint();
    }
    
    private void resetGame() {
        // สุ่มคำใหม่
        selectRandomWords();
        
        foundWords.clear();
        foundWordsList.clear();
        currentColorIndex = 0;  // รีเซ็ต index สี
        
        // อัพเดทรายการคำใหม่
        updateWordList();
        
        // Repaint ตาราง
        highlightPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordSearchGame());
    }
}
