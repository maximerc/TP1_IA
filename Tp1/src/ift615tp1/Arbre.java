/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ift615tp1;

import connect5.Grille;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antoine
 */
public class Arbre {

    public static class Noeud {

        public Grille g;
        private int l; //ligne du coup joué
        private int c; //colonne du coup joué
        private Noeud parent;
        public List<Noeud> enfants;

        public int getLigne() {
            return this.l;
        }

        public int getColonne() {
            return this.c;
        }

        public Noeud getCoup() {
            while (this.parent != null) {
                this.parent = this.parent.parent;
            }
            return this;
        }

        public void genererEnfants() {
            this.enfants = new ArrayList<>();
            int nbligne = this.g.getData().length;
            int nbcol = this.g.getData()[0].length;
            int joueur = (g.nbLibre() % 2 == 0) ? 1 : 2;
            for (int l = 0; l < nbligne; l++) {
                for (int c = 0; c < nbcol; c++) {
                    if (this.g.getData()[l][c] == 0) {
                        Noeud n = new Noeud();
                        n.parent = this;
                        n.l = l;
                        n.c = c;
                        n.g = clonerGrille(this.g);
                        n.g.set(l, c, joueur);
                        this.enfants.add(n);
                    }
                }
            }
        }

        public Grille clonerGrille(Grille g) {
            int nbligne = g.getData().length;
            int nbcol = g.getData()[0].length;
            Grille gg = new Grille(nbligne, nbcol);
            for (int ln = 0; ln < nbligne; ln++) {
                for (int col = 0; col < nbcol; col++) {
                    gg.set(ln, col, g.get(ln, col));
                }
            }
            return gg;
        }
    }
    public Noeud racine;
    public Noeud dernierEtage;

    public Arbre(Grille grilleRacine, int joueur) {
        racine = new Noeud();
        racine.g = grilleRacine;
        racine.enfants = new ArrayList<Noeud>();
    }

    public Grille clonerGrille(Grille g) {
        int nbligne = g.getData().length;
        int nbcol = g.getData()[0].length;
        Grille gg = new Grille(nbligne, nbcol);
        for (int ln = 0; ln < nbligne; ln++) {
            for (int col = 0; col < nbcol; col++) {
                gg.set(ln, col, g.get(ln, col));
            }
        }
        return gg;
    }

//	public void ajouterProfondeur(int increment) {
//		Noeud noeud = dernierEtage; 
//		int joueur = (racine.g.nbLibre()%2==0) ? 1 : 2;
//
//		while (true) {
//			//if temps limite ? 
//            			//break;
//			this.genererEnfants(joueur, noeud, increment);
//
//			if (noeud.voisinDroite == null)
//				break;
//			noeud = noeud.voisinDroite;
//		}
//	}
    public void genererEnfants(int joueur, Noeud noeud, int profondeur) {
        if (profondeur <= 0) {
            return;
        }

        List vides = new ArrayList();
        int nbligne = noeud.g.getData().length;
        int nbcol = noeud.g.getData()[0].length;
        Noeud voisinGauche = new Noeud();
        for (int l = 0; l < nbligne; l++) {
            boolean premierNoeud = true;
            for (int c = 0; c < nbcol; c++) {
                if (noeud.g.getData()[l][c] == 0) {
                    Noeud n = new Noeud();
                    n.parent = noeud;
                    n.l = l;
                    n.c = c;
                    n.g = clonerGrille(noeud.g);

                    racine.enfants.add(n);
                    if (premierNoeud) {
                        premierNoeud = false;
                        dernierEtage = n;
                    } else {
//						voisinGauche.voisinDroite = n;
                    }
                    voisinGauche = n;

                    int nextJoueur = (joueur == 1) ? 2 : 1; // prochain joueur avec différente couleur
                    this.genererEnfants(nextJoueur, n, profondeur - 1);
                }
            }
        }
    }
}
