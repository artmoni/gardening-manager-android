package org.gots.allotment.provider;

import org.gots.bean.BaseAllotmentInterface;

import java.util.List;

public interface AllotmentProvider {

    public BaseAllotmentInterface getCurrentAllotment();

    void setCurrentAllotment(BaseAllotmentInterface allotmentInterface);

    public List<BaseAllotmentInterface> getMyAllotments(boolean force);

    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment);

    int removeAllotment(BaseAllotmentInterface allotment);

    BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment);

    public abstract BaseAllotmentInterface getAllotmentByID(Integer id);


}
