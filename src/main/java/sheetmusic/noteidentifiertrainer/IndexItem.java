package sheetmusic.noteidentifiertrainer;

public class IndexItem {
    Integer[] coors = new Integer[3];
    String label = "";

    public IndexItem(Integer[] coors, String label) {
        this.coors = coors;
        this.label = label;
    }

    public IndexItem(Integer[] coors) {
        this.coors = coors;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
