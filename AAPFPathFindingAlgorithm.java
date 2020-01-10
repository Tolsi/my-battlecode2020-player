package mybot;

/**
 * ABSTRACT<br>
 * Template for all Path Finding Algorithms used.<br>
 */
public abstract class AAPFPathFindingAlgorithm {
    private static final int SNAPSHOT_INTERVAL = 0;
    private int snapshotCountdown = 0;

    protected AAPFGridGraph graph;

    protected int parent[];
    protected final int sizeX;
    protected final int sizeXplusOne;
    protected final int sizeY;

    protected final int sx;
    protected final int sy;
    protected final int ex;
    protected final int ey;
    
    private int ticketNumber = -1;

    private boolean usingStaticMemory = false;

    public AAPFPathFindingAlgorithm(AAPFGridGraph graph, int sizeX, int sizeY,
                                    int sx, int sy, int ex, int ey) {
        this.graph = graph;
        this.sizeX = sizeX;
        this.sizeXplusOne = sizeX+1;
        this.sizeY = sizeY;
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
    }
    
    protected void initialiseMemory(int size, float defaultDistance, int defaultParent, boolean defaultVisited) {
        usingStaticMemory = true;
        ticketNumber = AAPFMemory.initialise(size, defaultDistance, defaultParent, defaultVisited);
    }
    
    /**
     * Call this to compute the path.
     */
    public abstract void computePath();

    /**
     * @return retrieve the path computed by the algorithm
     */
    public abstract int[][] getPath();
    
    /**
     * An optimal overridable method which prints some statistics when called for.
     */
    public void printStatistics() {
    }
    
    protected int toOneDimIndex(int x, int y) {
        return graph.toOneDimIndex(x, y);
    }
    
    protected int toTwoDimX(int index) {
        return graph.toTwoDimX(index);
    }
    
    protected int toTwoDimY(int index) {
        return graph.toTwoDimY(index);
    }
    
    protected int goalParentIndex() {
        return toOneDimIndex(ex,ey);
    }
    
    private int getParent(int index) {
        if (usingStaticMemory) return AAPFMemory.parent(index);
        else return parent[index];
    }
    
    private void setParent(int index, int value) {
        if (usingStaticMemory) AAPFMemory.setParent(index, value);
        else parent[index] = value;
    }
    
    protected int getSize() {
        if (usingStaticMemory) return AAPFMemory.size();
        else return parent.length;
    }
    
    protected boolean selected(int index) {
        return false;
    }
}
