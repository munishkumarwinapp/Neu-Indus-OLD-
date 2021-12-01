package com.winapp.sot;

public class Area {
	 private String areacode;
	    private String areaname;
	    public boolean box;
	    
		public Area(String areacode, String areaname,boolean box) {
	        this.areacode = areacode;
	        this.areaname = areaname;
	        this.box = box;
	    }

		public String getAreacode() {
			return areacode;
		}

		public void setAreacode(String areacode) {
			this.areacode = areacode;
		}

		public String getAreaname() {
			return areaname;
		}

		public void setAreaname(String areaname) {
			this.areaname = areaname;
		}
		public boolean isBox() {
			return box;
		}

		public void setBox(boolean box) {
			this.box = box;
		}
	    
}
