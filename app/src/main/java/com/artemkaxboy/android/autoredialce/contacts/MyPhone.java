package com.artemkaxboy.android.autoredialce.contacts;

public class MyPhone {
	long id;
	int type;
	String label, number;
	public void setId( long id ) { this.id = id; }
	public long getId() { return id; }
	public void setType( int type ) { this.type = type; }
	public int getType() { return type; }
	public void setLabel( String label ) { this.label = label; }
	public String getLabel() { return label; }
	public void setNumber( String number ) { this.number = number; }
	public String getNumber() { return number; }
	
	
	
	
	
	public static boolean compare( String n1, String n2 ) {
		try {
			n1 = clean( n1 );
			n2 = clean( n2 );
			int lMin = 6, l1, l2;
			if(( l1 = n1.length()) < lMin ) lMin = l1;
			if(( l2 = n2.length()) < lMin ) lMin = l2;
			if( lMin < 3 ) return false;
			for( int i = 1; i <= lMin; i ++ )
				if( n1.charAt( l1 - i ) != n2.charAt( l2 - i ))
					return false;
			return true;
		} catch( Exception e ) {
			return false;
		}
	}
	public static String clean( String s1 ) {
		s1 = s1.replaceAll( "[^\\d]", "" );
		return s1;
	}
}
