/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 *
 */
public class Simulator {

	/**
	 * Length of car plate numbers
	 */
	public static final int PLATE_NUM_LENGTH = 3;

	/**
	 * Number of seconds in one hour
	 */
	public static final int NUM_SECONDS_IN_1H = 3600;

	/**
	 * Maximum duration a car can be parked in the lot
	 */
	public static final int MAX_PARKING_DURATION = 8 * NUM_SECONDS_IN_1H;

	/**
	 * Total duration of the simulation in (simulated) seconds
	 */
	public static final int SIMULATION_DURATION = 24 * NUM_SECONDS_IN_1H;

	/**
	 * The probability distribution for a car leaving the lot based on the duration
	 * that the car has been parked in the lot
	 */
	public static final TriangularDistribution departurePDF = new TriangularDistribution(0, MAX_PARKING_DURATION / 2,
			MAX_PARKING_DURATION);

	/**
	 * The probability that a car would arrive at any given (simulated) second
	 */
	private Rational probabilityOfArrivalPerSec;

	/**
	 * The simulation clock. Initially the clock should be set to zero; the clock
	 * should then be incremented by one unit after each (simulated) second
	 */
	private int clock;

	/**
	 * Total number of steps (simulated seconds) that the simulation should run for.
	 * This value is fixed at the start of the simulation. The simulation loop
	 * should be executed for as long as clock < steps. When clock == steps, the
	 * simulation is finished.
	 */
	private int steps;

	/**
	 * Instance of the parking lot being simulated.
	 */
	private ParkingLot lot;

	/**
	 * Queue for the cars wanting to enter the parking lot
	 */
	private Queue<Spot> incomingQueue;

	/**
	 * Queue for the cars wanting to leave the parking lot
	 */
	private Queue<Spot> outgoingQueue;

	/**
	 * @param lot   is the parking lot to be simulated
	 * @param steps is the total number of steps for simulation
	 */
	public Simulator(ParkingLot lot, int perHourArrivalRate, int steps) {
		if (lot == null) {
			throw new NullPointerException();
		}
		if (perHourArrivalRate < 1) {
			throw new IllegalArgumentException();
		}
		if (steps <= 0) {
			throw new IllegalArgumentException();
		}
	

		this.lot = lot;

		this.steps = steps;

		this.clock = 0;
		
		// YOUR CODE HERE! YOU SIMPLY NEED TO COMPLETE THE LINES BELOW:

		// What should the two questions marks be filled with? 
		// Hint: you are being given a perHourArrivalRate. 
		// All you need to do is to convert this hourly rate into 
		// a per-second rate (probability).
		
		this.probabilityOfArrivalPerSec = new Rational(perHourArrivalRate, 3600);

		
		// Finally, you need to initialize the incoming and outgoing queues

		incomingQueue = new LinkedQueue<Spot>();
		outgoingQueue = new LinkedQueue<Spot>();
	}


	/**
	 * Simulate the parking lot for the number of steps specified by the steps
	 * instance variable
	 * NOTE: Make sure your implementation of simulate() uses peek() from the Queue interface.
	 */
	public void simulate() {
		// Local variables can be defined here.
		steps = SIMULATION_DURATION;

		this.clock = 0;
		int duration = 0;
		
		
		
		// Note that for the specific purposes of A2, clock could have been 
		// defined as a local variable too.

		while (clock < steps) {
	
			// WRITE YOUR CODE HERE!
			

			if (RandomGenerator.eventOccurred(probabilityOfArrivalPerSec)) {
				Car cc = new Car(RandomGenerator.generateRandomString(PLATE_NUM_LENGTH));
				Spot newcar = new Spot(cc,clock);
				incomingQueue.enqueue(newcar);
				
				
			}

			//loop through whole parkinglot to check each car
			
			for (int i = 0; i < lot.getOccupancy();i++ ) {
				if (lot.getSpotAt(i) != null) {
					duration = clock - (lot.getSpotAt(i).getTimestamp());
				
				}
				
				if (duration >= MAX_PARKING_DURATION) {
					
					if(lot.getSpotAt(i) != null){
						outgoingQueue.enqueue(lot.getSpotAt(i));
						lot.remove(i);
							
					}

				}
				else if (duration < MAX_PARKING_DURATION){
					//checks if car leaving
					if(RandomGenerator.eventOccurred(departurePDF.pdf(duration))){
						
						outgoingQueue.enqueue(lot.getSpotAt(i));
						lot.remove(i);
					}
				}
			

			}
			
			if (!incomingQueue.isEmpty()) {
					
					//checks if spot is avaliable
					if (lot.attemptParking(incomingQueue.peek().getCar(),clock)) {
						incomingQueue.dequeue();
				
					}
					
				
			}
			if (!outgoingQueue.isEmpty()) {
				outgoingQueue.dequeue();
				

			}
			clock++;
			
		}
	
	
	}

	public int getIncomingQueueSize() {
		
		return incomingQueue.size();
	}
}