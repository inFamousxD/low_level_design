package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Theater {
    private final String id;
    private final String name;
    private final String city;
    private final String address;
    private final List<Screen> screens = new ArrayList<>();

    public Theater(String id, String name, String city, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
    }

    public void addScreen(Screen screen) { screens.add(screen); }

    public String getId()             { return id; }
    public String getName()           { return name; }
    public String getCity()           { return city; }
    public String getAddress()        { return address; }
    public List<Screen> getScreens()  { return Collections.unmodifiableList(screens); }

    @Override
    public String toString() {
        return "Theater[" + name + ", " + city + "]";
    }
}
