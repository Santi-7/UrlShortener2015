package urlshortener2015.candypink.domain;

import java.util.ArrayList;

public class CsvStatusInfo {
	
	private ArrayList<String> status;
	private String failed = "";
	
	public CsvStatusInfo(){
		status = new ArrayList<String>();
	}
	
	public void add(String s){
		status.add(s);
	}
	
	public void setFailed(String s){
		failed = "An error ocurred while shortening URL "+ s + ". No URL was shortened.";
	}
	
	public String getStatus(){
		if(failed.equals("")){
			String stat="";
			for(int n=0;n<status.size();n++){
				stat = stat + status.get(n) + "; ";
			}
			return stat;
		}
		else{ return failed; }
		
	}
}
