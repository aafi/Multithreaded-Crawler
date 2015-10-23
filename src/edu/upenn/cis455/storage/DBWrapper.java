package edu.upenn.cis455.storage;

import java.io.File;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DBWrapper {
	
	private static String envDirectory = null;
	
	private static Environment myEnv;
	private static EntityStore store;
	private DataAccessor da;
	
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
		 
		 myEnv = new Environment(new File(envDirectory),envConfig);
		 store = new EntityStore(myEnv, "EntityStore", storeConfig);
		 da = new DataAccessor(store);
	}
	
	/**
	 * Shuts down the Environment and EntityStore
	 */
	
	public void shutdown(){
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
//		System.out.println("Storing contents in db");
		da.domain.put(info);
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
	 * Checks if the database contains the url content
	 * @param url to be checked
	 * @return boolean indicating presence or absence
	 */
	public boolean containsDomain(String url){
		EntityCursor <DomainEntity> domain_pcursor = da.domain.entities();
		
		for(DomainEntity info : domain_pcursor){
			if(info.getUrl().equals(url))
				return true;
		}
		
		return false;
	}
	
//	public static void main(String [] args){
//		String dir = "/home/cis455/git/hw2/testdb";
//		DBWrapper db = new DBWrapper(dir);
//		
//		db.setup();
//		LoginInfo info = new LoginInfo();
//		DomainEntity dom = new DomainEntity();
////		info.setUsername("admin2");
////		info.setPassword("password");
////		dom.setUrl("admin2");
////		dom.setRaw_content("some content");
////		
////		db.putLoginInfo(info);
////		db.putDomainInfo(dom);
//		
////		System.out.println(db.getDomainInfo("admin2").getRaw_content());
////		System.out.println(db.getLoginInfo("admin2").getPassword());
//		
////		DomainEntity result = db.getDomainInfo("https://dbappserv.cis.upenn.edu/crawltest.html");
////		System.out.println("Url: "+result.getUrl()+" raw content: "+result.getRaw_content());
//		db.shutdown();
//	}
}
