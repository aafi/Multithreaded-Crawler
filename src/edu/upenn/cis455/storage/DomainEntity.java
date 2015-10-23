package edu.upenn.cis455.storage;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Entity class that stores the url and content mapping
 *
 */
@Entity
public class DomainEntity {
	
	@PrimaryKey
	private String url;
	
	private Date last_checked;
	private String raw_content;
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Date getLast_checked() {
		return last_checked;
	}
	
	public void setLast_checked(Date last_checked) {
		this.last_checked = last_checked;
	}
	
	public String getRaw_content() {
		return raw_content;
	}
	
	public void setRaw_content(String raw_content) {
		this.raw_content = raw_content;
	}
	
	

}
