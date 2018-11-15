package gov.va.ascent.starter.aws.transform;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.transfer.ServiceTransferObjectMarker;

public abstract class AbstractAwsS3Transformer<A extends Object, S extends ServiceTransferObjectMarker> {

	/** Constant for the logger for this class */
	public static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(AbstractAwsS3Transformer.class);

	/**
	 * Transform an {@link Object} to an {@link ServiceTransferObjectMarker}
	 *
	 * @param toTransform A the Object to transform
	 * @return S the ServiceTransferObjectMarker
	 */
	public abstract S transformToService(A toTransform);

	/**
	 * Transform an {@link ServiceTransferObjectMarker} to an {@link Object}
	 *
	 * @param toTransform S the ServiceTransferObjectMarker to transform
	 * @return A the Object
	 */
	public abstract A transformToAwsS3(S toTransform);

}
