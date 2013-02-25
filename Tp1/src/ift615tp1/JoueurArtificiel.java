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
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Vos noms et matricules
 */
public class JoueurArtificiel implements Joueur {

    private Random random = new Random();
    
    private char couleurJoueur = '0';
    private int jetonJoueur = 0;
     
    public int[] getProchainCoup(Grille g, int delais) {
        if (couleurJoueur == '0') {
            couleurJoueur = (g.nbLibre()%2==0)?'N':'B';
            jetonJoueur = (couleurJoueur=='N')? 1: 2;
        }
        
        Vector<Integer> vides = new Vector<Integer>();
        int nbcol = g.getData()[0].length;
        for(int l=0;l<g.getData().length;l++)
                for(int c=0;c<nbcol;c++)
                    if(g.getData()[l][c]==0)
                        vides.add(l*nbcol+c);

        
        int choix = random.nextInt(vides.size());
        choix = vides.get(choix);
        System.out.println("Nb libre : " + g.nbLibre());
        g.set(choix / nbcol, choix % nbcol, jetonJoueur);
        System.out.println("Nb libre : " + g.nbLibre());
        heuristic1(g);
        
        return new int[]{choix / nbcol, choix % nbcol};
    }

    public int evaluate(Grille g, int[] coupFictif)
    {
        int value = 0;
        
        //value += heuristic1(g,coupFictif);
        //value += heuristic2(g,coupFictif);
        //value += heuristic3(g,coupFictif);

        return value;
    }
    
    private int heuristic1(Grille g)
    {
        Set<Ligne> lignes = new HashSet<Ligne>();
        
        for(int l = 0 ; l < g.getData().length;l++)
            for(int c = 0; c < g.getData()[0].length ; c++)
                if(g.getData()[l][c] == jetonJoueur)
                    TesterLignesPossibles(l,c,g, lignes);    
        
        return lignes.size();
    }
    
    private void TesterLignesPossibles(int l, int c, Grille g, Set<Ligne> lignes)
    {
        int nbEnLigne =0;

        //****** HORIZONTAL *********
        for(int i = c -4 ; i <= c + 4; i++)
        {   
            if(i < 0 || i > g.getData()[0].length - 1)
                continue;

            if(g.getData()[l][i] == 0 || g.getData()[l][i] == jetonJoueur)
                nbEnLigne++;
            else
                nbEnLigne = 0;
            
            if(nbEnLigne >= 5)
            {      
                lignes.add(new Ligne(l, i - 4, l, i));
                
                nbEnLigne--;
            }
        }
        nbEnLigne = 0;
        //System.out.println("Horizontal : " + lignes.size());

        //****** VERTICAL *********
        for(int i = l -4 ; i <= l + 4; i++)
        {   
            if(i < 0 || i > g.getData().length - 1)
                continue;
   
            if(g.getData()[i][c] == 0 || g.getData()[i][c] == jetonJoueur)
                nbEnLigne++;
            else
                nbEnLigne = 0;
   
            if(nbEnLigne >= 5)
            {      
                lignes.add(new Ligne(i - 4, c, i, c));
                
                nbEnLigne--;
            }            
        }
        nbEnLigne = 0;
        //System.out.println("Vertical : " + lignes.size());
        
        //****** DIAGONAL \ *********
        for(int i = - 4 ; i <= 4; i++)
        {   
            if(l + i < 0 || c + i < 0 || 
                    l + i > g.getData().length -1 ||
                    c + i > g.getData()[0].length -1)
               continue;

            if(g.getData()[l + i][c + i] == 0 || g.getData()[l + i][c + i] == jetonJoueur)
                nbEnLigne++;
            else
                nbEnLigne = 0;
            
            if(nbEnLigne >= 5)
            {                    
                lignes.add(new Ligne(l + i - 4, c + i - 4,l + i, c + i));
                nbEnLigne--;
            }
            
        }
        nbEnLigne = 0;
        //System.out.println("Diago \\ : " + lignes.size());
        
        //****** DIAGONAL / *********
        for(int i = -4 ; i <= 4; i++)
        {   
            if(l + i < 0 || c - i < 0 ||
               l + i > g.getData().length -1 || c - i > g.getData()[0].length - 1)
               continue;

            if(g.getData()[l + i][c - i] == 0 || g.getData()[l + i][c - i] == jetonJoueur)
                nbEnLigne++;
            else
                nbEnLigne = 0;

            if(nbEnLigne >= 5)
            {                    
                lignes.add(new Ligne(l + i, c + i, l + i + 4, c + i + 4));
                nbEnLigne--;
            }
        }
        //System.out.println("Diago / : " + lignes.size());        
    }    
   
    public int heuristic2(Grille g, int[] coupFictif)
    {
        int value = 0;
        
        return value;
    }  
        
    public int heuristic3(Grille g, int[] coupFictif)
    {
        int value = 0;
        
        return value;
    }
    
    
}




