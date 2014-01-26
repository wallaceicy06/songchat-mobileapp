package edu.rice.harger.songchat;

import java.util.ArrayList;

public class YT_Wrapper {
	private String titles;
	private String URLs;
	
	public YT_Wrapper(String title, String URL)
	{
		setTitles(title);
		setURLs(URL);
	}

	public String getTitles() {
		return titles;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public String getURLs() {
		return URLs;
	}

	public void setURLs(String uRLs) {
		URLs = uRLs;
	}
	
	public String toString() {
		return titles;
	}
	
}
