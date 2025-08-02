import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GUI extends JFrame {
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton submitButton;
    private JButton minimizeButton;
    private JButton maximizeButton;
    private JButton closeButton;
    private JPanel titleBar;

    // For dragging functionality
    private Point mousePoint;
    private boolean isMaximized = false;
    private Rectangle normalBounds;

    public GUI() {
        initializeFrame();
        setupComponents();
        layoutComponents();
        setupEventHandlers();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("GUI");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close manually
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true); // Remove default decorations

        setAlwaysOnTop(true);

        // Make frame transparent
        setOpacity(0.9f);

        // Set background color with transparency support
        setBackground(new Color(0, 0, 0, 144));

        // Use a dark theme background
        getContentPane().setBackground(new Color(30, 30, 40, 230));

        // Store normal bounds for maximize/restore functionality
        normalBounds = getBounds();
    }

    private void setupComponents() {
        // Create custom title bar
        createTitleBar();

        // Output area at top left
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(40, 40, 50, 200));
        outputArea.setForeground(new Color(220, 220, 220));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setText("Welcome to GUI!\nOutput will appear here...");

        // Input field at bottom
        inputField = new JTextField();
        inputField.setBackground(new Color(50, 50, 60, 200));
        inputField.setForeground(new Color(255, 255, 255));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        inputField.setCaretColor(Color.WHITE);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(70, 130, 180, 200));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        submitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 1),
                new EmptyBorder(5, 15, 5, 15)
        ));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(90, 150, 200, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(70, 130, 180, 200));
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Add title bar at the top
        add(titleBar, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setOpaque(false);

        // Output area with scroll pane
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Bottom panel for input and button
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // Submit button action
        ActionListener submitAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (!input.isEmpty()) {
                    appendOutput("Input: " + input);
                    inputField.setText("");
                    processInput(input);
                }
            }
        };

        submitButton.addActionListener(submitAction);
        inputField.addActionListener(submitAction); // Enter key support

        // Setup window control actions
        setupWindowControls();

        // Setup dragging functionality
        setupDragging();
    }

    private void createTitleBar() {
        titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(45, 45, 55, 230));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 100, 120)));
        titleBar.setPreferredSize(new Dimension(0, 30));

        // Title label
        JLabel titleLabel = new JLabel("GUI Application");
        titleLabel.setForeground(new Color(220, 220, 220));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Control buttons panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        controlsPanel.setOpaque(false);

        // Create control buttons
        minimizeButton = createControlButton("_", new Color(100, 150, 100));
        maximizeButton = createControlButton("□", new Color(100, 100, 150));
        closeButton = createControlButton("×", new Color(150, 100, 100));

        controlsPanel.add(minimizeButton);
        controlsPanel.add(maximizeButton);
        controlsPanel.add(closeButton);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(controlsPanel, BorderLayout.EAST);
    }

    private JButton createControlButton(String text, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(200, 200, 200));
        button.setBackground(new Color(60, 60, 70, 150));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(26, 26));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        Color originalColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void setupWindowControls() {
        // Close button
        closeButton.addActionListener(e -> System.exit(0));

        // Minimize button
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));

        // Maximize button
        maximizeButton.addActionListener(e -> toggleMaximize());
    }

    private void toggleMaximize() {
        if (isMaximized) {
            // Restore to normal size
            setBounds(normalBounds);
            maximizeButton.setText("□");
            isMaximized = false;
        } else {
            // Store current bounds and maximize
            normalBounds = getBounds();
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle maxBounds = env.getMaximumWindowBounds();
            setBounds(maxBounds);
            maximizeButton.setText("❐");
            isMaximized = true;
        }
    }

    private void setupDragging() {
        // Make title bar draggable
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePoint = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Double-click to maximize/restore
                if (e.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mousePoint != null && !isMaximized) {
                    Point currentLocation = getLocation();
                    setLocation(
                            currentLocation.x + e.getX() - mousePoint.x,
                            currentLocation.y + e.getY() - mousePoint.y
                    );
                }
            }
        });
    }

    // Helper method to process input (override in subclasses)
    protected void processInput(String input) {
        appendOutput("Processed: " + input.toUpperCase());
    }

    // Helper Methods for Output Management

    /**
     * Appends text to the output area with a timestamp
     */
    public void appendOutput(String text) {
        String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
        );
        outputArea.append("\n[" + timestamp + "] " + text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * Sets the output text, replacing all existing content
     */
    public void setOutput(String text) {
        outputArea.setText(text);
    }

    /**
     * Clears all output text
     */
    public void clearOutput() {
        outputArea.setText("");
    }

    /**
     * Appends text without timestamp
     */
    public void appendOutputRaw(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * Sets the output text color
     */
    public void setOutputColor(Color color) {
        outputArea.setForeground(color);
    }

    /**
     * Gets the current output text
     */
    public String getOutputText() {
        return outputArea.getText();
    }

    /**
     * Sets the input field placeholder text
     */
    public void setInputPlaceholder(String placeholder) {
        inputField.setToolTipText(placeholder);
    }

    /**
     * Gets the current input text
     */
    public String getInputText() {
        return inputField.getText();
    }

    /**
     * Sets the input field text
     */
    public void setInputText(String text) {
        inputField.setText(text);
    }

    // Main method for testing
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new GUI();
        });
    }
}