package ift615tp1;

import connect5.Grille;
import java.util.List;

public class Ligne {
    private int[] debut; // [l,c]
    private int[] fin;   // [l,c]
    
    public Ligne(int l1, int c1, int l2, int c2) {
        debut = new int[] {l1,c1};
        fin   = new int[] {l2,c2};
    }
}