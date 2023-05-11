//The code is part of a package named practica_2
package com.example;

import javax.mail.MessagingException;
//Importing required classes from the packages javax.swing, java.awt, and java.util
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//Class Graph 
public class Graph {



    //Declaration of integer array Emails and ArrayList of Integer labels, and Neuron neuron
    

    //Declaration of List points as ArrayList of ChartPoint
    NeuralNetwork network ;
    SwingWorker<Void, Email> Paintworker;

    int trainEpochs= 650;
    List<Email> emails = new ArrayList<Email>();
    List<Email> EvaluateEmails = new ArrayList<Email>();

    

    //Constructor Graph()
    Graph(){

        

        
    }

    public void evaluateEmails(GUI gui){
        

        SwingWorker<Void, EmailData> trainerWorker = new SwingWorker<Void, EmailData>() {
            @Override
            protected Void doInBackground() throws Exception {
                // TODO Auto-generated method stub

                    System.out.println("Entramos");

                    List<EmailData> AllEmails = new ArrayList<EmailData>();

                    for (Email email : EvaluateEmails) {
                        email.calculateBagsForAll();
                        AllEmails.addAll(email.getEmails());
                    }

                   

                    for (EmailData data : AllEmails) {
                        int output =  roundUp(network.calculateOutputs(data.getBag())[0]);
                        if (output == 1) {
                            gui.legitCounter++;
                        } else{
                            gui.spamCounter++;
                        }

                        
                        System.out.println(" gui.legitCounter " + gui.legitCounter + " " + gui.spamCounter );
                    }

                    
                    

    
                return null;
            }
    
            // @Override
            // protected void process(List<EmailData> chunks){
            //     for (EmailData point : chunks) {
            //             if (point.getIsSpam() == 0) {
            //                 gui.legitCounter++;
            //                 gui.legit.setText("Legit: " + gui.legitCounter);
            //             } else {
            //                 gui.spamCounter++;
            //                 gui.spam.setText("Spam: " + gui.spamCounter);
            //             }
            //     }
            // }

            @Override
            protected void done(){
                // Paintworker.execute();
                gui.spam.setText("Spam: " + gui.spamCounter);
                gui.legit.setText("Legit: " + gui.legitCounter);
            }
            
        };
    
        trainerWorker.execute();
    }

    public void createVocabulary() throws MessagingException, IOException {
        List<EmailData> AllEmails = new ArrayList<EmailData>();

        for (Email email : emails) {
            AllEmails.addAll(email.getEmails());
        }

        Email.createVocabulary(AllEmails, 1);

        calculateBags();
    }

    public void saveData() throws FileNotFoundException, IOException{
        Properties props = new Properties();
        Email.saveData(props);
        network.saveDataOfNetwork(props);
        File file = new File("/home/oscar/Documents/projects/perceptron/proyecto2/myproject/src/main/resources/network.properties");
        file.createNewFile();
        props.store(new FileOutputStream(file), null);
    }

    public void loadData() throws IOException{
        Properties props = new Properties();
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("network.properties");
        props.load(input);
        Email.loadData(props);
        network= new NeuralNetwork();
        network.loadDataOfNetwork(props);
    }



    public void calculateBags() throws MessagingException, IOException{
        for (Email email : emails) {
            email.calculateBagsForAll();
        }
    }

    public static int roundUp(double value) {
        if (value >= 0.5) {
            return 1;
        } else {
            return 0;
        }
    }

    //Method to train the perceptron using points and labels
    public void trainPerceptron() {

        int layerSize []={Email.vocabulary.size(),5,5,1};
        network = new NeuralNetwork(layerSize);
       
    
        SwingWorker<Void, Email> trainerWorker = new SwingWorker<Void, Email>() {
            @Override
            protected Void doInBackground() throws Exception {
                // TODO Auto-generated method stub

    

                    List<EmailData> AllEmails = new ArrayList<EmailData>();

                    for (Email email : emails) {
                        AllEmails.addAll(email.getEmails());
                    }

                   

                    for (int i = 0; i < trainEpochs; i++) {
                        network.learn(AllEmails, 0.03);

                        double cost = network.cost(AllEmails);

                        System.out.println("COST " + cost);
                        if (cost < 0.02) {
                            break;
                        }
                        
                       
                        
                    }
                    // System.out.println("aux_y " +aux_y);
                    
                        // point.setColor(color);
                        // Thread.sleep(100);
                    

                // System.out.println("min_value " +min_value);
    
                return null;
            }
    
            @Override
            protected void done(){
                // Paintworker.execute();
            }
            
        };
    
        trainerWorker.execute();
       
    }

    
   


    
}
