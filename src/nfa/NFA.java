package nfa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dfa.DFA;
import dfa.State;

public class NFA {
    HashMap<String, ConversionState> dfaStates;
    HashMap<String, NFAState> nfaStates;
    HashMap<String, HashSet<String>> closureTable;
    DFA resultDFA;

    public NFA(String description) throws Exception {
        this.dfaStates = new HashMap<String, ConversionState>();
        this.nfaStates = new HashMap<String, NFAState>();
        this.closureTable = new HashMap<String, HashSet<String>>();
        parseNFA(description);
    }

    private void parseNFA(String description) throws Exception {
        String[] parameters = description.split("#");
        String[] zeroTransitions = parameters[0].split(";");
        String[] oneTransitions = parameters[1].split(";");
        String[] epsTransitions = parameters[2].split(";");
        String[] acceptStates = parameters[3].split(",");
        constructNFAWithoutEps(zeroTransitions, true);
        constructNFAWithoutEps(oneTransitions, false);
        addAcceptStatesToNFA(acceptStates);
        for (String state : nfaStates.keySet()) {
            closureTable.put(state, getEpsilonClosure(state, epsTransitions));
        }
        printNFA();
        printClosures();
        removeEpsilonFromNFA();
        printNFA();
        convertToDFA();
        addDFAAccepts(acceptStates);
        printDFAConversion();
        this.resultDFA = createDFAStates();
        for (State currentState : resultDFA.states) {
            System.out.println(currentState.label + "   " + currentState.zeroLabel + "  " + currentState.oneLabel);
        }
    }

    public boolean run(String testString) {
        return this.resultDFA.run(testString);
    }

