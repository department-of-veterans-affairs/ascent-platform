package gov.va.ascent.starter.aws.exception;

import gov.va.ascent.framework.exception.AscentRuntimeException;

/**
 * Generic base exception for Aws exceptions. Extend this class for specific
 * exceptions.
 */
public class SqsException extends AscentRuntimeException {

	private static final long serialVersionUID = -1823081544248832886L;

	/**
	 * Instantiates a new exception.
	 */
	public SqsException() {
		super();
	}

	/**
	 * Instantiates a new Aws exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public SqsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Aws exception.
	 *
	 * @param message
	 *            the message
	 */
	public SqsException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new Aws exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public SqsException(final Throwable cause) {
		super(cause);
	}

}
