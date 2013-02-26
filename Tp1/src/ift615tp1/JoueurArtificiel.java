package ift615tp1;

import connect5.Grille;
import connect5.Joueur;
import ift615tp1.Arbre.Noeud;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/*
 * Vincent Fortier            (11 085 764)
 * Antoine Croteau            (11 081 857)
 * Maxime Routhier Couture    (11 064 947)
 * Keven Fecteau              (11 089 305)
 */

public class JoueurArtificiel implements Joueur {
    private long finHorlogeDeGarde;
    private Noeud noeudMax = new Noeud();
    private int max = 0;
    private int profondeurInitiale=0;
    
    public int getDernierJoueur(Grille g) {
            return ( g.nbLibre()%2==0 ) ? 1 : 2;
    }
    
    public int[] getProchainCoup(Grille g, int delais) {
        int a = Integer.MAX_VALUE;
        int b = Integer.MIN_VALUE;
        
        
        this.max = 0;
//        this.noeudMax = null;
        
        Noeud noeud = iterativeDeepening(a, b, g, delais);
        noeud = noeud.getCoup();
    
        return new int[]{noeud.getLigne(), noeud.getColonne()};
    }

    public int evaluate(Noeud noeud)
    {
        int value = 0;
        value += heuristic1(noeud.g);
        //value += heuristic2(g);
        //value += heuristic3(g);

        return value;
    }
    
    public Noeud iterativeDeepening(int a, int b, Grille g, int delais) {
        int profondeur = 20;
        int joueur = getDernierJoueur(g);
        finHorlogeDeGarde = System.currentTimeMillis() + delais;
        long miaw = System.currentTimeMillis() + delais;
        
        try {
            // Une exception sera lancée lorsque le temps est écoulé
            while( true ) {
                
                if (System.currentTimeMillis() > miaw-50) {
                    break;
                }
//                arbre.ajouterProfondeur(profondeur);
//                profondeurInitiale = profondeur;
                Arbre arbre = new Arbre(g, joueur);
                alphaBeta(arbre.racine, profondeur, a, b, true);
                profondeur++;
            }
        } catch (Exception ex) {
            int i=0;
            // TODO : planter si l'exception n'est pas un "Timeout"
        }
        return this.noeudMax;
        
       // return /* TODO : return Le coup à jouer */0;
    }

    public int alphaBeta(Noeud noeud, int profondeur, int a, int b, boolean tour) throws Exception {
//        if (System.currentTimeMillis() < finHorlogeDeGarde) {
////            return this.noeudMax;
//            throw new Exception("Timeout");
//        }
//        
        if ( profondeur <= 0 || noeud.g.nbLibre()==0 ) {
            int h = evaluate(noeud); //heuristic
            
            if (this.max < h) {
                this.max = h;
                this.noeudMax = noeud;
            }
            
            return ( tour ? h : -h );
        }
        //creer les enfants
        noeud.genererEnfants();
        
        // Si c'est "notre" tour (Max)
        if (tour == true) {
            for (Noeud enfant : noeud.enfants) {
                a = alphaBeta(enfant, profondeur-1, a, b, !tour);
                if (b <= a)
                    break;
            }
        // Min
        } else {
            for (Noeud enfant : noeud.enfants) {
                b = Math.min(b, alphaBeta(enfant, profondeur-1, a, b, !tour));
                if (b <= a)
                    break;
            }
        }
        
        return 0;
    }
    
    
    private int heuristic1(Grille g)
    {
        Set<Ligne> lignes = new HashSet<Ligne>();
        int joueur = getDernierJoueur(g);
        
        for(int l = 0 ; l < g.getData().length;l++)
            for(int c = 0; c < g.getData()[0].length ; c++)
                if(g.getData()[l][c] == joueur)
                    TesterLignesPossibles(l,c,g, lignes, joueur);    
        
        return lignes.size();
    }
    
    private void TesterLignesPossibles(int l, int c, Grille g, Set<Ligne> lignes, int joueur)
    {
        int nbEnLigne =0;

        //****** HORIZONTAL *********
        for(int i = c -4 ; i <= c + 4; i++)
        {   
            if(i < 0 || i > g.getData()[0].length - 1)
                continue;

            if(g.getData()[l][i] == 0 || g.getData()[l][i] == joueur)
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
   
            if(g.getData()[i][c] == 0 || g.getData()[i][c] == joueur)
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

            if(g.getData()[l + i][c + i] == 0 || g.getData()[l + i][c + i] == joueur)
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

            if(g.getData()[l + i][c - i] == 0 || g.getData()[l + i][c - i] == joueur)
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
}
