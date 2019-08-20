package sheetmusic.noteidentifiertrainer;

public class Rest extends Symbol {

    public Rest(IndexItem it){
        super.x = it.coors[1];
        super.y = it.coors[2];
        super.length = lenToDouble(it.label);
        this.id = "rest";
    }

    public double lenToDouble(String len){
        switch (len){
            case "1/16":
                return .25;
            case "1/8":
                return .5;
            case "3/16":
                return .75;
            case "1":
                return 1;
            case "1 1/2":
                return 1.5;
            default:
                return 0;
        }
    }

    public String toString() {
        String returnMe = "Rest";
        returnMe += " with length " + length;
        return returnMe;
    }

}
