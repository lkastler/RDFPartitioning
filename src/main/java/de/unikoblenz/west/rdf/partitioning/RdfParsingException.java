package de.unikoblenz.west.rdf.partitioning;

/**
 * indicates a failure during the RDF parsing process.
 * @author lkastler
 */
public class RdfParsingException extends Exception {

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a new RdfParsingException with given message and given cause.
	 * @param message - message for exception.
	 * @param cause - cause for exception.
	 */
	public RdfParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * creates a new RdfParsingException with given message.
	 * @param message - message for exception.
	 */
	public RdfParsingException(String message) {
		super(message);
	}

	
	
}
