package net.todd.scorekeeper;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.UUID;

import net.todd.scorekeeper.data.Player;
import net.todd.scorekeeper.data.ScoreBoard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GamePresenterTest {
	@Mock
	private GameView view;
	@Mock
	private GameModel model;
	private Listener nextPlayerButtonListener;
	private Listener previousPlayerButtonListener;
	private Listener backButtonListener;
	private Listener cancelGameListener;
	private Listener scoreChangedListener;
	private Listener playerChangedListener;
	private Listener gameOverButtonListener;
	private Listener gameOverConfirmationListener;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		GamePresenter.create(view, model);

		ArgumentCaptor<Listener> nextPlayerButtonListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(view).addNextPlayerButtonListener(nextPlayerButtonListenerCaptor.capture());
		nextPlayerButtonListener = nextPlayerButtonListenerCaptor.getValue();

		ArgumentCaptor<Listener> previousPlayerButtonListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(view).addPreviousPlayerButtonListener(previousPlayerButtonListenerCaptor.capture());
		previousPlayerButtonListener = previousPlayerButtonListenerCaptor.getValue();

		ArgumentCaptor<Listener> backButtonListenerCaptor = ArgumentCaptor.forClass(Listener.class);
		verify(view).addBackPressedListener(backButtonListenerCaptor.capture());
		backButtonListener = backButtonListenerCaptor.getValue();

		ArgumentCaptor<Listener> cancelGameListenerCaptor = ArgumentCaptor.forClass(Listener.class);
		verify(view).addCancelGameListener(cancelGameListenerCaptor.capture());
		cancelGameListener = cancelGameListenerCaptor.getValue();

		ArgumentCaptor<Listener> scoreChangedListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(model).addScoreChangedListener(scoreChangedListenerCaptor.capture());
		scoreChangedListener = scoreChangedListenerCaptor.getValue();

		ArgumentCaptor<Listener> playerChangedListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(model).addPlayerChangedListener(playerChangedListenerCaptor.capture());
		playerChangedListener = playerChangedListenerCaptor.getValue();

		ArgumentCaptor<Listener> gameOverButtonListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(view).addGameOverButtonListener(gameOverButtonListenerCaptor.capture());
		gameOverButtonListener = gameOverButtonListenerCaptor.getValue();

		ArgumentCaptor<Listener> gameOverConfirmationListenerCaptor = ArgumentCaptor
				.forClass(Listener.class);
		verify(view).addGameOverConfirmationListener(gameOverConfirmationListenerCaptor.capture());
		gameOverConfirmationListener = gameOverConfirmationListenerCaptor.getValue();

		reset(view, model);
	}

	@Test
	public void currentPlayerIsSetInitially() {
		Player player = mock(Player.class);
		doReturn(player).when(model).getCurrentPlayer();

		GamePresenter.create(view, model);

		verify(view).setCurrentPlayer(player);
	}

	@Test
	public void currentPlayersScoreIsSetInitially() {
		int score = new Random().nextInt();
		doReturn(score).when(model).getCurrentPlayersScore();

		GamePresenter.create(view, model);

		verify(view).setCurrentPlayersScore(score);
	}

	@Test
	public void gameNameIsSetInitially() {
		String gameName = UUID.randomUUID().toString();
		doReturn(gameName).when(model).getGameName();

		GamePresenter.create(view, model);

		verify(view).setGameName(gameName);
	}

	@Test
	public void scoreBoardIsSetInitiallyFromModel() {
		ScoreBoard scoreBoard = mock(ScoreBoard.class);
		doReturn(scoreBoard).when(model).getScoreBoard();

		GamePresenter.create(view, model);

		verify(view).setScoreBoard(scoreBoard);
	}

	@Test
	public void currentPlayersScoreIsCollectedWhenNextPlayerButtonIsPressed() {
		int score = new Random().nextInt();
		doReturn(score).when(view).getScore();

		nextPlayerButtonListener.handle();

		verify(model).setScoreForCurrentPlayer(score);
	}

	@Test
	public void whenNextPlayerButtonIsPressedSetScoreForCurrentPlayerAndGoToNextPlayer() {
		nextPlayerButtonListener.handle();

		InOrder inOrder = inOrder(model);
		inOrder.verify(model).setScoreForCurrentPlayer(anyInt());
		inOrder.verify(model).nextPlayer();
	}

	@Test
	public void clearScoreWhenPreviousPlayerButtonIsPressed() {
		previousPlayerButtonListener.handle();

		verify(model).previousPlayer();
	}

	@Test
	public void pressingTheBackButtonPopsUpTheBackButtonDialog() {
		backButtonListener.handle();

		verify(view).popupNoBackButtonDialog();
	}

	@Test
	public void killingTheGameNotifiesTheModelThatTheGameIsOver() {
		cancelGameListener.handle();

		verify(model).cancelGame();
	}

	@Test
	public void whenTheScoreChangesOnTheModelThenClearTheScore() {
		scoreChangedListener.handle();

		verify(view).clearScore();
	}

	@Test
	public void whenTheScoreChangesOnTheModelThenClosetheSoftKeyboard() {
		scoreChangedListener.handle();

		verify(view).closeSoftKeyboard();
	}

	@Test
	public void whenTheScoreChangesOnTheModelThenSetTheScoreBoard() {
		ScoreBoard scoreBoard = mock(ScoreBoard.class);
		doReturn(scoreBoard).when(model).getScoreBoard();

		scoreChangedListener.handle();

		verify(view).setScoreBoard(scoreBoard);
	}

	@Test
	public void whenThePlayerChangesOnTheModelThenSetTheCurrentPlayerOnTheView() {
		Player player = mock(Player.class);
		doReturn(player).when(model).getCurrentPlayer();

		playerChangedListener.handle();

		verify(view).setCurrentPlayer(player);
	}

	@Test
	public void whenThePlayerChangesOnTheModelThenSetTheCurrentScoreOnTheView() {
		int score = new Random().nextInt();
		doReturn(score).when(model).getCurrentPlayersScore();

		playerChangedListener.handle();

		verify(view).setCurrentPlayersScore(score);
	}

	@Test
	public void whenGameOverButtonIsPressedThenPopupGameOverConfirmation() {
		gameOverButtonListener.handle();

		verify(view).popupGameOverConfirmation();
	}

	@Test
	public void whenGameOverIsConfirmedTheGameIsOver() {
		gameOverConfirmationListener.handle();

		verify(model).gameOver();
	}
}
