package ift615tp1;

import connect5.Grille;
import connect5.Joueur;
import ift615tp1.Arbre;
import ift615tp1.Arbre.Noeud;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


/*
 * Vincent Fortier            (11 085 764)
 * Antoine Croteau            (11 081 857)
 * Maxime Routhier Couture    (11 064 947)
 * Keven Fecteau              (11 089 305)
 */

public class JoueurArtificiel implements Joueur {

    private Random random = new Random();
    private char couleurJoueur = '0';
    private int joueur = 0;
    private long finHorlogeDeGarde;
    private Noeud noeudMax = new Noeud();
    private int profondeurInitiale=0;
    
    public int[] getProchainCoup(Grille g, int delais) {
        if (couleurJoueur == '0') {
            couleurJoueur = (g.nbLibre()%2==0)?'N':'B';
            joueur = (couleurJoueur=='N')?'1':'2';
        }
            
    //  long timeout = System.currentTimeMillis() + delais;
    //  while (System.currentTimeMillis() < timeout) {}
        
        
        int a = Integer.MAX_VALUE;
        int b = Integer.MIN_VALUE;
        iterativeDeepening(a, b, g, delais);
            
    
        return new int[]{noeudMax.getLigne(), noeudMax.getColonne()};
    }

    public int evaluate(Noeud noeud)
    {
        int value = 0;
        
        //value += heuristic1(g);
        //value += heuristic2(g);
        //value += heuristic3(g);

        return value;
    }
    
    public void iterativeDeepening(int a, int b, Grille g, int delais) {
        
        int profondeur = 2;
        finHorlogeDeGarde = System.currentTimeMillis() + delais;
        Arbre arbre = new Arbre(g, 1, joueur);
        
        try {
            // Une exception sera lancée lorsque le temps est écoulé
            while(true) {
                arbre.ajouterProfondeur(profondeur);
                profondeurInitiale = profondeur;
                alphaBeta(arbre.racine, profondeur, a, b, true);
                profondeur++;
            }
        } catch (Exception ex) {
            // TODO : planter si l'exception n'est pas un "Timeout"
        }
        
       // return /* TODO : return Le coup à jouer */0;
    }

    public int alphaBeta(Noeud noeud, int profondeur, int a, int b, boolean tour) throws Exception {
        if (System.currentTimeMillis() < finHorlogeDeGarde) {
            throw new Exception("Timeout");
        }
        
        if ((profondeur == 0) || (noeud.enfants.isEmpty())) {
            int h = evaluate(noeud); //heuristic
            return ( tour ? h : -h );
        }
        
        // Si c'est "notre" tour (Max)
        if (tour == true) {
            for (Noeud enfant : noeud.enfants) {
                int nouvelleEssai = alphaBeta(enfant, profondeur-1, a, b, !tour);
                if(a < nouvelleEssai){
                    a = nouvelleEssai;
                    if(profondeur == profondeurInitiale-1)
                        noeudMax = enfant;
                }
                
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
    
    






}