package com.example;


import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;



public class GUI {
    JFrame frame;
    JButton startButton;
    JButton clearButton;
    JButton saveButton;
    JButton evaluateButton;
    JButton loadButton;
    JLabel emails;
    JLabel legit;
    int legitCounter = 0;
    JLabel spam;
    int spamCounter = 0;
    Graph g = new Graph();

    GUI(){
        frame = new JFrame("Email SPAM detecter");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,500);
        frame.setLocationRelativeTo(null);
        
        clearButton = new JButton("clear data");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)            {
                spamCounter = 0;
                legitCounter = 0;
                legit.setText("Legit: " + legitCounter); 
                spam.setText("Spam: " + spamCounter); 
                g.EvaluateEmails = new ArrayList<Email>();
            }
        }); 
        clearButton.setBounds(100,400,130,30);

        // Creating start button
        startButton = new JButton("train");
        startButton.setBounds(265,400,100,30);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)            {
                //Here goes the action (method) you want to execute when clicked
                // System.out.println("start button ");
                // g.trainPerceptron();
                // g.repaint();
                int addMoreEmails = 0;
                do {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("/home/oscar/Documents/projects/perceptron/proyecto2/myproject/src/main/resources"));
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File directory = fileChooser.getSelectedFile();
                        List<File> files = searchDirectory(directory);
                        int label = JOptionPane.showConfirmDialog(null, "Are these emails spam(yes) or legit(no)?", "Answered", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                        System.out.println(label);
                        Email email = new Email(files, label);
                        g.emails.add(email);
                        // Display search results in a separate panel or table
                    }

                    addMoreEmails = JOptionPane.showConfirmDialog(null, "Would you like to add more emails?", "Answered", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    System.out.println("add more emails "+addMoreEmails);
                } while (addMoreEmails == 0);
                
                try {
                    Email.loadProperties();
                    g.createVocabulary();
                    g.trainPerceptron();
                } catch (MessagingException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        }); 

        saveButton = new JButton("save training");
        saveButton.setBounds(400,400,200,30);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)            {
                //Here goes the action (method) you want to execute when clicked
                // System.out.println("start button ");
                // g.trainPerceptron();
                // g.repaint();
                
                try {
                    g.saveData();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        }); 
        

        evaluateButton = new JButton("Evaluate");
        evaluateButton.setBounds(265,300,200,30);
        evaluateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)            {
                //Here goes the action (method) you want to execute when clicked
                // System.out.println("start button ");
                // g.trainPerceptron();
                // g.repaint();
                int addMoreEmails = 0;
                do {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("/home/oscar/Documents/projects/perceptron/proyecto2/myproject/src/main/resources"));
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File directory = fileChooser.getSelectedFile();
                        List<File> files = searchDirectory(directory);
                        Email email = new Email(files);
                        g.EvaluateEmails.add(email);
                        // Display search results in a separate panel or table
                    }

                    addMoreEmails = JOptionPane.showConfirmDialog(null, "Would you like to add more emails?", "Answered", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                } while (addMoreEmails == 0);
                
                changeNumbers();
                
                
            }
        }); 

        loadButton = new JButton("Load data");
        loadButton.setBounds(285,350,150,30);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)            {
                //Here goes the action (method) you want to execute when clicked
                // System.out.println("start button ");
                // g.trainPerceptron();
                // g.repaint();
                
                try {
                    g.loadData();
                    System.out.println("Data loaded");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        }); 



        
        emails = new JLabel("Emails:");     
        emails.setBounds(300,100,100,50);
        emails.setFont(new Font("Serif", Font.PLAIN, 20));
        
        legit = new JLabel("legit: " + legitCounter);     
        legit.setBounds(200,200,250,50);
        legit.setFont(new Font("Serif", Font.PLAIN, 20));
        
        spam = new JLabel("spam: " +spamCounter);     
        spam.setBounds(450,200,250,50);
        spam.setFont(new Font("Serif", Font.PLAIN, 20));


        //Input boxes 
       
        // g.setBounds(50,50,400,400);
        
       
        
        // Adding elements to the GUI
        
        frame.add(startButton); // Adds Button to content pane of frame
        frame.add(clearButton); // Adds Button to content pane of frame
        frame.add(saveButton); // Adds Button to content pane of frame
        frame.add(evaluateButton); // Adds Button to content pane of frame
        frame.add(loadButton); // Adds Button to content pane of frame
        frame.add(emails); // Adds Button to content pane of frame
        frame.add(legit); // Adds Button to content pane of frame
        frame.add(spam); // Adds Button to content pane of frame
        frame.setLayout(null);
        // frame.pack();
        frame.setVisible(true);
    }


    public List<File> searchDirectory(File directory) {
        List<File> results = new ArrayList<File>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    results.addAll(searchDirectory(file));
                } else {
                    // Perform search on file
                    results.add(file);
                }
            }
        }
        return results;
    }


    public void changeNumbers(){
        g.evaluateEmails(this);
    }



    public static void main(String args[]){
        GUI gui = new GUI();
    }
}
