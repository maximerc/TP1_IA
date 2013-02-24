/*
 * IFT615 - TP1
 * Session d'hiver 2009
 *
 * Université de Sherbrooke
 * Département d'informatique
 */

package connect5;

import java.util.EventListener;

/**
 *
 * @author Eric Beaudry
 */
public interface GrilleDisplayListener extends EventListener {

    public void caseClicked(int ligne, int colonne);

}
