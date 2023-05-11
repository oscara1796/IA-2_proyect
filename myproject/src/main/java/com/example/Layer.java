package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Layer {

    int id;
    int numNodesIn, numNodesOut;
    double [][] weights;
    double [] biases;
    double [][] costGradientweights;
    double [] costGradientbiases;
    double [] activations;
    double[] weightedInputs;
    double[] inputs;


    public Layer(int id, int numNodesIn, int numNodesOut){
        this.id= id;
        this.numNodesIn = numNodesIn;
        this.numNodesOut = numNodesOut;

        weights = new double[numNodesIn][numNodesOut];
        biases = new double[numNodesOut];
        costGradientweights = new double[numNodesIn][numNodesOut];
        costGradientbiases = new double[numNodesOut];
        weightedInputs = new double[numNodesOut];
        activations = new double[numNodesOut];
        InitializeRandomWeights();
    }

    public void setId(int id) {
        this.id = id;
    }

    public double [] calculateOutputs(double inputs[]){
        this.inputs = inputs;

        for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
             double weightedInput = biases[nodeOut];
             for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++) {
                    weightedInput += inputs[nodeIn] * weights[nodeIn][nodeOut];
             }
             weightedInputs[nodeOut] = weightedInput;
             activations[nodeOut] = activationFunction(weightedInput);
        }
        return activations;
    }

    double activationFunction(double weightedInput){
        return 1 / (1 + Math.exp(-weightedInput));
    }

    double activationDerivative(double weightedInput){
        double activation = activationFunction(weightedInput);
        return activation * (1-activation);
    }

    // double activationFunction(double weightedInput){
    //     if (weightedInput < 0) {
    //         return 0;
    //     } else {
    //         return 1;
    //     }
    // }

    // double activationDerivative(double weightedInput){
    //     return 0;
    // }

    double nodeCost(double outputActivation, double expectedOutput){
        double error = outputActivation - expectedOutput;
        return error*error;
    }

    double nodeCostDerivative(double ouputActivation, double expectedOutput){

        return 2*(ouputActivation - expectedOutput);
    }

    public void applyGradients(double learningRate){
        for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
            biases[nodeOut] -= costGradientbiases[nodeOut] * learningRate;
            for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++) {
                 weights[nodeIn][nodeOut] -= costGradientweights[nodeIn][nodeOut] * learningRate;
            }
        }
    }

    public void InitializeRandomWeights()
	{
        for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++) {
            for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
                double randomValue = Math.random() * 2 - 1;
                 weights[nodeIn][nodeOut] = randomValue / Math.sqrt(numNodesIn) ;
             }
             
        }

        for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
            biases[nodeOut] = Math.random() * 0.9 + 0.1;
         }
	}

    public double[] calculateOutputsLayerNodeValues(int reg_and) {
        double [] nodeValues = new double[1];
        for (int i = 0; i < nodeValues.length; i++) {
            
            double nodeCostDerivative = nodeCostDerivative(activations[i], reg_and);
            double activationDerivative = activationDerivative(weightedInputs[i]);
            nodeValues[i] = activationDerivative* nodeCostDerivative;
        }
        return  nodeValues;
    }

    public double [] calculateHiddenLayerNodeValues(Layer oldLayer, double [] oldNodeValues){
         double [] newNodeValues = new double[numNodesOut];

         for (int newNodeIndex = 0; newNodeIndex < newNodeValues.length; newNodeIndex++) {

            double newNodeValue = 0;
            for (int oldNodeIndex = 0; oldNodeIndex < oldNodeValues.length; oldNodeIndex++) {
                double weightedInputDerivative = oldLayer.weights[newNodeIndex][oldNodeIndex];
                newNodeValue += weightedInputDerivative * oldNodeValues[oldNodeIndex];
            }
            newNodeValue *= activationDerivative(weightedInputs[newNodeIndex]);
            newNodeValues[newNodeIndex] = newNodeValue;
            
         }
         return newNodeValues;
    }

    public void updateGradients(double [] nodeValues){

        for (int nodeOut = 0; nodeOut < numNodesOut; nodeOut++) {
            for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++) {
                double derivativeCostWerWeight = inputs[nodeIn] * nodeValues[nodeOut];


                costGradientweights[nodeIn][nodeOut] += derivativeCostWerWeight;
            }

            double derivativeCostWrtBias = 1 * nodeValues[nodeOut];
            costGradientbiases[nodeOut] += derivativeCostWrtBias;
       }
    }

    public void save(Properties props) throws IOException {
        
        props.setProperty(id+"_numNodesIn", String.valueOf(numNodesIn));
        props.setProperty(id+"_numNodesOut", String.valueOf(numNodesOut));
        props.setProperty(id+"_weights", Arrays.deepToString(weights));
        props.setProperty(id+"_biases", Arrays.toString(biases));
        props.setProperty(id+"_costGradientweights", Arrays.deepToString(costGradientweights));
        props.setProperty(id+"_costGradientbiases", Arrays.toString(costGradientbiases));
        props.setProperty(id+"_activations", Arrays.toString(activations));
        props.setProperty(id+"_weightedInputs", Arrays.toString(weightedInputs));
        props.setProperty(id+"_inputs", Arrays.toString(inputs));
       
    }

    public static Layer load(int id, Properties props) throws IOException {
        

        int numNodesIn = Integer.parseInt(props.getProperty(id+"_numNodesIn"));
        int numNodesOut = Integer.parseInt(props.getProperty(id+"_numNodesOut"));
        Layer layer = new Layer(id, numNodesIn, numNodesOut);

        layer.weights = parseDoubleDoubleArray(props.getProperty(id+"_weights"));
        layer.biases = parseDoubleArray(props.getProperty(id+"_biases"));
        layer.costGradientweights = parseDoubleDoubleArray(props.getProperty(id+"_costGradientweights"));
        layer.costGradientbiases = parseDoubleArray(props.getProperty(id+"_costGradientbiases"));
        layer.activations = parseDoubleArray(props.getProperty(id+"_activations"));
        layer.weightedInputs = parseDoubleArray(props.getProperty(id+"_weightedInputs"));
        layer.inputs = parseDoubleArray(props.getProperty(id+"_inputs"));

        return layer;
    }

    public  static double[][] parseDoubleDoubleArray(String str) {
        // Remove outer brackets

        // Split the string into rows
        String[] rows = str.replaceAll("\\[\\[", "[").replaceAll("\\],", "]").split("\\] \\[");
        int numRows = rows.length;

        // Split each row into its elements
        int numCols = rows[0].split(",").length;
        double[][] matrix = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] elements = rows[i].replaceAll("[\\[\\]]", "").split(",");
            // System.out.println(  " ROW " + Arrays.toString(elements));
            for (int j = 0; j < numCols; j++) {
                matrix[i][j] = Double.parseDouble(elements[j].trim());
            }
        }
        return matrix;
    }

    public  static double[] parseDoubleArray(String str) {
        return Arrays.stream(str.replaceAll("[\\[\\]]", "").split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

}