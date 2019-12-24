package com.efimchik.ifmo.web.mvc.repository;

public class Paging {
    public final Integer page;
    public final Integer itemPerPage;

    public Paging(final Integer page, final Integer itemPerPage) {
        if(page == null)
            this.page = 1;
        else this.page = page+1;
        if(itemPerPage == null)
            this.itemPerPage = 999;
        else this.itemPerPage = itemPerPage;
    }
}