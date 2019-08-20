package sheetmusic;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Random;

public class MusicDataSetLoader {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(MusicDataSetLoader.class);

    int height;
    int width;
    int channels;
    int seed = 234;
    Random randNumGen  = new Random(seed);
    int batchSize;
    int outputNum;
    File trainData;
    File testData;
    FileSplit train;
    FileSplit test;
    String mainDir = "/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data";
    String type;
    public ImageRecordReader recordReader;
    public DataSetIterator dataIter;
    private DataNormalization scalar;

    public MusicDataSetLoader(String type, int batchSize) throws Exception{
        this.batchSize = batchSize;
        init(type);

        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        recordReader = new ImageRecordReader(height, width, channels,labelMaker);


        recordReader.initialize(train);
        //recordReader.setListeners(new LogRecordListener());

        dataIter = new RecordReaderDataSetIterator(recordReader,batchSize,1, outputNum);

        scalar = new ImagePreProcessingScaler(0,1);

        scalar.fit(dataIter);
        dataIter.setPreProcessor(scalar);
        log.info(recordReader.getLabels().toString());

    }

    public void eval(MultiLayerNetwork model) throws Exception{

        recordReader.reset();

        recordReader.initialize(test);
        DataSetIterator testIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);
        scalar.fit(testIter);
        testIter.setPreProcessor(scalar);

        log.info(recordReader.getLabels().toString());

        Evaluation eval = new Evaluation(outputNum);

        // Evaluate the network
        while (testIter.hasNext()) {
            org.nd4j.linalg.dataset.DataSet next = testIter.next();
            INDArray output = model.output(next.getFeatures());
            // Compare the Feature Matrix from the model
            // with the labels from the RecordReader
            eval.eval(next.getLabels(), output);
        }

        System.out.println("\n****Evaluation*****");
        log.info("-------------------");
        System.out.println(eval.stats());
        log.info(eval.stats());
    }

    public MultiLayerNetwork load(int modelNumber){
        try{
            File locationToSave = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/TrainedModels/"
                +type+"/trained_model_"  + modelNumber + ".zip");
            System.out.println(locationToSave.getAbsolutePath());
            MultiLayerNetwork model =  ModelSerializer.restoreMultiLayerNetwork(locationToSave);

            return model;
        } catch(Exception e){
            System.out.println("ERROR: "+type+" Network did not load");
            return null;
        }
    }

    public boolean save(MultiLayerNetwork model) {

        try {
            System.out.println("*********Save Model**********");

            File saveDir = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/TrainedModels/" + type);

            File locationToSave = new File(saveDir, "trained_model_" + saveDir.list().length + ".zip");
            System.out.println(locationToSave.getAbsolutePath());
            boolean saveUpdater = false;
            //Model Serealizer needs modelname, location, saveupdater
            ModelSerializer.writeModel(model, locationToSave, saveUpdater);
            return true;

        } catch (Exception e){
            return false;
        }

    }

    private void init(String type){
        this.type = type;
        trainData = new File(mainDir+"/"+type+"/train");
        testData = new File(mainDir+"/"+type+"/test");
        train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        test = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        switch(type){
            case "Accidental":
                height = 108;
                width = 36;
                channels = 1;
                outputNum = 4;
                break;
            case "Rest":
                height = 20;
                width = 48;
                channels = 1;
                outputNum = 6;
                break;
            case "NoteFull":
                height = 37;
                width = 120;
                channels = 1;
                outputNum = 9;
                break;
            case "Note":
                height = 18;
                width = 26;
                channels = 1;
                outputNum = 4;
                break;
            case "Clef":
                height = 36;
                width = 108;
                channels = 1;
                outputNum = 3;
                break;
            case "Quarter":
                height = 37;
                width = 120;
                channels = 1;
                outputNum = 6;
                break;
        }
    }

    public static void main(String[] args) throws Exception{
        MusicDataSetLoader load = new MusicDataSetLoader("Accidental", 5);

        for (int i = 0;i < 3;i++){
            DataSet ds = load.dataIter.next();
            System.out.println(ds);
            System.out.println(load.dataIter.getLabels());
        }
    }
}
