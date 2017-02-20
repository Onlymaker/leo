package com.onlymaker.leo.data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    @Column(name = "cache")
    private String cache;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "sub_rank")
    private Integer subRank;

    @Column(name = "total_review")
    private Integer totalReview;

    @Column(name = "average_star")
    private Float averageStar;

    @Column(name = "star_1")
    private Float starOne;

    @Column(name = "star_2")
    private Float starTwo;

    @Column(name = "star_3")
    private Float starThree;

    @Column(name = "star_4")
    private Float starFour;

    @Column(name = "star_5")
    private Float starFive;

    @Column(name = "create_time")
    private Timestamp createTime;

    public Log() {
    }

    public Log(Entry entry) {
        this.entry = entry;
        this.createTime = new Timestamp(new Date().getTime());
    }

    public Log(Entry entry, String cache, Integer rank, Integer subRank, Integer totalReview, Float averageStar,
               Float starOne, Float starTwo, Float starThree, Float starFour, Float starFive) {
        this.entry = entry;
        this.cache = cache;
        this.rank = rank;
        this.subRank = subRank;
        this.totalReview = totalReview;
        this.averageStar = averageStar;
        this.starOne = starOne;
        this.starTwo = starTwo;
        this.starThree = starThree;
        this.starFour = starFour;
        this.starFive = starFive;
        this.createTime = new Timestamp(new Date().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getSubRank() {
        return subRank;
    }

    public void setSubRank(Integer subRank) {
        this.subRank = subRank;
    }

    public Integer getTotalReview() {
        return totalReview;
    }

    public void setTotalReview(Integer totalReview) {
        this.totalReview = totalReview;
    }

    public Float getAverageStar() {
        return averageStar;
    }

    public void setAverageStar(Float averageStar) {
        this.averageStar = averageStar;
    }

    public Float getStarOne() {
        return starOne;
    }

    public void setStarOne(Float starOne) {
        this.starOne = starOne;
    }

    public Float getStarTwo() {
        return starTwo;
    }

    public void setStarTwo(Float starTwo) {
        this.starTwo = starTwo;
    }

    public Float getStarThree() {
        return starThree;
    }

    public void setStarThree(Float starThree) {
        this.starThree = starThree;
    }

    public Float getStarFour() {
        return starFour;
    }

    public void setStarFour(Float starFour) {
        this.starFour = starFour;
    }

    public Float getStarFive() {
        return starFive;
    }

    public void setStarFive(Float starFive) {
        this.starFive = starFive;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public boolean validate() {
        boolean result = rank != null && subRank != null;
        if(result) {
            if(totalReview == null) totalReview = 0;
            if(averageStar == null) averageStar = 0f;
            if(starOne == null) starOne = 0f;
            if(starTwo == null) starTwo = 0f;
            if(starThree == null) starThree = 0f;
            if(starFour == null) starFour = 0f;
            if(starFive == null) starFive = 0f;
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "rank: %d, subRank: %d, totalReviews: %d, stars: %f (from 5 to 1: %f, %f, %f, %f, %f)",
                rank,
                subRank,
                totalReview,
                averageStar,
                starFive,
                starFour,
                starThree,
                starTwo,
                starOne
        );
    }
}