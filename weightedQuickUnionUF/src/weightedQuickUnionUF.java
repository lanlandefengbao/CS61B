
public class weightedQuickUnionUF {

    private int[] parent; //record unionship
    private int[] size; //record tree size

    public weightedQuickUnionUF(int n) {
        parent = new int[n];
        size = new int[n];
        for(int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    private int root(int i) {
        while(i != parent[i]){
            i = parent[i];
        }
        return i;
    }

    private void compression(int start, int stop){
        while(start != stop){
            int nxt = parent[start];
            parent[start] = stop;
            start = nxt;
        }
    }

    public void Connect(int a, int b) {
        int i = root(a);
        int j = root(b);
        if(i == j){
            return;
        }
        if(size[i] >= size[j]){
           /* without path compression */
//            parent[j] = i;

          /* with path compression */
            compression(b, i);
            size[i] += size[j];
        }
        else{
//            parent[i] = j;
            compression(a, j);
            size[j] += size[i];
        }
    }

    public boolean isConnected(int a, int b){
        int i = root(a);
        int j = root(b);
        if(i == j){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        weightedQuickUnionUF x = new weightedQuickUnionUF(1000);
        x.Connect(1,2);
        x.Connect(3,4);
        x.Connect(5,6);
        x.Connect(6,1);
        System.out.println(x.isConnected(1,5));
        System.out.println(x.isConnected(3,9));
    }
}
