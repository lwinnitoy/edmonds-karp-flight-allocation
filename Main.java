import java.util.*;

public class Main {
    // Students can add global variables or functions here (e.g., for capacities, flows, BFS, etc.)

    // Stores flight-pilot data: flightInfo (e.g. "A3"), pilotInfo (e.g. "012")
    private static List<Pair<String, String>> flightPilotPairings = new ArrayList<>();

    // All lines from stdin
    private static List<String> inputLines = new ArrayList<>();

    // Minimal Pair class
    private static class Pair<K, V> {
        private K key;
        private V value;
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey()   { return key; }
        public V getValue() { return value; }
    }

    /**
     * Solve one day's allocation using Edmonds-Karp for max-flow.
     * TODO (students):
     *   1) Construct your graph and capacities from flightPilotPairings.
     *   2) Implement the Edmonds-Karp algorithm (BFS in the residual graph).
     *   3) Output the 10-character assignment string or "!" if impossible.
     */

     //only 9 pilots max so we can use A=1
    private static void solveAllocationProblem() {
// Example placeholder: remove once you implement Edmonds-Karp

        // Example placeholder: remove once you implement Edmonds-Karp

        int maxFlow = 0;

        //array of all the flights
        ArrayList<String> flights = new ArrayList<>();

        //creates the residual graph
        Graph residualGraph = new Graph();

        //adds the source and terminal nodes to the graph/residual graph
        residualGraph.addVertex("src");
        residualGraph.addVertex("term");

        //loops through the flightPilotPairings and adds the flights and pilots to the graph/residual graph 
        for (int i = 0; i < flightPilotPairings.size(); i++) {
            
            String flightInfo = flightPilotPairings.get(i).getKey();  // ex flightInfo (e.g. "A3")
            String pilotInfo = flightPilotPairings.get(i).getValue(); // ex pilotInfo (e.gstarting by adding      . "012")

            //starting by adding the flight info to the graph
            String flight = flightInfo.substring(0, 1);
            residualGraph.addVertex(flight);
            flights.add(flight);

            //adding the number of flights as the edge weight to the terminal vertex
            int numFlights = Integer.parseInt(flightInfo.substring(1, 2));
            residualGraph.addEdge(flight, "term", numFlights);

            maxFlow += numFlights;

            //adding the pilots to the graph
            char[] pilots = pilotInfo.toCharArray();

            for (int j = 0; j < pilots.length; j++) {
                String pilot = Character.toString(pilots[j]);
                residualGraph.addVertex(pilot);

                //adding the edge weight of 1 to the pilot
                residualGraph.addEdge("src", pilot, 1);

                //adding the edge weight of 1 to the flight
                residualGraph.addEdge(pilot, flight, 1);
            }
        }
        
        int realMaxFlow = edmondsKarp(residualGraph, "src", "term");
        

        //System.out.print("Max flow: " + maxFlow + " Real max flow: " + realMaxFlow + " ");
        // Output the result
        if (maxFlow == realMaxFlow) {
            char[] output = {'_', '_', '_', '_', '_', '_', '_', '_', '_', '_'};

            for(String flight : flights){
                for(Graph.Edge edge : residualGraph.getEdges(flight)){
                    if(edge.getWeight() == 1){
                        output[Integer.parseInt(edge.getDest())] = flight.toCharArray()[0];
                    }
                }
            }
            System.out.print(String.valueOf(output));
        } else {
            System.out.print("!");
        }


        
        //residualGraph.printGraph();
    }

    private static int edmondsKarp(Graph residualGraph, String source, String sink) {
        int maxFlow = 0;

        //hashmap to store the parent of each node used to track the path
        Map<String, String> previous = new HashMap<>();
        
        //loop until there is no path from source to sink, meaning there are no more augmenting paths
        while (bfs(residualGraph, source, sink, previous)) {

            //preset to the maximum value of an int
            int pathFlow = Integer.MAX_VALUE;
    
            // Find the maximum flow through the path found.
            for (String v = sink; !v.equals(source); v = previous.get(v)) {
                String u = previous.get(v);
                int capacity = residualGraph.getEdge(u, v).getWeight();

                //find the minimum flow of the path, will be the capcity since pathflow is max int
                pathFlow = Math.min(pathFlow, capacity);
            }
    
            // Update residual capacities of the edges and reverse edges along the path.
            for (String v = sink; !v.equals(source); v = previous.get(v)) {
                String u = previous.get(v);

                //subtracks the flow from the edge
                residualGraph.getEdge(u, v).setWeight(residualGraph.getEdge(u, v).getWeight() - pathFlow);

                //adds the reverse edge if it does not exist, otherwise adds the flow to the edge
                if (residualGraph.getEdge(v, u) == null) {
                    residualGraph.addEdge(v, u, pathFlow);
                } else {
                    residualGraph.getEdge(v, u).setWeight(residualGraph.getEdge(v, u).getWeight() + pathFlow);
                }
            }
            
            // Add path flow to overall flow
            maxFlow += pathFlow;
        }
    
        return maxFlow;
    }


