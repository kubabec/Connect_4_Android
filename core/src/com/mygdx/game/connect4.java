package com.mygdx.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.game.GameState;
import sac.game.GameStateImpl;

public class connect4 extends GameStateImpl{
	
	public static final int m = 7;
	public static final int n = 9;
	
	public static final char player1 = 'O';
	public static final char player2 = 'X';
	public static final char empty = '-';

	public static final char emptyToken = 0;
	public static final char player1Token = 1;
	public static final char player2Token = 2;



	
	public char board[][] = null;
	
	public connect4(int x, int y){
		board = new char[m][n];
		for(int i = 0; i <m; i++)
			for(int j = 0; j<n; j++)
				board[i][j] = empty;
	}
	
	public connect4(connect4 copy)
	{
		board = new char[m][n];
		for(int i = 0; i <m; i++)
			for(int j = 0; j<n; j++)
				board[i][j] = copy.board[i][j];
		setMaximizingTurnNow(copy.isMaximizingTurnNow());
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();	
		for(int k = 0 ; k< n; k++)
			builder.append(k+"|");
			builder.append("\n");
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j <n ; j++)
				builder.append(board[i][j] + " ");
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	
	public Boolean makeMove(int columnNumber){ // wykonaj ruch na planszy w klasie
		if(columnNumber >=0 && columnNumber < n)
		{
			if(board[0][columnNumber] == empty){
				int counter = 0;
				while(board[counter][columnNumber] == empty && counter < m){
					counter += 1;
					if(counter == m){
					
						break;
					}
				}
				if(isMaximizingTurnNow()){
					board[counter-1][columnNumber] = player1;
				}else{
					board[counter-1][columnNumber] = player2;
				}
				setMaximizingTurnNow(!isMaximizingTurnNow());
				

			}
		}
		return true;
		
	}


	@Override
	public List<GameState> generateChildren() { // dziecko gry
		List<GameState> children = new ArrayList<GameState>();
		for(int i = 0; i<n; i++)
		{
			connect4 child = new connect4(this);
			child.makeMove(i);
			child.setMoveName(""+i);
			children.add(child);
		}
		return children;
	}

	public int hashCode()
	{
		int[] t = new int[m*n]; 		
		int k = 0;
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				t[k++] = board[i][j];
			return Arrays.hashCode(t);
	}
	
	public int hasPlayerWon(){ // sprawdz czy ktos wygral, zwroc kto
		for(int i = m-1 ; i >=0; i--){ // iterujemy od konca cala tablice gry
			for(int j = 0 ; j < n ; j++){
				if(board[i][j] != player1) continue; // szukamy komórki, gdzie jest wrzucona pilka gracza player1
				Boolean KolumnaDoPrzodu = true;
				Boolean WierszDoGory = true;
				Boolean WierszDoGoryWprawo = true;
				Boolean WierszDoGoryWlewo = true;
				for(int k = 1; k<4; k++){ // szukaj czterech kul kolo siebie
					if(i-k < 0 ){ // wyskoczyles poza wysokosc planszy
						WierszDoGory = false;
						WierszDoGoryWprawo = false;
						WierszDoGoryWlewo = false;
					}
					else{ // po kolei sprawdzaj mozliwosci
						if(board[i-k][j] != player1) WierszDoGory = false;

						if(j+k >= n ) WierszDoGoryWprawo = false; // jesli wyskoczysz w prawo za plansze
						else if(board[i-k][j+k] != player1) WierszDoGoryWprawo = false;

						if(j-k < 0 ) WierszDoGoryWlewo = false; // jesli wyskoczysz w lewo za plansze
						else if(board[i-k][j-k] != player1) WierszDoGoryWlewo = false;
					}
					if(j + k >= n ) KolumnaDoPrzodu = false;
					else{
						if(board[i][j+k] != player1) KolumnaDoPrzodu = false;
					}
				} // Jesli po 4 iteracjach nadal ktorys jest prawdziwy - player 1 wygral
				if(KolumnaDoPrzodu == true || WierszDoGory == true || WierszDoGoryWprawo == true || WierszDoGoryWlewo == true)
					return 1;
			}
		}
		
		for(int i = m-1 ; i >=0; i--){ // powtorz to samo dla player2
			for(int j = 0 ; j < n ; j++){
				if(board[i][j] != player2) continue;
				Boolean KolumnaDoPrzodu = true;
				Boolean WierszDoGory = true;
				Boolean WierszDoGoryWprawo = true;
				Boolean WierszDoGoryWlewo = true;
				for(int k = 1; k<4; k++){
					if(i-k < 0 ){
						WierszDoGory = false;
						WierszDoGoryWprawo = false;
						WierszDoGoryWlewo = false;
					}
					else{
						if(board[i-k][j] != player2) WierszDoGory = false;
						
						if(j+k >= n ) WierszDoGoryWprawo = false;
						else if(board[i-k][j+k] != player2) WierszDoGoryWprawo = false;
						
						if(j-k < 0 ) WierszDoGoryWlewo = false;
						else if(board[i-k][j-k] != player2) WierszDoGoryWlewo = false;
					}
					if(j + k >= n ) KolumnaDoPrzodu = false;
					else{
						if(board[i][j+k] != player2) KolumnaDoPrzodu = false;
					}			
				}
				if(KolumnaDoPrzodu == true || WierszDoGory == true || WierszDoGoryWprawo == true || WierszDoGoryWlewo == true)
					return 2;
			}
		}
		 // czy ktos dotknal sufitu
		for(int i = 0; i<n ; i++){
			if(board[0][i] == player1)
				return 1;
			else if (board[0][i] == player2)
				return 2;
		}
		
		return 0;
	}

