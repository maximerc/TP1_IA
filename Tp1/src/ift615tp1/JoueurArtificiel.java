package ift615tp1;

/*
 * Si vous utilisez Java, vous devez modifier ce fichier-ci.
 *
 * Vous pouvez ajouter d'autres classes sous le package ift615tp1.
 * 
 *
 * Prénom Nom    (00 000 000)
 * Prénom Nom    (00 000 000)
 */

import connect5.Grille;
import connect5.Joueur;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Vos noms et matricules
 */
public class JoueurArtificiel implements Joueur {

    private Random random = new Random();

    public int[] getProchainCoup(Grille g, int delais) {
        Vector<Integer> vides = new Vector<Integer>();
        int nbcol = g.getData()[0].length;
        for(int l=0;l<g.getData().length;l++)
                for(int c=0;c<nbcol;c++)
                    if(g.getData()[l][c]==0)
                        vides.add(l*nbcol+c);

        int choix = random.nextInt(vides.size());
        choix = vides.get(choix);
        return new int[]{choix / nbcol, choix % nbcol};
    }

}
