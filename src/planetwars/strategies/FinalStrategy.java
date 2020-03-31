
// Muntaqim Mehtaz (mehta216)

package planetwars.strategies;

// importing interfaces and java built-in classes

import planetwars.publicapi.*;

import java.util.*;

// creating Strategy class
public class FinalStrategy implements IStrategy {

    // declaring data members for Strategy class
    private static int TURN = 0;
    private static final int POPULATION_DIVISION = 5;
    private Random random;

    // creating constructor for Strategy class
    public FinalStrategy() {
        random = new Random();
    } // end constructor

    /* overriding takeTurn method
     * takes in @params planets, planetOperations and eventsToExecute
     */
    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
        System.out.println("IN MY : " + TURN); // printing TURNs to keep track of turns taken
        TURN++;


        /* 1st Data Structure : Dictionary
         * dictionary myVisiblePlanetsDictionary keeps track of visible planets that belong to me
         * dictionary neutralVisiblePlanetsDictionary keeps track of visible planets that are neutral
         * dictionary opponentVisiblePlanetsDictionary keeps track of visible planets that belongs to opponent
         */

        Dictionary<Integer, IVisiblePlanet> myVisiblePlanetsDictionary = new Hashtable<>();
        Dictionary<Integer, IVisiblePlanet> neutralVisiblePlanetsDictionary = new Hashtable<>();
        Dictionary<Integer, IVisiblePlanet> opponentVisiblePlanetsDictionary = new Hashtable<>();


        /* 2nd Data Structure : Queue
         * queue myMoves stores all the moves made
         */
        Queue<IEvent> myMoves = new LinkedList<>();

        IVisiblePlanet neutralPlanetWithLeastPop = null; // creating a neutral visible planet that is least populated
        IVisiblePlanet opponentPlanetWithLeastPop = null; // creating an opponent visible planet that is least populated
        IVisiblePlanet myPlanetWithLeastPop = null; // creating my own visible planet that is least populated

