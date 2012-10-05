package org.gots.seed.providers.simple;

import java.util.Date;

import org.gots.action.BaseActionInterface;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "action")
public class SimpleAction implements BaseActionInterface {

	@Attribute(name = "name")
	String name;

	@Attribute(name = "duration")
	int duration;

	@Override
	public Date getDateActionTodo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateActionTodo(Date dateActionTodo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateActionDone(Date dateActionDone) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getDateActionDone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDuration(int duration) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDuration() {
		return duration;
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
		return name;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLogId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLogId(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGrowingSeedId(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getGrowingSeedId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
