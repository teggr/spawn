package dev.rebelcraft.ai.spawn.chat;

public class PaginationOptions {

    private int page;
    private int size;

    public PaginationOptions() {
        this.page = 0;
        this.size = 20;
    }

    public PaginationOptions(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
