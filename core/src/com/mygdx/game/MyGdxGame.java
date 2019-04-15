package com.mygdx.game;

import java.util.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameStateImpl;
import sac.game.GameSearchConfigurator;



public class MyGdxGame extends ApplicationAdapter {

	public GameState gameState; // Status gry
	public GameProcess gameProcess; // Status rozgrywki
	public GameProcess previousGameProcess; //


	// Connect 4
	public connect4 Game ; //  obiekt gry
	public MyHeuristic ocenaSytuacji; // funkcja heurystyki
	public GameSearchAlgorithm algorytm; // algorytm przeszukiwania grafu
	private int whoWins; // ID zwyciescy
	private double graphDepth; // glebokosc przeszukiwania grafu
	//

	//Camera and render
	private OrthographicCamera cam; // parametry libGDX do renderu 2D
	private ShapeRenderer sr;
	//
	//Game objects
	private List<gameItem> balls; // Wszystkie kulki gry na planszy
	private int boardSizeX; // Wielkość planszy w osi X
	private int boardSizeY; // Wielkość planszy w osi Y
	private int[][] gameBoard; // Plansza do gry (logiczna)
	public float[] playArea; // Pole, na ktorym wyswietla sie plansza do gry
	public float singleCellWidth; // dlugosc pojedynczej komorki na planszy
	public float singleCellHeight; // szerokosc - -
	public SizeMode sizeMode; // tryb rozdzielczosci ekranu

	public onClickArea[] touchArea; // Tablica rozroznialnych obszarow 'klikalnych' na ekranie
	private int whatNumberPlayerIs; // przypisanie ID graczowi w zaleznosci kto zaczyna
	//
	// additional variables
	public int separator; // Pojedynczy odstep miedzy liniami definiujacymi pole gry
	public float ballX; // Docelowe X nowo tworzonej pilki
	public float ballY; // Docelowe Y nowo tworzonej pilki
	//


