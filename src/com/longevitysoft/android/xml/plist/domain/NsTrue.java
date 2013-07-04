package com.longevitysoft.android.xml.plist.domain;

/**
 * Represents a simple plist true element.
 */
public class NsTrue extends PListObject implements IPListSimpleObject<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3560354198720649001L;

	public NsTrue() {
		setType(PListObjectType.TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#getValue()
	 */
	@Override
	public Boolean getValue() {
		return new Boolean(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
	 * (java.lang.Object)
	 */
	@Override
	public void setValue(Boolean val) {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.longevitysoft.android.xml.plist.domain.IPListSimpleObject#setValue
	 * (java.lang.String)
	 */
	@Override
	public void setValue(java.lang.String val) {
		// noop
	}

}