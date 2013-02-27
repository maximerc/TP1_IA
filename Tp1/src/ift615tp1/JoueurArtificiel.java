package ift615tp1;

import connect5.Grille;
import connect5.GrilleVerificateur;
import connect5.Joueur;
import ift615tp1.Arbre.Noeud;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/*
 * Vincent Fortier            (11 085 764)
 * Antoine Croteau            (11 081 857)
 * Maxime Routhier Couture    (11 064 947)
 * Keven Fecteau              (11 089 305)
 */
public class JoueurArtificiel implements Joueur {

    private Noeud noeudMax = new Noeud();
    private Noeud dernierNoeudMaxValide = new Noeud();
    private int joueurCourant;
    private long timeout;
	
	// Nb de points ajouté à l'heuristique lorsque l'algo 
	// découvre une occucurence de plus d'une ligne possible
	private final int[] SCORE_LIGNES_POSSIBLES = new int[]{1, 2, 4, 8, 32};
	private final int[] SCORE_LIGNES_REELS = new int[]{1, 2, 4, 8, 32};
	private final int LIGNES_POSSIBLES_MULT = 1;	// On multiplie le score de cette partie d'heuristique par ce nombre
	private final int LIGNES_REELS_MULT = 10;		// On multiplie le score de cette partie d'heuristique par ce nombre

    public int getDernierJoueur(Grille g) {
            return ( g.nbLibre()%2==0 ) ? 2 : 1;
    }
    
    public int getProchainJoueur(Grille g) {
            return ( g.nbLibre()%2==0 ) ? 1 : 2;
    }
    public int[] getProchainCoup(Grille g, int delais) {
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        
        joueurCourant = getProchainJoueur(g);
        Noeud noeud = iterativeDeepening(a, b, g, delais);
        //noeud = noeud.getCoup();

        return new int[]{noeud.getLigne(), noeud.getColonne()};
    }

    public int evaluate(Noeud noeud) {
        int value = 0;
        value += heuristic1(noeud.g, joueurCourant);
		value -= heuristic1(noeud.g, (joueurCourant==1) ? 2 : 1);

        return value;
    }

