package com.example;

import java.io.File;
import java.util.Arrays;


public class EmailData {
    private File file;
    private int isSpam;

    private double [] bag;

    public EmailData(File file,int  isSpam) {
         this.file = file;
         this.isSpam = isSpam;
    }


    public EmailData(File file) {
        this.file = file;
   }

    public File getFile() {
        return file;
    }

    public int getIsSpam() {
        return isSpam;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setIsSpam(int isSpam) {
        this.isSpam = isSpam;
    }

    public double[] getBag() {
        return bag;
    }

    public void setBag(double[] bag) {

        // for (int i = 0; i < bag.length; i++) {
        //     System.out.println(bag[i]);
        // }
        this.bag = bag;
    }
   
}
  

    

       

    
    
    


