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

import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

import java.util.ArrayList;

public class SymbolDataSetIterator implements MultiDataSetIterator {

    public int batchSize;
    public int batchNum = 0;
    public int numExample;
    private SymbolDataSetLoader load;
    private MultiDataSetPreProcessor preProcessor;

    public SymbolDataSetIterator(int batchSize, ArrayList<VectorImage> img, String symbolType) {
        this.batchSize = batchSize;
        load = new SymbolDataSetLoader(img, symbolType);
        load.load();
        numExample = load.totalExamples();
    }

    public MultiDataSet next(int i) {
        batchNum += i;
        MultiDataSet mds = load.next(i);
        if (preProcessor != null) {
            preProcessor.preProcess(mds);
        }
        return mds;
    }

    @Override
    public void setPreProcessor(MultiDataSetPreProcessor multiDataSetPreProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public MultiDataSetPreProcessor getPreProcessor() {
        return preProcessor;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void reset() {
        batchNum = 0;
        load.reset();
    }

    @Override
    public boolean hasNext() {
        if(batchNum + batchSize < numExample){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MultiDataSet next() {
        return next(batchSize);
    }

}
