package com.sidespin.ha;

public interface IClusterizer {

	public int numInstances() ;
	public int numLocalInstances() ;
	public boolean isPrimary() ;
	public String getName() ;
	public void exitCluster() ;
}