	public int count3nearest(){ // heurystyka  - licz ile gracz ma 3 kul kolo siebie
		int player1counter =0;
		int player2counter =0;
		for(int i = m-1 ; i >=0; i--){
			for(int j = 0 ; j < n ; j++){
				if(board[i][j] != player1) continue;
				Boolean KolumnaDoPrzodu = true;
				Boolean WierszDoGory = true;
				Boolean WierszDoGoryWprawo = true;
				Boolean WierszDoGoryWlewo = true;
				for(int k = 1; k<3; k++){
					if(i-k < 0 ){
						WierszDoGory = false;
						WierszDoGoryWprawo = false;
						WierszDoGoryWlewo = false;
					}
					else{
						if(board[i-k][j] != player1) WierszDoGory = false;
						
						if(j+k >= n ) WierszDoGoryWprawo = false;
						else if(board[i-k][j+k] != player1) WierszDoGoryWprawo = false;
						
						if(j-k < 0 ) WierszDoGoryWlewo = false;
						else if(board[i-k][j-k] != player1) WierszDoGoryWlewo = false;
						
					}
					if(j + k >= n ) KolumnaDoPrzodu = false;
					else{
						if(board[i][j+k] != player1) KolumnaDoPrzodu = false;
					}		
					
					if((k == 2 )&& (KolumnaDoPrzodu== true || WierszDoGory==true || WierszDoGoryWlewo == true || WierszDoGoryWprawo == true))
						player1counter+= 2;
					
					if((k == 1 )&& (KolumnaDoPrzodu== true || WierszDoGory==true || WierszDoGoryWlewo == true || WierszDoGoryWprawo == true))
						player1counter+= 1;
				}
			
			}
		}
		
		for(int i = m-1 ; i >=0; i--){
			for(int j = 0 ; j < n ; j++){
				if(board[i][j] != player2) continue;
				Boolean KolumnaDoPrzodu = true;
				Boolean WierszDoGory = true;
				Boolean WierszDoGoryWprawo = true;
				Boolean WierszDoGoryWlewo = true;
				for(int k = 1; k<3; k++){
					if(i-k < 0 ){
						WierszDoGory = false;
						WierszDoGoryWprawo = false;
						WierszDoGoryWlewo = false;
					}
					else{
						if(board[i-k][j] != player2) WierszDoGory = false;
						
						if(j+k >= n ) WierszDoGoryWprawo = false;
						else if(board[i-k][j+k] != player2) WierszDoGoryWprawo = false;
						
						if(j-k < 0 ) WierszDoGoryWlewo = false;
						else if(board[i-k][j-k] != player2) WierszDoGoryWlewo = false;
					}
					if(j + k >= n ) KolumnaDoPrzodu = false;
					else{
						if(board[i][j+k] != player2) KolumnaDoPrzodu = false;
					}
					// Przyznaj punkty za ilosc kulek kolo siebie
					if((k == 2 )&& (KolumnaDoPrzodu== true || WierszDoGory==true || WierszDoGoryWlewo == true || WierszDoGoryWprawo == true))
						player2counter+= 2;
					if((k == 1 )&& (KolumnaDoPrzodu== true || WierszDoGory==true || WierszDoGoryWlewo == true || WierszDoGoryWprawo == true))
						player2counter+= 1;
					
				}
	
			}
		}
	
		return player1counter - player2counter;
		
	}
}
