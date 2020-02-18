package dfa;

public class State{
    public int label;
    public int zeroLabel;
    public int oneLabel;
    public State(int label, int zeroLabel,int oneLabel){
        this.label = label;
        this.zeroLabel = zeroLabel;
        this.oneLabel = oneLabel;
    }

}