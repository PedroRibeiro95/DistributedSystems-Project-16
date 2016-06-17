package pt.upa.broker.ws;

public class BrokerWsException extends Exception{

	private static final long serialVersionUID = 1L;

	public BrokerWsException() {
	}

	public BrokerWsException(String message) {
		super(message);
	}

	public BrokerWsException(Throwable cause) {
		super(cause);
	}

	public BrokerWsException(String message, Throwable cause) {
		super(message, cause);
	}

}