    public Noeud iterativeDeepening(int a, int b, Grille g, int delais) {
        int profondeur = 1;
        int joueur = getProchainJoueur(g);
        timeout = System.currentTimeMillis() + delais;
        
        try {
            // Une exception sera lancée lorsque le temps est écoulé
            while (true) {
                System.out.println("profondeur: " + profondeur + " - nblibre : " + g.nbLibre());
                Arbre arbre = new Arbre(g, joueur);
                alphaBeta(arbre.racine, profondeur, a, b, true);
                this.dernierNoeudMaxValide = this.noeudMax;
                profondeur++;
                if (profondeur > g.nbLibre()) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Message "+ex.getMessage());
            // TODO : planter si l'exception n'est pas un "Timeout"
        }
        System.out.println("Noeud max : " + this.evaluate(this.dernierNoeudMaxValide));
        return this.dernierNoeudMaxValide;

    }

    public int alphaBeta(Noeud noeud, int profondeur, int a, int b, boolean tour) throws Exception {
//        if (System.currentTimeMillis() < finHorlogeDeGarde) {
////            return this.noeudMax;
//            throw new Exception("Timeout");
//        }
//        
        
        if (System.currentTimeMillis() > timeout - 100) {
            throw new Exception("Fin temps limite");
        }
        
        if (profondeur <= 0 || noeud.g.nbLibre() == 0) {
            int h = evaluate(noeud); //heuristic
            return h;//(tour ? -h : h);
        }
        //creer les enfants
        noeud.genererEnfants();

        // Si c'est "notre" tour (Max)
        if (tour == true) {
            for (Noeud enfant : noeud.enfants) {
                int alpha = alphaBeta(enfant, profondeur - 1, a, b, !tour);
                if (alpha > a) {
                    a = alpha;
                    this.noeudMax = enfant;
                }
                if (b <= a) {
                    break;
                }
            }
            return a;
        } else {  // Min
            for (Noeud enfant : noeud.enfants) {
                int beta = alphaBeta(enfant, profondeur - 1, a, b, !tour);
                if (beta < b) {
                    b = beta;
                }
                if (b <= a) {
                    break;
                }
            }
            return b;
        }
    }

    private int heuristic1(Grille g, int paramJoueur) {
		// HashMap contenant les lignesPossibles possibles
		// Clé: String contenant les coord des points de début et fin de la ligne 
		//		Exemple: pour ligne({1,1},{1,5}), la clé serait 1115
        HashMap<String, Ligne> lignesPossibles = new HashMap<>();
		HashMap<String, Ligne> lignesReels = new HashMap<>();
		
        int joueur = paramJoueur;
		int heuristic = 0;

        for (int l = 0; l < g.getData().length; l++) {
            for (int c = 0; c < g.getData()[0].length; c++) {
                if (g.getData()[l][c] == joueur) {
                    heuristic += TesterLignes(l, c, g, lignesPossibles, lignesReels, joueur);
                }
            }
        }

        return heuristic;
    }
    
	/*
     private int heuristic2(Grille g) {
        int joueur = joueurCourant;
        GrilleVerificateur gv = new GrilleVerificateur();
        
        if(gv.determineGagnant(g) == joueur)
            return 1000;
        else
            return 0;
    }
	 */
	 
    private int TesterLignes(	int l, int c, Grille g, HashMap<String, Ligne> lignesPossibles, 
								HashMap<String, Ligne> lignesReels, int joueur) {
        int nbEnLignePossible = 0;
		int nbEnLigneReel = 0;
		int hLignePossible = 0;		// Heuristique pour les lignesPossibles possibles
		int hLigneReel = 0;			// Heuristique pour les lignesPossibles réeles

        //****** HORIZONTAL *********
        for (int i = c - 4; i <= c + 4; i++) {
            if (i < 0 || i > g.getData()[0].length - 1) {
                continue;
            }

            if (g.getData()[l][i] == 0 || g.getData()[l][i] == joueur) {
                nbEnLignePossible++;
            } else {
                nbEnLignePossible = 0;
            }
			
            if (g.getData()[l][i] == joueur) {
                nbEnLigneReel++;
            } else {
                nbEnLigneReel = 0;
            }

            if (nbEnLignePossible >= 5) {
				hLignePossible += ajouterLignePossible(lignesPossibles, l, i - 4, l, i);

                nbEnLignePossible--;
            }
			
            if (nbEnLigneReel >= 2) {
				hLigneReel += ajouterLigneReel(lignesReels, l, i - 4, l, i, nbEnLigneReel);

                nbEnLigneReel--;
            }
        }
        nbEnLignePossible = 0;
		nbEnLigneReel = 0;
        //System.out.println("Horizontal : " + lignesPossibles.size());

        //****** VERTICAL *********
        for (int i = l - 4; i <= l + 4; i++) {
            if (i < 0 || i > g.getData().length - 1) {
                continue;
            }

            if (g.getData()[i][c] == 0 || g.getData()[i][c] == joueur) {
                nbEnLignePossible++;
            } else {
                nbEnLignePossible = 0;
            }
			
            if (g.getData()[i][c] == joueur) {
                nbEnLigneReel++;
            } else {
                nbEnLigneReel = 0;
            }

            if (nbEnLignePossible >= 5) {
				hLignePossible += ajouterLignePossible(lignesPossibles, i - 4, c, i, c);

                nbEnLignePossible--;
            }
			
            if (nbEnLigneReel >= 2) {
				hLigneReel += ajouterLigneReel(lignesReels, i - 4, c, i, c, nbEnLigneReel);

                nbEnLigneReel--;
            }
        }
        nbEnLignePossible = 0;
		nbEnLigneReel = 0;
        //System.out.println("Vertical : " + lignesPossibles.size());

        //****** DIAGONAL \ *********
        for (int i = - 4; i <= 4; i++) {
            if (l + i < 0 || c + i < 0
                    || l + i > g.getData().length - 1
                    || c + i > g.getData()[0].length - 1) {
                continue;
            }

            if (g.getData()[l + i][c + i] == 0 || g.getData()[l + i][c + i] == joueur) {
                nbEnLignePossible++;
            } else {
                nbEnLignePossible = 0;
            }
			
            if (g.getData()[l + i][c + i] == joueur) {
                nbEnLigneReel++;
            } else {
                nbEnLigneReel = 0;
            }

            if (nbEnLignePossible >= 5) {
				hLignePossible += ajouterLignePossible(lignesPossibles, l + i - 4, c + i - 4, l + i, c + i);
                nbEnLignePossible--;
            }
			
            if (nbEnLigneReel >= 2) {
				hLigneReel += ajouterLigneReel(lignesReels,  l + i - 4, c + i - 4, l + i, c + i, nbEnLigneReel);

                nbEnLigneReel--;
            }

        }
        nbEnLignePossible = 0;
		nbEnLigneReel = 0;
        //System.out.println("Diago \\ : " + lignesPossibles.size());

        //****** DIAGONAL / *********
        for (int i = -4; i <= 4; i++) {
            if (l + i < 0 || c - i < 0
                    || l + i > g.getData().length - 1 || c - i > g.getData()[0].length - 1) {
                continue;
            }

            if (g.getData()[l + i][c - i] == 0 || g.getData()[l + i][c - i] == joueur) {
                nbEnLignePossible++;
            } else {
                nbEnLignePossible = 0;
            }
			
            if (g.getData()[l + i][c - i] == joueur) {
                nbEnLigneReel++;
            } else {
                nbEnLigneReel = 0;
            }

            if (nbEnLignePossible >= 5) {
				hLignePossible += ajouterLignePossible(lignesPossibles, l + i, c + i, l + i + 4, c + i + 4);
                nbEnLignePossible--;
            }
			
            if (nbEnLigneReel >= 2) {
				hLigneReel += ajouterLigneReel(lignesReels, l + i, c + i, l + i + 4, c + i + 4, nbEnLigneReel);

                nbEnLigneReel--;
            }
        }
        //System.out.println("Diago / : " + lignesPossibles.size());
		return (hLignePossible * LIGNES_POSSIBLES_MULT) + (hLigneReel * LIGNES_REELS_MULT);
    }
    
    private int ajouterLignePossible(HashMap<String, Ligne> lignes, int l1, int c1, int l2, int c2) {
		
		Ligne dansHashmap = lignes.get(""+l1+c1+l2+c2);
		int heuristic = 0;
        
        // Si la ligne existe déjà dans le hashmap, on augmente son nb d'occurence
		if(dansHashmap != null) {
			int nbOccurences = dansHashmap.getNbOccurences();
			heuristic = SCORE_LIGNES_POSSIBLES[nbOccurences - 1];	// - 1 car le tableau commence à 0
			nbOccurences++;
			dansHashmap.setNbOccurences(nbOccurences);
		// Sinon on l'ajoute au HashMap
		} else {
			lignes.put(""+l1+c1+l2+c2,new Ligne(l1, c1, l2, c2));
			heuristic = SCORE_LIGNES_POSSIBLES[0];
		}
		
		return heuristic;
    }
	
    private int ajouterLigneReel(HashMap<String, Ligne> lignes, int l1, int c1, int l2, int c2, int nbEnLigne) {
		
		Ligne dansHashmap = lignes.get(""+l1+c1+l2+c2);
		int heuristic = 0;
        
        // Si la ligne existe déjà dans le hashmap, on passe
		if(dansHashmap == null) {
			lignes.put(""+l1+c1+l2+c2,new Ligne(l1, c1, l2, c2));
			heuristic = SCORE_LIGNES_REELS[nbEnLigne];
		}
		
		return heuristic;
    }
}
