package dfa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DFA{
    public State[] states;
    public int[] acceptLabels;
    public DFA(String dfaSpec){
        
        String[] dfaParams = dfaSpec.split("#");
        String[] stateSpecs = dfaParams[0].split(";");
        String[] acceptLabelSpecs = dfaParams[1].split(",");
        this.acceptLabels = new int[acceptLabelSpecs.length];
        // parse accept state labels
        for(int i = 0; i < acceptLabels.length; i++){
            acceptLabels[i] = Integer.parseInt(acceptLabelSpecs[i]);
        }
        
        // parse state transition labels
        this.states = new State[stateSpecs.length]; 
        for(int i = 0; i < stateSpecs.length; i++){
            String[] stateParams = stateSpecs[i].split(",");
            states[i] = new State(Integer.parseInt(stateParams[0]), Integer.parseInt(stateParams[1]), Integer.parseInt(stateParams[2]));
        }
    }
    public DFA(State[] states, int[] acceptLabels){
        this.states = states;
        this.acceptLabels = acceptLabels;
    }

    public boolean run(String input){
        State currentState = states[0];
        for(int i = 0; i < input.length(); i++){
          if(Integer.parseInt(input.charAt(i)+"") == 0){
              currentState = states[currentState.zeroLabel];
          }else{
              if(Integer.parseInt(input.charAt(i)+"") == 1){
                  currentState = states[currentState.oneLabel];
              }else{
                  throw new Error("Unidentified alphabet character " + input.charAt(i));
              }
          }

      }
      for(int acceptLabel : acceptLabels){
        if(acceptLabel == currentState.label){
            return true;
        }
    }
    return false;   
    }


    public static void main(String [] args) throws IOException{
        System.out.println("Please enter the DFA spec");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DFA dfa = new DFA(br.readLine());
        System.out.println("Now, please enter a test case");
        while(true){
            System.out.println(dfa.run(br.readLine()));
        }
    }
}