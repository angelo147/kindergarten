package edu.kindergarten.registration.rest;

public class Response {
	private Integer responseCode;

    private String responseMessage;

    public Response() {
        super();
    }

    public Response(int responseCode, String responseMessage) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public Response(ResponseCode ec) {
        super();
        this.responseCode = ec.getResponseCode();
        this.responseMessage = ec.getResponseMessage();
    }

    public void setErrorCode(ResponseCode ec) {
        this.responseCode = ec == null ? null : ec.getResponseCode();
        this.responseMessage =  ec == null ? null : ec.getResponseMessage();
    }

	public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}
