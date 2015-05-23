/**
 * Created by Tomasz on 22-05-2015.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BeesGUI {

    public static void run() throws IOException {

        final double[] neighborhoodSize = new double[1];
        final int[] beesSentToBetterPlaces = new int[1];
        final int[] beesSentToOtherPlaces = new int[1];
        final int[] betterPlaces = new int[1];
        final int[] chosenPlaces = new int[1];
        final int[] iterations = new int[1];
        final int[] scoutBees = new int[1];
        final double[] averageResult = new double[1];
        final double[] bestResult = new double[1];
        final double[] heuristics = new double[1];
        final double[] worstResult = new double[1];
        final double[] time = new double[1];

        // creating window
        JFrame frame = new JFrame("Bees Algorithm");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);                      // showing window in the middle of the screen

        // creating panels in layout
        JPanel gui = new JPanel(new BorderLayout());
        JPanel main = new JPanel(new BorderLayout(10, 120));
        main.setBorder(new EmptyBorder(50, 10, 120, 120));
        frame.setContentPane(gui);
        JPanel panel1 = new JPanel(new GridLayout(0, 1));
        JPanel panel2 = new JPanel(new GridLayout(0, 1));
        JPanel panel3 = new JPanel(new BorderLayout(0, 30));
        JPanel panel4 = new JPanel(new GridLayout(0, 2, 10, 30));
        main.add(panel1, BorderLayout.WEST);
        main.add(panel2, BorderLayout.CENTER);
        main.add(panel3, BorderLayout.SOUTH);
        panel3.add(panel4, BorderLayout.WEST);

        // fields for file and FileChooser
        JLabel path = new JLabel();                             // field for file name
        final String[] s = new String[1];

        // Choosing a file
        JLabel fileLabel = new JLabel("Choose a file");
        JLabel selectedPath = new JLabel("Selected path");
        JButton chooseFile = new JButton("Open");
        chooseFile.addActionListener(e -> {
            JFileChooser openFile = new JFileChooser();
            openFile.showOpenDialog(null);
            path.setText(openFile.getSelectedFile().getName());
            s[0] = openFile.getSelectedFile().getAbsolutePath();
        });

        // layout for future charts
        gui.add(main, BorderLayout.WEST);
        JPanel chartPanel = new JPanel(new BorderLayout());
        gui.add(chartPanel, BorderLayout.EAST);

        // loading bee's image
        BufferedImage myImage = ImageIO.read(new File("C:\\Users\\Janusz\\IdeaProjects\\BeesTSP\\TSPLIB\\Bee.png"));
        JLabel pictureLabel = new JLabel(new ImageIcon(myImage));
        main.add(pictureLabel, BorderLayout.NORTH);

        // Creating arguments' labels
        Label label1 = new Label("Neighbourhood size");
        JTextField text1 = new JTextField(2);
        Label label2 = new Label("Bees sent to better places");
        JTextField text2 = new JTextField(2);
        Label label3 = new Label("Bees sent to other places");
        JTextField text3 = new JTextField(2);
        Label label4 = new Label("Better places");
        JTextField text4 = new JTextField(2);
        Label label5 = new Label("Chosen places");
        JTextField text5 = new JTextField(2);
        Label label6 = new Label("Iterations");
        JTextField text6 = new JTextField(2);
        Label label7 = new Label("Scout bees");
        JTextField text7 = new JTextField(2);
        JLabel chart = new JLabel("CHART");

        // adding labels to panels
        chartPanel.add(chart, BorderLayout.NORTH);
        panel1.add(fileLabel);
        panel1.add(selectedPath);
        panel2.add(chooseFile);
        panel2.add(path);
        panel1.add(Box.createVerticalStrut(50));        // space between previous and next component in 1st panel
        panel2.add(Box.createVerticalStrut(50));        // space between previous and next component in 2nd panel
        panel1.add(label1);
        panel2.add(text1);
        panel1.add(label2);
        panel2.add(text2);
        panel1.add(label3);
        panel2.add(text3);
        panel1.add(label4);
        panel2.add(text4);
        panel1.add(label5);
        panel2.add(text5);
        panel1.add(label6);
        panel2.add(text6);
        panel1.add(label7);
        panel2.add(text7);

        // Creating START button
        JButton startingButton = new JButton("START");
        panel3.add(startingButton, BorderLayout.NORTH);

        // Creating fields for showing counted results and time
        JLabel result1 = new JLabel("NN heuristics result");
        panel4.add(result1, BorderLayout.SOUTH);
        JLabel textResult1 = new JLabel();
        panel4.add(textResult1, BorderLayout.SOUTH);

        JLabel result2 = new JLabel("Bees algo worst result");
        panel4.add(result2, BorderLayout.SOUTH);
        JLabel textResult2 = new JLabel();
        panel4.add(textResult2, BorderLayout.SOUTH);

        JLabel result3 = new JLabel("Bees algo average result");
        panel4.add(result3, BorderLayout.SOUTH);
        JLabel textResult3 = new JLabel();
        panel4.add(textResult3, BorderLayout.SOUTH);

        JLabel result4 = new JLabel("Bees algo best result");
        panel4.add(result4, BorderLayout.SOUTH);
        JLabel textResult4 = new JLabel();
        panel4.add(textResult4, BorderLayout.SOUTH);

        JLabel result5 = new JLabel("Time");
        panel4.add(result5, BorderLayout.SOUTH);
        JLabel textResult5 = new JLabel();
        panel4.add(textResult5, BorderLayout.SOUTH);

        // showing frame with panels
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);      // opening in full screen mode
        frame.setVisible(true);

        // reaction for click START button
        startingButton.addActionListener(e -> {
            if (e.getSource() == startingButton) {          // if START button was clicked
                // loading parameters
                neighborhoodSize[0] = Double.parseDouble(text1.getText());
                beesSentToBetterPlaces[0] = Integer.parseInt(text2.getText());
                beesSentToOtherPlaces[0] = Integer.parseInt(text3.getText());
                betterPlaces[0] = Integer.parseInt(text4.getText());
                chosenPlaces[0] = Integer.parseInt(text5.getText());
                iterations[0] = Integer.parseInt(text6.getText());
                scoutBees[0] = Integer.parseInt(text7.getText());
                BeesAlgo beesAlgo = new BeesAlgo(neighborhoodSize[0], beesSentToBetterPlaces[0], beesSentToOtherPlaces[0],
                        betterPlaces[0], chosenPlaces[0], iterations[0], scoutBees[0], s[0]);
                long start = System.currentTimeMillis();
                try {
                    beesAlgo.startAlgo();                   // starting algorithm
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                beesAlgo.setDistances();
                heuristics[0] = beesAlgo.generateFirstSolution();
                beesAlgo.fullRandom();
                beesAlgo.run();
                averageResult[0] = beesAlgo.getAverageResult();
                bestResult[0] = beesAlgo.getBestResult();
                worstResult[0] = beesAlgo.getWorstResult();
                long end = System.currentTimeMillis();
                time[0] = (double) (end - start) / 1000;
                System.out.println("Time: " + (double) (end - start) / 1000);

                // showing results under START button
                textResult1.setText(Double.toString(heuristics[0]));
                textResult2.setText(Double.toString(worstResult[0]));
                textResult3.setText(Double.toString(averageResult[0]));
                textResult4.setText(Double.toString(bestResult[0]));
                textResult5.setText(Double.toString(time[0]) + " seconds");
            }
        });
    }
}