        try { // try and catch statements to find exception

            for (IPlanet planet : planets) { // traversing through the planets
                if (planet instanceof IVisiblePlanet) { // checking if yhe planet is visible
                    IVisiblePlanet visiblePlanet = (IVisiblePlanet) planet; // creating a new visible planet
                    if (visiblePlanet.getOwner() == Owner.SELF) { // checking if the visible planet's owner is me
                        myVisiblePlanetsDictionary.put(planet.getId(), visiblePlanet); // putting the planet in the dictionary
                        if (myPlanetWithLeastPop == null) { // seeing whether myPlanetWithLeastPop is empty
                            myPlanetWithLeastPop = visiblePlanet; // making visiblePlanet myPlanetWithLeastPop
                        } else if (visiblePlanet.getPopulation() < myPlanetWithLeastPop.getPopulation()) { // seeing whether visiblePlanet's population is less than myPlanetWithLeastPop's population
                            myPlanetWithLeastPop = visiblePlanet; // making visiblePlanet myPlanetWithLeastPop
                        } // end else if
                    } else if (visiblePlanet.getOwner() == Owner.OPPONENT) { // checking if the visible planet's owner is opponent
                        opponentVisiblePlanetsDictionary.put(planet.getId(), visiblePlanet); // putting the planet in the dictionary
                        if (opponentPlanetWithLeastPop == null) { // seeing whether opponentPlanetWithLeastPop is empty
                            opponentPlanetWithLeastPop = visiblePlanet; // making visiblePlanet opponentPlanetWithLeastPop
                        } else if (visiblePlanet.getPopulation() < opponentPlanetWithLeastPop.getPopulation()) { // seeing whether visiblePlanet's population is less than opponentPlanetWithLeastPop's population
                            opponentPlanetWithLeastPop = visiblePlanet; // making visiblePlanet opponentPlanetWithLeastPop
                        } // end else if
                    } else if (visiblePlanet.getOwner() == Owner.NEUTRAL) { // checking if the visible planet's owner is neutral
                        neutralVisiblePlanetsDictionary.put(planet.getId(), visiblePlanet); // putting the planet in the dictionary
                        if (neutralPlanetWithLeastPop == null) { // seeing whether neutralPlanetWithLeastPop is empty
                            neutralPlanetWithLeastPop = visiblePlanet; // making visiblePlanet neutralPlanetWithLeastPop
                        } else if (visiblePlanet.getPopulation() < neutralPlanetWithLeastPop.getPopulation()) { // seeing whether visiblePlanet's population is less than neutralPlanetWithLeastPop's population
                            neutralPlanetWithLeastPop = visiblePlanet; // making visiblePlanet neutralPlanetWithLeastPop
                        } // end else if
                    } // end else if
                } // end if
            } // end for

            Enumeration enumeration = myVisiblePlanetsDictionary.elements(); // creating enumeration for dictionary
            while (enumeration.hasMoreElements()) { // seeing if the dictionary has more elements
                IVisiblePlanet sourcePlanet = (IVisiblePlanet) enumeration.nextElement(); // making the next element in the dictionary sourcePlanet
                Set<IEdge> sourcePlanetEdges = sourcePlanet.getEdges(); // retrieving edges from sourcePlanet

                /* 3rd Data Structure : List
                 * list opponentPlanetsInEdges keeps track of opponent planets that have edges with my planet
                 * list myPlanetInEdges keeps track of my own planets that have edges with my planet
                 * list neutralPlanetsInEdges keeps track of neutral planets that have edges with my planet
                 */
                List<IVisiblePlanet> opponentPlanetsInEdges = new ArrayList<>();
                List<IVisiblePlanet> myPlanetsInEdges = new ArrayList<>();
                List<IVisiblePlanet> neutralPlanetsInEdges = new ArrayList<>();

                Iterator sourcePlanetEdgeIterator = sourcePlanetEdges.iterator(); // creating iterator object to go through sourcePlanet's edges

                boolean hasNeutralInEdgeWithFewPop = false; // boolean variable checks whether sourcePlanet's edge has neutralPlanetWithLeastPop
                boolean hasOpponentInEdgeWithFewPop = false; // boolean variable checks whether sourcePlanet's edge has opponentPlanetWithLeastPop
                boolean hasOwnInEdgeWithFewPop = false; // boolean variable checks whether sourcePlanet's edge has myPlanetWithLeastPop

                while (sourcePlanetEdgeIterator.hasNext()) { // using iterator object to check if there's a next edge
                    IEdge edge = (IEdge) sourcePlanetEdgeIterator.next(); // calling next edge and giving that value to new edge variable
                    int edgeDestinationPlanetId = edge.getDestinationPlanetId(); // retrieving destination planet from the edge's Id
                    //Check if this is a neutral planet
                    if (((Hashtable<Integer, IVisiblePlanet>) neutralVisiblePlanetsDictionary).containsKey(edgeDestinationPlanetId)) {
                        neutralPlanetsInEdges.add(neutralVisiblePlanetsDictionary.get(edgeDestinationPlanetId)); // adding it to list of neutralPlanetsInEdges
                        // Check if destinationPlanet is neutralPlanetWithLeastPop
                        if (edge.getDestinationPlanetId() == neutralPlanetWithLeastPop.getId()) {
                            hasNeutralInEdgeWithFewPop = true;
                        } // end if
                        // Check if this is opponent's planet
                    } else if (((Hashtable<Integer, IVisiblePlanet>) opponentVisiblePlanetsDictionary).containsKey(edgeDestinationPlanetId)) {
                        opponentPlanetsInEdges.add(opponentVisiblePlanetsDictionary.get(edgeDestinationPlanetId)); // adding it to list of opponentPlanetsInEdges
                        // Check if destinationPlanet is opponentPlanetWithLeastPop
                        if (edge.getDestinationPlanetId() == opponentPlanetWithLeastPop.getId()) {
                            hasOpponentInEdgeWithFewPop = true;
                        } // end if
                        // Check if ths is my own planet
                    }else if (((Hashtable<Integer, IVisiblePlanet>) myVisiblePlanetsDictionary).containsKey(edgeDestinationPlanetId)) {
                        myPlanetsInEdges.add(myVisiblePlanetsDictionary.get(edgeDestinationPlanetId)); // adding it to list of myPlanetsInEdges
                        // Check if destinationPlanet is myPlanetWithLeastPop
                        if (edge.getDestinationPlanetId() == myPlanetWithLeastPop.getId()) {
                            hasOwnInEdgeWithFewPop = true;
                        } // end if
                    } // end else if
                } //end while

                // Checking if list of NeutralPlanetsInEdges contains planets
                if (neutralPlanetsInEdges.size() > 0) {
                    // Checking if it has neutralPlanetWithLeastPop in its edge
                    if (hasNeutralInEdgeWithFewPop) {
                        long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.9); // transferring 90% of population
                        IEvent myMove = planetOperations.transferPeople(sourcePlanet, neutralPlanetWithLeastPop, transferPopulation);
                        ((LinkedList<IEvent>) myMoves).push(myMove); // adding the move made to the queue
                    } else {
                        //select one in random from list
                        long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.5); // transferring 50% of population
                        IPlanet destinationPlanet = neutralPlanetsInEdges.get(random.nextInt(neutralPlanetsInEdges.size()));
                        IEvent myMove = planetOperations.transferPeople(sourcePlanet, destinationPlanet, transferPopulation);
                        ((LinkedList<IEvent>) myMoves).push(myMove); // adding the move to the queue

                    } // end else
                    // Checking if list of opponentPlanetsInEdges contains planets
                } else if (opponentPlanetsInEdges.size() > 0) {
                    // Checking if it has opponentPlanetWithLeastPop in its edge
                    if (hasOpponentInEdgeWithFewPop) {
                        long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.9); // transferring 90% of population
                        IEvent myMove = planetOperations.transferPeople(sourcePlanet, opponentPlanetWithLeastPop, transferPopulation);
                        ((LinkedList<IEvent>) myMoves).push(myMove); // adding the move to the queue
                    } else {
                        //select one in random from list
                        long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.5); // transferring 50% of population
                        IPlanet destinationPlanet = opponentPlanetsInEdges.get(random.nextInt(opponentPlanetsInEdges.size()));
                        IEvent myMove = planetOperations.transferPeople(sourcePlanet, destinationPlanet, transferPopulation);
                        ((LinkedList<IEvent>) myMoves).push(myMove); // adding the move to the queue

                    } // end else
                    // Checking if list of myPlanetsInEdges contains planets
                }  else if (myPlanetsInEdges.size() > 0) {
                    // Checking if it has myPlanetWithLeastPop in its edge
                    if (hasOwnInEdgeWithFewPop) {
                        long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.4); // transferring 40% of population
                        IEvent myMove = planetOperations.transferPeople(sourcePlanet, myPlanetWithLeastPop, transferPopulation);
                        ((LinkedList<IEvent>) myMoves).push(myMove); // adding move to the queue
                    } else {
                        // Checking if sourcePlanet's population is nearing the max capacity
                        if ( sourcePlanet.getPopulation() > sourcePlanet.getSize() * 0.9) {
                            //select one in random
                            long transferPopulation = (long) (sourcePlanet.getPopulation() * 0.5); // transferring 50% of population
                            IPlanet destinationPlanet = myPlanetsInEdges.get(random.nextInt(myPlanetsInEdges.size()));
                            IEvent myMove = planetOperations.transferPeople(sourcePlanet, destinationPlanet, transferPopulation);
                            ((LinkedList<IEvent>) myMoves).push(myMove); // adding move to the queue
                        } // end else

                    } // end else
                } // end else if


            } // end while

        } catch (Exception e) { // statement to catch exception
            System.out.println(e.getMessage()); // printing out message
            e.printStackTrace();
        } // end catch
        eventsToExecute.addAll(myMoves); // adding all moves made to eventsToExecute queue
    } // end takeTurn

    /* overriding getName method
     * @returns String value
     * named the strategy "mehta216"
     */
    @Override
    public String getName() {
        return "mehta216";
    } // end getName

    /* overriding compete method
     * @returns boolean value
     */
    @Override
    public boolean compete() {
        return false;
    } // end compete

}// end FinalStrategy

