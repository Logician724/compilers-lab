package nfa;

import java.util.HashSet;

public class ConversionState {
    HashSet<String> labels;
    HashSet<String> zeros;
    HashSet<String> ones;
    boolean isAccept;

    public ConversionState(String label) {
        this.labels = new HashSet<String>();
        this.labels.add(label);
        this.zeros = new HashSet<String>();
        this.ones = new HashSet<String>();
    }

    public ConversionState(HashSet<String> labels) {
        this.labels = labels;
        this.zeros = new HashSet<String>();
        this.ones = new HashSet<String>();
    }

    public ConversionState(NFAState nfaState) {
        this.labels = new HashSet<String>();
        this.labels.add(nfaState.label);
        this.zeros = nfaState.zeros;
        this.ones = nfaState.ones;
    }

    public ConversionState(HashSet<String> labels, HashSet<String> zero, HashSet<String> one) {
        this.labels = labels;
        this.zeros = zero;
        this.ones = one;
    }

    public String getLabel(){
        return String.join("",this.labels);
    }
}