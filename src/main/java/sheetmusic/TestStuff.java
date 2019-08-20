/*
 *   _____      _ _                                 _        ___   ___  __  ___
 *  / ____|    | | |                               | |      |__ \ / _ \/_ |/ _ \
 * | |     __ _| | |_   _ _ __ ___     ___ ___   __| | ___     ) | | | || | (_) |
 * | |    / _` | | | | | | '_ ` _ \   / __/ _ \ / _` |/ _ \   / /| | | || |> _ <
 * | |___| (_| | | | |_| | | | | | | | (_| (_) | (_| |  __/  / /_| |_| || | (_) |
 *  \_____\__,_|_|_|\__,_|_| |_| |_|  \___\___/ \__,_|\___| |____|\___/ |_|\___/
 *
 */
package sheetmusic;


import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;

public class TestStuff {

    static int nChannels = 1;
    static int outputNumAccidental = 3;
    static int nEpochs = 40;
    static int iterations = 1;
    static int seed = 123;

    public TestStuff() {
        double[] img = {
            0,1,0,
            1,1,1,
            0,1,0};
        int[] size = {3,3};
        INDArray newArr = Nd4j.create(img,size,'c');
        //System.out.println(newArr.toString());

    }

    public static void main(String[] args) {
        //new TestStuff();
        ArrayList<INDArray> accidentalsTrainArr = new ArrayList<>();
        ExtractFiles ex = new ExtractFiles();
        ex.extract();
        for (VectorImage img:ex.accidentals){
            img.scale(36,108);
            accidentalsTrainArr.add(Nd4j.create(img.dataToDoubles(),new int[]{img.width,img.height},'c'));
        }

        for (VectorImage img:ex.timeSigs){
            img.scale(25,56);
        }

        for (VectorImage img:ex.clefs){
            img.scale(36,108);
        }

        for (VectorImage img:ex.rests){
            img.scale(20,48);
        }

        for (VectorImage img:ex.notes){
            img.scale(26,18);
            img.fScale(37,120);
        }

//        for (INDArray ind:accidentalsTrainArr){
//            System.out.println(ind.toString()+"\n");
//        }

        ComputationGraph accidentalsModel = configModel(36,108);

        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        accidentalsModel.setListeners(new ScoreIterationListener(10), new StatsListener( statsStorage));

        int batchSize;

        ArrayList<VectorImage> accidentalsTrain = new ArrayList<>();
        ArrayList<VectorImage> accidentalsTest = new ArrayList<>();

        for (VectorImage img:ex.accidentals){
            if (Math.random()<0.25){
                accidentalsTest.add(img);
            } else {
                accidentalsTrain.add(img);
            }
        }


        System.out.println("Training data size: "+accidentalsTrain.size());
        System.out.println("Testing data size: "+accidentalsTest.size());

        SymbolDataSetIterator aTrainMulIterator = new SymbolDataSetIterator(1, accidentalsTrain,"Accidental");
        SymbolDataSetIterator aTestMulIterator = new SymbolDataSetIterator(1, accidentalsTest,"Accidental");
        //SymbolDataSetIterator aValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.accidentals,"Accidental");


//        int look[][] = aTrainMulIterator.next().getFeatures()[0].toIntMatrix();
//        for (int[] row:look){
//            for(int i:row){
//                System.out.print(i);
//            }
//            System.out.println();
//        }
//        System.out.println("*************************************************************************************************");
//        int ij = 0;
//        aTrainMulIterator.reset();
//        System.out.println(aTrainMulIterator.batchNum+"<"+aTrainMulIterator.numExample);
//        System.out.println(aTrainMulIterator.hasNext());
//        while(aTrainMulIterator.hasNext()) {
//            ij++;
//            for (int j = 0;j<aTrainMulIterator.next().getFeatures()[0].getRow(0).length();j++){
                aTrainMulIterator.next();
//                System.out.println(t+"\t");
//            }
//            System.out.println("\n");
//        }
//        System.out.println("test 1:\n"+aTestMulIterator.next().toString());
//        System.out.println("test 2:\n"+aTestMulIterator.next().toString());
//        System.out.println("*************************************************************************************************");

        batchSize = 10;

        SymbolDataSetIterator cTrainMulIterator = new SymbolDataSetIterator(batchSize, ex.clefs,"Clef");
        SymbolDataSetIterator cTestMulIterator = new SymbolDataSetIterator(batchSize, ex.clefs,"Clef");
        SymbolDataSetIterator cValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.clefs,"Clef");

        //System.out.println(cTrainMulIterator.next().toString());

        batchSize = 4;

