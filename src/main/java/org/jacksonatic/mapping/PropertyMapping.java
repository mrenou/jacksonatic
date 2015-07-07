package org.jacksonatic.mapping;

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
        this.mappedName = name;
    }

    public void map(String mappedName) {
        this.mapped = true;
        this.mappedName = mappedName;
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
        return !mappedName.equals(name);
    }
}