	// Przyciski gry
		public onClickArea Play_Again;
		public onClickArea Player_Start;
		public onClickArea Computer_Start;
		public onClickArea veryEasy;
		public onClickArea easy;
		public onClickArea normal;
		public onClickArea expert;
	//
	// Bitmapy i kolory tla
	public Color bgColor1,bgColor2,bgColor3,bgColor4;
	SpriteBatch batch;
	Texture PlayAgainButton, YouWonButton,ComputerWonButton;
	Texture YouStartButton,ComputerStartsButton,WhoseTurnComputer, WhoseTurnPlayer;
	Texture Rules, notSupported,tut1,tut2,tut3,tut4;
	Texture very_easyTex, easyTex, normalTex, expertTex;
	//
	@Override
	public void create () {
		boardSizeX = 9; // Ustaw wielkosc logicznej planszy do gry
		boardSizeY = 7;
		graphDepth = 0; // domyslna glebokosc przeszukiwania
		bgColor1 = Color.SKY;
		bgColor2 = Color.SKY;
		bgColor3 = Color.YELLOW;
		bgColor4 = Color.YELLOW;

		if(Gdx.graphics.getWidth() <1300) // ustaw tryb wyswietlania w zaleznosci od rozdzielczosci
			sizeMode = SizeMode.SMALL;
		else
			sizeMode = SizeMode.LARGE;

		whoWins = 0; // random
		Game = new connect4(boardSizeX,boardSizeY); // nowa gra
		gameState = GameState.GAME_WHO_STARTS; // inicjalizacja stanu gry
		gameProcess = GameProcess.WAITING_FOR_PLAYER_ACTION; // inicjalizacja procesu gry
		previousGameProcess = GameProcess.BALL_DROPDOWN; //  --

		GameSearchConfigurator configurator = new GameSearchConfigurator(); // konfiguracja glebokosci grafu
		configurator.setDepthLimit(graphDepth);
		ocenaSytuacji = new MyHeuristic(); // nowa heurystyka
		connect4.setHFunction(ocenaSytuacji); // ustawienie heurystyki dla SaC
		algorytm = new AlphaBetaPruning(Game, configurator); // przeslanie parametrow algorytmowi

		touchArea = new onClickArea[boardSizeX]; // Utworz boardSizeX okienek klikalnych
		gameBoard = new int[boardSizeX][boardSizeY]; // Generuj plansze do gry o zadanej wielkosci
		fillWithZeros(); // Wypelnij plansze gameBoard zerami (nie ma w niej pilek)

		playArea = new float[2]; // wspolrzedne X (0) i Y (1) dla rysowanego kwadratu oznaczajacego pole gry
		playArea[0] = Gdx.graphics.getWidth()*(float)0.7; // Oblicz, gdzie sie zaczyna pole do gry
		playArea[1] = Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/7; // Oblicz gdzie sie zaczyna w osi Y
		singleCellWidth = playArea[0] / boardSizeX;
		singleCellHeight = playArea[1] / boardSizeY;
		separator = (int)singleCellWidth ; // odstep miedzy pionowymi kreskami na planszy

		setUpTouchArea(); // Stworz obszary w ktore bedzie mozna klikac na ekranie

		balls = new ArrayList<gameItem>(); // Stworz pusty zbior kulek do gry
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sr = new ShapeRenderer() ;

		/// Bitmapy

		if(sizeMode == SizeMode.LARGE){
			WhoseTurnComputer = new Texture("whoseTurnComputer.png");
			WhoseTurnPlayer = new Texture("whoseTurnPlayer.png");
			tut1 = new Texture("tut1.png");
			tut2 = new Texture("tut2.png");
			tut3 = new Texture("tut3.png");
			tut4 = new Texture("tut4.png");
			very_easyTex = new Texture("very_easy.png");
			easyTex = new Texture("easy.png");
			normalTex = new Texture("normal.png");
			expertTex = new Texture("expert.png");
			YouStartButton = new Texture("youStart.png");
			ComputerStartsButton = new Texture("computerStarts.png");
			Rules = new Texture("rules.png");
		}else if(sizeMode == SizeMode.SMALL){
			WhoseTurnComputer = new Texture("whoseTurnComputerS.png");
			WhoseTurnPlayer = new Texture("whoseTurnPlayerS.png");
			tut1 = new Texture("tut1S.png");
			tut2 = new Texture("tut2S.png");
			tut3 = new Texture("tut3S.png");
			tut4 = new Texture("tut4S.png");
			very_easyTex = new Texture("very_easyS.png");
			easyTex = new Texture("easyS.png");
			normalTex = new Texture("normalS.png");
			expertTex = new Texture("expertS.png");
			YouStartButton = new Texture("youStartS.png");
			ComputerStartsButton = new Texture("computerStartsS.png");
			Rules = new Texture("rulesS.png");
		}
		batch = new SpriteBatch();
		PlayAgainButton = new Texture("playAgain.png");
		YouWonButton = new Texture("youWon.png");
		ComputerWonButton = new Texture("computerWon.png");
		notSupported = new Texture("notSupported.png");
		//

	}

