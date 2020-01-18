package mybot;

//        AAPFGridAndGoals gridAndGoals = AAPFAnyAnglePathfinding.loadMaze();
//        int[][] path = AAPFUtility.generatePath(gridAndGoals.gridGraph, gridAndGoals.startGoalPoints.sx, gridAndGoals.startGoalPoints.sy, gridAndGoals.startGoalPoints.ex, gridAndGoals.startGoalPoints.ey);
//        System.out.println(path);
public class AAPFAStarStaticMemory extends AAPFPathFindingAlgorithm {

    protected AAPFReusableIndirectHeap pq;

    protected int finish;

    public AAPFAStarStaticMemory(AAPFGridGraph graph, int sx, int sy, int ex, int ey) {
        super(graph, graph.sizeX, graph.sizeY, sx, sy, ex, ey);
    }

    @Override
    public void computePath() {
        int totalSize = (graph.sizeX+1) * (graph.sizeY+1);

        int start = toOneDimIndex(sx, sy);
        finish = toOneDimIndex(ex, ey);

        pq = new AAPFReusableIndirectHeap(totalSize);
        this.initialiseMemory(totalSize, Integer.MAX_VALUE, -1, false);

        initialise(start);

        int lastDist = -1;
        while (!pq.isEmpty()) {
            int dist = pq.getMinValue();

            int current = pq.popMinIndex();

            if (Math.abs(dist - lastDist) > 0.01f) { lastDist = dist;}

            if (current == finish || distance(current) == Integer.MAX_VALUE) {
                //maybeSaveSearchSnapshot();
                break;
            }
            setVisited(current, true);

            int x = toTwoDimX(current);
            int y = toTwoDimY(current);

            tryRelaxNeighbour(current, x, y, x-1, y-1);
            tryRelaxNeighbour(current, x, y, x, y-1);
            tryRelaxNeighbour(current, x, y, x+1, y-1);

            tryRelaxNeighbour(current, x, y, x-1, y);
            tryRelaxNeighbour(current, x, y, x+1, y);

            tryRelaxNeighbour(current, x, y, x-1, y+1);
            tryRelaxNeighbour(current, x, y, x, y+1);
            tryRelaxNeighbour(current, x, y, x+1, y+1);

            //maybeSaveSearchSnapshot();
        }
    }

    protected void tryRelaxNeighbour(int current, int currentX, int currentY, int x, int y) {
        if (!graph.isValidCoordinate(x, y))
            return;

        int destination = toOneDimIndex(x,y);
        if (visited(destination))
            return;
        if (!graph.neighbourLineOfSight(currentX, currentY, x, y))
            return;

        if (relax(current, destination, weight(currentX, currentY, x, y))) {
            // If relaxation is done.
            pq.decreaseKey(destination, distance(destination) + heuristic(x,y));
        }
    }

    protected int heuristic(int x, int y) {
        //return 0;
        return graph.distance(x, y, ex, ey);
    }


    protected int weight(int x1, int y1, int x2, int y2) {
        // todo pass function??
        return graph.distance(x1, y1, x2, y2);
    }

    protected boolean relax(int u, int v, int weightUV) {
        // return true iff relaxation is done.

        int newWeight = distance(u) + weightUV;
        if (newWeight < distance(v)) {
            setDistance(v, newWeight);
            setParent(v, u);
            //maybeSaveSearchSnapshot();
            return true;
        }
        return false;
    }


    protected final void initialise(int s) {
        pq.decreaseKey(s, 0);
        AAPFMemory.setDistance(s, 0);
    }


    private int pathLength() {
        int length = 0;
        int current = finish;
        while (current != -1) {
            current = parent(current);
            length++;
        }
        return length;
    }

    public int[][] getPath() {
        int length = pathLength();
        int[][] path = new int[length][];
        int current = finish;
        
        int index = length-1;
        while (current != -1) {
            int x = toTwoDimX(current);
            int y = toTwoDimY(current);
            
            path[index] = new int[2];
            path[index][0] = x;
            path[index][1] = y;
            
            index--;
            current = parent(current);
        }
        
        return path;
    }
    
    @Override
    protected boolean selected(int index) {
        return visited(index);
    }

    
    protected int parent(int index) {
        return AAPFMemory.parent(index);
    }
    
    protected void setParent(int index, int value) {
        AAPFMemory.setParent(index, value);
    }
    
    protected int distance(int index) {
        return AAPFMemory.distance(index);
    }
    
    protected void setDistance(int index, int value) {
        AAPFMemory.setDistance(index, value);
    }
    
    protected boolean visited(int index) {
        return AAPFMemory.visited(index);
    }
    
    protected void setVisited(int index, boolean value) {
        AAPFMemory.setVisited(index, value);
    }
}
