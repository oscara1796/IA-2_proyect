package com.example;
import java.awt.*;

import javax.mail.*;
import javax.mail.internet.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

class Email{


    int isSpam = 0;
    List<EmailData> emails = new ArrayList<EmailData>();
    static double [] bagOfWords;


    static  Properties props = new Properties();
    static Session session;
    static Set<String> vocabulary;


    
    Email(List<File> emails , int isSpam){

        for (File file : emails) {
            EmailData emailData = new EmailData(file, isSpam);
            this.emails.add(emailData);
        }
        this.isSpam = isSpam;

    }

    Email(List<File> emails){

        for (File file : emails) {
            EmailData emailData = new EmailData(file);
            this.emails.add(emailData);
        }

    }


    public static void saveData(Properties propsSave){
        propsSave.setProperty("vocabulary", String.join(",", vocabulary));
        
    }

    public static void loadData(Properties propsSave){
        String[] vocabularyArray = propsSave.getProperty("vocabulary").split(",");
        vocabulary = new HashSet<>(Arrays.asList(vocabularyArray));
    }

    public void calculateBagsForAll() throws MessagingException, IOException{
        for (EmailData data : this.emails) {

            
           
            data.setBag(createBagOfWords(data.getFile()));
        }
    }

    static void loadProperties() throws FileNotFoundException, IOException{
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties");
        props.load(input);
    }

    public List<EmailData> getEmails() {
        return this.emails;
    }


    static double [] getBag(){
        return bagOfWords;
    }

    public void setIsSpam(int label){
        this.isSpam= label;
    }

    public int getIsSpam(){
        return this.isSpam;
    }

    public static String convert_to_string(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }


    static String readEmail(File emailFile) throws MessagingException, IOException{
        Message message = new MimeMessage(session, new FileInputStream(emailFile));

        // Print message details
        // System.out.println("Subject: " + message.getSubject());
        // System.out.println("Content: " + message.getContent());
        // String content = message.getContent().toString(); // Get the content
        // message.setContent(content, "text/plain; charset=utf-8"); 

        String content = "";
        if (message.isMimeType("text/plain")) {
            try {
                content = (String) message.getContent();
            } catch (UnsupportedEncodingException uex) {
                InputStream is = message.getInputStream();
                /*
                 * Read the input stream into a byte array.
                 * Choose a charset in some heuristic manner, use
                 * that charset in the java.lang.String constructor
                 * to convert the byte array into a String.
                 */
                 content = convert_to_string(is);
                //  System.out.println(content);
            } catch (Exception ex) {
                // Handle other exceptions appropriately
            }
        }
        
        
        return message.getSubject() + " " + content;
    }

    // Helper method to read a text file into a string
    private static String readFileToString(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
    }
    
    // Helper method to split a string into words
    private static String[] splitWords(String text) {
        return text.toLowerCase().split("\\W*[\\d_]+\\W*|\\W+");
    }
    
    // Method to create a vocabulary from a collection of documents
    public static void createVocabulary(List<EmailData> documents, int minFrequency) throws MessagingException, IOException {
        Map<String, Integer> wordCountMap = new HashMap<String, Integer>();
        for (EmailData  document : documents) {
                String[] words = splitWords(readEmail(document.getFile()));
                for (String word : words) {
                    if (wordCountMap.containsKey(word)) {
                        int count = wordCountMap.get(word) + 1;
                        wordCountMap.put(word, count);
                    } else {
                        wordCountMap.put(word, 1);
                    }
                }
           
        }
        vocabulary = wordCountMap.keySet();
        vocabulary.removeIf(word -> wordCountMap.get(word) < minFrequency);
        int i = 1;
        // for (String s : vocabulary) {
        //     System.out.println(i+" " +s);
        //     i++;
        // }
    }

    // Helper method to create the bag of words for a document
    static double []  createBagOfWords(File document) throws MessagingException, IOException {
        bagOfWords = new double[vocabulary.size()];
        String[] words = splitWords(readEmail(document));
        for (String word : words) {
            if (vocabulary.contains(word)) {
                int index = getWordIndex(word, vocabulary);
                bagOfWords[index]++;
            }
        }

        return bagOfWords;
    }

    // Helper method to get the index of a word in the dictionary
    private static int getWordIndex(String word, Set<String> dictionary) {
        int index = 0;
        for (String dictWord : dictionary) {
            if (dictWord.equals(word)) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    // // Main method to test the vocabulary builder
    // public static void main(String[] args) throws FileNotFoundException {
    //     String[] documents = {readFileToString("doc1.txt"), readFileToString("doc2.txt"), readFileToString("doc3.txt")};
    //     Set<String> vocabulary = createVocabulary(documents, 2);
    //     System.out.println(vocabulary);
    // }



    
 }
