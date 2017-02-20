package com.onlymaker.leo.data;

import com.onlymaker.leo.util.AmazonStore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "entry")
public class Entry {
    public static final String DEFAULT_THUMB = "images/holder.jpg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "asin")
    private String asin;

    @Column(name = "thumb")
    private String thumb;

    @Column(name = "url")
    private String url;

    @Column(name = "store")
    private String store;

    @Column(name = "status")
    private Integer status;

    @Column(name = "error")
    private Integer error;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    public Entry() {
    }

    public Entry(String asin, String store) {
        this.asin = asin;
        this.thumb = DEFAULT_THUMB;
        this.url = AmazonStore.STORES.get(store) + asin;
        this.store = store;
        this.status = 0;
        this.error = 0;
        this.createTime = new Timestamp(new Date().getTime());
        this.updateTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}