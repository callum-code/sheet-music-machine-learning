package sheetmusic;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SymbolDataSetLoader {

    ArrayList<VectorImage> symbols;
    String symbolType;
    public int currentNum = 0;
    private int numExample;
    private int startAt = 0;

    public SymbolDataSetLoader(ArrayList<VectorImage> symbols, String symbolType){
        this.symbols  = symbols;
        this.symbolType = symbolType;
    }

    public void load(){
        this.numExample = symbols.size();
    }

    public MultiDataSet next(int batchSize) {
        try {
            currentNum+=batchSize;
            MultiDataSet result = convertDataSet(currentNum);
            return result;
        } catch (Exception e) {
            System.out.println("aaaa");
        }
        return null;
    }

    public MultiDataSet convertDataSet(int num) {

        INDArray[] featuresMask = null;
        INDArray[] labelMask = null;

        List<MultiDataSet> multiDataSets = new ArrayList<>();

        for (int i = startAt; i < num; i++) {
                VectorImage symbol = symbols.get(i);
                INDArray feature = symbol.getINDArray();
                if (symbolType.equals("NoteFull")) {
                    feature = symbol.getFullINDArray();
                }
                INDArray[] features = new INDArray[]{feature};
                INDArray[] labels = new INDArray[symbolType.equals("TimeSig") ? 2 : 1];

                assignLabels(labels, symbol);
                multiDataSets.add(new MultiDataSet(features, labels, featuresMask, labelMask));
        }
        MultiDataSet result = MultiDataSet.merge(multiDataSets);





        startAt = num;
        return result;
    }

    private void assignLabels(INDArray[] labels, VectorImage symbol) {
        switch (symbolType){
            case "Accidental":
                int aNum = symbol.info.contains("n") ? 0 : (symbol.info.contains("s") ? 1 : 2);
                labels[0] =  Nd4j.zeros(1, 3).putScalar(new int[]{0, aNum}, 1);
                break;
            case "Clef":
                int cNum = symbol.info.contains("f") ? 0 : 1;
                labels[0] =  Nd4j.zeros(1, 2).putScalar(new int[]{0, cNum}, 1);
                break;
            case "TimeSig":
                Scanner s = new Scanner(symbol.info).useDelimiter("/");
                int tNumTop = s.nextInt();
                System.out.println(tNumTop);
                int tNumBottom = s.nextInt();
                labels[0] =  Nd4j.zeros(1, 12).putScalar(new int[]{0, tNumTop}, 1);
                labels[1] =  Nd4j.zeros(1, 12).putScalar(new int[]{0, tNumBottom}, 1);
                break;
            case "Note":
                double nLength = Double.parseDouble(symbol.info);
                int nNum = nLength < 2 ? 0 : (nLength >= 4 ? 2 : 1);
                labels[0] =  Nd4j.zeros(1, 3).putScalar(new int[]{0, nNum}, 1);
                break;
            case "NoteFull":
                int nfNum = determineLength(Double.parseDouble(symbol.info));
                labels[0] =  Nd4j.zeros(1, 16).putScalar(new int[]{0, nfNum}, 1);
                break;
            case "Rest":
                int rNum = determineLength(Double.parseDouble(symbol.info));
                labels[0] =  Nd4j.zeros(1, 16).putScalar(new int[]{0, rNum}, 1);
                break;
            default:
                labels[0] =  Nd4j.zeros(1, 2);
        }
    }

    private int determineLength(double length) {
        return (int)((length*4)-1);
    }

    public void reset() {
        load();
    }
    public int totalExamples() {
        return numExample;
    }
}
