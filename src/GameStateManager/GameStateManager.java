package GameStateManager;

public class GameStateManager {
	private GameState currentState;
	public GameState requestedState;
	public boolean stateChangeRequested;
	
	public GameStateManager() {
		
	}
	
	public void requestStateChange(GameState gs) {
		requestedState = gs;
		stateChangeRequested = true;
		
	}
	
	public void setState(GameState gs) {
		currentState = gs;
		currentState.initialize(this);
		stateChangeRequested = false;		
	}
	
	public GameState getGameState() {
		return currentState;
	}
	
	public boolean stateChangeRequested() {
		return stateChangeRequested;
	}
}
