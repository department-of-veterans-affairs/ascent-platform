package gov.va.ascent.starter.aws.exception;

import gov.va.ascent.framework.exception.AscentRuntimeException;

/**
 * Generic base exception for RefData exceptions. Extend this class for specific
 * exceptions.
 */
public class S3Exception extends AscentRuntimeException {

	private static final long serialVersionUID = -1823081544248832886L;

	/**
	 * Instantiates a new exception.
	 */
	public S3Exception() {
		super();
	}

	/**
	 * Instantiates a new RefData exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public S3Exception(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RefData exception.
	 *
	 * @param message
	 *            the message
	 */
	public S3Exception(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new RefData exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public S3Exception(final Throwable cause) {
		super(cause);
	}

}
