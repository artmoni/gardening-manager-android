package org.gots.action.provider.simple;

import org.gots.action.BaseActionInterface;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "action")
public class SimpleAction implements BaseActionInterface {

    @Attribute(name = "name")
    String name;

    @Attribute(name = "duration")
    int duration;

    @Attribute(name = "uuid")
    String uuid;

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
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }
}
