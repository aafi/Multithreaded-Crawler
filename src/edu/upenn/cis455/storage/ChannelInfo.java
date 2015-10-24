package edu.upenn.cis455.storage;

import java.util.ArrayList;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

import static com.sleepycat.persist.model.Relationship.*;

/**
 * Stores User Defined Channel Information
 * @author cis455
 *
 */

@Entity
public class ChannelInfo {
	
	@PrimaryKey
	String channel;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	String username;
	
	ArrayList <String> xpaths = null;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList<String> getXpaths() {
		return xpaths;
	}

	public void setXpaths(ArrayList<String> xpaths) {
		this.xpaths = xpaths;
	}

}
