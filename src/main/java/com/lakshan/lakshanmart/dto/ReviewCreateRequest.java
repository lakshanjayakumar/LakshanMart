package com.lakshan.lakshanmart.dto;

public class ReviewCreateRequest {

    private Long productId;
    private Integer rating;
    private String comment;

    public ReviewCreateRequest() {
    }

    public ReviewCreateRequest(Long productId, Integer rating, String comment) {
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
