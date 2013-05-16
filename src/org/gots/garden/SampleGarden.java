package org.gots.garden;

import java.util.Date;

import org.gots.bean.Address;

public class SampleGarden implements GardenInterface {

	private String countryName="France";
	private String sampleLocality;

	@Override
	public String getAdminArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminArea(String adminArea) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCountryName() {
		// TODO Auto-generated method stub
		return countryName;
	}

	@Override
	public void setCountryName(String countryname) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocality() {
		// TODO Auto-generated method stub
		return sampleLocality;
	}

	@Override
	public void setLocality(String locality) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAddress(Address address) {
		// TODO Auto-generated method stub

	}

	@Override
	public Address getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGpsLongitude(double gpsLongitude) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getGpsLongitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGpsLatitude(double gpsLatitude) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDateLastSynchro(Date dateLastSynchro) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getDateLastSynchro() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getGpsLatitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getGpsAltitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGpsAltitude(double gpsAltitude) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUUID(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUUID() {
		return null;
	}

}
