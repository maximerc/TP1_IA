package ift615tp1;

public class Ligne {

    private int[] debut; // [l,c]
    private int[] fin;   // [l,c]
	private int nbOccurences;

    public Ligne(int l1, int c1, int l2, int c2) {
        debut = new int[]{l1, c1};
        fin = new int[]{l2, c2};
		nbOccurences = 1;
    }
	
	public int getNbOccurences() {
		return nbOccurences;
	}
	
	public void setNbOccurences(int nbOcc) {
		nbOccurences = nbOcc;
	}
}