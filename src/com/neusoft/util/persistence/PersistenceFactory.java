package com.neusoft.util.persistence;

public class PersistenceFactory {
	/**
	 * 
	 * @return
	 */
	public static Persistence getInstance(){
		return new DBPersistence() ;
	}
}
