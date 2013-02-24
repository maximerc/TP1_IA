/*
 * IFT615 - TP1
 * Session d'hiver 2009
 *
 * Université de Sherbrooke
 * Département d'informatique
 */

package connect5;

/**
 *
 * @author Eric Beaudry
 */
public interface GameObserver {

    public void grilleChanged(Grille g);
    
    public void message(String msg);
    
}
