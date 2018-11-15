package gov.va.ascent.starter.aws.util;

/**
 * Constants used by Hystrix for the service implementation
 */
public final class HystrixCommandConstants {

	/** Aws starter app Thread Pool Group. */
	public static final String AWS_SERVICE_GROUP_KEY = "VetServicesAwsServiceGroup";

	/**
	 * Instantiation not allowed
	 */
	private HystrixCommandConstants() {
		throw new UnsupportedOperationException();
	}

}
