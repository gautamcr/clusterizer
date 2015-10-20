package com.dsto.ei.ha;

public interface IClusterizer {

	public int numInstances() ;
	public int numLocalInstances() ;
	public boolean isPrimary() ;
}
