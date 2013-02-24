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
public class Grille {

    public Grille(int nblignes, int nbcols){
        data = new byte[nblignes][nbcols];
    }
    
    public void set(int l, int c, int v){
        data[l][c] = (byte) v;
    }
    
    public int get(int l, int c){
        return data[l][c];
    }
    
    public void reset(){
        for(byte[] b : data)
            for(int i=0;i<b.length;i++)
                b[i] = 0;
    }
    
    public int nbLibre(){
        int n=0;
        for(byte[] b : data)
            for(byte bb : b)
                if(bb==0)
                    n++;
        return n;
    }
    
    public String toString(){
        char[] table = {'0', 'N', 'B' };
        String result = "" + data.length + " " + data[0].length + "\n";
        for(byte[] b : data){
            char[] c = new char[b.length + 1];
            for(int i=0;i<b.length;i++)
                c[i] = table[b[i]];
            result += new String(c);
            result += '\n';
        }
        return result;
    }
    
    public byte[][] getData(){
        return data;
    }
    
    protected byte[][]     data;
    
}
