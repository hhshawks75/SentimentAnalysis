/**This program is a SentimentAnalysisApp. Assignment3.java is
 * the main class which implements the GUI and runs the SentimentAnalysisApp
 * program. The user will interact with the interface generated to
 * obtain the desired results.
 * @author Zach Almaraz
 *
 */


package movieReviewClassification;

import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.IOException;

/**
 @author metsis
 @author tesic
 @author wen
 */


public class Assignment3 {

        //Create ReviewHandler object
        private static final ReviewHandler rh = new ReviewHandler();

        //Create Log
        static protected final Logger log = Logger.getLogger("SentimentAnalysisApp");



        /**
         * Main method
         */

        public static void main(String[] args) throws IOException {
            FileHandler fh;

            try {
                // Configures the logger with handler and formatter
                fh = new FileHandler("SentimentAnalysis.%u.%g.log");
                log.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.setLevel(Level.INFO);

            // Load database if it exists
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();

                    File databaseFile = new File(ReviewHandler.DATA_FILE_NAME);
                    if (databaseFile.exists()) {
                        rh.loadSerialDB();
                    }

                }
            });
        }

        //Components for the layout
        static private final JPanel topPanel = new JPanel();
        static private final JPanel bottomPanel = new JPanel();;
        static private final JLabel commandLabel = new JLabel("Select",JLabel.RIGHT);
        static private final JComboBox comboBox = new JComboBox();
        static private final JButton databaseButton = new JButton("Show Database");
        static private final JButton saveButton = new JButton("Save Database");
        //Output area.
        // Set as global to be edited in different methods.
        static protected final JTextArea outputArea = new JTextArea();
        static private final JScrollPane outputScrollPane = new JScrollPane(outputArea);
        //width and height of monitor
        private static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        private static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        //width and height of window
        private static int windowsWidth = 1100;
        private static int windowsHeight = 800;

        /**
         * Initialize the JFrame and JPanels and set the location to the middle of the screen.
         */

        private static void createAndShowGUI() {

            createTopPanel();
            createBottomPanel();

            topPanel.getIgnoreRepaint();
            JPanel panelContainer = new JPanel();
            panelContainer.setLayout(new GridLayout(2,0));
            panelContainer.add(topPanel);
            panelContainer.add(bottomPanel);

            JFrame.setDefaultLookAndFeelDecorated(true);
            JFrame frame = new JFrame("SentimentAnalysisApp");

            // Save when quit.

            frame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    log.info("Closing window.");
                    outputArea.append("Closing window. Database will be saved.\n");
                    super.windowClosing(e);
                    log.info("Saving database.");
                    rh.saveSerialDB();
                    log.info("System shutdown.");
                    System.exit(0);
                }

            });
            panelContainer.setOpaque(true);
            frame.setBounds((width - windowsWidth) / 2,
                    (height - windowsHeight) / 2, windowsWidth, windowsHeight);
            frame.setContentPane(panelContainer);

            frame.setVisible(true);


        }

        /**
         * Method to initialize TopPanel
         */

        private static void createTopPanel() {
            comboBox.addItem("Choose an option below...");
            comboBox.addItem(" 1. Load new movie review collection (given a folder or a file path).");
            comboBox.addItem(" 2. Delete movie review from database (given its id).");
            comboBox.addItem(" 3. Search movie reviews in database by id.");
            comboBox.addItem(" 4. Search movie reviews in database by substring.");
            comboBox.addItem(" 0. Exit program.");
            comboBox.setSelectedIndex(0);

            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    log.info("Command chosen, Item = " + e.getItem());
                    log.info("StateChange = " + e.getStateChange());
                    if (e.getStateChange() == 1) {
                        if (e.getItem().equals("Please select...")) {
                            outputArea.setText("");
                            outputArea.append(rh.database.size() + " records in database.\n");
                            outputArea.append("Please select a command to continue.\n");
                            topPanel.removeAll();
                            topPanel.add(commandLabel);
                            topPanel.add(comboBox);
                            //Keep the comboBox at the first line.
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());

                            topPanel.add(new JLabel());
                            topPanel.add(new JLabel());
                            topPanel.add(databaseButton);
                            topPanel.add(saveButton);
                            topPanel.updateUI();
                        } else if (e.getItem().equals(" 1. Load new movie review collection (given a folder or a file path).")) {
                            loadReviews();
                        } else if (e.getItem().equals(" 2. Delete movie review from database (given its id).")) {
                            deleteReviews();
                        } else if (e.getItem().equals(" 3. Search movie reviews in database by id.")) {
                            searchReviewsId();
                        } else if (e.getItem().equals(" 4. Search movie reviews in database by substring.")) {
                            searchReviewsSubstring();
                        } else if (e.getItem().equals(" 0. Exit program.")) {
                            exit();
                        }
                    }

                }
            });

            //multi-threading
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("Save button clicked.");
                    Runnable myRunnable = new Runnable() {

                        public void run() {
                            rh.saveSerialDB();
                            outputArea.append("Database saved.\n");

                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });

            //multi-threading
            databaseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("database button clicked.");
                    Runnable myRunnable = new Runnable() {

                        public void run() {
                            printJTable(rh.searchBySubstring(""));
                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });



            GridLayout topPanelGridLayout = new GridLayout(0,2,10,10);
            topPanel.setLayout(topPanelGridLayout);
            topPanel.add(commandLabel);
            topPanel.add(comboBox);

            // Ensure comboBox is at the first line.
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());

            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(databaseButton);
            topPanel.add(saveButton);
            topPanel.updateUI();
        }

        /**
         * This method initialize the bottom panel, which is the output area.
         * Just a TextArea that not editable.
         */

        private static void createBottomPanel() {

            final Font fontCourier = new Font("Courier", Font.PLAIN, 14);
            DefaultCaret caret = (DefaultCaret)outputArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            outputArea.setFont(fontCourier);

            outputArea.setText("Sentiment Analysis Application.\n");
            outputArea.setEditable(false);

            final Border border = BorderFactory.createLineBorder(Color.BLACK);
            outputArea.setBorder(BorderFactory.createCompoundBorder(border,
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            bottomPanel.setBorder(BorderFactory.createCompoundBorder(border,
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            outputScrollPane.createVerticalScrollBar();
            outputScrollPane.createHorizontalScrollBar();
            bottomPanel.setLayout(new GridLayout(1,0));
            bottomPanel.add(outputScrollPane);
        }


    /**
     * Method command
     * 0. Quit and save program
     */

    public static void exit() {

        outputArea.setText("");
        outputArea.append(rh.database.size() + " records in database.\n");
        outputArea.append("Command 0\n");
        outputArea.append("Please click Confirm to save and exit the system.\n");

        topPanel.removeAll();
        topPanel.add(commandLabel);
        topPanel.add(comboBox);

        final JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Confirm button clicked. (Command 0)");
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        log.info("Saving database");
                        rh.saveSerialDB();

                        outputArea.append("Database saved. System will be closed in 4 seconds.\n");
                        outputArea.append("Thank you for using!\n");

                        log.info("Exit the database. (Command 0)");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        log.info("System shutdown.");
                        System.exit(0);
                    }
                };

                Thread thread = new Thread(myRunnable);
                thread.start();
            }

        });

        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());
        topPanel.add(new JLabel());

        topPanel.add(new JLabel());
        topPanel.add(confirmButton);
        topPanel.add(databaseButton);
        topPanel.add(saveButton);
        topPanel.updateUI();
        topPanel.updateUI();
    }

        /**
         * Method Command 1.
         * Load new movie review collection(given folder or filepath)
         *
         */

        static int realClass = 0;
        public static void loadReviews() {
            outputArea.setText("");
            outputArea.append(rh.database.size() + " records in database.\n");
            outputArea.append("Command 1\n");
            outputArea.append("Please input the path of file or folder.\n");

            topPanel.removeAll();
            topPanel.add(commandLabel);
            topPanel.add(comboBox);

            final JLabel pathLabel = new JLabel("File path:",JLabel.RIGHT);
            final JTextField pathInput = new JTextField("");

            final JLabel realClassLabel = new JLabel("Real class:",JLabel.RIGHT);
            final JComboBox realClassComboBox = new JComboBox();
            realClassComboBox.addItem("Negative");
            realClassComboBox.addItem("Positive");
            realClassComboBox.addItem("Unknown");

            realClassComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    log.info("Real class chosen, real class = " + e.getItem());
                    log.info("StateChange = " + e.getStateChange());
                    if (e.getStateChange() == 1) {
                        if (e.getItem().equals("Negative")) {
                            realClass = 0;
                        } else if (e.getItem().equals("Positive")) {
                            realClass = 1;
                        } else if (e.getItem().equals("Unknown")) {
                            realClass = 2;
                        }
                    }

                }
            });

            final JButton confirmButton = new JButton("Confirm");

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("Confirm button clicked. (Command 1)");
                    Runnable myRunnable = new Runnable() {

                        public void run() {
                            String path = pathInput.getText();
                            rh.loadReviews(path, realClass);

                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });
            topPanel.add(pathLabel);
            topPanel.add(pathInput);
            topPanel.add(realClassLabel);
            topPanel.add(realClassComboBox);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());

            topPanel.add(new JLabel());
            topPanel.add(confirmButton);
            topPanel.add(databaseButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

            outputArea.append(rh.database.size() + " records in database.\n");
        }

        /**
         * Method command
         * 2. Delete movie review given ID
         *
         */

        public static void deleteReviews() {
            outputArea.setText("");
            outputArea.append(rh.database.size() + " records in database.\n");
            outputArea.append("Command 2\n");
            outputArea.append("Please input the review ID.\n");

            topPanel.removeAll();
            topPanel.add(commandLabel);
            topPanel.add(comboBox);

            final JLabel reviewIdLabel = new JLabel("Review ID:",JLabel.RIGHT);
            final JTextField reviewIdInput = new JTextField("");

            final JButton confirmButton = new JButton("Confirm");

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("Confirm button clicked. (Command 2)");
                    Runnable myRunnable = new Runnable() {

                        public void run() {
                            String idStr = reviewIdInput.getText();
                            if (!idStr.matches("-?(0|[1-9]\\d*)")) {
                                // Input is not an integer
                                outputArea.append("Illegal input.\n");
                            } else {
                                int id = Integer.parseInt(idStr);
                                rh.deleteReview(id);
                            }
                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });
            topPanel.add(reviewIdLabel);
            topPanel.add(reviewIdInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(confirmButton);
            topPanel.add(databaseButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

            outputArea.append(rh.database.size() + " records in database.\n");

        }

        /**
         * Method command
         * 3. search reviews from database by Id
         *
         */

        public static void searchReviewsId() {
            outputArea.setText("");
            outputArea.append(rh.database.size() + " records in database.\n");
            outputArea.append("Command 3\n");
            outputArea.append("Please input the review ID.\n");

            topPanel.removeAll();
            topPanel.add(commandLabel);
            topPanel.add(comboBox);

            final JLabel reviewIdLabel = new JLabel("Review ID:",JLabel.RIGHT);
            final JTextField reviewIdInput = new JTextField("");

            final JButton confirmButton = new JButton("Confirm");

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("Confirm button clicked. (Command 3)");
                    Runnable myRunnable = new Runnable() {

                        public void run() {
                            String idStr = reviewIdInput.getText();
                            if (!idStr.matches("-?(0|[1-9]\\d*)")) {
                                // Input is not an integer
                                outputArea.append("Illegal input.\n");
                            } else {
                                int id = Integer.parseInt(idStr);
                                MovieReview mr = rh.searchById(id);
                                if (mr != null) {
                                    List<MovieReview> reviewList = new ArrayList<MovieReview>();
                                    reviewList.add(mr);
                                    printJTable(reviewList);
                                } else {
                                    outputArea.append("Review not found.\n");
                                }
                            }
                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });
            topPanel.add(reviewIdLabel);
            topPanel.add(reviewIdInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());

            topPanel.add(new JLabel());
            topPanel.add(confirmButton);
            topPanel.add(databaseButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

            outputArea.append(rh.database.size() + " records in database.\n");
        }

        /**
         * Method command
         * 4. search reviews from database by Id.
         *
         */

        public static void searchReviewsSubstring() {
            outputArea.setText("");
            outputArea.append(rh.database.size() + " records in database.\n");
            outputArea.append("Command 4\n");
            outputArea.append("Please input the review substring.\n");

            topPanel.removeAll();
            topPanel.add(commandLabel);
            topPanel.add(comboBox);

            final JLabel subStringLabel = new JLabel("Review ID:",JLabel.RIGHT);
            final JTextField subStringInput = new JTextField("");

            final JButton confirmButton = new JButton("Confirm");

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.info("Confirm button clicked. (Command 4)");
                    Runnable myRunnable = new Runnable() {

                        public void run() {

                            String substring = subStringInput.getText();
                            List<MovieReview> reviewList = rh.searchBySubstring(substring);
                            if (reviewList != null) {
                                printJTable(reviewList);
                                outputArea.append(reviewList.size() + " reviews found.\n");

                            } else {
                                outputArea.append("Review not found.\n");
                            }

                        }
                    };

                    Thread thread = new Thread(myRunnable);
                    thread.start();
                }

            });
            topPanel.add(subStringLabel);
            topPanel.add(subStringInput);
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());
            topPanel.add(new JLabel());

            topPanel.add(new JLabel());
            topPanel.add(confirmButton);
            topPanel.add(databaseButton);
            topPanel.add(saveButton);
            topPanel.updateUI();

            outputArea.append(rh.database.size() + " records in database.\n");
        }

        /**
         * Print JTable for target_list
         */

        public static void printJTable(List<MovieReview> target_List) {
            // Create columns names
            String columnNames[] = {"ID", "Predicted", "Real", "Text"};
            // Create some data
            String dataValues[][]= new String[target_List.size()][4];
            for(int i = 0; i < target_List.size(); i++) {
                String predicted = "";
                if (target_List.get(i).getPredictedPolarity() == 0) {
                    predicted = "Negative";
                } else if (target_List.get(i).getPredictedPolarity() == 1) {
                    predicted = "Positive";
                } else if (target_List.get(i).getPredictedPolarity() == 2) {
                    predicted = "Unknown";
                }
                String real = "";
                if (target_List.get(i).getRealPolarity() == 0) {
                    real = "Negative";
                } else if (target_List.get(i).getRealPolarity() == 1) {
                    real = "Positive";
                } else if (target_List.get(i).getRealPolarity() == 2) {
                    real = "Unknown";
                }
                dataValues[i][0] = String.valueOf(target_List.get(i).getId());
                dataValues[i][1] = predicted;
                dataValues[i][2] = real;
                dataValues[i][3] = target_List.get(i).getText();

            }
            // Create a table
            JTable table = new JTable(dataValues, columnNames) {
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

            // Adds table to scrolling pane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.createVerticalScrollBar();
            scrollPane.createHorizontalScrollBar();
            scrollPane.createVerticalScrollBar();
            scrollPane.createHorizontalScrollBar();
            JFrame.setDefaultLookAndFeelDecorated(true);
            JFrame resultFrame = new JFrame("Search Result: Movie Reviews");
            resultFrame.setBounds((width - windowsWidth) / 4,
                    (height - windowsHeight) / 4, windowsWidth, windowsHeight/2);
            resultFrame.setContentPane(scrollPane);
            resultFrame.setVisible(true);
        }

    }
