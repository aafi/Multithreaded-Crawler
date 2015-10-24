package edu.upenn.cis455.storage;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DBWrapper {
	
	private static String envDirectory = null;
	
	private static Environment myEnv;
	private static EntityStore store;
	private static DataAccessor da;
//	private EntityCursor <DomainEntity> domain_pcursor;
	
	/**
	 * Sets the environment directory in the constructor
	 * @param dir
	 */
	public DBWrapper(String dir){
		envDirectory = dir;
	}
	
	/**
	 * Sets up the Environment and the EntityStore
	 */
	public void setup(){
		 EnvironmentConfig envConfig = new EnvironmentConfig();
		 StoreConfig storeConfig = new StoreConfig();
		 
		 envConfig.setAllowCreate(true);
		 storeConfig.setAllowCreate(true);
//		 envConfig.setTransactional(true);
//		 storeConfig.setTransactional(true);
		 
		 myEnv = new Environment(new File(envDirectory),envConfig);
		 store = new EntityStore(myEnv, "EntityStore", storeConfig);
		 da = new DataAccessor(store);
	}
	
	/**
	 * Shuts down the Environment and EntityStore
	 */
	
	public void shutdown(){
//		domain_pcursor.close();
		store.close();
		myEnv.close();
	}
	
	/**
	 * Puts the login information in the database
	 * @param object to be added
	 */
	public void putLoginInfo(LoginInfo info){
		da.login.put(info);
	}
	
	/**
	 * Puts the domain information in the database
	 * @param object to be added
	 */
	public void putDomainInfo(DomainEntity info){
//		System.out.println("Storing "+info.getUrl());
		da.domain.put(info);
	}
	
	/**
	 * Puts channel info in the database
	 * @param info
	 */
	public void putChannelInfo(ChannelInfo info){
		da.channel.put(info);
	}
	
	/**
	 * Puts Xpath info into the database
	 * @param info
	 */
	public void putXPathInfo(XPathInfo info){
		da.xpath.put(info);
	}
	
	/**
	 * Gets the login information from the database
	 * @param key to be looked up
	 * @return LoginInfo
	 */
	public LoginInfo getLoginInfo(String username){
		return da.login.get(username);
	}
	
	/**
	 * Gets the domain information from the database
	 * @param key to be looked up
	 * @return DomainEntityObject
	 */
	public DomainEntity getDomainInfo(String url){
		return da.domain.get(url);
	}
	
	/**
	 * Returns the channel info looked up by channel name
	 * @param name
	 * @return channel info object
	 */
	
	public ChannelInfo getChannelInfo(String name){
		return da.channel.get(name);
	}
	
	/**
	 * Returns all the channels present in the database
	 * @param channel
	 * @return
	 */
	public ArrayList<ChannelInfo> getAllChannels(){
		ArrayList<ChannelInfo> channel_list = new ArrayList<ChannelInfo>();
		EntityCursor<ChannelInfo> cursor = da.channel.entities();
		try{
			for(ChannelInfo ci : cursor){
				channel_list.add(ci);
			}
		}finally{
			cursor.close();
		}
		
		return channel_list;
	}
	
	/**
	 * Returns a list of channel info objects based on the usernames
	 * @param username
	 * @return
	 */
	public ArrayList<ChannelInfo> getChannelsByUser(String username){
		ArrayList<ChannelInfo> channel_list = new ArrayList<ChannelInfo>();
		EntityCursor<ChannelInfo> sec_cursor = da.channel_by_user.subIndex(username).entities();
		try{
			for(ChannelInfo channel : sec_cursor){
				channel_list.add(channel);
			}
		}finally{
			sec_cursor.close();
		}
		
		return channel_list;
	}
	
	/**
	 * Returns xpath info based on the xpath string
	 * @param xpath
	 * @return
	 */
	
	public XPathInfo getXpathInfo(String xpath){
		return da.xpath.get(xpath);
	}
	
	/**
	 * Deletes a channel
	 * @param name
	 */
	public void deleteChannel(String name){
		da.channel.delete(name);
	}
	
//	/**
//	 * Checks if the database contains the url content
//	 * @param url to be checked
//	 * @return boolean indicating presence or absence
//	 */
//	public boolean containsDomain(String url){
//		domain_pcursor = da.domain.entities();
//		
//		for(DomainEntity info : domain_pcursor){
//			if(info.getUrl().equals(url))
//				return true;
//		}
//		
//		return false;
//	}
	
//	private static int printurls(){
//		EntityCursor <DomainEntity> domain_pcursor = da.domain.entities();
//		int total=0;
//		for(DomainEntity info : domain_pcursor){
//			total++;
//		}
//		domain_pcursor.close();
//		return total;
//	}
////	
//	public static void main(String [] args){
//		String dir = "/home/cis455/git/hw2/testdb";
//		DBWrapper db = new DBWrapper(dir);
//		
//		db.setup();
////			LoginInfo info = new LoginInfo();
//////		DomainEntity dom = new DomainEntity();
//////		dom.setUrl("blah");
//////		dom.setRaw_content("blah");
//////		db.putDomainInfo(dom);
//		System.out.println(printurls());
//////		info.setUsername("admin2");
//////		info.setPassword("password");
//////		dom.setUrl("admin2");
//////		dom.setRaw_content("some content");
//////		
//////		db.putLoginInfo(info);
//////		db.putDomainInfo(dom);
////		
//////		System.out.println(db.getDomainInfo("https://dbappserv.cis.upenn.edu/crawltest.html").getRaw_content());
//////		System.out.println(db.getLoginInfo("admin2").getPassword());
////			System.out.println(containsDomain("https://dbappserv.cis.upenn.edu/crawltest/nytimes/crawltest/nytimes/"));
////		System.out.println(db.containsDomain("https://dbappserv.cis.upenn.edu/crawltest/bbc/frontpage.xml"));
//////		DomainEntity result = db.getDomainInfo("https://dbappserv.cis.upenn.edu/crawltest.html");
//////		System.out.println("Url: "+result.getUrl()+" raw content: "+result.getRaw_content());
//		db.shutdown();
//	}
}
