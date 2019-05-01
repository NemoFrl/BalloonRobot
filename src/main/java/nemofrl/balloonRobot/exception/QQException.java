package nemofrl.balloonRobot.exception;

public class QQException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3253385874818291295L;
	private String msg;
	private String code;
	private Exception e;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Exception getE() {
		return e;
	}
	public void setE(Exception e) {
		this.e = e;
	}
	public QQException(String msg, String code, Exception e) {
		super();
		this.msg = msg;
		this.code = code;
		this.e = e;
	}
	public QQException(String msg, Exception e) {
		super();
		this.msg = msg;
		this.e = e;
	}
	public QQException(String msg) {
		super();
		this.msg = msg;
	}
	
}