    public void printClosures() {
        StringBuilder sb = new StringBuilder();
        for (String nfaState : closureTable.keySet()) {
            sb.append(nfaState + " :   ");
            HashSet<String> closureStates = closureTable.get(nfaState);
            closureStates.forEach(state -> sb.append(state + ", "));
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public void printNFA() {
        String[][] table = new String[nfaStates.keySet().size() + 1][3];
        table[0] = new String[] { "label", "0", "1" };
        int i = 1;
        for (String state : nfaStates.keySet()) {
            table[i] = new String[] { nfaStates.get(state).label, nfaStates.get(state).zeros.toString(),
                    nfaStates.get(state).ones.toString() };
            i++;
        }
        for (String[] row : table) {
            System.out.format("%-15s%-15s%-15s\n", row);
        }

    }

    public void printDFAConversion() {
        String[][] table = new String[dfaStates.keySet().size() + 1][3];
        table[0] = new String[] { "label", "0", "1" };
        int i = 1;
        for (String state : dfaStates.keySet()) {
            table[i] = new String[] { dfaStates.get(state).getLabel(), dfaStates.get(state).zeros.toString(),
                    dfaStates.get(state).ones.toString() };
            i++;
        }
        for (String[] row : table) {
            System.out.format("%-15s%-15s%-15s\n", row);
        }

    }

    private void constructNFAWithoutEps(String[] transitions, boolean isZeros) throws Exception {

        for (String transition : transitions) {
            String[] transitionStates = transition.split(",");
            if (transitionStates.length != 2) {
                throw new Exception("Fatal transition parsing error: " + transition);
            }
            addTransitionToNFA(transitionStates[0], transitionStates[1], isZeros);
        }

    }

    private void addTransitionToNFA(String state1, String state2, boolean isZeros) {
        NFAState currentState = tryAddStateFromTransition(state1);
        tryAddStateFromTransition(state2);
        if (isZeros) {
            currentState.zeros.add(state2);
        } else {
            currentState.ones.add(state2);
        }
    }

    private NFAState tryAddStateFromTransition(String stateLabel) {
        if (nfaStates.containsKey(stateLabel)) {
            return nfaStates.get(stateLabel);
        }

        nfaStates.put(stateLabel, new NFAState(stateLabel));
        return nfaStates.get(stateLabel);
    }

    public String[] getEpsilonTransitions(String state, String[] epsTransitions) {
        ArrayList<String> epsilonTransitions = new ArrayList<String>();
        for (String transition : epsTransitions) {
            String[] transitionStates = transition.split(",");
            if (state.equals(transitionStates[0])) {
                epsilonTransitions.add(transitionStates[1]);
            }
        }
        return epsilonTransitions.toArray(new String[epsilonTransitions.size()]);
    }

    private void addAcceptStatesToNFA(String[] acceptStates) throws Exception {
        for (String acceptState : acceptStates) {
            if (nfaStates.containsKey(acceptState)) {
                nfaStates.get(acceptState).isAccept = true;
            } else {
                throw new Exception("Could not parse accept states. State not found to accept" + acceptState);
            }
        }
    }

    private HashSet<String> getEpsilonClosure(String state, String[] epsTransitions) {
        HashSet<String> stateClosure = new HashSet<String>();
        stateClosure.add(state);
        HashSet<String> visitedStates = new HashSet<String>();
        // Do DFS search for epsilon closure
        LinkedList<String> searchList = new LinkedList<String>();
        NFAState targetState = nfaStates.get(state);
        String currentState = targetState.label;
        while (currentState != null) {
            if (visitedStates.contains(currentState)) {
                currentState = searchList.poll();
            } else {
                String[] transitionStates = getEpsilonTransitions(currentState, epsTransitions);
                // Add the epsilon transitions of the state
                for (String transitionState : transitionStates) {
                    stateClosure.add(transitionState);
                    if (!visitedStates.contains(transitionState)) {
                        searchList.add(transitionState);
                    }
                }
                visitedStates.add(currentState);
                currentState = searchList.poll();
            }
        }
        return stateClosure;
    }

    private void removeEpsilonFromNFA() {

        for (String nfaStateLabel : nfaStates.keySet()) {
            NFAState currentNFAState = nfaStates.get(nfaStateLabel);
            removeEpsilonFromState(currentNFAState, true);
            removeEpsilonFromState(currentNFAState, false);
        }
    }

    private void removeEpsilonFromState(NFAState state, boolean isZero) {
        HashSet<String> newStates = new HashSet<String>();
        closureTable.get(state.label).forEach(closureState -> {
            HashSet<String> nextStates = isZero ? nfaStates.get(closureState).zeros : nfaStates.get(closureState).ones;
            nextStates.forEach(nextStateLabel -> newStates.addAll(closureTable.get(nextStateLabel)));
        });
        if (isZero) {
            state.zeros = newStates;
        } else {
            state.ones = newStates;
        }
    }

    public void convertToDFA() {
        NFAState startNFAState = nfaStates.get("0");
        String startNFAStateLabel = startNFAState.label;
        HashSet<String> startDFAStateLabels = new HashSet<String>();
        startDFAStateLabels.add(startNFAStateLabel);
        ConversionState startDFAState = new ConversionState(startDFAStateLabels, startNFAState.zeros,
                startNFAState.ones);
        LinkedList<ConversionState> searchList = new LinkedList<ConversionState>();
        ConversionState currentDFAState = startDFAState;
        HashSet<ConversionState> visitedStates = new HashSet<ConversionState>();
        while (currentDFAState != null) {
            if (!visitedStates.contains(currentDFAState)) {
                String label = currentDFAState.getLabel();
                if (!dfaStates.containsKey(label)) {
                    dfaStates.put(label, currentDFAState);
                    searchList.add(getConversionState(currentDFAState.zeros));
                    searchList.add(getConversionState(currentDFAState.ones));
                }
                visitedStates.add(currentDFAState);
            }
            currentDFAState = searchList.poll();
        }
        dfaStates.put("", new ConversionState(new HashSet<String>(), new HashSet<String>(), new HashSet<String>()));
    }

    public DFA createDFAStates() {

        HashMap<String, Integer> labelMap = new HashMap<String, Integer>();
        int counter = 0;

        for (String label : dfaStates.keySet()) {
            labelMap.put(label, counter);
            counter++;
        }
        State[] states = new State[dfaStates.size()];
        ArrayList<Integer> acceptLabels = new ArrayList<Integer>();
        int i = 0;
        for (String label : dfaStates.keySet()) {
            ConversionState currentState = dfaStates.get(label);
            states[i] = new State(labelMap.get(label), labelMap.get(String.join("", currentState.zeros)),
                    labelMap.get(String.join("", currentState.ones)));
            if (currentState.isAccept) {
                acceptLabels.add(labelMap.get(label));
            }
            i++;
        }
        return new DFA(states, acceptLabels.stream().mapToInt(elm -> elm).toArray());
    }

    public void addDFAAccepts(String[] acceptLabels) {
        List<String> acceptList = Arrays.asList(acceptLabels);
        for (String dfaLabel : dfaStates.keySet()) {
            ConversionState currentState = dfaStates.get(dfaLabel);
            currentState.labels.forEach(label -> {
                if (acceptList.contains(label)) {
                    currentState.isAccept = true;
                }
            });
        }
    }

    public ConversionState getConversionState(HashSet<String> labels) {
        HashSet<String> zeroTransition = new HashSet<String>();
        HashSet<String> oneTransition = new HashSet<String>();
        for (String label : labels) {
            zeroTransition.addAll(nfaStates.get(label).zeros);
            oneTransition.addAll(nfaStates.get(label).ones);
        }
        return new ConversionState(labels, zeroTransition, oneTransition);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the NFA spec");
        NFA nfa = new NFA(br.readLine());
        System.out.println("Now, please enter a test case");
        while (true) {
            System.out.println(nfa.run(br.readLine()));
        }
    }
}