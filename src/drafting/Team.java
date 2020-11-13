package drafting;

public class Team {

	private Side side; // represents the side
	private String firstHero; // represents the first hero picked
	private String secondHero; // represents the second hero picked
	private String thirdHero; // represents the third hero picked
	private String fourthHero; // represents the fourth hero picked
	private String fifthHero; // represents the fifth hero picked

	/**
	 * Default constructor
	 */
	public Team() {

	}

	/**
	 * @param side       the team's side (Radiant or Dire)
	 * @param firstHero  the first hero picked by the team
	 * @param secondHero the second hero picked by the team
	 * @param thirdHero  the third hero picked by the team
	 * @param fourthHero the fourth hero picked by the team
	 * @param fifthHero  the fifth hero picked by the team
	 */
	public Team(Side side, String firstHero, String secondHero, String thirdHero, String fourthHero, String fifthHero) {
		this.side = side;
		this.firstHero = firstHero;
		this.secondHero = secondHero;
		this.thirdHero = thirdHero;
		this.fourthHero = fourthHero;
		this.fifthHero = fifthHero;
	}

	/**
	 * Sets the side of the team
	 * 
	 * @param side, the side to set
	 */
	public void setSide(Side side) {
		this.side = side;
	}

	/**
	 * Returns the side @return, the side
	 */
	public Side getSide() {
		return this.side;
	}

	/**
	 * @return the firstHero
	 */
	public String getFirstHero() {
		return firstHero;
	}

	/**
	 * @param firstHero the firstHero to set
	 */
	public void setFirstHero(String firstHero) {
		this.firstHero = firstHero;
	}

	/**
	 * @return the secondHero
	 */
	public String getSecondHero() {
		return secondHero;
	}

	/**
	 * @param secondHero the secondHero to set
	 */
	public void setSecondHero(String secondHero) {
		this.secondHero = secondHero;
	}

	/**
	 * @return the thirdHero
	 */
	public String getThirdHero() {
		return thirdHero;
	}

	/**
	 * @param thirdHero the thirdHero to set
	 */
	public void setThirdHero(String thirdHero) {
		this.thirdHero = thirdHero;
	}

	/**
	 * @return the fourthHero
	 */
	public String getFourthHero() {
		return fourthHero;
	}

	/**
	 * @param fourthHero the fourthHero to set
	 */
	public void setFourthHero(String fourthHero) {
		this.fourthHero = fourthHero;
	}

	/**
	 * @return the fifthHero
	 */
	public String getFifthHero() {
		return fifthHero;
	}

	/**
	 * @param fifthHero the fifthHero to set
	 */
	public void setFifthHero(String fifthHero) {
		this.fifthHero = fifthHero;
	}

	public void printHeroes() {
		System.out.println("The heroes on " + this.side + " are: " + this.firstHero + ", " + this.secondHero + ", "
				+ this.thirdHero + ", " + this.fourthHero + ", and " + this.fifthHero +".");
	}

} // end of class
