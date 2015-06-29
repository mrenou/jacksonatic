package org.jacksonatic;

public class PropertyMapping {

    private String name;

    private String mappedName = "";

    private boolean ignored = false;

    private boolean mapped = false;

    public PropertyMapping(String name) {
        this.name = name;
    }

    public void ignore() {
        this.ignored = true;
    }

    public void map() {
        this.mapped = true;
    }

    public String getName() {
        return name;
    }

    public String getMappedName() {
        return mappedName;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public boolean isMapped() {
        return mapped;
    }

    public boolean hasMappedName() {
        return !mappedName.isEmpty();
    }
}
