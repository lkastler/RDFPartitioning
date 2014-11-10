package de.unikoblenz.west.rdf.partitioning;

/**
 * indicates an failure during the partitioning execution.
 * 
 * @author lkastler
 */
public class RdfPartitioningException extends Exception {

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a new RdfPartitioningException with given message and given cause.
	 * @param message - the message what happened.
	 * @param cause - the cause for the execution interruption.
	 */
	public RdfPartitioningException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * creates a new RdfPartitioningException with given message.
	 * @param message - the message what happened.
	 */
	public RdfPartitioningException(String message) {
		super(message);
	}

}
