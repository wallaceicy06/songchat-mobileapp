package edu.rice.harger.songchat;

public class MessageInfo {

	private String _senderId;
	private String _recipientId;
	private String _text;
	private String _url;
	private int _startTime;
	private int _length;
	
	public MessageInfo(String senderId, String recipientId, String text, String url, int startTime, int length) {
		_senderId = senderId;
		_recipientId = recipientId;
		_text = text;
		_url = url;
		_startTime = startTime;
		_length = length;
	}
	
	public String getSenderId() {
		return _senderId;
	}
	
	public String getRecipientId() {
		return _recipientId;
	}
	
	public String getText() {
		return _text;
	}
	
	public String getUrl() {
		return _url;
	}
	
	public int getStartTime() {
		return _startTime;
	}
	
	public int getLength() {
		return _length;
	}
	
	public String toString() {
		return _senderId;
	}
}
