package edu.upenn.cis455.storage;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class DataAccessor {
	
	//Primary Key for LoginInfo
	PrimaryIndex<String,LoginInfo> login;
	
	//Primary key for DomainEntity
	PrimaryIndex<String,DomainEntity> domain;
	
	public DataAccessor(EntityStore store){
		login = store.getPrimaryIndex(String.class, LoginInfo.class);
		domain = store.getPrimaryIndex(String.class, DomainEntity.class);
	}
}
