package sheetmusic;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class MusicNet {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(MusicNet.class);

    static MusicDataSetLoader accidentalData;
    static MultiLayerNetwork accidentalNet;
    static MusicDataSetLoader restData;
    static MultiLayerNetwork restNet;
    static MusicDataSetLoader noteData;
    static MultiLayerNetwork noteNet;
    static MusicDataSetLoader noteFullData;
    static MultiLayerNetwork noteFullNet;
    static MusicDataSetLoader clefData;
    static MultiLayerNetwork clefNet;
    static MusicDataSetLoader quarterData;
    static MultiLayerNetwork quarterNet;
    static MusicDataSetLoader timeSigData;
    static MultiLayerNetwork timeSigNet;

    public MusicNet() {
    }

    public static int layer2In(String type){
        switch (type){
            case "Accidental":
                return 16640;
            case "Rest":
                return 3520;
            case "NoteFull":
                return 18560;
            case "Quarter":
                return 18560;
            case "Clef":
                return 16640;
            case "Note":
                return 2048;
            default:
                return 4096;
        }
    }

    public static MultiLayerNetwork configure(MusicDataSetLoader data){

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(data.seed)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs(0.006, 0.9))
            .l2(1e-4)
            .list()
            .layer(0, new DenseLayer.Builder()
                .nIn(data.height * data.width)
                .nOut(100)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nIn(100)
                .nOut(data.outputNum)
                .activation(Activation.SOFTMAX)
                .weightInit(WeightInit.XAVIER)
                .build())
//            .updater(new Nesterovs(.0001, 0.9))
//            .l2(1e-4)
//            .list()
//            .layer(0, new ConvolutionLayer.Builder(5, 5)
//                .nIn(data.channels)
//                .stride(1, 1)
//                .nOut(20)
//                .activation(Activation.IDENTITY)
//                .build())
//            .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
//                .kernelSize(2, 2)
//                .stride(2, 2)
//                .build())
//            .layer(2, new DenseLayer.Builder().activation(Activation.RELU)
//                .nIn(layer2In(data.type))
//                .nOut(1024).build())
//            .layer(3, new DenseLayer.Builder().activation(Activation.RELU)
//                .nIn(1024)
//                .nOut(256).build())
//            .layer(4, new DenseLayer.Builder().activation(Activation.RELU)
//                .nIn(256)
//                .nOut(64).build())
//            .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
//                .nIn(64)
//                .nOut(data.outputNum)
//                .activation(Activation.SOFTMAX)
//                .build())
            .setInputType(InputType.convolutional(data.height, data.width, data.channels))
            .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(10));
        return net;
    }

    public static void load() throws Exception{
        accidentalData = new MusicDataSetLoader("Accidental", 10);
        restData = new MusicDataSetLoader("Rest", 10);
        noteData = new MusicDataSetLoader("Note", 10);
        noteFullData = new MusicDataSetLoader("NoteFull", 10);
        clefData = new MusicDataSetLoader("Clef", 10);
        quarterData = new MusicDataSetLoader("Quarter", 10);
        accidentalNet = accidentalData.load(6);
        restNet = restData.load(4);
        clefNet = clefData.load(2);
        noteNet = noteData.load(1);
        quarterNet = quarterData.load(4);
    }

    public static String pass(Image data, String type) throws Exception {
        MusicDataSetLoader dataSet = dataSetByType(type);
        MultiLayerNetwork model = networkByType(type);

        NativeImageLoader loader = new NativeImageLoader(dataSet.height, dataSet.width, dataSet.channels);
        INDArray img = loader.asMatrix(data);
        DataNormalization scalar = new ImagePreProcessingScaler(0,1);
        scalar.transform(img);

        INDArray output = model.output(img);
        return classification(output, type);
    }

    public static String classification(INDArray data, String type){
        switch (type){
            case "Accidental":
                int aIndex = getAccidentalIndex(data);
                return new String[]{"flat", "natural", "other", "sharp"}[aIndex];
            case "Rest":
                int rIndex = getRestIndex(data);
                return new String[]{"1/16", "1/8", "3/16", "1", "1 1/2","other"}[rIndex];
            case "Clef":
                int cIndex = getClefIndex(data);
                return new String[]{"bass", "trebble", "other"}[cIndex];
            case "Note":
                int nIndex = getNoteIndex(data);
                return new String[]{"half", "other","quarter","whole"}[nIndex];
            case "Quarter":
                int qIndex = getQuarterNoteIndex(data);
                return new String[]{"sixteenth", "sixteenth", "eighth","eighth","dotted eighth","quarter","other"}[qIndex];
            default:
                return null;
        }
    }

    public static int getAccidentalIndex(INDArray data){
        DataBuffer dataBuffer = data.data();
        double[] array = dataBuffer.asDouble();
        if (array[0]>.75){
            return 0;
        } else if (array[3]>.80){
            return 3;
        } else if (array[1]>array[2] && array[1]>.55){
            return 1;
        } else {
            return 2;
        }
    }

    public static int getNoteIndex(INDArray data){
        DataBuffer dataBuffer = data.data();
        double[] array = dataBuffer.asDouble();
        if (array[0]>.6){
            return 0;
        } else if (array[3]>.55){
            return 3;
        } else if (array[2] > .90){
            return 2;
        } else {
            return 1;
        }
    }

    public static int getQuarterNoteIndex(INDArray data){
        DataBuffer dataBuffer = data.data();
        double[] array = dataBuffer.asDouble();
        if (array[0]>.8){
            return 0;
        } else if (array[1]>.8){
            return 1;
        } else if (array[2] > .8){
            return 2;
        } else if (array[3] > .8){
            return 3;
        } else if (array[4] > .8){
            return 4;
        }else if (array[5] > .8){
            return 5;
        }else {
            return 6;
        }
    }

    public static int getClefIndex(INDArray data){
        DataBuffer dataBuffer = data.data();
        double[] array = dataBuffer.asDouble();
        if (array[0]>.95){
            return 0;
        } else if (array[1]>.95){
            return 1;
        } else {
            return 2;
        }
    }

    public static int getRestIndex(INDArray data){
        DataBuffer dataBuffer = data.data();
        double[] array = dataBuffer.asDouble();
        for (int i = 0;i<array.length;i++){
            if (array[i]>.90){
                return i;
            }
        }
        return 5;
    }

    public static MusicDataSetLoader dataSetByType(String type) {
        switch (type){
            case "Accidental":
                return accidentalData;
            case "Rest":
                return restData;
            case "NoteFull":
                return noteFullData;
            case "Note":
                return noteData;
            case "Clef":
                return clefData;
            case "TimeSig":
                return timeSigData;
            case "Quarter":
                return quarterData;
            default:
                return null;
        }
    }

    public static MultiLayerNetwork networkByType(String type) {
        switch (type){
            case "Accidental":
                return accidentalNet;
            case "Rest":
                return restNet;
            case "NoteFull":
                return noteFullNet;
            case "Note":
                return noteNet;
            case "Clef":
                return clefNet;
            case "TimeSig":
                return timeSigNet;
            case "Quarter":
                return quarterNet;
            default:
                return null;
        }
    }

    public static void main(String[] args) throws Exception{


        int epochs = 30;
        //accidentalData = new MusicDataSetLoader("Accidental", 5);
        MusicDataSetLoader data = new MusicDataSetLoader("Quarter", 10);
        MultiLayerNetwork net  = configure(data);

        for (int i = 0; i< epochs;i++){
            System.out.println("Epoch "+i);
            net.fit(data.dataIter);
        }


        data.save(net);

//        MusicNet.load();
        data.eval(net);

    }
}

/*
.layer(1,  new ConvolutionLayer.Builder()
                .nIn(24)
                .kernelSize(5,5)
                .nOut(64)
                .activation( Activation.RELU)
                .build())
            .layer(2,  new SubsamplingLayer.Builder()
                .poolingType(org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType.MAX)
                .kernelSize(new int[]{2,1})
                .stride( new int[]{2, 1})
                .build())
 */
