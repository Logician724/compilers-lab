package fdfa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FDFA {
    public HashMap<String, FDFAState> states;
    public Stack<FDFAState> stateStack;

    public FDFA(String description) {
        String[] fdfaParams = description.split("#");
        String[] transitions = fdfaParams[0].split(";");
        List<String> acceptStates = Arrays.asList(fdfaParams[1].split(","));
        this.states = new HashMap<String, FDFAState>();
        for (String transition : transitions) {
            String[] transitionParams = transition.split(",");
            FDFAState newState = new FDFAState(Integer.parseInt(transitionParams[0]),
                    Integer.parseInt(transitionParams[1]), Integer.parseInt(transitionParams[2]), transitionParams[3]);
            if (acceptStates.contains(transitionParams[0])) {
                newState.isAccept = true;
            }
            states.put(transitionParams[0], newState);
        }
        states.values().forEach(state -> System.out.println(state));
        this.stateStack = new Stack<FDFAState>();
    }

    public String run(String testString) throws Exception {
        String result = "";
        int left = 0;
        while (left < testString.length() - 1) {
            FDFAState currentState = this.states.get("0");
            stateStack.push(currentState);
            // traverse the test string with right pointer
            int right;
            for (right = left; right < testString.length(); right++) {
                String currentLabel = testString.charAt(right) + "";
                if (currentLabel.equals("0")) {
                    currentState = this.states.get(currentState.zeroLabel + "");
                    stateStack.push(currentState);
                } else {
                    if (currentLabel.equals("1")) {
                        currentState = this.states.get(currentState.oneLabel + "");
                        stateStack.push(currentState);
                    } else {
                        throw new Exception("Unsupported label");
                    }
                }
            }
            boolean isAcceptStateFound = false;
            while (right >= left) {
                FDFAState testState = stateStack.pop();
                if (testState.isAccept) {
                    result += testState.action;
                    stateStack = new Stack<FDFAState>();
                    isAcceptStateFound = true;
                    break;
                } else {
                    right--;
                }

            }
            if (!isAcceptStateFound) {
                break;
            }
            left = right;

        }

        if (result.equals("")) {
            return "ERROR";
        } else {
            return result;
        }
    }

    public static void main(String[] args) throws IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the FDFA spec");
        FDFA fdfa = new FDFA(br.readLine());
        System.out.println("Now, please enter a test case");
        // 0,1,0,00;1,3,2,111;2,3,4,001;3,4,5,010;4,5,6,011;5,0,2,101;6,6,1,11#6,3
        while (true) {
            System.out.println(fdfa.run(br.readLine()));
        }
    }
}