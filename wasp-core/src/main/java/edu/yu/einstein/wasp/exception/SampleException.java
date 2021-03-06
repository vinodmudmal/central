package edu.yu.einstein.wasp.exception;

/**
 * Reporting of WASP metadata exceptions
 * @author asmclellan
 *
 */
public class SampleException extends WaspException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6538777217970617620L;

	public SampleException(){
		super();
	}
		
	public SampleException(String message){
		super(message);
	}
	
	public SampleException(String message, Throwable cause){
		super(message, cause);
	}
}
