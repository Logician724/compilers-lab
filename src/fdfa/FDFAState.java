package fdfa;

public class FDFAState {
    int label;
    int zeroLabel;
    int oneLabel;
    String action;
    boolean isAccept;

    public FDFAState(int label, int zeroLabel, int oneLabel, String action) {
        this.label = label;
        this.zeroLabel = zeroLabel;
        this.oneLabel = oneLabel;
        this.action = action;
        this.isAccept = false;
    }

    public String toString(){
        return this.label + "\t" + this.zeroLabel + "\t" + this.oneLabel + "\t" + this.action + "\t" + this.isAccept; 
    }
}