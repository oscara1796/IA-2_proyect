package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class NeuralNetwork{
    Layer [] layers;
    int [] layerSizes;


    public NeuralNetwork(int [] layerSizes){
        this.layerSizes = layerSizes;
        layers = new Layer[layerSizes.length-1];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(i,layerSizes[i], layerSizes[i+1]);
        }
    }

    public NeuralNetwork(){

    }


    void saveDataOfNetwork(Properties props ) throws IOException{
        
        props.setProperty("layerSizes", Arrays.toString(this.layerSizes));
        for (Layer layer : layers) {
            layer.save(props);
        }

        
    }

    void loadDataOfNetwork(Properties props) throws IOException{
        
        double[] doubleArray = Layer.parseDoubleArray(props.getProperty("layerSizes"));

        this.layerSizes = new int[doubleArray.length];

        for (int i = 0; i < doubleArray.length; i++) {
            this.layerSizes[i] = (int) doubleArray[i];
        }
        

        layers = new Layer[layerSizes.length-1];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = Layer.load(i,props);
        }
    }

    double [] calculateOutputs(double [] inputs){
        int layerNum = 1;
        for (Layer layer : layers) {
            inputs = layer.calculateOutputs(inputs);
            // System.out.println("layerNum " + layerNum + " " + Arrays.toString(inputs));
            layerNum++;
        }
        return inputs;
    }

    int classify(double [] inputs){
        double [] outputs = calculateOutputs(inputs);
        return MaxValueIndex(outputs);
    }

    public int MaxValueIndex(double[] values)
	{
		double maxValue = Double.MIN_VALUE;
		int index = 0;
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] > maxValue)
			{
				maxValue = values[i];
				index = i;
			}
		}

		return index;
	}

    double cost(EmailData point){
        double outputs [] = calculateOutputs( point.getBag());
        Layer outLayer = layers[layers.length-1];
        double cost = 0;

        for (int nodeOut = 0; nodeOut < outputs.length; nodeOut++) {
            cost += outLayer.nodeCost(outputs[nodeOut], point.getIsSpam());
        }

        return cost;
    }

    double cost(List<EmailData> points ){
        double totalCost = 0;
        for (EmailData point : points) {
            totalCost += cost(point);
        }

        return totalCost / points.size();
    }

    // public void learn(List<ChartPoint> points, double learningRate){
    //     final double h = 0.0001;
    //     double originalCost = cost(points);

    //     for (Layer layer : layers) {
    //         double deltaCost;
    //         for (int nodeIn = 0; nodeIn < layer.numNodesIn; nodeIn++) {
    //             for (int nodeOut = 0; nodeOut < layer.numNodesOut; nodeOut++) {
    //                  layer.weights[nodeIn][nodeOut] += h;
    //                  deltaCost = cost(points) -originalCost;
    //                  layer.weights[nodeIn][nodeOut] -= h;
    //                  layer.costGradientweights[nodeIn][nodeOut] = deltaCost /h;
    //              }
    //         }
    //         for (int biasIndex = 0; biasIndex < layer.biases.length; biasIndex++) {
    //             layer.biases[biasIndex] += h;
    //              deltaCost = cost(points) - originalCost;
    //             layer.biases[biasIndex] -= h;
    //             layer.costGradientbiases[biasIndex] = deltaCost/ h;

    //         }
    //     }

    //     applyAllGradients(learningRate);

    // }

    public void learn(List<EmailData> points, double learningRate){
        

        for (EmailData point : points) {
            // System.out.println("Point " +  Arrays.toString(point.getDoublePoints()));
            updateAllGradients(point);
        }

        applyAllGradients(learningRate / points.size());

    }

    private void applyAllGradients(double avergaeLearnRate) {
        for (Layer layer : layers) {
            layer.applyGradients(avergaeLearnRate);
        }
    }

    void updateAllGradients(EmailData point){


        double output [] =calculateOutputs(point.getBag());

        // for (int i = 0; i < point.getBag().length; i++) {
            
        //     System.out.println(point.getBag()[i]);
        // }
        System.out.println("output " + Arrays.toString(output) + " flag " + point.getIsSpam());

        Layer outLayer =  layers[layers.length -1];

        double [] nodeValues = outLayer.calculateOutputsLayerNodeValues(point.getIsSpam());
        // System.out.println("point.getReg_and() " + point.getReg_and() + " nodeValue " + Arrays.toString(nodeValues));
        outLayer.updateGradients(nodeValues);

        for (int hiddenLayerIndex = layers.length-2; hiddenLayerIndex >= 0; hiddenLayerIndex--) {
            Layer hiddLayer = layers[hiddenLayerIndex];

            nodeValues = hiddLayer.calculateHiddenLayerNodeValues(layers[hiddenLayerIndex+1], nodeValues);
            hiddLayer.updateGradients(nodeValues);
        }
    }


}