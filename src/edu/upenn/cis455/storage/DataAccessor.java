package edu.upenn.cis455.storage;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class DataAccessor {
	
	//Primary Key for LoginInfo
	PrimaryIndex<String,LoginInfo> login;
	
	//Primary key for DomainEntity
	PrimaryIndex<String,DomainEntity> domain;
	
	//Primary and secondary keys for Channel Info
	PrimaryIndex<String,ChannelInfo> channel;
	SecondaryIndex<String,String,ChannelInfo> channel_by_user;
	
	//Primary key for XPathInfo
	PrimaryIndex<String,XPathInfo> xpath;
	
	public DataAccessor(EntityStore store){
		login = store.getPrimaryIndex(String.class, LoginInfo.class);
		domain = store.getPrimaryIndex(String.class, DomainEntity.class);
		channel = store.getPrimaryIndex(String.class, ChannelInfo.class);
		channel_by_user = store.getSecondaryIndex(channel, String.class, "username");
		xpath = store.getPrimaryIndex(String.class, XPathInfo.class);
	}
}
