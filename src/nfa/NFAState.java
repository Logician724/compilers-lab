package nfa;

import java.util.HashSet;

public class NFAState {
    HashSet<String> zeros;
    HashSet<String> ones;
    String label;
    boolean isAccept;

    public NFAState(String label) {
        this.label = label;
        this.zeros = new HashSet<String>();
        this.ones = new HashSet<String>();
        this.isAccept = false;
    }

}