	@Override
	public void render () { // pętla gry

		//ROZGRYWKA SIĘ TOCZY
		if (gameState == GameState.GAME_BEGAN) { // stan rozpoczetej gry
			cam.update();
			Gdx.gl.glClearColor(0, (float)0.4, (float)0.8, 1); // ustawienia rendera
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sr.begin(ShapeRenderer.ShapeType.Filled);
			sr.set(ShapeRenderer.ShapeType.Filled);
			sr.rect(
					0,
					0,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(),
					bgColor1,
					bgColor2,
					bgColor3,
					bgColor4
			);

			sr.setColor(Color.BROWN);
			// Rozdziel (graficznie) obszar gry liniami
			int separateLength = -(int)(playArea[0]*(float)0.01); /// Rysowanie pionowych linii planszy o rozstrzale separateLength
			for (int i = 0; i < boardSizeX+1; i++) {
				sr.rectLine(new Vector2(Gdx.graphics.getWidth() - playArea[0] + 10 + separateLength, 0), new Vector2(Gdx.graphics.getWidth() - playArea[0] + 10 + separateLength, playArea[1]), singleCellWidth*(float)0.05);
				separateLength += separator;
			}
			float c; // zmienna pomocnicza, zaleznosc dla wielkosci kulek
			if (singleCellHeight < singleCellWidth) c = singleCellHeight;
			else c = singleCellWidth;

			// Rysuj wszystkie pilki na planszy. Ich wspolrzedne sa w wektorze balls
			for (int i = 0; i < balls.size(); i++) {
				sr.setColor(balls.get(i).myColor); // Ustaw kolor tej pilki;
				sr.circle(balls.get(i).x, balls.get(i).y, c/2);
			}

			// Kiedy nie mozna kliknac, bo dodano nowa pilke i jeszcze spada
			if (gameProcess == GameProcess.WAITING_FOR_PLAYER_ACTION ) {
				if (Gdx.input.justTouched()) { // Czy uzytkownik kliknal
					int a; // zmienne pomocnicze - odpowiednik x,y
					int b;
					// sprawdz czy kliknieto w dozwolony obszar. Jesli tak, to do zmiennej 'a' wpisz którą kolumne wybrano
					if ((a = getClickedAreaID(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) != -1) {
						b = getFirstEmptyFromColumn(a); // Uzyskaj pierwszy wolny wiersz w kolumnie 'a'
						if (b != -5) { // Jesli w tej kolumnie mozna jeszcze dodac kulke
							Game.makeMove(a);
							getBallXY(a, b); // Ustaw docelowe wspolrzedne logiczne kulki na komorke a,b
							getNewBall(a); // Wstaw (dodaj) nowa kulke do koumny 'a'
							gameBoard[a][b] = 1; // Zmien wartosc w logicznej tablicy gry, ze w komorce a,b jest juz pilka
							previousGameProcess = gameProcess;
							gameProcess = GameProcess.BALL_DROPDOWN; // Niech pilka spada
						}
					}
				}
			}else if (gameProcess == GameProcess.BALL_DROPDOWN) {
				int falses = 0; // Zlicz, czy ktoras pilka jeszcze spada
				for (int i = 0; i < balls.size(); i++) {
					if (!balls.get(i).fallDown()) { // Wykonaj na kazdej pilce funkcje fallDown,
						// jesli zwroci falsz, oznacza ze jeszcze spada
						falses += 1;
					}
				}
				// Jesli zadna juz nie spada - pozwol na ruch uzytkownikowi
				if (falses == 0) {
					if (previousGameProcess == GameProcess.WAITING_FOR_PLAYER_ACTION && Game.hasPlayerWon() == 0) {
						previousGameProcess = gameProcess;
						gameProcess = GameProcess.WAITING_FOR_COMPUTER_ACTION;
					} else if (previousGameProcess == GameProcess.WAITING_FOR_COMPUTER_ACTION && Game.hasPlayerWon() == 0) {
						previousGameProcess = gameProcess;
						gameProcess = GameProcess.WAITING_FOR_PLAYER_ACTION;
					}
					if (Game.hasPlayerWon() != 0) { // sprawdz czy ktos wygral , jesli tak - przejdz do stanu gry FREEZE
						whoWins = Game.hasPlayerWon();
						gameState = GameState.FREEZE;
					}
				}
			}else if (gameProcess == GameProcess.WAITING_FOR_COMPUTER_ACTION ) { // kolej komputera
				algorytm.execute(); // wykonaj algorytm gry
				String Computer = algorytm.getFirstBestMove(); // pobierz najlepszy ruch
				int ComputerMove = Integer.valueOf(Computer);
				Game.makeMove(ComputerMove); // wykonaj ruch
				int b = getFirstEmptyFromColumn(ComputerMove); // Uzyskaj pierwszy wolny wiersz w kolumnie 'a'

				getBallXY(ComputerMove, b); // Ustaw docelowe wspolrzedne logiczne kulki na komorke a,b
				getNewBall(ComputerMove); // Wstaw (dodaj) nowa kulke do koumny 'a'
				gameBoard[ComputerMove][b] = 1; // Zmien wartosc w logicznej tablicy gry, ze w komorce a,b jest juz pilka

				previousGameProcess = gameProcess; // Uzytkownik nie moze teraz kliknac (dopoki nie spadnie dodana kulka)
				gameProcess = GameProcess.BALL_DROPDOWN;

			}

			if (whoWins !=0 && whoWins == whatNumberPlayerIs ) { // jesli wygral gracz
				batch.begin();
				batch.draw(YouWonButton, Gdx.graphics.getWidth()/2-168, Gdx.graphics.getHeight() - 100);
				batch.end();

			} else if (whoWins != 0  && whoWins != whatNumberPlayerIs ) { // jesli wygral komputer
				batch.begin();
				batch.draw(ComputerWonButton, Gdx.graphics.getWidth()/2-282, Gdx.graphics.getHeight() -100);
				batch.end();

			}

			sr.end(); // koniec rendera

			//rysowanie bitmap
			if (gameProcess == GameProcess.WAITING_FOR_COMPUTER_ACTION || previousGameProcess == GameProcess.WAITING_FOR_COMPUTER_ACTION) {
				batch.begin();
				batch.draw(WhoseTurnComputer, 10, Gdx.graphics.getHeight()/4 * 3 );
				batch.end();
			} else if (gameProcess == GameProcess.WAITING_FOR_PLAYER_ACTION || previousGameProcess == GameProcess.WAITING_FOR_PLAYER_ACTION) {
				batch.begin();
				batch.draw(WhoseTurnPlayer, 10, Gdx.graphics.getHeight()/4 * 3);
				batch.end();
			}

		}else if(gameState == GameState.GAME_WHO_STARTS){ // stan gry - wybierz kto zaczyna
			cam.update();
			sr.begin(ShapeRenderer.ShapeType.Filled); // render
			Gdx.gl.glClearColor((float)0.3, (float)0.5, (float)0.7, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sr.set(ShapeRenderer.ShapeType.Filled);
			sr.rect(
					0,
					0,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(),
					bgColor1,
					bgColor2,
					bgColor3,
					bgColor4
			);
			if (Gdx.input.justTouched()) { // czytaj dotyk
				int a;
				// ustaw gameProcess w zaleznosci, co wybral gracz i przejdz do wyboru poziomu
				if ((a = getClickedAreaID(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) != -1) {
					if(a == -20 ) { //Player_Start
						gameProcess = GameProcess.WAITING_FOR_PLAYER_ACTION;
						whatNumberPlayerIs = 1;
						gameState = GameState.GAME_LEVEL_SETING;

					}else if( a == -21){
						gameProcess = GameProcess.WAITING_FOR_COMPUTER_ACTION;
						whatNumberPlayerIs = 2;
						gameState = GameState.GAME_LEVEL_SETING;
					}
				}
			}
			sr.end();
			// rysowanie bitmap
			int resolutionResizer = 0 ;
			if(sizeMode == SizeMode.LARGE )	 resolutionResizer = 500;
			else if(sizeMode == SizeMode.SMALL)  resolutionResizer = 250;
			if(sizeMode == SizeMode.LARGE ) {
				batch.begin();
				batch.draw(YouStartButton, 30, 30);
				batch.end();

				batch.begin();
				batch.draw(ComputerStartsButton, Gdx.graphics.getWidth() - 682, 30);
				batch.end();

				batch.begin();
				batch.draw(Rules, Gdx.graphics.getWidth() / 2 - 116, Gdx.graphics.getHeight() - 100);
				batch.end();
			}else if(sizeMode == SizeMode.SMALL ){
				batch.begin();
				batch.draw(YouStartButton, 30, 30);
				batch.end();

				batch.begin();
				batch.draw(ComputerStartsButton, Gdx.graphics.getWidth() - 330, 30);
				batch.end();

				batch.begin();
				batch.draw(Rules, Gdx.graphics.getWidth() / 2 - 58, Gdx.graphics.getHeight() - 70);
				batch.end();
			}
			batch.begin();
			batch.draw(tut1, Gdx.graphics.getWidth()/2 - resolutionResizer*(float)1.55, Gdx.graphics.getHeight()- Gdx.graphics.getHeight()*(float)0.5);
			batch.end();
			batch.begin();
			batch.draw(tut2, Gdx.graphics.getWidth()/2 - resolutionResizer*(float)0.52, Gdx.graphics.getHeight()- Gdx.graphics.getHeight()*(float)0.5);
			batch.end();
			batch.begin();
			batch.draw(tut3, Gdx.graphics.getWidth()/2 + resolutionResizer*(float)0.52, Gdx.graphics.getHeight()- Gdx.graphics.getHeight()*(float)0.5);
			batch.end();
			batch.begin();
			batch.draw(tut4, Gdx.graphics.getWidth()/2 - resolutionResizer*(float)0.52, Gdx.graphics.getHeight()- Gdx.graphics.getHeight()*(float)0.87);
			batch.end();
		}else if(gameState == GameState.FREEZE){ // zamrozona gra po zakonczeniu rozgrywki
			cam.update(); // render
			if(whoWins == whatNumberPlayerIs)
				Gdx.gl.glClearColor(0, (float)0.9, (float)0.4, 1);
			else
				Gdx.gl.glClearColor((float)0.6, (float)0.1, (float)0.1, 1);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			sr.begin(ShapeRenderer.ShapeType.Filled);

			if (Gdx.input.isTouched()) { // czytaj dotyk - czy grac ponownie
				int a;
				if ((a = getClickedAreaID(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) != -1) {
					if (a == -10) { //PlayAgain
						gameState = GameState.GAME_WHO_STARTS; // przejdz do stanu wyboru kto zaczyna
					}
				}
			}

			sr.setColor(Color.GRAY);
			// Rozdziel (graficznie) obszar gry liniami
			int separateLength = -(int)(playArea[0]*(float)0.01); /// Rysowanie pionowych linii planszy o rozstrzale separateLength
			for (int i = 0; i < boardSizeX+1; i++) {
				sr.rectLine(new Vector2(Gdx.graphics.getWidth() - playArea[0] + 10 + separateLength, 0), new Vector2(Gdx.graphics.getWidth() - playArea[0] + 10 + separateLength, playArea[1]), singleCellWidth*(float)0.05);
				separateLength += separator;
			}


			float c; // tak jak w funkcji rozgrywki
			if (singleCellHeight < singleCellWidth) c = singleCellHeight;
			else c = singleCellWidth;

			for (int i = 0; i < balls.size(); i++) {
				sr.setColor(balls.get(i).myColor); // Ustaw kolor tej pilki;

				sr.circle(balls.get(i).x, balls.get(i).y, c/2);
			}

			sr.end();
			// rysowanie bitmap
			if (whoWins !=0 && whoWins == whatNumberPlayerIs ) {
				batch.begin();
				batch.draw(YouWonButton, Gdx.graphics.getWidth()/2-168, Gdx.graphics.getHeight() - 100);
				batch.end();

			} else if (whoWins != 0  && whoWins != whatNumberPlayerIs ) {
				batch.begin();
				batch.draw(ComputerWonButton, Gdx.graphics.getWidth()/2-282, Gdx.graphics.getHeight() -100);
				batch.end();

			}
			batch.begin();
			batch.draw(PlayAgainButton, Gdx.graphics.getWidth() / 2 - 232, Gdx.graphics.getHeight() / 2);
			batch.end();
		}else if(gameState == GameState.GAME_LEVEL_SETING){ // wybor poziomu trudnosci
			cam.update(); // render
			sr.begin(ShapeRenderer.ShapeType.Filled);
			Gdx.gl.glClearColor((float) 0.1, 1, (float) 0.3, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sr.set(ShapeRenderer.ShapeType.Filled);
			sr.rect(
					0,
					0,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(),
					bgColor1,
					bgColor2,
					bgColor3,
					bgColor4
			);
			if (Gdx.input.justTouched()) { // czytaj dotyk
				int a;
				if ((a = getClickedAreaID(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) != -1) {
					if (a == -30) { // ustaw glebokosc przeszukiwania grafu w zaleznosci co wybral gracz
						graphDepth = 0.5;
					}
					if (a == -31){
						graphDepth = 2;
					}
					if (a == -32){
						graphDepth = 3;
					}
					if (a == -33){
						graphDepth =3.5 ;
					}

					// Nadpisz obiekt Game nowa grą , wybierz algorytm i heurystykę ...
					Game = new connect4(boardSizeX,boardSizeY);
					GameSearchConfigurator configurator = new GameSearchConfigurator();
					configurator.setDepthLimit(graphDepth);
					ocenaSytuacji = new MyHeuristic();
					connect4.setHFunction(ocenaSytuacji);
					algorytm = new AlphaBetaPruning(Game, configurator);
					whoWins = 0;

					// wyczysc wektor pilek i wypelnij zerami tablice logiczna gry
					balls.clear();
					fillWithZeros();

					gameState = GameState.GAME_BEGAN;

				}
			}
				sr.end();
			if(sizeMode == SizeMode.SMALL) {
				batch.begin();
				batch.draw(very_easyTex, Gdx.graphics.getWidth() / 2 - 110, Gdx.graphics.getHeight() - 100);
				batch.end();

				batch.begin();
				batch.draw(easyTex, Gdx.graphics.getWidth() / 2 - 62, Gdx.graphics.getHeight() - 200);
				batch.end();

				batch.begin();
				batch.draw(normalTex, Gdx.graphics.getWidth() / 2 - 95, Gdx.graphics.getHeight() - 300);
				batch.end();

				batch.begin();
				batch.draw(expertTex, Gdx.graphics.getWidth() / 2 - 80, Gdx.graphics.getHeight() - 400);
				batch.end();
			}else if(sizeMode == SizeMode.LARGE){
				batch.begin();
				batch.draw(very_easyTex, Gdx.graphics.getWidth() / 2 - 176, Gdx.graphics.getHeight() - 150);
				batch.end();

				batch.begin();
				batch.draw(easyTex, Gdx.graphics.getWidth() / 2 - 91, Gdx.graphics.getHeight() - 300);
				batch.end();

				batch.begin();
				batch.draw(normalTex, Gdx.graphics.getWidth() / 2 - 145, Gdx.graphics.getHeight() - 450);
				batch.end();

				batch.begin();
				batch.draw(expertTex, Gdx.graphics.getWidth() / 2 - 124, Gdx.graphics.getHeight() - 600);
				batch.end();
			}
		}
	}

	@Override
	public void dispose () { // czyszczenie smieci w bufforze rendera
		sr.dispose();

		if(gameState == GameState.GAME_BEGAN){
			WhoseTurnComputer.dispose();
			WhoseTurnPlayer.dispose();
			ComputerWonButton.dispose();
			YouWonButton.dispose();
		}else if(gameState == GameState.GAME_WHO_STARTS){
			Rules.dispose();
			ComputerStartsButton.dispose();
			YouStartButton.dispose();
			tut1.dispose();
			tut2.dispose();
			tut3.dispose();
			tut4.dispose();
		}else if(gameState == GameState.FREEZE){
			PlayAgainButton.dispose();
			ComputerWonButton.dispose();
			YouWonButton.dispose();
		}

	}

	//Wypelnij plansze gry zerami
	public void fillWithZeros(){
		for(int i = 0; i< boardSizeX; i ++){
			for(int j=0; j<boardSizeY; j++){
				gameBoard[i][j] = 0;
			}
		}
	}

	// Stworz obszary 'klikalne' na ekranie
	public void setUpTouchArea(){
		int separateLength = 5 ; // ile jest kolumn w ktore mozna bedzie kliknac
		for(int i = 0; i < boardSizeX; i++){
			// Dla kazdej klikalnej strefy, oblicz jej wielkosc
			touchArea[i] = new onClickArea(Gdx.graphics.getWidth() - playArea[0] + separateLength,
					0, separator+10,
					Gdx.graphics.getHeight());

			separateLength += separator; // Zwieksz odstep o pojedynczy odstep
		}


		Play_Again = new onClickArea(Gdx.graphics.getWidth()/2 - 232, Gdx.graphics.getHeight()/2-47,
				465,95);


		if(sizeMode == SizeMode.SMALL) {
			veryEasy = new onClickArea(Gdx.graphics.getWidth() / 2 - 110, Gdx.graphics.getHeight() - 100,
					220, 60);
			easy = new onClickArea(Gdx.graphics.getWidth() / 2 - 62, Gdx.graphics.getHeight() - 200,
					120, 60);
			normal = new onClickArea(Gdx.graphics.getWidth() / 2 - 95, Gdx.graphics.getHeight() - 300,
					190, 60);
			expert = new onClickArea(Gdx.graphics.getWidth() / 2 - 80, Gdx.graphics.getHeight() - 400,
					160, 60);
			Player_Start = new onClickArea(30, 30,181,51);
			Computer_Start = new onClickArea(Gdx.graphics.getWidth() - 330, 30,300,51);
		}else if(sizeMode == SizeMode.LARGE){
			veryEasy = new onClickArea(Gdx.graphics.getWidth() / 2 - 176, Gdx.graphics.getHeight() - 150,
					352, 81);
			easy = new onClickArea(Gdx.graphics.getWidth() / 2 - 91, Gdx.graphics.getHeight() - 300,
					183, 81);
			normal = new onClickArea(Gdx.graphics.getWidth() / 2 - 145, Gdx.graphics.getHeight() - 450,
					289, 81);
			expert = new onClickArea(Gdx.graphics.getWidth() / 2 - 124, Gdx.graphics.getHeight() - 600,
					248, 80);
			Player_Start = new onClickArea(30, 30,380,84);
			Computer_Start = new onClickArea(Gdx.graphics.getWidth() - 682, 30,652,85);
		}



	}

	// Sprawdz, w ID ktorej klikalnej strefy uzytkownik kliknal i zwroc to ID
	private int getClickedAreaID(float x, float y){ // x,y to wspolrzedne punktu dotkniecia
			// Sprawdz, czy punkt (x,y) znajduje sie w ktorejs z dozwolonych stref klikniecia

			if(gameState == GameState.GAME_BEGAN) {
				for(int i =0; i <boardSizeX; i ++ ) {
					if (x >= touchArea[i].x && x <= touchArea[i].x + touchArea[i].width // Jesli trafiles w to pole
							&& y >= touchArea[i].y && y <= touchArea[i].y + touchArea[i].height) {
						return i; // Jesli trafiles w pole, zwroc ID tego pola
					}
				}
			}else if(gameState == GameState.FREEZE){
				if (x >= Play_Again.x && x <= Play_Again.x + Play_Again.width // Jesli trafiles w to pole
						&& y >= Play_Again.y && y <= Play_Again.y + Play_Again.height) {
					return -10; // Play_Again
				}

			}else if(gameState == GameState.GAME_WHO_STARTS){
				if (x >= Player_Start.x && x <= Player_Start.x + Player_Start.width // Jesli trafiles w to pole
						&& y >= Player_Start.y && y <= Player_Start.y + Player_Start.height) {
					return -20; // Player_Start
				}

				if (x >= Computer_Start.x && x <= Computer_Start.x + Computer_Start.width // Jesli trafiles w to pole
						&& y >= Computer_Start.y && y <= Computer_Start.y + Computer_Start.height) {
					return -21; // Player_Start
				}
			}else if(gameState == GameState.GAME_LEVEL_SETING){
				if (x >= veryEasy.x && x <= veryEasy.x + veryEasy.width // veryeasy
						&& y >= veryEasy.y && y <= veryEasy.y + veryEasy.height) {
					return -30; // Player_Start
				}
				if (x >= easy.x && x <= easy.x + easy.width // veryeasy
						&& y >= easy.y && y <= easy.y + easy.height) {
					return -31; // Player_Start
				}
				if (x >= normal.x && x <= normal.x + normal.width // veryeasy
						&& y >= normal.y && y <= normal.y + normal.height) {
					return -32; // Player_Start
				}
				if (x >= expert.x && x <= expert.x + expert.width // veryeasy
						&& y >= expert.y && y <= expert.y + expert.height) {
					return -33; // Player_Start
				}


			}



		// Jesli punkt nie lezy w zadnym z pol, zwroc blad (-1)
		return -1;
	}

	// Wstaw nowa pilke (Graficznie) do planszy, do kolumny o indeksie 'k'
	public void getNewBall(int k){
		Color color = Color.RED;
		if(gameProcess == GameProcess.WAITING_FOR_PLAYER_ACTION)
			color = Color.PURPLE;
		if(gameProcess == GameProcess.WAITING_FOR_COMPUTER_ACTION)
			color = Color.LIME;

		balls.add(new gameItem(k, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getWidth()- playArea[0], separator, ballX, ballY, color));
	}


	// Oblicz, na podstawie logicznych wspolrzednych planszy do gry (x,y)
	// ,do jakiego miejsca (graficznie) pilka powinna doleciec, kiedy spada i wpisz
	// te wartosci do zmiennych klasy ballX i ballY
	public void getBallXY(int col, int row){
		row+=1 ;
		ballX = playArea[0] +  col * singleCellWidth/2;
		float c;
		if (singleCellHeight < singleCellWidth) c = singleCellHeight;
		else c = singleCellWidth;

		ballY = (playArea[1]-c/3) - row * (singleCellHeight)+(singleCellHeight);
	}

	// Sprawdź, do którego wiersza w kolumnie 'e' mozna teraz wpisac nowa pilke
	public int getFirstEmptyFromColumn(int e){
		for(int i = boardSizeY-1; i >= 0 ; i--){ // Sprawdz od dolu cala plansze logiczna gry
			if(gameBoard[e][i] == 0 ) // Jesli w kolumnie 'e', ktorys wiersz jest wolny (0)
				return i; // zwroc indeks tego wiersza
		}
		return -5; // Jesli zaden wiersz nie jest w tej kolumnie wolny, zwroc blad (-5)
	}
}
