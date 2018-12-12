package gov.va.ascent.starter.aws.s3.config;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    private Integer maxconcurrentrequests;
    private Integer maxqueuesize;
    private Integer multipartthreshold;
    private Integer multipartchunksize;
    private Boolean useaccelerateendpoint;
    private String addressingstyle;
    private Boolean payloadsigningenabled;
	/**
	 * @return the maxconcurrentrequests
	 */
	public Integer getMaxconcurrentrequests() {
		return maxconcurrentrequests;
	}
	/**
	 * @param maxconcurrentrequests the maxconcurrentrequests to set
	 */
	public void setMaxconcurrentrequests(Integer maxconcurrentrequests) {
		this.maxconcurrentrequests = maxconcurrentrequests;
	}
	/**
	 * @return the maxqueuesize
	 */
	public Integer getMaxqueuesize() {
		return maxqueuesize;
	}
	/**
	 * @param maxqueuesize the maxqueuesize to set
	 */
	public void setMaxqueuesize(Integer maxqueuesize) {
		this.maxqueuesize = maxqueuesize;
	}
	/**
	 * @return the multipartthreshold
	 */
	public Integer getMultipartthreshold() {
		return multipartthreshold;
	}
	/**
	 * @param multipartthreshold the multipartthreshold to set
	 */
	public void setMultipartthreshold(Integer multipartthreshold) {
		this.multipartthreshold = multipartthreshold;
	}
	/**
	 * @return the multipartchunksize
	 */
	public Integer getMultipartchunksize() {
		return multipartchunksize;
	}
	/**
	 * @param multipartchunksize the multipartchunksize to set
	 */
	public void setMultipartchunksize(Integer multipartchunksize) {
		this.multipartchunksize = multipartchunksize;
	}
	/**
	 * @return the useaccelerateendpoint
	 */
	public Boolean getUseaccelerateendpoint() {
		return useaccelerateendpoint;
	}
	/**
	 * @param useaccelerateendpoint the useaccelerateendpoint to set
	 */
	public void setUseaccelerateendpoint(Boolean useaccelerateendpoint) {
		this.useaccelerateendpoint = useaccelerateendpoint;
	}
	/**
	 * @return the addressingstyle
	 */
	public String getAddressingstyle() {
		return addressingstyle;
	}
	/**
	 * @param addressingstyle the addressingstyle to set
	 */
	public void setAddressingstyle(String addressingstyle) {
		this.addressingstyle = addressingstyle;
	}
	/**
	 * @return the payloadsigningenabled
	 */
	public Boolean getPayloadsigningenabled() {
		return payloadsigningenabled;
	}
	/**
	 * @param payloadsigningenabled the payloadsigningenabled to set
	 */
	public void setPayloadsigningenabled(Boolean payloadsigningenabled) {
		this.payloadsigningenabled = payloadsigningenabled;
	}
    
    
	
	
	
}

