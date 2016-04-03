package org.falcon.ImageDiff;

import java.io.File;

class Diff
{
	private File adiffb;
	private File bdiffa;
	private File combine;

	public File getAdiffb()             { return adiffb; }
	public File getBdiffa()             { return bdiffa; }
	public File getCombine()            { return combine; }
	
	public void setAdiffb(File adiffb)  { this.adiffb = adiffb; }
	public void setBdiffa(File bdiffa)  { this.bdiffa = bdiffa; }
    public void setCombine(File combine) { this.combine = combine; }
		
}