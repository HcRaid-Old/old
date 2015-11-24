package core.hcrg.bob;

public class BobPlayer {
	private String name;
	private int lives;

	public BobPlayer(String name, int lives) {
		this.name = name;
		this.lives = lives;
	}

	public void playerDied() {
		lives--;
	}

	public int getLivesRemaining() {
		return lives;
	}

	public boolean isOut() {
		return lives == 0;
	}

	public String getName() {
		return name;
	}

}