        SymbolDataSetIterator tsTrainMulIterator = new SymbolDataSetIterator(batchSize, ex.timeSigs,"TimeSig");
        SymbolDataSetIterator tsTestMulIterator = new SymbolDataSetIterator(batchSize, ex.timeSigs,"TimeSig");
        SymbolDataSetIterator tsValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.timeSigs,"TimeSig");



        batchSize = 5;

        SymbolDataSetIterator rTrainMulIterator = new SymbolDataSetIterator(batchSize, ex.rests,"Rest");
        SymbolDataSetIterator rTestMulIterator = new SymbolDataSetIterator(batchSize, ex.rests,"Rest");
        SymbolDataSetIterator rValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.rests,"Rest");

        //System.out.println(rTrainMulIterator.next().toString());

        batchSize = 30;

        SymbolDataSetIterator nTrainMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"Note");
        SymbolDataSetIterator nTestMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"Note");
        SymbolDataSetIterator nValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"Note");

        //System.out.println(nTrainMulIterator.next().toString());

        SymbolDataSetIterator nfTrainMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"NoteFull");
        SymbolDataSetIterator nfTestMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"NoteFull");
        SymbolDataSetIterator nfValidateMulIterator = new SymbolDataSetIterator(batchSize, ex.notes,"NoteFull");

        //System.out.println(nfTrainMulIterator.next().toString());


        for ( int i = 0; i < nEpochs; i ++ ) {
            System.out.println("Epoch=====================" + i);
            accidentalsModel.fit(aTrainMulIterator);
        }

        modelPredict(accidentalsModel,aTestMulIterator);

    }

    public static ComputationGraph configModel(int w, int h){

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
            .seed(seed)
            //.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .l2(1e-3)
            .updater(new Adam(1e-4))
            .weightInit( WeightInit.XAVIER_UNIFORM)
            .graphBuilder()
            .addInputs("trainFeatures")
            .setOutputs("6")
//            .addLayer("cnn1",  new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
//                .nIn(1).nOut(48).activation( Activation.RELU).build(), "trainFeatures")
//            .addLayer("maxpool1",  new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,2}, new int[]{2, 2}, new int[]{0, 0})
//                .build(), "cnn1")
//            .addLayer("cnn2",  new ConvolutionLayer.Builder(new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0})
//                .nOut(64).activation( Activation.RELU).build(), "maxpool1")
//            .addLayer("maxpool2",  new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,1}, new int[]{2, 1}, new int[]{0, 0})
//                .build(), "cnn2")
//            .addLayer("cnn3",  new ConvolutionLayer.Builder(new int[]{3, 3}, new int[]{1, 1}, new int[]{0, 0})
//                .nOut(128).activation( Activation.RELU).build(), "maxpool2")
//            .addLayer("maxpool3",  new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,2}, new int[]{2, 2}, new int[]{0, 0})
//                .build(), "cnn3")
//            .addLayer("ffn0",  new DenseLayer.Builder().nOut(1536)
//                .build(), "maxpool3")
//            .addLayer("ffn1",  new DenseLayer.Builder().nOut(1536)
//                .build(), "ffn0")
//            .addLayer("out1", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
//                .nOut(outputNumAccidental).activation(Activation.SOFTMAX).build(), "ffn1")
            .layer(0, new ConvolutionLayer.Builder(5, 5)
                .nIn(1)
                .stride(1, 1)
                .nOut(20)
                .activation(Activation.IDENTITY)
                .build(), "trainFeatures")
            .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .build(),"0")
            .layer(2, new ConvolutionLayer.Builder(5, 5)
                .nIn(20)
                .stride(1, 1)
                .nOut(50)
                .activation(Activation.IDENTITY)
                .build(),"1")
            .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .build(),"2")
            .layer(4, new DenseLayer.Builder().activation(Activation.RELU)
                .nIn(800)
                .nOut(128)
                .build(),"3")
            .layer(5, new DenseLayer.Builder().activation(Activation.RELU)
                .nIn(128)
                .nOut(64)
                .build(),"4")
            .layer(6, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(outputNumAccidental)
                .activation(Activation.SOFTMAX)
                .build(),"5")
            .setInputTypes(InputType.convolutionalFlat(h, w,1))
            .backprop(true).pretrain(false).build();
            //.build();

        // Construct and initialize model
        ComputationGraph model = new ComputationGraph(config);
        model.init();

        return model;
    }

    public static void modelPredict(ComputationGraph model, SymbolDataSetIterator iterator) {
        int sumCount = 0;
        int correctCount = 0;
        int iteration = 0;

        while(iterator.hasNext()){
            System.out.println("iteration: "+iteration);
            iteration++;
            MultiDataSet mds = iterator.next();
            INDArray[]  output = model.output(mds.getFeatures());
            INDArray[] labels = mds.getLabels();
            for (int i = 0; i < output.length; i++){
                INDArray predictedOut = output[0].getRow(i);
                int predictedOutValue = Nd4j.argMax(predictedOut, 1).getInt(0);
                INDArray realOut = labels[0].getRow(i);
                int realOutValue = Nd4j.argMax(realOut, 1).getInt(0);
                System.out.println("Predicted: "+predictedOutValue+", Actually: "+ realOutValue);
                if (predictedOutValue==realOutValue){
                    correctCount++;
                }
                sumCount++;
            }
        }
        if (sumCount>0) {
            System.out.println("\nPercent Correct: "+(correctCount/sumCount));
        }
    }

}