    private static boolean bfs(Graph residualGraph, String source, String sink, Map<String, String> previous) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        //add the source to the queue and visited HashSet
        queue.add(source);
        visited.add(source);
        
        //loop until the queue is empty
        while (!queue.isEmpty()) {
            String u = queue.remove();
            
            //loop through the edges of the node
            for (Graph.Edge edge : residualGraph.getEdges(u)) {
                String v = edge.getDest();

                //if the node has not been visited and the edge weight is greater than 0, add the node to the queue and visited HashSet
                if (!visited.contains(v) && edge.getWeight() > 0) {
                    queue.add(v);
                    visited.add(v);

                    //add the parent of the node to the previous hashmap
                    previous.put(v, u);
    
                    if (v.equals(sink)) {
                        // Found a path to the sink
                        return true;
                    }
                }
            }
        }
        
        // No path to the sink, thus no augmenting path
        return false;
    }

    /**
     * Process input data day by day; each day ends when an empty line is encountered.
     */
    private static void processInputData(List<String> lines) {
        int i = 0;
        while (i < lines.size()) {
            flightPilotPairings.clear();

            // Read until blank line or end
            while (i < lines.size() && !lines.get(i).isEmpty()) {
                String line = lines.get(i++);
                String[] tokens = line.split("\\s+");
                if (tokens.length < 2) continue;

                String flightInfo = tokens[0];
                String pilotInfo  = tokens[1];
                if (!pilotInfo.isEmpty() && pilotInfo.charAt(pilotInfo.length() - 1) == ';') {
                    pilotInfo = pilotInfo.substring(0, pilotInfo.length() - 1);
                }

                flightPilotPairings.add(new Pair<>(flightInfo, pilotInfo));
            }

            // Solve this day if there's data
            if (!flightPilotPairings.isEmpty()) {
                solveAllocationProblem();
                System.out.println();
            }

            // Skip any empty lines (between days)
            while (i < lines.size() && lines.get(i).isEmpty()) {
                i++;
            }
        }
    }


    //TDOO: implement this properly
    // graph implementation using hashmap and arraylist
    // edge list implementation
    static class Graph{

        private Map<String, List<Edge>> map;

        public Graph(){
            this.map = new HashMap<>();
        }

        static class Edge{
            private int weight;
            private String dest;
            Edge(String dest, int weight){
                this.weight = weight;
                this.dest = dest;
            }
            public int getWeight() {return this.weight;}
            public String getDest() {return this.dest;}
            public void setWeight(int weight) {this.weight = weight;}
        }

        public void addVertex(String s){
            map.putIfAbsent(s, new ArrayList<Edge>());
        }
        
        public Edge[] getEdges(String s){
            List<Edge> edges = map.get(s);
            return edges.toArray(new Edge[edges.size()]);
        }

        //adds an edge to the graph, only if the edge does not already exist
        public void addEdge(String source, String dest, int weight){
            map.putIfAbsent(source, new ArrayList<Edge>());
            for (Edge edge : map.get(source)){
                if (edge.dest.equals(dest)){
                    return;
                }
            }
            map.get(source).add(new Edge(dest, weight));
        }

        public List<Edge> getVertex(String src){
            return map.get(src);
        }

        public Edge getEdge(String src, String dest){
            for (Edge edge : map.get(src)){
                if (edge.dest.equals(dest)){
                    return edge;
                }
            }
            return null;
        }

        // Print the adjacency list
        public void printGraph() {
            for (var entry : map.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (Edge edge : entry.getValue()) {
                System.out.print("(" + edge.dest + ", " + edge.weight + ") ");
            }
            System.out.println();
            }
        }

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            inputLines.add(sc.nextLine());
        }
        sc.close();
        processInputData(inputLines);
    }
}
