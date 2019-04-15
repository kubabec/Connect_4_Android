package com.mygdx.game;


import com.mygdx.game.connect4;

import sac.State;
import sac.StateFunction;

public class MyHeuristic extends StateFunction {
	public double calculate(State obiekt){
		if(((com.mygdx.game.connect4) obiekt).hasPlayerWon() == 1){
			return Double.POSITIVE_INFINITY;
			//return Double.NEGATIVE_INFINITY;
		}else if (((com.mygdx.game.connect4) obiekt).hasPlayerWon() == 2)
			//return Double.POSITIVE_INFINITY;
			 return Double.NEGATIVE_INFINITY;
		

		return ((connect4) obiekt).count3nearest();
	}
}
