package dev.rebelcraft.ai.spawn.mcp;

public class McpServerResponse {

    private String name;
    private String icon;
    private String description;
    private boolean favorite;

    // Constructors
    public McpServerResponse() {
    }

    public McpServerResponse(String name, String icon, String description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public McpServerResponse(String name, String icon, String description, boolean favorite) {
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.favorite = favorite;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
