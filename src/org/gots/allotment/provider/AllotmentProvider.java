package org.gots.allotment.provider;

import java.util.List;

import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.garden.GardenInterface;

public interface AllotmentProvider {

	public BaseAllotmentInterface getCurrentAllotment();
	
	public List<BaseAllotmentInterface> getMyAllotments();

	public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment);

	int removeAllotment(BaseAllotmentInterface allotment);

	BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment);

    void setCurrentAllotment(BaseAllotmentInterface allotmentInterface);
	
	
}
