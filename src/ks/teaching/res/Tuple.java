package ks.teaching.res;

/**
 * Class for making two sized tuple
 * @author kwss
 *
 * @param <S1> The class of the first element
 * @param <S2> The class of the second element
 */
public class Tuple<S1, S2> {
	
	// 
	private S1 first;
	private S2 second;
	
	/**
	 * Constructor for an int and point2D tuple
	 * @param first the first element
	 * @param second the second element
	 */
	public Tuple(S1 first, S2 second) {
		this.first = first;
		this.second = second;
	}

	public S1 getFirst() {
		return first;
	}

	public void setFirst(S1 first) {
		this.first = first;
	}

	public S2 getSecond() {
		return second;
	}

	public void setSecond(S2 second) {
		this.second = second;
	}

	
}
