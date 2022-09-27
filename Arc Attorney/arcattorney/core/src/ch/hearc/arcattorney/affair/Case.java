package ch.hearc.arcattorney.affair;

public class Case {

	private String imagePath;
	private String description;
	private String recap;
	private int index;

	public Case(String imagePath, String description, String recap, int index) {
		this.imagePath = imagePath;
		this.description = description;
		this.recap = recap;
		this.index = index;
	}

	public String getRecap() {
		return this.recap;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public String getDescription() {
		return this.description;
	}

	public int getIndex() {
		return this.index;
	}
}
