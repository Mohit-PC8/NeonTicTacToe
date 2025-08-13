import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class TicTacToeGUI extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';
    private boolean gameOver = false;
    private JLabel statusLabel;
    private int playerXScore = 0;
    private int playerOScore = 0;
    private int moves = 0;
    private List<Point> winningLine = new ArrayList<>();
    private javax.swing.Timer animationTimer;
    private float animationProgress = 0f;
    private Color[] backgroundColors = {
        new Color(30, 30, 50), 
        new Color(40, 40, 60), 
        new Color(50, 50, 70)
    };
    private Color[] highlightColors = {
        new Color(255, 105, 97), 
        new Color(119, 221, 119), 
        new Color(97, 175, 255)
    };
    private Color boardColor = new Color(25, 25, 40);
    private Color accentColor = new Color(255, 215, 0);
    private ParticleSystem particleSystem;
    private javax.swing.Timer particleTimer;
    private JPanel boardPanel;

    public TicTacToeGUI() {
        setTitle("Neon Tic Tac Toe");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(boardColor);
        setResizable(false);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(boardColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("NEON TIC TAC TOE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(accentColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create score panel
        JPanel scorePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        scorePanel.setOpaque(false);
        
        JPanel xScorePanel = createScorePanel("PLAYER X", playerXScore, new Color(255, 105, 97));
        JPanel oScorePanel = createScorePanel("PLAYER O", playerOScore, new Color(97, 175, 255));
        
        scorePanel.add(xScorePanel);
        scorePanel.add(oScorePanel);
        headerPanel.add(scorePanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create game board panel
        boardPanel = new JPanel(new GridLayout(3, 3, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (particleSystem != null) {
                    particleSystem.draw((Graphics2D) g);
                }
            }
        };
        boardPanel.setBackground(boardColor);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        initializeBoard(boardPanel);
        add(boardPanel, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(boardColor);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        statusLabel = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusLabel.setForeground(Color.WHITE);
        controlPanel.add(statusLabel, BorderLayout.CENTER);
        
        JButton resetButton = new JButton("NEW GAME");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setBackground(accentColor);
        resetButton.setForeground(boardColor);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resetButton.addActionListener(e -> resetGame());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        setVisible(true);
        
        // Initialize animation timer
        animationTimer = new javax.swing.Timer(20, e -> {
            animationProgress = (animationProgress + 0.05f) % 1.0f;
            repaint();
        });
        animationTimer.start();
        
        // Initialize particle system
        particleSystem = new ParticleSystem();
        
        // Initialize particle timer
        particleTimer = new javax.swing.Timer(30, e -> {
            particleSystem.update();
            boardPanel.repaint();
        });
        particleTimer.start();
    }

    private JPanel createScorePanel(String player, int score, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel playerLabel = new JLabel(player);
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playerLabel.setForeground(color);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(playerLabel, BorderLayout.NORTH);
        
        JLabel scoreValue = new JLabel(String.valueOf(score));
        scoreValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        scoreValue.setForeground(Color.WHITE);
        scoreValue.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(scoreValue, BorderLayout.CENTER);
        
        return panel;
    }

    private void initializeBoard(JPanel boardPanel) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = ' ';
                
                // Create final copies for use in inner class
                final int finalRow = row;
                final int finalCol = col;
                
                // Create custom button with rounded corners and gradient
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Paint gradient background
                        GradientPaint gradient = new GradientPaint(
                            0, 0, backgroundColors[(finalRow + finalCol) % backgroundColors.length], 
                            getWidth(), getHeight(), backgroundColors[(finalRow + finalCol + 1) % backgroundColors.length]
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                        
                        // Paint border
                        g2d.setPaint(new GradientPaint(
                            0, 0, highlightColors[(finalRow + finalCol) % highlightColors.length],
                            getWidth(), getHeight(), highlightColors[(finalRow + finalCol + 1) % highlightColors.length]
                        ));
                        g2d.setStroke(new BasicStroke(3f));
                        g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 25, 25);
                        
                        // Draw X or O
                        if (getText().equals("X")) {
                            drawAnimatedX(g2d);
                        } else if (getText().equals("O")) {
                            drawAnimatedO(g2d);
                        }
                        
                        g2d.dispose();
                    }
                    
                    private void drawAnimatedX(Graphics2D g2d) {
                        float progress = Math.min(1.0f, animationProgress * 1.5f);
                        
                        int padding = 20;
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;
                        int size = Math.min(getWidth(), getHeight()) - padding * 2;
                        
                        // Draw animated X
                        g2d.setColor(new Color(255, 105, 97, 200));
                        g2d.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        
                        int startX1 = centerX - size/2;
                        int startY1 = centerY - size/2;
                        int endX1 = startX1 + (int)(size * progress);
                        int endY1 = startY1 + (int)(size * progress);
                        g2d.drawLine(startX1, startY1, endX1, endY1);
                        
                        int startX2 = centerX + size/2;
                        int startY2 = centerY - size/2;
                        int endX2 = startX2 - (int)(size * progress);
                        int endY2 = startY2 + (int)(size * progress);
                        g2d.drawLine(startX2, startY2, endX2, endY2);
                        
                        // Draw glow effect
                        if (progress >= 1.0f) {
                            float glowIntensity = (float)(0.5 + 0.5 * Math.sin(animationProgress * Math.PI * 4));
                            g2d.setPaint(new RadialGradientPaint(
                                centerX, centerY, size/2,
                                new float[]{0.0f, 1.0f},
                                new Color[]{
                                    new Color(255, 105, 97, (int)(100 * glowIntensity)),
                                    new Color(255, 105, 97, 0)
                                }
                            ));
                            g2d.fillOval(centerX - size/2, centerY - size/2, size, size);
                        }
                    }
                    
                    private void drawAnimatedO(Graphics2D g2d) {
                        float progress = Math.min(1.0f, animationProgress * 1.5f);
                        
                        int padding = 20;
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;
                        int size = Math.min(getWidth(), getHeight()) - padding * 2;
                        
                        // Draw animated O
                        g2d.setColor(new Color(97, 175, 255, 200));
                        g2d.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        
                        Arc2D arc = new Arc2D.Float(
                            centerX - size/2, centerY - size/2, 
                            size, size, 90, 360 * progress, Arc2D.OPEN
                        );
                        g2d.draw(arc);
                        
                        // Draw glow effect
                        if (progress >= 1.0f) {
                            float glowIntensity = (float)(0.5 + 0.5 * Math.sin(animationProgress * Math.PI * 4));
                            g2d.setPaint(new RadialGradientPaint(
                                centerX, centerY, size/2,
                                new float[]{0.0f, 1.0f},
                                new Color[]{
                                    new Color(97, 175, 255, (int)(100 * glowIntensity)),
                                    new Color(97, 175, 255, 0)
                                }
                            ));
                            g2d.fillOval(centerX - size/2, centerY - size/2, size, size);
                        }
                    }
                };
                
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setFont(new Font("Segoe UI", Font.BOLD, 60));
                button.setForeground(Color.WHITE);
                button.addActionListener(new ButtonClickListener(row, col));
                
                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;
        
        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameOver || board[row][col] != ' ') {
                return;
            }
            
            board[row][col] = currentPlayer;
            moves++;
            
            // Get button position relative to board panel
            Rectangle bounds = buttons[row][col].getBounds();
            Point position = buttons[row][col].getLocation();
            int x = position.x + bounds.width/2;
            int y = position.y + bounds.height/2;
            
            // Create particles
            if (currentPlayer == 'X') {
                particleSystem.createParticleExplosion(x, y, new Color(255, 105, 97));
            } else {
                particleSystem.createParticleExplosion(x, y, new Color(97, 175, 255));
            }
            
            buttons[row][col].setText(String.valueOf(currentPlayer));
            
            if (haveWon(board, currentPlayer)) {
                gameOver = true;
                highlightWinningLine();
                
                if (currentPlayer == 'X') {
                    playerXScore++;
                    statusLabel.setText("Player X Wins!");
                } else {
                    playerOScore++;
                    statusLabel.setText("Player O Wins!");
                }
                updateScoreDisplay();
                
                // Create winning particles
                for (Point p : winningLine) {
                    Rectangle winBounds = buttons[p.x][p.y].getBounds();
                    Point winPosition = buttons[p.x][p.y].getLocation();
                    int winX = winPosition.x + winBounds.width/2;
                    int winY = winPosition.y + winBounds.height/2;
                    particleSystem.createParticleExplosion(winX, winY, accentColor);
                }
                return;
            }
            
            if (moves == 9) {
                gameOver = true;
                statusLabel.setText("It's a Draw!");
                
                // Create particles for draw
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        Rectangle drawBounds = buttons[r][c].getBounds();
                        Point drawPosition = buttons[r][c].getLocation();
                        int drawX = drawPosition.x + drawBounds.width/2;
                        int drawY = drawPosition.y + drawBounds.height/2;
                        particleSystem.createParticleExplosion(drawX, drawY, new Color(119, 221, 119));
                    }
                }
                return;
            }
            
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            statusLabel.setText("Player " + currentPlayer + "'s Turn");
        }
    }

    public boolean haveWon(char[][] board, char player) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                winningLine.add(new Point(row, 0));
                winningLine.add(new Point(row, 1));
                winningLine.add(new Point(row, 2));
                return true;
            }
        }
        
        // Check columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                winningLine.add(new Point(0, col));
                winningLine.add(new Point(1, col));
                winningLine.add(new Point(2, col));
                return true;
            }
        }
        
        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            winningLine.add(new Point(0, 0));
            winningLine.add(new Point(1, 1));
            winningLine.add(new Point(2, 2));
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            winningLine.add(new Point(0, 2));
            winningLine.add(new Point(1, 1));
            winningLine.add(new Point(2, 0));
            return true;
        }
        
        return false;
    }
    
    private void highlightWinningLine() {
        // Start a timer to animate the winning line
        javax.swing.Timer highlightTimer = new javax.swing.Timer(50, new ActionListener() {
            int count = 0;
            Color[] colors = {Color.YELLOW, new Color(255, 215, 0), Color.ORANGE};
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count >= 15) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    return;
                }
                
                for (Point p : winningLine) {
                    buttons[p.x][p.y].setBackground(colors[count % colors.length]);
                    buttons[p.x][p.y].setOpaque(true);
                }
                
                count++;
            }
        });
        highlightTimer.start();
    }
    
    private void updateScoreDisplay() {
        // Get the header panel (which is the first component in BorderLayout.NORTH)
        Component headerComp = getContentPane().getComponent(0);
        if (headerComp instanceof JPanel) {
            JPanel headerPanel = (JPanel) headerComp;
            
            // Get the score panel (which is the second component in headerPanel)
            Component scoreComp = headerPanel.getComponent(1);
            if (scoreComp instanceof JPanel) {
                JPanel scorePanel = (JPanel) scoreComp;
                
                // Get both player score panels
                Component[] scorePanels = scorePanel.getComponents();
                
                // First panel is for player X
                if (scorePanels.length > 0 && scorePanels[0] instanceof JPanel) {
                    JPanel xPanel = (JPanel) scorePanels[0];
                    updatePanelScore(xPanel, playerXScore);
                }
                
                // Second panel is for player O
                if (scorePanels.length > 1 && scorePanels[1] instanceof JPanel) {
                    JPanel oPanel = (JPanel) scorePanels[1];
                    updatePanelScore(oPanel, playerOScore);
                }
            }
        }
    }
    
    private void updatePanelScore(JPanel panel, int score) {
        // Find the score label in the panel
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() == 36) { // Score label has font size 36
                    label.setText(String.valueOf(score));
                }
            }
        }
    }

    private void resetGame() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = ' ';
                buttons[row][col].setText("");
                buttons[row][col].setOpaque(false);
            }
        }
        
        currentPlayer = 'X';
        gameOver = false;
        moves = 0;
        winningLine.clear();
        statusLabel.setText("Player X's Turn");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel for modern UI
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TicTacToeGUI();
        });
    }
    
    // Particle class for visual effects
    static class Particle {
        float x, y;
        float vx, vy;
        float size;
        Color color;
        int life;
        int maxLife;
        
        public Particle(float x, float y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = (float) (5 + Math.random() * 10);
            this.vx = (float) (Math.random() * 8 - 4);
            this.vy = (float) (Math.random() * 8 - 4);
            this.maxLife = (int) (20 + Math.random() * 30);
            this.life = maxLife;
        }
        
        public void update() {
            x += vx;
            y += vy;
            vy += 0.1; // gravity
            life--;
        }
        
        public void draw(Graphics2D g2d) {
            float alpha = (float) life / maxLife;
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            g2d.fillOval((int)x, (int)y, (int)size, (int)size);
            
            // Add glow effect
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 100)));
            g2d.fillOval((int)(x-size/2), (int)(y-size/2), (int)(size*2), (int)(size*2));
        }
        
        public boolean isAlive() {
            return life > 0;
        }
    }
    
    // Particle system to manage all particles
    static class ParticleSystem {
        private List<Particle> particles = new ArrayList<>();
        
        public void createParticleExplosion(int x, int y, Color color) {
            for (int i = 0; i < 50; i++) {
                particles.add(new Particle(x, y, color));
            }
        }
        
        public void update() {
            Iterator<Particle> it = particles.iterator();
            while (it.hasNext()) {
                Particle p = it.next();
                p.update();
                if (!p.isAlive()) {
                    it.remove();
                }
            }
        }
        
        public void draw(Graphics2D g2d) {
            for (Particle p : particles) {
                p.draw(g2d);
            }
        }
    }
}