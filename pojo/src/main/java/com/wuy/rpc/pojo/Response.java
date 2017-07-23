package com.wuy.rpc.pojo;

public class Response {
	private Long id;
	private Object result;
	private String code = "0000";// 0000表示成功，其他表示失败！
	private String msg;// 失败的原因

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", result=" + result + ", code=" + code
				+ ", msg=" + msg + "]";
	}
	
}
