package edu.rice.harger.songchat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class YouTube extends AsyncTask<String,Void,ArrayList<YT_Wrapper>> {

	private YouTubeListener callback;
	private ArrayList<YT_Wrapper> toRet;
	private ArrayAdapter<YT_Wrapper> listAdapter;
	
	
	public YouTube(YouTubeListener listener, ArrayAdapter<YT_Wrapper> adapter) {
		callback = listener;
		listAdapter = adapter;
	}
	
	private ArrayList<YT_Wrapper> doThings(String searchText)
    {
    	//We will put the data into a StringBuilder
        StringBuilder builder=new StringBuilder();
        StringBuilder builder2=new StringBuilder();
        toRet=new ArrayList<YT_Wrapper>();
        try {
        URL url;
		
		url = new URL("http://gdata.youtube.com/feeds/api/videos?q="+searchText+"&max-results=20"+
						"&fields=entry(title,link[@rel=%27alternate%27])");
		
         
        XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp=factory.newPullParser();
         
        
		xpp.setInput(getInputStream(url), "UTF_8");
		
         
        int eventType=xpp.getEventType();
        while(eventType!=XmlPullParser.END_DOCUMENT){
           //Looking for a start tag
          if(eventType==XmlPullParser.START_TAG){
            //We look for "title" tag in XML response
            if(xpp.getName().equalsIgnoreCase("title")){
              //Once we found the "title" tag, add the text it contains to our builder
	              try {
	            	  String t=xpp.nextText();
					builder.append(t+"\n");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
          }
         
          try {
			eventType=xpp.next();
          } 
          catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
          }
          
        }
        
        
        factory=XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp=factory.newPullParser();
         
        
		xpp.setInput(getInputStream(url), "UTF_8");
		
         
        eventType=xpp.getEventType();
        while(eventType!=XmlPullParser.END_DOCUMENT){
           //Looking for a start tag
          if(eventType==XmlPullParser.START_TAG){
            //We look for "title" tag in XML response
            if(xpp.getName().equalsIgnoreCase("link")){
              builder2.append(xpp.getAttributeValue(null, "href") +"\n");
            }
          }
         
          try {
			eventType=xpp.next();
          } 
          catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
          }
          
        }
        String titles[]=builder.toString().split("\n");
        String links[]=builder2.toString().split("\n");
        
        for(int i=0;i<titles.length;i++)
        {
        	YT_Wrapper wrapper = new YT_Wrapper(titles[i].toString(),links[i].toString());
        	//listAdapter.add(wrapper);
            toRet.add(wrapper);
        }
		
        
   } catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     return toRet;   
    }
	
	 private InputStream getInputStream(URL url) {
   	  try {
   	    return url.openConnection().getInputStream();
   	  } catch (IOException e) {
   	    return null;
   	  }
   	}

	@Override
	protected ArrayList<YT_Wrapper> doInBackground(String... searchText) {
		// TODO Auto-generated method stub
		return doThings(searchText[0]);
		
	}
	
	protected void onPostExecute(ArrayList<YT_Wrapper> result) 
	{
		Log.i("CALLBACK", "we got here");
		listAdapter.clear();
		
		for(YT_Wrapper w: toRet) {
			listAdapter.add(w);
		}
		Log.i("WADSWORTH", Integer.toString(listAdapter.getCount()));
		listAdapter.notifyDataSetChanged();
        //callback.onResultsObtained(toRet);
    }
	
	public interface YouTubeListener {
		public void onResultsObtained(ArrayList<YT_Wrapper> results);
	}
}