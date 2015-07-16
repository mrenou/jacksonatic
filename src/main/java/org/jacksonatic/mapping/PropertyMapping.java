package org.jacksonatic.mapping;

public class PropertyMapping {

    private String name;

    private String mappedName;

    private boolean ignored = false;

    private boolean mapped = false;

    PropertyMapping(String name, String mappedName, boolean ignored, boolean mapped) {
        this.name = name;
        this.mappedName = mappedName;
        this.ignored = ignored;
        this.mapped = mapped;
    }

    public PropertyMapping(String name) {
        this.name = name;
        this.mappedName = name;
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
        return !mappedName.equals(name);
    }

    PropertyMapping copy() {
        return new PropertyMapping(name, mappedName, ignored, mapped);
    }

    PropertyMapping copyWithParentMapping(PropertyMapping parentMapping) {
        return new PropertyMapping(name,
                hasMappedName() ? mappedName : parentMapping.mappedName,
                (ignored == false && mapped == false) ? parentMapping.ignored : ignored,
                (ignored == false && mapped == false) ? parentMapping.mapped : mapped);
    }

}
