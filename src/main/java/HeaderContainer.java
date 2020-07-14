import java.util.Objects;

class HeaderContainer{
    String jsonPath;
    String oldAttributeName;
    String newAttributeName;

    public HeaderContainer(String jsonPath, String oldAttributeName, String newAttributeName) {
        this.jsonPath = jsonPath;
        this.oldAttributeName = oldAttributeName;
        this.newAttributeName = newAttributeName;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getOldAttributeName() {
        return oldAttributeName;
    }

    public String getNewAttributeName() {
        return newAttributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderContainer that = (HeaderContainer) o;
        return Objects.equals(jsonPath, that.jsonPath) &&
                Objects.equals(oldAttributeName, that.oldAttributeName) &&
                Objects.equals(newAttributeName, that.newAttributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonPath, oldAttributeName, newAttributeName);
    }
}